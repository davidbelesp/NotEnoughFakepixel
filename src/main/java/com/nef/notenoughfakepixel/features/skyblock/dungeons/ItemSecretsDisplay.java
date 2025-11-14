package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.variables.MobDisplayTypes;

import java.awt.*;

@RegisterEvents
public class ItemSecretsDisplay {

    String idTrainingWeights = "32d530e8-2686-3a8c-bc41-ce3650e12bdf";
    String idTreasureTalisman = "9c287464-1a06-3eed-8974-6dcc511d63b2";
    String idWitherEssence = "96f80a4b-daac-35e7-9c04-1a1544aadeb3";

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsItemSecretsDisplay) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (!TablistParser.currentLocation.isDungeon()) return;

        WorldClient world = Minecraft.getMinecraft().theWorld;

        Color color = ColorUtils.getColor(Config.feature.dungeons.dungeonsItemSecretsColor);


        MobDisplayTypes hitboxType;
        if (Config.feature.dungeons.dungeonsItemSecretsBig) hitboxType = MobDisplayTypes.ITEMBIG;
        else {
            hitboxType = MobDisplayTypes.ITEM;
        }

        world.loadedEntityList.forEach(entity -> {
            if (entity == null) return;
            if (entity.getName() == null) return;
            if (entity.getName().equals("item.item.skull.char")) {
                NBTTagCompound nbt = entity.serializeNBT();
                if (nbt.hasKey("Item") && nbt.getCompoundTag("Item").hasKey("tag") && nbt.getCompoundTag("Item").getCompoundTag("tag").hasKey("SkullOwner") && nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("SkullOwner").hasKey("Id")) {
                    String id = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("SkullOwner").getString("Id");
                    if (id.equals(idTrainingWeights) || id.equals(idTreasureTalisman)) {
                        GlStateManager.disableDepth();
                        RenderUtils.renderEntityHitbox(
                                entity,
                                event.partialTicks,
                                color,
                                hitboxType);
                        GlStateManager.enableDepth();
                    }
                }
            } else if (entity.getName().equals("item.item.shears") || entity.getName().equals("item.item.monsterPlacer")) {
                GlStateManager.disableDepth();
                RenderUtils.renderEntityHitbox(
                        entity,
                        event.partialTicks,
                        color,
                        hitboxType
                );
                GlStateManager.enableDepth();
            } else if (entity instanceof EntityArmorStand) {
                ItemStack it = ((EntityArmorStand) entity).getEquipmentInSlot(4); // Head slot
                if (it != null && it.getItem() == Items.skull) {
                    NBTTagCompound nbt = it.getTagCompound();
                    if (nbt != null && nbt.hasKey("SkullOwner") && nbt.getCompoundTag("SkullOwner").hasKey("Id")) {
                        String id = nbt.getCompoundTag("SkullOwner").getString("Id");
                        if (id.equals(idWitherEssence)) {
                            GlStateManager.disableDepth();
                            RenderUtils.renderEntityHitbox(
                                    entity,
                                    event.partialTicks,
                                    color,
                                    MobDisplayTypes.WITHERESSENCE
                            );
                            GlStateManager.enableDepth();
                        }
                    }
                }
            }
        });
    }
}
