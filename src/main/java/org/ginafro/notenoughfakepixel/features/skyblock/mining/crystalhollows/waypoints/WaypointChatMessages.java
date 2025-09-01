package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class WaypointChatMessages {
    private WaypointChatMessages() {}

    private static final Locale ES = new Locale("es", "ES");
    private static final DecimalFormat DF;
    static {
        DecimalFormatSymbols sym = new DecimalFormatSymbols(ES);
        DF = new DecimalFormat("0.0", sym);
    }

    public static String formatCoordsEs(ChWaypoint w) {
        return "x" + DF.format(w.x) + ", y" + DF.format(w.y) + ", z" + DF.format(w.z);
    }

    public static void sendWaypointLine(ICommandSender sender, ChWaypoint w) {
        String coords = formatCoordsEs(w);

        ChatComponentText root = new ChatComponentText("");

        ChatComponentText name = new ChatComponentText("- " + w.name + " ");
        name.getChatStyle().setColor(EnumChatFormatting.YELLOW).setBold(true);
        root.appendSibling(name);

        ChatComponentText pos = new ChatComponentText("(" + coords + ") ");
        pos.getChatStyle().setColor(EnumChatFormatting.GRAY);
        root.appendSibling(pos);

        ChatComponentText del = new ChatComponentText("[DELETE]");
        del.getChatStyle()
                .setColor(EnumChatFormatting.RED)
                .setBold(true)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chw del " + w.id))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Delete waypoint")));
        root.appendSibling(new ChatComponentText(" "));
        root.appendSibling(del);

        ChatComponentText copy = new ChatComponentText("[COPY]");
        copy.getChatStyle()
                .setColor(EnumChatFormatting.AQUA)
                .setBold(true)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chw copy " + w.id))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy coordinates to clipboard")));
        root.appendSibling(new ChatComponentText(" "));
        root.appendSibling(copy);

        ChatComponentText share = new ChatComponentText("[SHARE]");
        share.getChatStyle()
                .setColor(EnumChatFormatting.YELLOW)
                .setBold(true)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chw share " + w.id))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Share coordinates in chat")));
        root.appendSibling(new ChatComponentText(" "));
        root.appendSibling(share);

        sender.addChatMessage(root);
    }

    public static String formatCoordsEs(double x, double y, double z) {
        return "x" + DF.format(x) + ", y" + DF.format(y) + ", z" + DF.format(z);
    }

}
