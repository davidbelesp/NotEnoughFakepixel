package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Location;
import com.nef.notenoughfakepixel.variables.Skins;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class GiftWaypoints {

    public static List<BlockPos> giftWaypoints = new ArrayList<>();
    public static List<BlockPos> foundGifts = new ArrayList<>();
    private long lastGiftSearchTime = 0;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Config.feature.waypoints.giftWaypoints) return;
        if (SkyblockData.getCurrentLocation() != Location.JERRY) return;
        List<BlockPos> toRender = new ArrayList<>(giftWaypoints);

        renderStJerry(event.partialTicks);

        for (BlockPos pos : toRender) {
            if (foundGifts.stream().anyMatch(blockPos -> blockPos.distanceSq(pos) < 0.01)) {
                continue;
            }
            renderGiftWaypoint(pos.add(0, 1, 0), event.partialTicks, ColorUtils.getColor(Config.feature.waypoints.giftWaypointsColor));
        }

    }

    public void renderStJerry(float partialTicks) {
        if (!Config.feature.waypoints.stJerryLocation) return;
        renderGiftBeacon(new BlockPos(-22.5, 76, 92.5), partialTicks, new Color(224, 78, 78));
        RenderUtils.renderWaypointText("St. Jerry", new BlockPos(-22.5, 77.5, 92.5), partialTicks, false);
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (!Config.feature.waypoints.giftWaypoints) return;
        if (SkyblockData.getCurrentLocation() != Location.JERRY) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (lastGiftSearchTime == 0) lastGiftSearchTime = System.currentTimeMillis();
        if (System.currentTimeMillis() - lastGiftSearchTime > 1000L) return;

        lastGiftSearchTime = System.currentTimeMillis();

        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList){
            if (entity instanceof EntityArmorStand) {
                EntityArmorStand armorStand = (EntityArmorStand) entity;
                ItemStack head = armorStand.getEquipmentInSlot(4);
                if (head == null) continue;
                if (isTargetHead(head)) {
                    BlockPos giftPos = armorStand.getPosition();
                    if (!giftWaypoints.contains(giftPos)) {
                        giftWaypoints.add(giftPos);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Config.feature.waypoints.giftWaypoints) return;
        if (SkyblockData.getCurrentLocation() != Location.JERRY) return;

        if (event.message.getUnformattedText().equals("GIFT! You found all of the Gifts! Talk to St. Jerry to receive a reward!")) {
            giftWaypoints.clear();
            return;
        }

        if (
            event.message.getUnformattedText().equals("You have already found this Gift this year!") ||
            event.message.getUnformattedText().startsWith("GIFT! You found a White Gift!")
        ) {
            Entity entity = Minecraft.getMinecraft().objectMouseOver.entityHit;
            if (entity instanceof EntityArmorStand) {
                EntityArmorStand armorStand = (EntityArmorStand) entity;
                BlockPos giftPos = armorStand.getPosition();
                if (!foundGifts.contains(giftPos)) {
                    foundGifts.add(giftPos);
                }
                giftWaypoints.remove(giftPos);
            }
        }
    }

    private boolean isTargetHead(ItemStack head) {
        if (head == null) return false;
        String texture = ItemUtils.getSkullTexture(head);
        if (texture == null || texture.isEmpty()) return false;
        return Skins.equalsSkin(texture, Skins.GIFT);
    }

    private void renderGiftWaypoint(BlockPos pos, float partialTicks, Color color) {
        renderGiftBeacon(pos, partialTicks, color);
        renderWaypointBox(pos, partialTicks, color);
    }

    private void renderGiftBeacon(BlockPos pos, float partialTicks, Color color) {
        RenderUtils.renderBeaconBeam(pos, color.getRGB(), 1.0f, partialTicks);
    }

    private void renderWaypointBox(BlockPos pos, float partialTicks, Color color) {
        RenderUtils.highlightBlock(pos, color, true, partialTicks);
    }

    @SubscribeEvent
    public void onWorldUnload(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
        giftWaypoints.clear();
        foundGifts.clear();
    }


}
