package com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.util.StringUtils;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.NumberUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.variables.Location;

import java.awt.*;
import java.util.regex.Matcher;

@RegisterEvents
public class ChWaypointEvents {

    private static final java.util.regex.Pattern CHW_PAYLOAD = java.util.regex.Pattern.compile(
            "\\bCHW\\s*:\\s*([^:]+)\\s*:\\s*" +
                    "([-+]?\\d+(?:[\\.,]\\d+)?)\\s*:\\s*" +
                    "([-+]?\\d+(?:[\\.,]\\d+)?)\\s*:\\s*" +
                    "([-+]?\\d+(?:[\\.,]\\d+)?)",
            java.util.regex.Pattern.CASE_INSENSITIVE);

    private static final java.util.regex.Pattern CHW_STRICT = java.util.regex.Pattern.compile(
            "^(?:\\[[^\\]]+\\]\\s*)?[A-Za-z0-9_]{1,16}:\\s*CHW\\s*:\\s*([^:]+)\\s*:\\s*" +
                    "([-+]?\\d+(?:[\\.,]\\d+)?)\\s*:\\s*" +
                    "([-+]?\\d+(?:[\\.,]\\d+)?)\\s*:\\s*" +
                    "([-+]?\\d+(?:[\\.,]\\d+)?)$",
            java.util.regex.Pattern.CASE_INSENSITIVE);

    // Save waypoints on world unload
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        CrystalWaypoints.getInstance().saveIfDirty();
    }

    // Save waypoints on disconnect
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        CrystalWaypoints.getInstance().saveIfDirty();
    }

    // Render waypoints
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS) || !Config.feature.mining.crystalWaypoints) return;

        if (Config.feature.mining.crystalWaypointsBeacons) {
            Color color = ColorUtils.getColor(Config.feature.mining.crystalWaypointColor);
            for (ChWaypoint waypoint : CrystalWaypoints.getInstance().getAll()) {
                if (Boolean.FALSE.equals(waypoint.toggled)) continue;
                RenderUtils.renderBeaconBeam(new BlockPos(waypoint.x, waypoint.y, waypoint.z), color.getRGB(), 1, event.partialTicks);
            }
        }

        if (Config.feature.mining.crystalWaypointsNames) {
            for (ChWaypoint waypoint : CrystalWaypoints.getInstance().getAll()) {
                if (Boolean.FALSE.equals(waypoint.toggled)) continue;
                RenderUtils.renderWaypointText(waypoint.getName() ,new BlockPos(waypoint.x, waypoint.y + 3, waypoint.z), event.partialTicks);
            }
        }

    }

    // Chat Event to check for waypoints
    @SubscribeEvent
    public void onChatReceived(net.minecraftforge.client.event.ClientChatReceivedEvent event) {

        try {
            if (event.message.getUnformattedText().contains("[ADD]")) return;

            String raw = StringUtils.clean(event.message.getUnformattedText());

            Matcher m = CHW_PAYLOAD.matcher(raw);
            if (!m.find()) return;

            String name = m.group(1).trim();
            String sx = m.group(2), sy = m.group(3), sz = m.group(4);

            double x = NumberUtils.parseDoubleFlexible(sx);
            double y = NumberUtils.parseDoubleFlexible(sy);
            double z = NumberUtils.parseDoubleFlexible(sz);

            String cmdAdd = String.format(java.util.Locale.ROOT, "/chw add %s %.3f %.3f %.3f", name, x, y, z);

            net.minecraft.util.ChatComponentText space = new net.minecraft.util.ChatComponentText(" ");
            net.minecraft.util.ChatComponentText add = new net.minecraft.util.ChatComponentText("[ADD]");
            add.getChatStyle()
                    .setColor(EnumChatFormatting.AQUA)
                    .setBold(true)
                    .setChatClickEvent(new net.minecraft.event.ClickEvent(net.minecraft.event.ClickEvent.Action.RUN_COMMAND, cmdAdd))
                    .setChatHoverEvent(new net.minecraft.event.HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT,
                            new net.minecraft.util.ChatComponentText("Add Waypoint")));
            event.message.appendSibling(space);
            event.message.appendSibling(add);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
