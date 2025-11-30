package com.nef.notenoughfakepixel.alerts;

import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.text.WordUtils;

public enum Variables {

    PLAYER("player","The Display Name of the player playing the mod") {
        @Override
        public String getValue() {
            return Minecraft.getMinecraft().thePlayer != null
                    ? Minecraft.getMinecraft().thePlayer.getDisplayNameString()
                    : "Unknown Player";
        }
    },
    LOCATION("location","The current Island Location of the player") {
        @Override
        public String getValue() {
            return SkyblockData.getCurrentLocation() != null ? WordUtils.capitalizeFully(SkyblockData.getCurrentLocation().name()).replace("_"," ") : "Unknown Location";
        }
    },
    PING("ping", "Your current ping to the server") {
        @Override
        public String getValue() {
            return Minecraft.getMinecraft().getNetHandler() != null
                    ? Minecraft.getMinecraft().getNetHandler().getPlayerInfo(Minecraft.getMinecraft().thePlayer.getUniqueID()).getResponseTime() + "ms"
                    : "Unknown Ping";
        }
    },
    TIME("time", "System time (HH:mm a)") {
        @Override
        public String getValue() {
            return new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date());
        }
    },
    AREA("area"," The current area on island of the player") {
        @Override public String getValue() {
            return SkyblockData.getCurrentArea().getArea() != null ? SkyblockData.getCurrentArea().getArea() : "Unknown Area";
        }
    };

    public final String variableText,variableUsage;

    Variables(String variableText,String usage) {
        this.variableText = "${" + variableText + "}";
        this.variableUsage = usage;
    }
    public abstract String getValue();

    public static String replaceVariables(String text) {
        String result = text;
        for (Variables var : values()) {
            result = result.replace(var.variableText, var.getValue());
        }
        return result;
    }
}