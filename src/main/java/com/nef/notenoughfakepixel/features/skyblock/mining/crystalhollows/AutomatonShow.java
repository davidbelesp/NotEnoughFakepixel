package com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Area;
import com.nef.notenoughfakepixel.variables.MobDisplayTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@RegisterEvents
public class AutomatonShow {

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (!SkyblockData.getCurrentArea().equals(Area.CH_LOST_PRECURSOR) && !SkyblockData.getCurrentArea().equals(Area.CH_PRECURSOR)) return;
        if (!Config.feature.mining.crystalShowAutomaton) return;
        onRender(event);
    }

    private void onRender(RenderWorldLastEvent event) {
        Color color = ColorUtils.getColor(Config.feature.mining.automatonColor);
        final float partialTicks = event.partialTicks;

        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;

        world.loadedEntityList.forEach(entity -> {
            if (entity == null || entity.getName() == null) return;
            if (!(entity instanceof EntityArmorStand)) return;

            // Render only if entity is within 100 blocks
            if (entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > 100) return;
            if (entity.getDisplayName().getUnformattedText().contains("Automaton")) {
                RenderUtils.renderEntityHitbox(entity, partialTicks, color, MobDisplayTypes.AUTOMATON);
            }
        });


    }

}
