package org.ginafro.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;

import java.awt.*;

@RegisterEvents
public class WitherBloodKeysTracers {
    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsKeyTracers) return;
        if (!TablistParser.currentLocation.isDungeon()) return;
        Minecraft.getMinecraft().theWorld.loadedEntityList.forEach(entity -> {
            if (entity == null) return;
            if (entity.getName() == null) return;
            if (entity instanceof EntityArmorStand) {
                if (!entity.getName().equals("§8Wither key") && !entity.getName().equals("§cBlood key")) return;
                Color color = entity.getName().equals("§8Wither key") ? Color.BLACK : Color.RED;
                RenderUtils.draw3DLine(new Vec3(entity.posX, entity.posY + 1.75, entity.posZ),
                        Minecraft.getMinecraft().thePlayer.getPositionEyes(event.partialTicks),
                        color,
                        8,
                        true,
                        event.partialTicks
                );
            }
        });
    }
}
