package com.nef.notenoughfakepixel.features.skyblock.dungeons.mobs;

import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.variables.MobDisplayTypes;
import com.nef.notenoughfakepixel.variables.Skins;

import java.awt.*;

@RegisterEvents
public class FelMobDisplay {

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsFelMob) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (!TablistParser.currentLocation.isDungeon()) return;

        WorldClient world = Minecraft.getMinecraft().theWorld;

        world.loadedEntityList.forEach(entity -> {
            if (entity == null) return;
            if (entity.getName() == null) return;
            if (entity instanceof EntityArmorStand) {
                EntityArmorStand armorStand = (EntityArmorStand) entity;
                // checking if armor stand have a helmet
                if (armorStand.getEquipmentInSlot(4) == null) return;
                if (armorStand.getEquipmentInSlot(4).getTagCompound() == null) return;

                ItemStack head = armorStand.getEquipmentInSlot(4);
                if (ItemUtils.hasSkinValue(Skins.ENDERMAN_HEAD, head)) {
                    Color color = new Color(
                            ColorUtils.getColor(Config.feature.dungeons.dungeonsFelColor).getRGB()
                    );

                    RenderUtils.renderEntityHitbox(
                            entity,
                            event.partialTicks,
                            color,
                            MobDisplayTypes.FEL
                    );
                }

            }
        });
    }
}
