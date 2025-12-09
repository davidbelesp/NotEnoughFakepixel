package com.nef.notenoughfakepixel.utils;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.variables.MobDisplayTypes;
import com.nef.notenoughfakepixel.variables.Resources;
import net.minecraft.block.BlockLever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

public class RenderUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawOnSlot(int size, int xSlotPos, int ySlotPos, int colour) {
        drawOnSlot(size, xSlotPos, ySlotPos, colour, -1);
    }

    public static void drawOnSlot(int size, int xSlotPos, int ySlotPos, int colour, int number) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int guiLeft = (scaledResolution.getScaledWidth() - 176) / 2;
        int guiTop = (scaledResolution.getScaledHeight() - 222) / 2;
        int x = guiLeft + xSlotPos;
        int y = guiTop + ySlotPos;

        // Move down when chest isn't 6 rows
        if (size != 90) y += (6 - (size - 36) / 9) * 9;

        GL11.glTranslated(0, 0, 1);
        Gui.drawRect(x, y, x + 16, y + 16, colour);
        GL11.glTranslated(0, 0, -1);

        if (number != -1) {
            String text = String.valueOf(number);
            int textWidth = mc.fontRendererObj.getStringWidth(text);

            // Push OpenGL states
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 300); // Bring the text to the foreground
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GlStateManager.disableDepth();

            // Render the string
            mc.fontRendererObj.drawStringWithShadow(text, x + 8 - textWidth / 2, y + 8 - 4, 0xFFFFFF);

            // Restore OpenGL states
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GL11.glEnable(GL11.GL_LIGHTING);
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        drawItemStackWithText(stack, x, y, null);
    }

    public static void drawItemStackWithText(ItemStack stack, int x, int y, String text) {
        if (stack == null) return;

        RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();

        RenderHelper.enableGUIStandardItemLighting();
        itemRender.zLevel = -145;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, x, y, text);
        itemRender.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
    }

    private static final ResourceLocation beaconBeam = Resources.BEACON.getResource();

    public static void renderBeaconBeam(BlockPos block, int rgb, float alphaMult, float partialTicks) {
        double viewerX;
        double viewerY;
        double viewerZ;

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;


        double x = block.getX() - viewerX;
        double y = block.getY() - viewerY;
        double z = block.getZ() - viewerZ;

        double distSq = x * x + y * y + z * z;

        RenderUtils.renderBeaconBeam(x, y, z, rgb, 1.0f, partialTicks, distSq > 10 * 10);
    }

    public static void renderBeaconBeam(
            double x, double y, double z, int rgb, float alphaMult,
            float partialTicks, Boolean disableDepth
    ) {
        int height = 300;
        int bottomOffset = 0;
        int topOffset = bottomOffset + height;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        if (disableDepth) {
            GlStateManager.disableDepth();
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(beaconBeam);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        double time = Minecraft.getMinecraft().theWorld.getTotalWorldTime() + (double) partialTicks;
        double d1 = MathHelper.func_181162_h(-time * 0.2D - (double) MathHelper.floor_double(-time * 0.1D));

        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;
        double d2 = time * 0.025D * -1.5D;
        double d4 = 0.5D + Math.cos(d2 + 2.356194490192345D) * 0.2D;
        double d5 = 0.5D + Math.sin(d2 + 2.356194490192345D) * 0.2D;
        double d6 = 0.5D + Math.cos(d2 + (Math.PI / 4D)) * 0.2D;
        double d7 = 0.5D + Math.sin(d2 + (Math.PI / 4D)) * 0.2D;
        double d8 = 0.5D + Math.cos(d2 + 3.9269908169872414D) * 0.2D;
        double d9 = 0.5D + Math.sin(d2 + 3.9269908169872414D) * 0.2D;
        double d10 = 0.5D + Math.cos(d2 + 5.497787143782138D) * 0.2D;
        double d11 = 0.5D + Math.sin(d2 + 5.497787143782138D) * 0.2D;
        double d14 = -1.0D + d1;
        double d15 = (double) (height) * 2.5D + d14;
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(1.0D, d15).color(r, g, b, alphaMult).endVertex();
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(0.0D, d15).color(r, g, b, alphaMult).endVertex();
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(1.0D, d15).color(r, g, b, alphaMult).endVertex();
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(0.0D, d15).color(r, g, b, alphaMult).endVertex();
        worldrenderer.pos(x + d6, y + topOffset, z + d7).tex(1.0D, d15).color(r, g, b, alphaMult).endVertex();
        worldrenderer.pos(x + d6, y + bottomOffset, z + d7).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d10, y + bottomOffset, z + d11).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d10, y + topOffset, z + d11).tex(0.0D, d15).color(r, g, b, alphaMult).endVertex();
        worldrenderer.pos(x + d8, y + topOffset, z + d9).tex(1.0D, d15).color(r, g, b, alphaMult).endVertex();
        worldrenderer.pos(x + d8, y + bottomOffset, z + d9).tex(1.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d4, y + bottomOffset, z + d5).tex(0.0D, d14).color(r, g, b, 1.0F).endVertex();
        worldrenderer.pos(x + d4, y + topOffset, z + d5).tex(0.0D, d15).color(r, g, b, alphaMult).endVertex();
        tessellator.draw();

        GlStateManager.disableCull();
        double d12 = -1.0D + d1;
        double d13 = height + d12;
        float alphaConst = 0.25F;

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.2D).tex(1.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.2D).tex(1.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.2D).tex(0.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.2D).tex(0.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.8D).tex(1.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.8D).tex(1.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.8D).tex(0.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.8D).tex(0.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.2D).tex(1.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.2D).tex(1.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.8D, y + bottomOffset, z + 0.8D).tex(0.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.8D, y + topOffset, z + 0.8D).tex(0.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.8D).tex(1.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.8D).tex(1.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.2D, y + bottomOffset, z + 0.2D).tex(0.0D, d12).color(r, g, b, alphaConst).endVertex();
        worldrenderer.pos(x + 0.2D, y + topOffset, z + 0.2D).tex(0.0D, d13).color(r, g, b, alphaConst * alphaMult).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();
        if (disableDepth) {
            GlStateManager.enableDepth();
        }
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    /**
     * Renders a colored, outlined 3D box at specified world coordinates.
     *
     * <p>This function accounts for the player's current interpolated position using {@code partialTicks}
     * to ensure the box is rendered in the correct location relative to the camera.</p>
     *
     * <p>It disables depth, lighting, and texture rendering temporarily to ensure
     * the box remains visible regardless of surrounding objects.</p>
     *
     * @param minX         The minimum X coordinate of the box in world space.
     * @param minY         The minimum Y coordinate of the box in world space.
     * @param minZ         The minimum Z coordinate of the box in world space.
     * @param maxX         The maximum X coordinate of the box in world space.
     * @param maxY         The maximum Y coordinate of the box in world space.
     * @param maxZ         The maximum Z coordinate of the box in world space.
     * @param partialTicks The current render tick delta for smooth interpolation.
     * @param color        The RGBA color of the box lines.
     */
    public static void renderBoxAtCoords(
            double minX, double minY, double minZ,
            double maxX, double maxY, double maxZ,
            float partialTicks,
            Color color, boolean disableDepth
    ) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity player = mc.getRenderViewEntity();

        // Interpolated player position
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        // Offset box coords relative to player
        double x1 = minX - playerX;
        double y1 = minY - playerY;
        double z1 = minZ - playerZ;
        double x2 = maxX - playerX;
        double y2 = maxY - playerY;
        double z2 = maxZ - playerZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        if (disableDepth) GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL11.glLineWidth(2.0f); // Optional: set line width

        // Set color
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        // Draw bounding box
        AxisAlignedBB box = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
        drawOutlinedBox(box);

        if (disableDepth) GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    /**
     * Draws an outlined (wireframe) Axis-Aligned Bounding Box (AABB) using OpenGL lines.
     *
     * <p>This method assumes that the appropriate OpenGL state (e.g., color, blending, depth)
     * has already been set. It simply renders the 12 edges of the box using GL_LINES.</p>
     *
     * @param box The AxisAlignedBB instance to draw.
     */
    public static void drawOutlinedBox(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        // Bottom square
        buffer.pos(box.minX, box.minY, box.minZ).endVertex();
        buffer.pos(box.maxX, box.minY, box.minZ).endVertex();

        buffer.pos(box.maxX, box.minY, box.minZ).endVertex();
        buffer.pos(box.maxX, box.minY, box.maxZ).endVertex();

        buffer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        buffer.pos(box.minX, box.minY, box.maxZ).endVertex();

        buffer.pos(box.minX, box.minY, box.maxZ).endVertex();
        buffer.pos(box.minX, box.minY, box.minZ).endVertex();

        // Top square
        buffer.pos(box.minX, box.maxY, box.minZ).endVertex();
        buffer.pos(box.maxX, box.maxY, box.minZ).endVertex();

        buffer.pos(box.maxX, box.maxY, box.minZ).endVertex();
        buffer.pos(box.maxX, box.maxY, box.maxZ).endVertex();

        buffer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
        buffer.pos(box.minX, box.maxY, box.maxZ).endVertex();

        buffer.pos(box.minX, box.maxY, box.maxZ).endVertex();
        buffer.pos(box.minX, box.maxY, box.minZ).endVertex();

        // Vertical lines
        buffer.pos(box.minX, box.minY, box.minZ).endVertex();
        buffer.pos(box.minX, box.maxY, box.minZ).endVertex();

        buffer.pos(box.maxX, box.minY, box.minZ).endVertex();
        buffer.pos(box.maxX, box.maxY, box.minZ).endVertex();

        buffer.pos(box.maxX, box.minY, box.maxZ).endVertex();
        buffer.pos(box.maxX, box.maxY, box.maxZ).endVertex();

        buffer.pos(box.minX, box.minY, box.maxZ).endVertex();
        buffer.pos(box.minX, box.maxY, box.maxZ).endVertex();

        tessellator.draw();
    }


    public static void renderEntityHitbox(Entity entity, float partialTicks, Color color, MobDisplayTypes type) {
        if (type == MobDisplayTypes.ITEMBIG) {
            renderItemBigHitbox(entity, partialTicks, color);
            return;
        }

        Vector3f loc = new Vector3f(
                (float) entity.posX - 0.5f,
                (float) entity.posY - 0.5f,
                (float) entity.posZ - 0.5f);

        if (type == MobDisplayTypes.BAT ||
                type == MobDisplayTypes.ENDERMAN_BOSS ||
                type == MobDisplayTypes.WOLF_BOSS ||
                type == MobDisplayTypes.SPIDER_BOSS ||
                type == MobDisplayTypes.M7ORBS ||
                type == MobDisplayTypes.AUTOMATON
        ) {
            GlStateManager.disableDepth();
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        Entity player = mc.getRenderViewEntity();
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        double x = loc.x - playerX + 0.5;
        double y = loc.y - playerY - 0.5;
        if (type == MobDisplayTypes.BAT) {
            y = (loc.y - playerY) + 1;
        } else if (type == MobDisplayTypes.FEL) {
            y = loc.y - playerY + 2.3;
        }
        double z = loc.z - playerZ + 0.5;

        double y1 = y + type.getY1();
        double y2 = y + type.getY2();
        double x1 = x + type.getX1();
        double x2 = x + type.getX2();
        double z1 = z + type.getZ1();
        double z2 = z + type.getZ2();

        drawHitbox(x1, x2, y1, y2, z1, z2, color, type);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void renderItemBigHitbox(Entity entity, float partialTicks, Color color) {
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        if (bb == null) return;

        double scale = Config.feature.dungeons.dungeonsScaleItemDrop;

        Entity player = mc.getRenderViewEntity();
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        // Compute original box coordinates relative to player
        double x1 = bb.minX - playerX;
        double x2 = bb.maxX - playerX;
        double y1 = bb.minY - playerY;
        double y2 = bb.maxY - playerY;
        double z1 = bb.minZ - playerZ;
        double z2 = bb.maxZ - playerZ;

        // Compute the center of the bounding box
        double centerX = (x1 + x2) / 2;
        double centerY = (y1 + y2) / 2;
        double centerZ = (z1 + z2) / 2;

        // Scale bounding box relative to center
        x1 = centerX + (x1 - centerX) * scale;
        x2 = centerX + (x2 - centerX) * scale;
        y1 = centerY + (y1 - centerY) * scale;
        y2 = centerY + (y2 - centerY) * scale;
        z1 = centerZ + (z1 - centerZ) * scale;
        z2 = centerZ + (z2 - centerZ) * scale;

        double yOffset = (Config.feature.dungeons.dungeonsScaleItemDrop - 1f) * (entity.height / 2f);
        y1 += yOffset;
        y2 += yOffset;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        drawHitbox(x1, x2, y1, y2, z1, z2, color, MobDisplayTypes.ITEMBIG);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }


    private static void drawHitbox(double x1, double x2, double y1, double y2, double z1, double z2, Color color, MobDisplayTypes type) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        if (type == MobDisplayTypes.GAIA) {
            GL11.glLineWidth(5.0f);
        } else {
            GL11.glLineWidth(3.0f);
        }

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        double[][] vertices = {
                {x1, y1, z1}, {x2, y1, z1}, {x2, y2, z1}, {x1, y2, z1},
                {x1, y1, z2}, {x2, y1, z2}, {x2, y2, z2}, {x1, y2, z2}
        };

        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        for (int[] edge : edges) {
            worldRenderer.pos(vertices[edge[0]][0], vertices[edge[0]][1], vertices[edge[0]][2])
                    .color(red, green, blue, alpha).endVertex();
            worldRenderer.pos(vertices[edge[1]][0], vertices[edge[1]][1], vertices[edge[1]][2])
                    .color(red, green, blue, alpha).endVertex();
        }

        tessellator.draw();
    }

    /**
     * Renders a floating name tag in 3D space at the specified position, facing the player.
     * Includes a semi-transparent background and dynamically scales with distance.
     *
     * @param str          The string to render as the tag.
     * @param pos          The world position as {x, y, z}.
     * @param color        The color of the tag text.
     * @param partialTicks The render partial ticks used for smooth interpolation.
     */
    public static void drawTag(String str, double[] pos, Color color, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRendererObj;
        EntityPlayerSP player = mc.thePlayer;
        RenderManager renderManager = mc.getRenderManager();

        Vec3 viewerPos = getInterpolatedPos(player, partialTicks);
        Vec3 tagPos = new Vec3(pos[0] - viewerPos.xCoord + 0.5, pos[1] - viewerPos.yCoord + 0.5, pos[2] - viewerPos.zCoord + 0.5);

        double distance = player.getDistance(pos[0], pos[1], pos[2]);
        float scale = Math.max(2.0F, (float) distance / 5.0F) * 0.016666668F;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.translate(tagPos.xCoord, tagPos.yCoord + 2.5, tagPos.zCoord);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);

        setupRenderStateForText();

        drawTagBackground(font, str);
        font.drawString(str, -font.getStringWidth(str) / 2, 0, colorToInt(color));

        restoreRenderState();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    /**
     * Calculates an interpolated position for the given entity based on partial ticks.
     * This creates smooth transitions between render frames.
     *
     * @param entity       The entity whose position to interpolate.
     * @param partialTicks The partial tick value for interpolation.
     * @return A {@link Vec3} representing the smoothly interpolated world position.
     */
    private static Vec3 getInterpolatedPos(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        return new Vec3(x, y, z);
    }

    /**
     * Configures OpenGL state for rendering floating text in the world.
     * Disables lighting, depth testing, and enables blending for transparency.
     */
    private static void setupRenderStateForText() {
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
    }

    /**
     * Restores OpenGL state after rendering a 3D floating label.
     * Re-enables depth testing, lighting, and disables blending to prevent side effects.
     */
    private static void restoreRenderState() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    /**
     * Draws a semi-transparent black background behind the name tag text.
     *
     * @param font The {@link FontRenderer} used to measure the string width.
     * @param str  The text string being rendered (used to size the box).
     */
    private static void drawTagBackground(FontRenderer font, String str) {
        int width = font.getStringWidth(str) / 2;

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        GlStateManager.disableTexture2D();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(-width - 1, -1, 0.0D).color(0, 0, 0, 64).endVertex();
        wr.pos(-width - 1, 8, 0.0D).color(0, 0, 0, 64).endVertex();
        wr.pos(width + 1, 8, 0.0D).color(0, 0, 0, 64).endVertex();
        wr.pos(width + 1, -1, 0.0D).color(0, 0, 0, 64).endVertex();
        tess.draw();
        GlStateManager.enableTexture2D();
    }

    public static void draw3DLine(Vec3 pos1, Vec3 pos2, Color color, int lineWidth, boolean depth, float partialTicks) {
        draw3DLine(pos1, pos2, color, lineWidth, depth, partialTicks, false, false, null);
    }

    /**
     * Draws a 3D line between two world-space positions using OpenGL line rendering.
     * <p>
     * This version supports advanced customization, including drawing from the player's head direction
     * and offsetting the starting point when targeting levers with specific orientations.
     *
     * @param pos1         The starting {@link Vec3} world position.
     * @param pos2         The ending {@link Vec3} world position.
     * @param color        The color of the line, including alpha.
     * @param lineWidth    The width of the line in pixels (OpenGL units).
     * @param depth        If {@code true}, respects depth testing; if {@code false}, renders on top of everything.
     * @param partialTicks The partial tick value used for interpolating the render position.
     * @param fromHead     If {@code true}, overrides {@code pos2} with a vector in the player's look direction.
     * @param isLever      If {@code true}, adjusts {@code pos1} to align with the center of a lever's hitbox.
     * @param orientation  The lever's orientation (used only if {@code isLever} is {@code true}).
     */
    public static void draw3DLine(Vec3 pos1, Vec3 pos2, Color color, int lineWidth, boolean depth,
                                  float partialTicks, boolean fromHead, boolean isLever, BlockLever.EnumOrientation orientation) {

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        Vec3 interp = getInterpolatedPosition(viewer, partialTicks);

        Vec3 start = isLever ? getLeverCenter(pos1, orientation) : pos1;
        Vec3 end = fromHead ? getPlayerLookVec() : pos2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-interp.xCoord, -interp.yCoord, -interp.zCoord);
        setupRenderState(depth, lineWidth);

        renderLine(start, end, color);

        cleanupRenderState(depth);
        GlStateManager.translate(interp.xCoord, interp.yCoord, interp.zCoord);
        GlStateManager.popMatrix();
    }

    /**
     * Calculates the interpolated position of an entity based on the current partial ticks.
     * This allows for smooth rendering between ticks.
     *
     * @param entity       The entity to interpolate (e.g., the render view entity).
     * @param partialTicks The current render partial ticks.
     * @return The interpolated {@link Vec3} position.
     */
    private static Vec3 getInterpolatedPosition(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        return new Vec3(x, y, z);
    }

    /**
     * Calculates the central position of a lever based on its orientation.
     * This adjusts the start point of the line so it appears to originate from the correct part of the lever.
     *
     * @param pos         The base {@link Vec3} block position of the lever.
     * @param orientation The orientation of the lever.
     * @return A {@link Vec3} representing the center of the lever face.
     */
    private static Vec3 getLeverCenter(Vec3 pos, BlockLever.EnumOrientation orientation) {
        double x = pos.xCoord, y = pos.yCoord, z = pos.zCoord;

        switch (orientation) {
            case UP_X:
            case UP_Z:
                return new Vec3(x + 0.5, y + 0.1, z + 0.5);
            case NORTH:
                return new Vec3(x + 0.5, y + 0.5, z + 0.875);
            case SOUTH:
                return new Vec3(x + 0.5, y + 0.5, z + 0.125);
            case WEST:
                return new Vec3(x + 0.875, y + 0.5, z + 0.5);
            case EAST:
                return new Vec3(x + 0.125, y + 0.5, z + 0.5);
            default:
                return new Vec3(x + 0.5, y + 0.5, z - 1.125);
        }
    }

    /**
     * Gets a direction vector based on the player's current yaw and pitch, simulating a "look" direction.
     * Used when drawing lines from the player's head or eye position.
     *
     * @return A normalized {@link Vec3} representing the player's look direction.
     */
    private static Vec3 getPlayerLookVec() {
        Minecraft mc = Minecraft.getMinecraft();
        float yaw = -mc.thePlayer.rotationYaw;
        float pitch = -mc.thePlayer.rotationPitch;
        return new Vec3(0, 0, 1)
                .rotatePitch((float) Math.toRadians(pitch))
                .rotateYaw((float) Math.toRadians(yaw));
    }

    /**
     * Configures the OpenGL state for line rendering.
     * Disables textures, lighting, and depth as needed, and sets blend modes.
     *
     * @param depth     If false, disables depth testing and depth writes.
     * @param lineWidth The width of the OpenGL line to draw.
     */
    private static void setupRenderState(boolean depth, int lineWidth) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(lineWidth);

        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
    }

    /**
     * Restores OpenGL state after rendering to avoid interfering with other game rendering operations.
     * Re-enables depth testing, texture, lighting, and alpha.
     *
     * @param depth If false, re-enables depth testing and depth mask.
     */
    private static void cleanupRenderState(boolean depth) {
        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }

        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a single 3D line between two points in the world.
     *
     * @param start The starting {@link Vec3} position.
     * @param end   The ending {@link Vec3} position.
     * @param color The line color, including alpha transparency.
     */
    private static void renderLine(Vec3 start, Vec3 end, Color color) {
        WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        wr.pos(start.xCoord, start.yCoord, start.zCoord).endVertex();
        wr.pos(end.xCoord, end.yCoord, end.zCoord).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void highlightBlock(BlockPos pos, Color color, boolean disableDepth, float partialTicks) {
        highlightBlock(pos, color, disableDepth, false, partialTicks);
    }

    public static void highlightBlock(BlockPos pos, Color color, boolean disableDepth, boolean isButton, float partialTicks) {
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

        double x = pos.getX() - viewerX;
        double y = pos.getY() - viewerY;
        double z = pos.getZ() - viewerZ;

        if (disableDepth) GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        double initialToAddX = 0;
        if (!disableDepth) {
            initialToAddX = .05;
        }
        if (!isButton) {
            if (disableDepth) {
                RenderUtils.drawFilledBoundingBox(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), 1f, color);
            } else {
                RenderUtils.drawFilledBoundingBox(new AxisAlignedBB(x - initialToAddX, y, z, x + 1 + initialToAddX, y + 1, z + 1), 1f, color);
            }
        } else {
            RenderUtils.drawFilledBoundingBox(new AxisAlignedBB(x, y + 0.5 - 0.13, z + 0.5 - 0.191, x - .13, y + 0.5 + 0.13, z + 0.5 + 0.191), 1f, color);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        if (disableDepth) GlStateManager.enableDepth();
        GlStateManager.enableCull();
    }

    public static void drawLeverBoundingBox(BlockPos pos, EnumFacing facing, Color color, float partialTicks) {
        // Get the player's camera position
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

        // Convert world position to render position
        double x = pos.getX() - viewerX;
        double y = pos.getY() - viewerY;
        double z = pos.getZ() - viewerZ;

        // Define bounding box relative to lever position
        AxisAlignedBB boundingBox;
        switch (facing) {
            case NORTH:
                boundingBox = new AxisAlignedBB(x + 0.25, y + 0.1875, z + 0.75, x + 0.75, y + 0.8125, z + 1);
                break;
            case SOUTH:
                boundingBox = new AxisAlignedBB(x + 0.25, y + 0.1875, z, x + 0.75, y + 0.8125, z + 0.25);
                break;
            case WEST:
                boundingBox = new AxisAlignedBB(x + 0.75, y + 0.1875, z + 0.25, x + 1, y + 0.8125, z + 0.75);
                break;
            case EAST:
                boundingBox = new AxisAlignedBB(x, y + 0.1875, z + 0.25, x + 0.25, y + 0.8125, z + 0.75);
                break;
            default:
                boundingBox = new AxisAlignedBB(x + 0.25, y + 0.1875, z - 1.25, x + 0.75, y + 0.8125, z - 1);
                break;
        }

        // Disable culling and lighting for proper rendering
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);

        // Render bounding box
        RenderUtils.drawFilledBoundingBox(boundingBox, 1f, color);

        // Restore rendering settings
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
    }

    public static void drawFilledBoundingBox(AxisAlignedBB box, float alpha, Color color) {

        setupGlStateForBox();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = MathHelper.clamp_float(color.getAlpha() / 255f * alpha, 0.0f, 1.0f);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        // Bottom
        drawFace(wr, tess,
                box.minX, box.minY, box.minZ,
                box.maxX, box.minY, box.maxZ,
                r, g, b, a);

        // Top
        drawFace(wr, tess,
                box.minX, box.maxY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                r, g, b, a);

        // North
        drawFace(wr, tess,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.minZ,
                r, g, b, a);

        // South
        drawFace(wr, tess,
                box.minX, box.minY, box.maxZ,
                box.maxX, box.maxY, box.maxZ,
                r, g, b, a);

        // West
        drawFace(wr, tess,
                box.minX, box.minY, box.minZ,
                box.minX, box.maxY, box.maxZ,
                r, g, b, a);

        // East
        drawFace(wr, tess,
                box.maxX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                r, g, b, a);

        restoreGlState();
    }

    /**
     * Prepares the OpenGL state for rendering a filled bounding box.
     * Disables lighting, textures, and depth writing, and enables blending.
     * Call this before drawing faces.
     */
    private static void setupGlStateForBox() {
        GlStateManager.pushAttrib();
        GlStateManager.disableTexture2D();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    }

    /**
     * Restores the OpenGL state after rendering a filled bounding box.
     * Re-enables depth writing, textures, lighting, and disables blending.
     * Call this after finishing all drawing operations.
     */
    private static void restoreGlState() {
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popAttrib();
    }

    /**
     * Draws a single axis-aligned rectangular face (quad) between two opposing corners.
     * <p>
     * The face is automatically determined to lie on one of the three axis-aligned planes (XY, XZ, YZ)
     * based on which coordinate is constant (i.e., the same in both corners). The function handles
     * sorting the coordinates and drawing the quad with proper vertex ordering and color.
     * </p>
     *
     * @param wr   The {@link WorldRenderer} instance used for buffering vertex data.
     * @param tess The {@link Tessellator} instance used to execute the buffered draw call.
     * @param x1   The x-coordinate of the first corner.
     * @param y1   The y-coordinate of the first corner.
     * @param z1   The z-coordinate of the first corner.
     * @param x2   The x-coordinate of the opposite corner.
     * @param y2   The y-coordinate of the opposite corner.
     * @param z2   The z-coordinate of the opposite corner.
     * @param r    The red color component (0.0 to 1.0).
     * @param g    The green color component (0.0 to 1.0).
     * @param b    The blue color component (0.0 to 1.0).
     * @param a    The alpha (transparency) component (0.0 to 1.0).
     */
    private static void drawFace(WorldRenderer wr, Tessellator tess,
                                 double x1, double y1, double z1,
                                 double x2, double y2, double z2,
                                 float r, float g, float b, float a) {
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Sort coordinates
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);

        // Determine the face orientation and assign vertices accordingly
        if (minX == maxX) {
            // X-face (YZ plane)
            wr.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
            wr.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
            wr.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
            wr.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        } else if (minY == maxY) {
            // Y-face (XZ plane)
            wr.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
            wr.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
            wr.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
            wr.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
        } else if (minZ == maxZ) {
            // Z-face (XY plane)
            wr.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
            wr.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
            wr.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
            wr.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
        }

        tess.draw();
    }

    /**
     * Converts a {@link Color} object to a single 24-bit RGB integer.
     * <p>
     * The resulting integer uses the format <code>0xRRGGBB</code> where:
     * <ul>
     *   <li><b>RR</b> is the red component (8 bits)</li>
     *   <li><b>GG</b> is the green component (8 bits)</li>
     *   <li><b>BB</b> is the blue component (8 bits)</li>
     * </ul>
     *
     * @param color The {@link Color} to convert. Alpha is ignored.
     * @return An integer representation of the color in 0xRRGGBB format.
     */
    public static int colorToInt(Color color) {
        return (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
    }

    public static void drawFilledBoundingBoxEntity(AxisAlignedBB aabb, float alpha, Color color, float partialTicks) {
        // Used for BlazeAttunements
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();

        double coordX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double coordY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double coordZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-coordX, -coordY, -coordZ);

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (color.getAlpha() / 255f) * alpha);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        // Draw the six faces of the box
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        tessellator.draw();

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        tessellator.draw();

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        tessellator.draw();

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        tessellator.draw();

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        tessellator.draw();

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldrenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        tessellator.draw();

        // Restore OpenGL state
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void renderWaypointText(String str, BlockPos loc, float partialTicks) {
        renderWaypointText(str, loc, partialTicks, true);
    }

    public static void renderWaypointText(String str, BlockPos loc, float partialTicks, boolean showDistance) {
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;

        double x = loc.getX() + 0.5 - viewerX;
        double y = loc.getY() + 0.5 - viewerY - viewer.getEyeHeight();
        double z = loc.getZ() + 0.5 - viewerZ;

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);
        if (distSq > 144) {
            x *= 12 / dist;
            y *= 12 / dist;
            z *= 12 / dist;
        }
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(0, viewer.getEyeHeight(), 0);

        float scale = 2.0F;
        GlStateManager.scale(scale, scale, scale);

        drawNametag(str);

        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, -0.25f, 0);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

        if (showDistance) drawNametag(EnumChatFormatting.YELLOW.toString() + Math.round(dist) + "m");

        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void drawNametag(String str) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.pushMatrix();
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 0;

        int j = fontrenderer.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-j - 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(-j - 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(j + 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(j + 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
        GlStateManager.depthMask(true);
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aabb, Color color, float width, float partialTicks) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(width);

        RenderGlobal.drawOutlinedBoundingBox(aabb, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static Vector3f getInterpolatedPlayerPosition(float partialTicks) {
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        Vector3f lastPos = new Vector3f(
                (float) viewer.lastTickPosX,
                (float) viewer.lastTickPosY,
                (float) viewer.lastTickPosZ
        );
        Vector3f currentPos = new Vector3f(
                (float) viewer.posX,
                (float) viewer.posY,
                (float) viewer.posZ
        );
        Vector3f movement = Vector3f.sub(currentPos, lastPos, currentPos);
        movement.scale(partialTicks);
        return Vector3f.add(lastPos, movement, lastPos);
    }

    public static void renderBlockBox(BlockPos pos, Color c, float partialTicks) {
        renderBlockBox(pos, c, partialTicks, false);
    }

    public static void renderBlockBox(BlockPos pos, Color c, float partialTicks, boolean disableDepth) {
        Vector3f interpolatedPlayerPosition = getInterpolatedPlayerPosition(partialTicks);
        renderBoundingBoxInViewSpace(
                pos.getX() - interpolatedPlayerPosition.x,
                pos.getY() - interpolatedPlayerPosition.y,
                pos.getZ() - interpolatedPlayerPosition.z,
                c,
                disableDepth
        );
    }

    private static void renderBoundingBoxInViewSpace(double x, double y, double z, Color c, boolean disableDepth) {
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);

        if (disableDepth) GlStateManager.disableDepth();
        GlStateManager.disableCull();
        GlStateManager.disableTexture2D();
        drawFilledBoundingBox(bb, c, 1f);
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
        if (disableDepth) GlStateManager.enableDepth();
    }

    public static void drawFilledBoundingBox(AxisAlignedBB p_181561_0_, Color c, float alpha) {

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f * alpha);

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        tessellator.draw();

        GlStateManager.color(
                c.getRed() / 255f * 0.8f,
                c.getGreen() / 255f * 0.8f,
                c.getBlue() / 255f * 0.8f,
                c.getAlpha() / 255f * alpha
        );

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();

        GlStateManager.color(
                c.getRed() / 255f * 0.9f,
                c.getGreen() / 255f * 0.9f,
                c.getBlue() / 255f * 0.9f,
                c.getAlpha() / 255f * alpha
        );

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.minY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.maxX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        worldrenderer.pos(p_181561_0_.minX, p_181561_0_.maxY, p_181561_0_.maxZ).endVertex();
        tessellator.draw();
    }

    public static void renderBoundingBox(BlockPos pos, int rgb, float partialTicks) {
        renderBoundingBox(pos, rgb, partialTicks, true);
    }

    public static void renderBoundingBox(BlockPos pos, int rgb, float partialTicks, boolean ignoreDepth) {
        double playerX = Minecraft.getMinecraft().thePlayer.prevPosX + (Minecraft.getMinecraft().thePlayer.posX - Minecraft.getMinecraft().thePlayer.prevPosX) * partialTicks;
        double playerY = Minecraft.getMinecraft().thePlayer.prevPosY + (Minecraft.getMinecraft().thePlayer.posY - Minecraft.getMinecraft().thePlayer.prevPosY) * partialTicks;
        double playerZ = Minecraft.getMinecraft().thePlayer.prevPosZ + (Minecraft.getMinecraft().thePlayer.posZ - Minecraft.getMinecraft().thePlayer.prevPosZ) * partialTicks;

        double x = pos.getX() - playerX;
        double y = pos.getY() - playerY;
        double z = pos.getZ() - playerZ;

        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        if (ignoreDepth) GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_LIGHTING);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0f);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        double minX = x, maxX = x + 1;
        double minY = y, maxY = y + 1;
        double minZ = z, maxZ = z + 1;

        // Bottom face
        worldRenderer.pos(minX, minY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, minY, minZ).color(r, g, b, 1.0f).endVertex();

        // Top face
        worldRenderer.pos(minX, maxY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, maxY, minZ).color(r, g, b, 1.0f).endVertex();

        // Vertical edges
        worldRenderer.pos(minX, minY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, maxY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(r, g, b, 1.0f).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(r, g, b, 1.0f).endVertex();

        tessellator.draw();

        if (ignoreDepth) GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_LIGHTING);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

}
