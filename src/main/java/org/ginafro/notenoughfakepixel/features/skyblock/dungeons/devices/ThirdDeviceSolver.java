package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.devices;

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
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.events.PacketWriteEvent;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ThirdDeviceSolver {

    private final Minecraft mc = Minecraft.getMinecraft();

    // Map of expected rotations for specific positions
    private static final Map<BlockPos, Integer> itemFramesRotations = new HashMap<>();

    static {
        // Define expected rotations for item frames in goldor phase (Y + 1)
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

    // Cancel click when sea lantern behind
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacket(PacketWriteEvent event) {
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsThirdDeviceSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        if (event.packet instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = (C02PacketUseEntity) event.packet;
            Entity entityHit = packet.getEntityFromWorld(mc.theWorld);

            if (entityHit instanceof EntityItemFrame) {
                EntityItemFrame itemFrame = (EntityItemFrame) entityHit;
                ItemStack item = itemFrame.getDisplayedItem();

                if (item != null && item.getItem() == Items.arrow) {
                    BlockPos posItemFrame = new BlockPos(entityHit.getPosition().getX(),entityHit.getPosition().getY(),entityHit.getPosition().getZ());
                    if (itemFramesRotations.containsKey(posItemFrame)) {
                        int desiredRotation = itemFramesRotations.get(posItemFrame);
                        int currentRotation = itemFrame.getRotation();
                        int clicksNeeded = (desiredRotation - currentRotation + 8) % 8;
                        if (clicksNeeded == 0) {
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!NotEnoughFakepixel.feature.dungeons.dungeonsThirdDeviceSolver) return;
        if (!DungeonManager.checkEssentialsF7()) return;

        mc.theWorld.loadedEntityList.forEach(entity -> {
            if (entity instanceof EntityItemFrame) {
                EntityItemFrame itemFrame = (EntityItemFrame) entity;
                ItemStack item = itemFrame.getDisplayedItem();
                if (item == null || item.getItem() != Items.arrow) return;
                BlockPos pos = getBlockUnderItemFrame(itemFrame);
                if (pos == null) return;

                // Determine block color
                Color color = mc.theWorld.getBlockState(pos).getBlock() instanceof BlockSeaLantern
                        ? ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsCorrectColor)
                        : ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsAlternativeColor);

                // Highlight the block
                RenderUtils.highlightBlock(pos, color, false, event.partialTicks);

                BlockPos posItemFrame = new BlockPos(entity.getPosition().getX(),entity.getPosition().getY(),entity.getPosition().getZ());
                // Handle rotation adjustment
                if (itemFramesRotations.containsKey(posItemFrame)) {
                    int desiredRotation = itemFramesRotations.get(posItemFrame);
                    int currentRotation = itemFrame.getRotation();
                    int clicksNeeded = (desiredRotation - currentRotation + 8) % 8;

                    // Prepare position for rendering text
                    double[] renderPos = new double[]{posItemFrame.getX() - 0.7, posItemFrame.getY() - 3.3, posItemFrame.getZ()};

                    // Display clicks needed
                    if (clicksNeeded == 0) {
                        RenderUtils.drawTag(String.valueOf(clicksNeeded), renderPos, ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsCorrectColor), event.partialTicks);
                    } else {
                        RenderUtils.drawTag(String.valueOf(clicksNeeded), renderPos, ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsAlternativeColor), event.partialTicks);
                    }
                }
            }
        });
    }

    // Get block behind item frame according direction
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
