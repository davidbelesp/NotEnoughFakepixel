package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.variables.Skins;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RegisterEvents
public class HideFlamingFists {

    private static final UUID TARGET_UUID = UUID.fromString("eaf71309-6ae5-3bf3-bf7f-51a579bbc6ee");
    private static final Set<EntityArmorStand> trackedStands = new HashSet<>();
    private static int checkTimer = 0;

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (!Config.feature.qol.qolHideFlamingFists || !SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (!event.world.isRemote) return; // client only

        if (event.entity instanceof EntityArmorStand) {
            trackedStands.add((EntityArmorStand) event.entity);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Config.feature.qol.qolHideFlamingFists || event.phase != TickEvent.Phase.END) return;
        if (++checkTimer >= 5) {
            checkTimer = 0;
            checkArmorStands();
        }
    }

    private void checkArmorStands() {
        trackedStands.removeIf(s -> s == null || s.isDead || !s.isEntityAlive());

        for (EntityArmorStand stand : trackedStands) {
            ItemStack head = stand.getEquipmentInSlot(4);  // helmet (1-based API)

            if (head == null) continue;
            if (isTargetSkull(head)) {
                stand.setInvisible(true);
            }
        }
    }

    // Unified skull detection method
    public static boolean isTargetSkull(ItemStack stack) {
        if (stack == null || stack.getItem() != Items.skull) return false;
        String texture = ItemUtils.getSkullTexture(stack);
        if (texture == null || texture.isEmpty()) return false;
        return Skins.equalsSkin(texture, Skins.FLAMING_FIST);
    }

    @SubscribeEvent
    public void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        if (event.world.isRemote) trackedStands.clear();
    }

    @SubscribeEvent
    public void onRenderArmorStand(RenderLivingEvent.Pre<?> event) {
        if (!Config.feature.qol.qolHideFlamingFists || !(event.entity instanceof EntityArmorStand)) return;

        EntityArmorStand stand = (EntityArmorStand) event.entity;
        ItemStack head = stand.getEquipmentInSlot(4); // helmet
        if (isTargetSkull(head)) {
            event.setCanceled(true);
        }
    }
}