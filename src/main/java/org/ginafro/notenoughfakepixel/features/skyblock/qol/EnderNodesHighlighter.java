package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.SpawnedParticleEvent;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.highlighters.BlockHighlighter;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.NumberUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;

import java.awt.*;

@RegisterEvents
public class EnderNodesHighlighter extends BlockHighlighter {

    @SubscribeEvent
    public void onParticleSpawn(SpawnedParticleEvent event) {
        if (!isEnabled()) return;
        if (event.getParticleTypes() == EnumParticleTypes.PORTAL) {
            double x = event.getXCoord();
            double y = event.getYCoord();
            double z = event.getZCoord();

            double dist = 0.2;

            boolean xZero = NumberUtils.basicallyEqual((x) % 1, 0, dist);
            boolean yZero = NumberUtils.basicallyEqual((y) % 1, 0, dist);
            boolean zZero = NumberUtils.basicallyEqual((z) % 1, 0, dist);

            double truncX = NumberUtils.truncateTwoDecimalPlaces(Math.abs(x % 1));
            double truncY = NumberUtils.truncateTwoDecimalPlaces(Math.abs(y % 1));
            double truncZ = NumberUtils.truncateTwoDecimalPlaces(Math.abs(z % 1));

            if (truncY < 0.25 && xZero && zZero) {
                if (tryRegisterInterest(x, y - 1, z)) return;
            }
            if (truncY > 0.75 && xZero && zZero) {
                if (tryRegisterInterest(x, y + 1, z)) return;
            }
            if (truncX < 0.25 && yZero && zZero) {
                if (tryRegisterInterest(x + 1, y, z)) return;
            }
            if (truncX > 0.75 && yZero && zZero) {
                if (tryRegisterInterest(x - 1, y, z)) return;
            }
            if (truncZ < 0.25 && yZero && xZero) {
                if (tryRegisterInterest(x, y, z + 1)) return;
            }
            if (truncZ < 0.75 && yZero && xZero) {
                tryRegisterInterest(x, y, z - 1);
            }
        }
    }

    @Override
    protected boolean isEnabled() {
        return TablistParser.currentLocation.equals(Location.THE_END)
                && Config.feature.qol.qolEndNodeHighlighter;
    }

    @Override
    protected boolean isValidHighlightSpot(BlockPos key) {
        World w = Minecraft.getMinecraft().theWorld;
        if (w == null) return false;
        Block b = w.getBlockState(key).getBlock();
        return b == Blocks.end_stone || b == Blocks.obsidian;
    }

    @Override
    protected Color getColor(BlockPos blockPos) {
        return ColorUtils.getColor(Config.feature.qol.endNodeColor);
    }

}
