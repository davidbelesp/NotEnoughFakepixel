package com.nef.notenoughfakepixel.features.skyblock.dungeons.devices;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.PacketWriteEvent;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import net.minecraft.block.BlockSeaLantern;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterEvents
public class ThirdDeviceSolver {

    private final Minecraft mc = Minecraft.getMinecraft();

    private static final Map<BlockPos, Integer> itemFramesRotations = new HashMap<>();
    private static final Map<BlockPos, Integer> clientRemainingClicks = new HashMap<>();
    private static final Set<String> processedPackets = new HashSet<>();
    private long lastResyncTime = 0;
    private int tickCounter = 0;

    static {
        itemFramesRotations.put(new BlockPos(6, 122, 85), 3);
        itemFramesRotations.put(new BlockPos(6, 121, 85), 1);
        itemFramesRotations.put(new BlockPos(6, 121, 84), 1);
        itemFramesRotations.put(new BlockPos(6, 121, 83), 7);
        itemFramesRotations.put(new BlockPos(6, 122, 83), 7);
        itemFramesRotations.put(new BlockPos(6, 123, 83), 7);
        itemFramesRotations.put(new BlockPos(6, 124, 83), 7);
        itemFramesRotations.put(new BlockPos(6, 125, 83), 5);
        itemFramesRotations.put(new BlockPos(6, 125, 84), 5);
        itemFramesRotations.put(new BlockPos(6, 125, 85), 5);
        itemFramesRotations.put(new BlockPos(6, 125, 86), 5);
        itemFramesRotations.put(new BlockPos(6, 125, 87), 3);
        itemFramesRotations.put(new BlockPos(6, 124, 87), 3);
        itemFramesRotations.put(new BlockPos(6, 123, 87), 3);
        itemFramesRotations.put(new BlockPos(6, 122, 87), 3);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacket(PacketWriteEvent event) {
        if (!Config.feature.dungeons.dungeonsThirdDeviceSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        if (event.packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) event.packet;
            Entity entityHit = packet.getEntityFromWorld(mc.theWorld);

            if (entityHit instanceof EntityItemFrame) {
                EntityItemFrame itemFrame = (EntityItemFrame) entityHit;
                ItemStack item = itemFrame.getDisplayedItem();

                if (item != null && item.getItem() == Items.arrow) {
                    BlockPos posItemFrame = new BlockPos(entityHit.getPosition());
                    if (itemFramesRotations.containsKey(posItemFrame)) {
                        String packetId = posItemFrame + ":" + tickCounter;
                        if (processedPackets.contains(packetId)) {
                            event.setCanceled(true);
                            return;
                        }

                        int desiredRotation = itemFramesRotations.get(posItemFrame);
                        int currentRotation = itemFrame.getRotation();
                        int remaining = clientRemainingClicks.getOrDefault(
                                posItemFrame,
                                (desiredRotation - currentRotation + 8) % 8
                        );

                        if (remaining <= 0) {
                            event.setCanceled(true);
                            return;
                        }

                        processedPackets.add(packetId);
                        clientRemainingClicks.put(posItemFrame, remaining - 1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!Config.feature.dungeons.dungeonsThirdDeviceSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        tickCounter++;
        processedPackets.clear();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastResyncTime >= 5000) {
            resyncClientRemainingClicks();
            lastResyncTime = currentTime;
        }
    }

    private void resyncClientRemainingClicks() {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityItemFrame) {
                EntityItemFrame itemFrame = (EntityItemFrame) entity;
                BlockPos posItemFrame = new BlockPos(entity.getPosition());

                if (itemFramesRotations.containsKey(posItemFrame)) {
                    int desiredRotation = itemFramesRotations.get(posItemFrame);
                    int currentRotation = itemFrame.getRotation();
                    int clicksNeeded = (desiredRotation - currentRotation + 8) % 8;
                    clientRemainingClicks.put(posItemFrame, clicksNeeded);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Config.feature.dungeons.dungeonsThirdDeviceSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        mc.theWorld.loadedEntityList.forEach(entity -> {
            if (entity instanceof EntityItemFrame) {
                EntityItemFrame itemFrame = (EntityItemFrame) entity;
                ItemStack item = itemFrame.getDisplayedItem();
                if (item == null || item.getItem() != Items.arrow) return;
                BlockPos pos = getBlockUnderItemFrame(itemFrame);
                if (pos == null) return;

                Color color = mc.theWorld.getBlockState(pos).getBlock() instanceof BlockSeaLantern
                        ? ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor)
                        : ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor);

                RenderUtils.highlightBlock(pos, color, false, event.partialTicks);

                BlockPos posItemFrame = new BlockPos(entity.getPosition());
                if (itemFramesRotations.containsKey(posItemFrame)) {
                    int desiredRotation = itemFramesRotations.get(posItemFrame);
                    int currentRotation = itemFrame.getRotation();
                    int clicksNeeded = clientRemainingClicks.getOrDefault(posItemFrame,
                            (desiredRotation - currentRotation + 8) % 8);

                    double[] renderPos = new double[]{posItemFrame.getX() - 0.7, posItemFrame.getY() - 3.3, posItemFrame.getZ()};

                    Color textColor = (clicksNeeded == 0)
                            ? ColorUtils.getColor(Config.feature.dungeons.dungeonsCorrectColor)
                            : ColorUtils.getColor(Config.feature.dungeons.dungeonsAlternativeColor);
                    RenderUtils.drawTag(String.valueOf(clicksNeeded), renderPos, textColor, event.partialTicks);
                }
            }
        });
    }

    private static BlockPos getBlockUnderItemFrame(EntityItemFrame itemFrame) {
        switch (itemFrame.facingDirection) {
            case NORTH:
                return new BlockPos(itemFrame.posX, itemFrame.posY, itemFrame.posZ + 1);
            case EAST:
                return new BlockPos(itemFrame.posX - 1, itemFrame.posY, itemFrame.posZ);
            case SOUTH:
                return new BlockPos(itemFrame.posX, itemFrame.posY, itemFrame.posZ - 1);
            case WEST:
                return new BlockPos(itemFrame.posX + 1, itemFrame.posY, itemFrame.posZ);
            default:
                return null;
        }
    }
}