package com.nef.notenoughfakepixel.features.skyblock.mining;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@RegisterEvents
public class PuzzlerSolver {

    private static BlockPos overlayLoc = null;

    // Thanks to @Moulberry for the original code

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Config.feature.mining.miningPuzzlerSolver) {
            overlayLoc = null;
            return;
        }

        if (event.message.getFormattedText().startsWith("\u00A7r\u00A7e[NPC] \u00A7r\u00A7dPuzzler") &&
                event.message.getUnformattedText().contains(":")) {
            String clean = ColorUtils.cleanColor(event.message.getUnformattedText());
            clean = clean.split(":")[1].trim();
            BlockPos pos = new BlockPos(181, 195, 135);

            for (int i = 0; i < clean.length(); i++) {
                char c = clean.charAt(i);

                if (c == '\u25C0') { //Left
                    pos = pos.add(1, 0, 0);
                } else if (c == '\u25B2') { //Up
                    pos = pos.add(0, 0, 1);
                } else if (c == '\u25BC') { //Down
                    pos = pos.add(0, 0, -1);
                } else if (c == '\u25B6') { //Right
                    pos = pos.add(-1, 0, 0);
                } else {
                    return;
                }
            }

            overlayLoc = pos;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        overlayLoc = null;
    }

    @SubscribeEvent()
    public void onWorldUnload(WorldEvent.Unload event) {
        overlayLoc = null;
    }


    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        if (SkyblockData.getCurrentLocation() != Location.DWARVEN) return;
        if (overlayLoc == null) return;

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks;

        AxisAlignedBB bb = new AxisAlignedBB(
                overlayLoc.getX() - viewerX,
                overlayLoc.getY() - viewerY,
                overlayLoc.getZ() - viewerZ,
                overlayLoc.getX() + 1 - viewerX,
                overlayLoc.getY() + 1 - viewerY,
                overlayLoc.getZ() + 1 - viewerZ
        ).expand(0.01f, 0.01f, 0.01f);

        GlStateManager.disableCull();
        RenderUtils.drawFilledBoundingBox(bb, 1f, new Color(0, 255, 0, 100));
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
    }

}
