package com.nef.notenoughfakepixel.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.regex.Pattern;

public class ChatUtils {
    public static Pattern middleBar = Pattern.compile("(§6|§c)[0-9]+/[0-9]+❤(.)+");

    public static void notifyChat(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
