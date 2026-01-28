package com.nef.notenoughfakepixel.features.skyblock.overlays.waypoints;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public abstract class IslandWaypoints {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Config.feature.waypoints.generalWaypointToggle) return;
        if (getIslandType() != SkyblockData.getCurrentLocation()) return;
        if (!shouldShow()) return;
        for (Waypoint waypoint : getWaypoints()) {
            Color waypointColor = getWaypointColor();
            if (waypoint.overrideColor != null) waypointColor = waypoint.overrideColor;
            if (renderBeacon()){
                RenderUtils.renderBeaconBeam(new BlockPos(waypoint.x, waypoint.y, waypoint.z), waypointColor.getRGB(), 1, event.partialTicks);
            }
            RenderUtils.renderWaypointText(waypoint.getName() ,new BlockPos(waypoint.x, waypoint.y + 3, waypoint.z), event.partialTicks);
        }
    }

    public abstract Waypoint[] getWaypoints();
    public abstract boolean shouldShow();
    public abstract Location getIslandType();
    public boolean renderBeacon() { return false; }
    public Color getWaypointColor() { return Color.WHITE; }

    @AllArgsConstructor @Data
    public static class Waypoint {
        private final String name;
        private final double x;
        private final double y;
        private final double z;
        private final Color overrideColor;

        public Waypoint(String name, double x, double y, double z) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.overrideColor = null;
        }

    }

}
