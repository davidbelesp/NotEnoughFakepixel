package com.nef.notenoughfakepixel.events.handlers;

import com.google.gson.Gson;
import com.nef.notenoughfakepixel.utils.ConfigHandler;
import com.nef.notenoughfakepixel.utils.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RepoHandler {

    // ==== Configuration ====

    private static final long TTL_MS = TimeUnit.DAYS.toMillis(1);
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final String USER_AGENT   = "YourModName/1.0 (Minecraft 1.8.9)";

    // Mini-pool for IO
    private static final ExecutorService IO_POOL = new ThreadPoolExecutor(
            1, 2, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(64),
            r -> {
                Thread t = new Thread(r, "RepoHandler-IO-Pool");
                t.setDaemon(true);
                return t;
            },
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    private static final ScheduledExecutorService EXEC =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "RepoHandler-IO");
                t.setDaemon(true);
                return t;
            });

    private RepoHandler() {}

    private static final Gson gson = ConfigHandler.GSON;

    // State per source (if we want to add more sources in the future)
    private static final class SourceState {
        final String url;
        final long ttlMs;
        final AtomicReference<String> json = new AtomicReference<>(null);
        final AtomicBoolean loading = new AtomicBoolean(false);

        volatile long lastFetch = 0L;
        volatile String etag = null;
        volatile String lastModified = null;

        SourceState(String url, long ttlMs) {
            this.url = url;
            this.ttlMs = ttlMs;
        }
    }

    // Cached JSON
    private static final ConcurrentMap<String, SourceState> SOURCES = new ConcurrentHashMap<>();

    public static void registerSource(String key, String url, long ttlMs) {
        SOURCES.compute(key, (k, prev) -> {
            if (prev == null) return new SourceState(url, ttlMs);
            prev.lastFetch = 0L;
            prev.etag = null;
            prev.lastModified = null;
            return new SourceState(url, ttlMs);
        });
    }

    // Loading on mod init
    public static void warmupAllAsync() {
        for (Map.Entry<String, SourceState> e : SOURCES.entrySet()) {
            loadIfStaleAsync(e.getKey(), false);
        }
    }

    // Loading on server connect
    public static void refreshAsync(Set<String> keys) {
        for (String k : keys) loadIfStaleAsync(k, true);
    }

    // Loading on GUI (if needed)
    public static void ensureLoadedAsync(String key) {
        loadIfStaleAsync(key, true);
    }

    public static boolean isLoaded(String key) {
        SourceState s = SOURCES.get(key);
        return s != null && s.json.get() != null;
    }

    public static String getJson(String key) {
        SourceState s = SOURCES.get(key);
        return s == null ? null : s.json.get();
    }

    public static <T> T getAs(String key, Class<T> type) {
        String raw = getJson(key);
        if (raw == null) return null;
        try { return gson.fromJson(raw, type); }
        catch (Exception ex) {
            Logger.logErrorConsole("JSON parse error (" + key + "): " + ex.getMessage());
            return null;
        }
    }

    // Shutdown pool on mod unload
    public static void shutdown() {
        IO_POOL.shutdownNow();
    }

    // Core loading
    private static void loadIfStaleAsync(String key, boolean forceIfExpired) {
        final SourceState s = SOURCES.get(key);
        if (s == null) return;

        long now = System.currentTimeMillis();
        boolean expired = (now - s.lastFetch) >= s.ttlMs;

        if (!forceIfExpired && !expired && s.json.get() != null) return;
        if (!s.loading.compareAndSet(false, true)) return;

        IO_POOL.execute(() -> {
            try {
                String result = downloadWithCache(s);
                if (result != null) {
                    s.json.set(result);
                    s.lastFetch = System.currentTimeMillis();
                } else if (s.json.get() == null) {
                    s.lastFetch = now - (s.ttlMs - TimeUnit.MINUTES.toMillis(1));
                }
            } catch (Exception ex) {
                Logger.logErrorConsole("Fetch fail (" + key + "): " + ex.getMessage());
                s.lastFetch = now - (s.ttlMs - TimeUnit.MINUTES.toMillis(1));
            } finally {
                s.loading.set(false);
            }
        });
    }

    private static String downloadWithCache(SourceState s) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(s.url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestProperty("User-Agent", USER_AGENT);

        if (s.etag != null)         conn.setRequestProperty("If-None-Match", s.etag);
        if (s.lastModified != null) conn.setRequestProperty("If-Modified-Since", s.lastModified);

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_NOT_MODIFIED) {
            return s.json.get();
        }
        if (code >= 200 && code < 300) {
            String etag = conn.getHeaderField("ETag");
            String lm   = conn.getHeaderField("Last-Modified");
            String body = readAll(conn);

            if (etag != null) s.etag = etag;
            if (lm != null)   s.lastModified = lm;
            return body;
        }
        if (s.json.get() != null) return null;
        throw new RuntimeException("HTTP " + code + " on " + s.url);
    }

    private static String readAll(HttpURLConnection conn) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder(4096);
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }

    // Dynamic data loading

    private static final class ParsedCache {
        volatile Object parsed;
        volatile String lastJsonRef;
    }

    private static final ConcurrentMap<String, ConcurrentMap<Type, ParsedCache>> PARSED =
            new ConcurrentHashMap<>();

    private static ParsedCache cacheFor(String key, Type type) {
        return PARSED
                .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(type, t -> new ParsedCache());
    }

    public static <T> T getData(String key, Type type, T defaultValue) {
        // kick off a load if needed, but return immediately
        ensureLoadedAsync(key);

        final String json = getJson(key);
        if (json == null) return defaultValue;

        final ParsedCache pc = cacheFor(key, type);

        // parse only if JSON changed (by content, NOT reference)
        if (!Objects.equals(json, pc.lastJsonRef)) {
            try {
                T parsed = gson.fromJson(json, type);
                if (parsed != null) {
                    pc.parsed = parsed;
                    pc.lastJsonRef = json;
                }
            } catch (Exception e) {
                Logger.logErrorConsole("JSON parse error (" + key + "): " + e.getMessage());
            }
        }

        @SuppressWarnings("unchecked")
        T result = (T) pc.parsed;
        return result != null ? result : defaultValue;
    }

    public static <T> T getData(String key, Class<T> clazz, T defaultValue) {
        return getData(key, (Type) clazz, defaultValue);
    }

}
