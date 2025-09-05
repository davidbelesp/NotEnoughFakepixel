package org.ginafro.notenoughfakepixel.envcheck;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.handlers.RepoHandler;
import org.ginafro.notenoughfakepixel.utils.ChatUtils;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.ginafro.notenoughfakepixel.utils.NumberUtils;

@RegisterEvents
public class VersionChecker {

    private static boolean notified = false;
    private static volatile UpdateData CACHED_UPDATE_DATA = null;
    private static final UpdateData STUB_LOAD_FAIL = new UpdateData(
            "0.0.0", "Version not found", "(link not found)", "(discord not found)", false, ""
    );
    private static volatile String LAST_JSON_REF = null;

    private static final Gson gson = new Gson();

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!notified && event.message.getUnformattedText().equals("Welcome to Fakepixel SkyBlock!")) {
            notified = true;
            String currentVersion = getModVersion().replace("-beta", "");
            String latestVersion = getCachedUpdateData().getVersion().replace("-beta", "");
            Logger.log(currentVersion);
            Logger.log(latestVersion);
            Logger.log(isNewerVersion(currentVersion, latestVersion));
            if (!isNewerVersion(latestVersion, currentVersion)) return;

            String message = getCachedUpdateData().getUpdate_msg().replace("${currentVersion}", currentVersion);
            ChatUtils.notifyChat(message);

            if (getCachedUpdateData().isHas_warning()) {
                if (getCachedUpdateData().getWarning_msg() != null || !getCachedUpdateData().getWarning_msg().isEmpty()) {
                    ChatUtils.notifyChat(getCachedUpdateData().getWarning_msg());
                }
            }
        }
    }

    public static boolean isNewerVersion(String current, String latest) {
        String[] currentParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");

        int length = Math.max(currentParts.length, latestParts.length);

        for (int i = 0; i < length; i++) {
            int currentNum = i < currentParts.length ? NumberUtils.parseIntSafe(currentParts[i]) : 0;
            int latestNum = i < latestParts.length ? NumberUtils.parseIntSafe(latestParts[i]) : 0;

            if (latestNum > currentNum) return true;
            if (latestNum < currentNum) return false;
        }
        return false;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!notified && CACHED_UPDATE_DATA == null) CACHED_UPDATE_DATA = getCachedUpdateData();
    }

    public static UpdateData getCachedUpdateData() {
        if (!RepoHandler.isLoaded("update")) {
            RepoHandler.ensureLoadedAsync("update");
            return CACHED_UPDATE_DATA != null ? CACHED_UPDATE_DATA : STUB_LOAD_FAIL;
        }
        final String json = RepoHandler.getJson("update");
        if (json == null) {
            return CACHED_UPDATE_DATA != null ? CACHED_UPDATE_DATA : STUB_LOAD_FAIL;
        }
        if (json != LAST_JSON_REF) {
            try {
                UpdateData parsed = gson.fromJson(json, UpdateData.class);
                if (parsed != null){
                    CACHED_UPDATE_DATA = parsed;
                    LAST_JSON_REF = json;
                }
            } catch (Exception e) {
                Logger.logErrorConsole("Error parsing UPDATE JSON: " + e.getMessage());
            }
        }
        return CACHED_UPDATE_DATA != null ? CACHED_UPDATE_DATA : STUB_LOAD_FAIL;
    }

    public static String getModVersion() {
        ModContainer container = Loader.instance().getIndexedModList().get("notenoughfakepixel");
        if (container != null) {
            return container.getVersion();
        }
        return "0.0.0";
    }

    @AllArgsConstructor @Getter public static class UpdateData {
        private String version;
        private String update_msg;
        private String update_link;
        private String discord_link;

        private boolean has_warning;
        private String warning_msg;
    }

}
