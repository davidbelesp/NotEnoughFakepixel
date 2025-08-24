package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.mobs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.passive.EntityBat;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.MobDisplayTypes;

import java.awt.*;

@RegisterEvents
public class BatMobDisplay {

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsBatMobs) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (!TablistParser.currentLocation.isDungeon()) return;

        WorldClient world = Minecraft.getMinecraft().theWorld;

        world.loadedEntityList.forEach(entity -> {
            if (entity == null) return;
            if (entity.getName() == null) return;
            if (entity instanceof EntityBat) {
                Color color = new Color(
                        ColorUtils.getColor(Config.feature.dungeons.dungeonsBatColor).getRGB()
                );

                RenderUtils.renderEntityHitbox(
                        entity,
                        event.partialTicks,
                        color,
                        MobDisplayTypes.BAT
                );
            }
        });
    }


}
