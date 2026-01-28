package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;
import java.util.List;

@RegisterEvents
public class SmoothAote {

    public SmoothAote() {
        INSTANCE = this;
    }

    private static SmoothAote INSTANCE = null;
    public static SmoothAote getInstance() {
        return INSTANCE;
    }

    private final List<String> tpItemNames = Arrays.asList(
            "ASPECT_OF_THE_END",
            "ASPECT_OF_THE_VOID"
    );

    public int teleportTime = 125;
    public int teleportMillis = 0;
    public Vector3f teleportCurrentPos = null;
    private int tick;
    public long lastMillis = 0;
    public long tpLastMillis = 0;

    public Vector3f getCurrentPosition() {
        if (teleportMillis <= 0) return null;
        return teleportCurrentPos;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR &&
            event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        ItemStack held = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (held == null) return;
        String internal = ItemUtils.getInternalName(held);
        if (internal == null) return;

        boolean hasShadowWarp = false;

        NBTTagCompound tag = held.getTagCompound();
        if (tag != null && tag.hasKey("ExtraAttributes", 10)) {
            NBTTagCompound ea = tag.getCompoundTag("ExtraAttributes");
            if (ea.getTag("shadow_warp") != null) {
                hasShadowWarp = true;
            }
        }

        if (teleportTime <= 0 || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) return;

        boolean aote = Config.feature.qol.enableSmoothAote &&
                tpItemNames.contains(internal);
        boolean hyp = Config.feature.qol.enableSmoothAote && hasShadowWarp;

        if (aote || hyp) {
            tpLastMillis = System.currentTimeMillis();
            if (teleportCurrentPos == null) {
                teleportCurrentPos = new Vector3f();
                teleportCurrentPos.x = (float) Minecraft.getMinecraft().thePlayer.posX;
                teleportCurrentPos.y = (float) Minecraft.getMinecraft().thePlayer.posY;
                teleportCurrentPos.z = (float) Minecraft.getMinecraft().thePlayer.posZ;
            }
        }

    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer == null) return;

        long currentTime = System.currentTimeMillis();
        int delta = (int) (currentTime - lastMillis);
        lastMillis = currentTime;
        if (delta <= 0) return;

        if (teleportMillis > teleportTime * 2) {
            teleportMillis = teleportTime * 2;
        }
        if (teleportMillis < 0) teleportMillis = 0;

        if (currentTime - tpLastMillis > 1000 && teleportMillis <= 0) {
            teleportCurrentPos = null;
        }

        if (teleportCurrentPos == null) {
            tpLastMillis = 0;
            teleportMillis = 0;
            return;
        }

        if (teleportMillis > 0) {
            int deltaMin = Math.min(delta, teleportMillis);

            float factor = deltaMin / (float) teleportMillis;
            float dX = teleportCurrentPos.x - (float) Minecraft.getMinecraft().thePlayer.posX;
            float dY = teleportCurrentPos.y - (float) Minecraft.getMinecraft().thePlayer.posY;
            float dZ = teleportCurrentPos.z - (float) Minecraft.getMinecraft().thePlayer.posZ;

            teleportCurrentPos.x -= dX * factor;
            teleportCurrentPos.y -= dY * factor;
            teleportCurrentPos.z -= dZ * factor;

            if (Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(teleportCurrentPos.x,
                    teleportCurrentPos.y, teleportCurrentPos.z
            )).getBlock().getMaterial() != Material.air) {
                teleportCurrentPos.y = (float) Math.ceil(teleportCurrentPos.y);
            }

            teleportMillis -= deltaMin;
        } else {
            teleportCurrentPos.x = (float) Minecraft.getMinecraft().thePlayer.posX;
            teleportCurrentPos.y = (float) Minecraft.getMinecraft().thePlayer.posY;
            teleportCurrentPos.z = (float) Minecraft.getMinecraft().thePlayer.posZ;
        }
    }

    @SubscribeEvent
    public void onGameTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;

        tick++;
        if (tick > Integer.MAX_VALUE / 2) tick = 0;
    }


}
