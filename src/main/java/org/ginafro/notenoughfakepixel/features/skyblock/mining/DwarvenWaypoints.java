package org.ginafro.notenoughfakepixel.features.skyblock.mining;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;

@RegisterEvents
public class DwarvenWaypoints {

    private static final DwarvenWaypoint[] WAYPOINTS = {
            new DwarvenWaypoint("The Lift", -62, 200, -121),
            new DwarvenWaypoint("Dwarven Vilage", 7, 200, -121),
            new DwarvenWaypoint("Lava Springs", 57, 207, -9),
            new DwarvenWaypoint("The Forge", 0, 148, -53),
            new DwarvenWaypoint("Rampart's Quarry", -77, 153, -10),
            new DwarvenWaypoint("Far Reserve", -154, 149, -17),
            new DwarvenWaypoint("Goblin Burrows", -137, 146, 128),
            new DwarvenWaypoint("The Great Ice Wall", 0, 128, 160),
            new DwarvenWaypoint("Royal Palace", 129, 195, 176),
            new DwarvenWaypoint("Royal Mines", 150, 151, 33),
            new DwarvenWaypoint("Cliffside Veins", 38, 128, 32),
            new DwarvenWaypoint("Divan's Gateway", 0, 128, 96),
            new DwarvenWaypoint("Upper Mines", -117, 181, -63),
            new DwarvenWaypoint("The Mist", -12, 76, 109),
    };

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (TablistParser.currentLocation != Location.DWARVEN) return;
        if (!Config.feature.mining.miningDwarvenWaypoints) return;
        boolean renderBeacon = Config.feature.mining.miningDwarvenBeacons;
        for (DwarvenWaypoint waypoint : WAYPOINTS) {
            if (renderBeacon){
                RenderUtils.renderBeaconBeam(new BlockPos(waypoint.x, waypoint.y, waypoint.z), ColorUtils.getColor(Config.feature.mining.miningDwarvenBeaconsColor).getRGB(), 1, event.partialTicks);
            }
            RenderUtils.renderWaypointText(waypoint.getName() ,new BlockPos(waypoint.x, waypoint.y + 3, waypoint.z), event.partialTicks);
        }
    }

    @AllArgsConstructor
    @Data
    private static class DwarvenWaypoint {
        private final String name;
        private final double x;
        private final double y;
        private final double z;
    }

}
