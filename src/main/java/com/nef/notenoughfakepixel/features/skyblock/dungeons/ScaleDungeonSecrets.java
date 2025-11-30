package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

public class ScaleDungeonSecrets {
    public static void scaleItemDrop(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!Config.feature.dungeons.dungeonsItemSecretsBig) return;
        if (!SkyblockData.getCurrentLocation().isDungeon()) return;

        ItemStack stack = entity.getEntityItem();
        if (stack == null) return;

        Item item = stack.getItem();
        boolean shouldScale = false;

        if (item instanceof ItemShears) {
            shouldScale = true;
        } else if (item == Items.skull && stack.getItemDamage() == 3) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("SkullOwner", 10)) {
                NBTTagCompound skullOwner = tag.getCompoundTag("SkullOwner");
                String uuidStr = skullOwner.getString("Id");
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    UUID trainingWeightsUUID = UUID.fromString("32d530e8-2686-3a8c-bc41-ce3650e12bdf");
                    UUID treasureTalismanUUID = UUID.fromString("9c287464-1a06-3eed-8974-6dcc511d63b2");
                    if (uuid.equals(trainingWeightsUUID) || uuid.equals(treasureTalismanUUID)) {
                        shouldScale = true;
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        } else if (item == Items.spawn_egg) {
            String displayName = stack.getDisplayName();
            if (displayName.contains("Decoy") || displayName.contains("Inflatable Jerry")) {
                shouldScale = true;
            }
        }

        if (shouldScale) {
            float scale = Config.feature.dungeons.dungeonsScaleItemDrop;
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0, (Config.feature.dungeons.dungeonsScaleItemDrop - 1f) * (entity.height / 2f - 1.125f / 16f), 0);
        }
    }
}