package org.ginafro.notenoughfakepixel.events.handlers;

import com.google.gson.Gson;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RegisterEvents
public class RepoHandler {


    // ==== Configuration ====
    private static final String GITHUB_RAW_URL = "https://raw.githubusercontent.com/davidbelesp/NotEnoughFakepixel-REPO/refs/heads/main/data/fairysouls.json";
    private static final long TTL_MS = TimeUnit.DAYS.toMillis(1);
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    // ==== States ====
    private static final AtomicReference<String> CACHED_JSON = new AtomicReference<>(null);
    private static volatile long lastFetch = 0L;
    private static volatile boolean loading = false;
    private static volatile String lastEtag = null;
    private static volatile String lastModified = null;

    // Single thread executor for IO tasks
    private static final ScheduledExecutorService EXEC =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "RepoHandler-IO");
                t.setDaemon(true);
                return t;
            });

    private RepoHandler() {}

    private static final Gson gson = new Gson();

    // Loading on mod init
    public static void warmupAsync() {
        loadIfStaleAsync(false);
    }

    // Loading on server connect
    public static void refreshOnJoinAsync() {
        loadIfStaleAsync(true);
    }

    // Loading on GUI (if needed)
    public static void ensureLoadedAsync() {
        loadIfStaleAsync(true);
    }

    public static boolean isLoaded() {
        return CACHED_JSON.get() != null;
    }

    public static String getCachedJson() {
        if (isLoaded()) {
            return CACHED_JSON.get();
        }
        return null;
    }


    // Core loading
    private static void loadIfStaleAsync(boolean forceExpired) {
        long now = System.currentTimeMillis();
        boolean expired = (now - lastFetch) >= TTL_MS;

        if (!forceExpired && !expired && CACHED_JSON.get() != null) {
            // Fresh cache, no need to reload
            return;
        }

        if (loading) { return; };

        loading = true;
        EXEC.execute(() -> {
            try {
                String json = downloadFromGitHubWithCache();
                CACHED_JSON.set(json);
                lastFetch = System.currentTimeMillis();
            } catch (Exception e) {
                Logger.logConsole("RepoHandler fetch fail: " + e.getMessage());
                lastFetch = System.currentTimeMillis() - (TTL_MS - TimeUnit.MINUTES.toMillis(1));

            } finally {
                loading = false;
            }
        });

    }

    private static String downloadFromGitHubWithCache() throws Exception {
        HttpURLConnection conn = getHttpURLConnection();

        int code = conn.getResponseCode();

        if (code >= 200 && code < 300) {
            String etag = conn.getHeaderField("ETag");
            String lm = conn.getHeaderField("Last-Modified");
            String body = readStream(conn);

            if (etag != null) { lastEtag = etag; }
            if (lm != null) { lastModified = lm; }

            return body;
        }

        throw new RuntimeException("HTTP: " + code);


    }

    private static @NotNull HttpURLConnection getHttpURLConnection() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new java.net.URL(GITHUB_RAW_URL).openConnection();
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "NotEnoughFakepixel (Minecraft 1.8.9)");

        if (lastEtag != null) { conn.setRequestProperty("If-None-Match", lastEtag); }
        if (lastModified != null) { conn.setRequestProperty("If-Modified-Since", lastModified); }
        return conn;
    }

    private static String readStream(HttpURLConnection conn) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder(4096);
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }

}
