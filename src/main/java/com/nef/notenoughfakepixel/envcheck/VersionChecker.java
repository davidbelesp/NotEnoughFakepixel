package com.nef.notenoughfakepixel.envcheck;

import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import com.nef.notenoughfakepixel.utils.ChatUtils;
import com.nef.notenoughfakepixel.utils.NumberUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@RegisterEvents
public class VersionChecker {

    private static boolean notified = false;

    private static final UpdateData STUB_UPDATE = new UpdateData(
            "0.0.0", "Version not found", "(link not found)", "(discord not found)", false, ""
    );

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!notified && "Welcome to Fakepixel SkyBlock!".equals(event.message.getUnformattedText())) {
            notified = true;

            UpdateData update = getCachedUpdateData();
            String currentVersion = normalizeVersion(getModVersion());
            String latestVersion  = normalizeVersion(update.getVersion());

            if (update.isHas_warning()
                    && update.getWarning_msg() != null
                    && !update.getWarning_msg().isEmpty()) {
                ChatUtils.notifyChat(update.getWarning_msg());
            }

            if (!isNewerVersion(currentVersion, latestVersion)) return;

            String msg = update.getUpdate_msg()
                    .replace("${currentVersion}", currentVersion)
                    .replace("${latestVersion}", latestVersion);
            ChatUtils.notifyChat(msg);


        }
    }

    private static String normalizeVersion(String v) {
        if (v == null) return "0.0.0";
        return v.replaceFirst("[-.](beta|snapshot).*$", "");
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

    public static UpdateData getCachedUpdateData() {
        return RepoHandler.getData("update", UpdateData.class, STUB_UPDATE);
    }

    public static String getModVersion() {
        ModContainer container = Loader.instance().getIndexedModList().get("notenoughfakepixel");
        return container != null ? container.getVersion() : "0.0.0";
    }

    @AllArgsConstructor @Getter
    public static class UpdateData {
        private String version;
        private String update_msg;
        private String update_link;
        private String discord_link;
        private boolean has_warning;
        private String warning_msg;
    }

}
