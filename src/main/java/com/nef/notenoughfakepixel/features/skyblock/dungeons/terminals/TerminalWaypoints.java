package com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@RegisterEvents
public class TerminalWaypoints {

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (Config.feature.dungeons.dungeonsTerminalWaypoints && DungeonManager.checkEssentialsF7()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer == null) return;

            for (Entity entity : mc.theWorld.loadedEntityList) {
                String name = entity.getDisplayName().getUnformattedText();
                if (name.contains("Inactive Device")
                        || name.contains("Not Activated")
                        || name.contains("Inactive Terminal")) {

                    // Get player's interpolated position
                    AxisAlignedBB bb = getAxisAlignedBB(event, entity, mc);

                    GlStateManager.pushMatrix();
                    // Setup for filled box rendering (disable depth, texture, lighting, and culling)
                    GlStateManager.disableDepth();
                    GlStateManager.disableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableCull();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    // Bright light blue color (RGBA)
                    Color color = new Color(0, 191, 255, 150);

                    // Draw a filled bounding box using our helper method
                    drawFilledBB(bb, color);

                    // Restore GL state
                    GlStateManager.enableCull();
                    GlStateManager.enableLighting();
                    GlStateManager.enableTexture2D();
                    GlStateManager.enableDepth();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    private static @NotNull AxisAlignedBB getAxisAlignedBB(RenderWorldLastEvent event, Entity entity, Minecraft mc) {
        double px = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks;
        double py = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks;
        double pz = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks;

        // Determine the block position for the entity (using floor to align with block boundaries)
        double blockX = Math.floor(entity.posX);
        double blockY = Math.floor(entity.posY);
        double blockZ = Math.floor(entity.posZ);

        // Create a block-sized bounding box (1x1x1) in world space relative to the camera
        return new AxisAlignedBB(
                blockX - px, blockY - py, blockZ - pz,
                blockX - px + 1, blockY - py + 1, blockZ - pz + 1
        );
    }

    private void drawFilledBB(AxisAlignedBB bb, Color color) {
        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        float alpha = color.getAlpha() / 255f;
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_QUADS);

        // Bottom face
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);

        // Top face
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

        // Front face
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);

        // Back face
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

        // Left face
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);

        // Right face
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);

        GL11.glEnd();
    }
}
