package com.nef.notenoughfakepixel.features.skyblock.dungeons;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.variables.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Map;

@RegisterEvents
public class DungeonsMap {

    private static final float playerMarkerScale = 1.4F;
    private static final float othersMarkerScale = 1.25F;
    private double playerPositionX, playerPositionY = 0;
    private static final ResourceLocation mapIconsTexture = Resources.MAP_ICONS.getResource();
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean finalScreen = false;
    private final Tessellator tessellator = Tessellator.getInstance();
    private final WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    public DungeonsMap() {
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!Config.feature.dungeons.dungeonsMap) return;
        if (!DungeonManager.checkEssentials()) return;

        ItemStack map = mc.thePlayer.inventory.getStackInSlot(8);
        if (map == null || !(map.getItem() instanceof ItemMap)) return;

        MapData data = ((ItemMap) map.getItem()).getMapData(map, mc.theWorld);
        if (data != null) {
            drawMap(data);
            drawBorderMap();
            drawMarkers(data.mapDecorations);
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!DungeonManager.checkEssentials()) return;
        String message = event.message.getUnformattedText();
        if (message.contains("> EXTRA STATS <")) {
            finalScreen = true;
        } else if (message.equals("[NPC] Mort: Good luck.")) {
            finalScreen = false;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!DungeonManager.checkEssentials()) return;
        finalScreen = false;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!DungeonManager.checkEssentials()) return;
        finalScreen = false;
    }

    public void drawMap(MapData data) {
        GlStateManager.pushMatrix();
        ScaledResolution sr = new ScaledResolution(mc);
        int mapWidth = (int) (128 * Config.feature.dungeons.dungeonsMapScale);
        int mapHeight = (int) (128 * Config.feature.dungeons.dungeonsMapScale);
        float x = Config.feature.dungeons.dungeonsMapPos.getAbsX(sr, mapWidth);
        float y = Config.feature.dungeons.dungeonsMapPos.getAbsY(sr, mapHeight);

        // Adjust for centering if centerX or centerY is true
        if (Config.feature.dungeons.dungeonsMapPos.isCenterX()) {
            x -= mapWidth / 2;
        }
        if (Config.feature.dungeons.dungeonsMapPos.isCenterY()) {
            y -= mapHeight / 2;
        }

        float x1 = x;
        float y1 = y;
        float x2 = x1 + mapWidth;
        float y2 = y1 + mapHeight;

        int scaleFactor = sr.getScaleFactor();
        int scissorX = (int) (x1 * scaleFactor);
        int scissorY = (int) (y1 * scaleFactor);
        int scissorWidth = (int) ((x2 - x1) * scaleFactor);
        int scissorHeight = (int) ((y2 - y1) * scaleFactor);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, mc.displayHeight - (scissorY + scissorHeight), scissorWidth, scissorHeight);

        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(Config.feature.dungeons.dungeonsMapScale, Config.feature.dungeons.dungeonsMapScale, Config.feature.dungeons.dungeonsMapScale);

        if (data == null) {
            GlStateManager.color(0.5f, 0.5f, 0.5f, 1.0f);
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            worldrenderer.pos(0, 0, 0).endVertex();
            worldrenderer.pos(128, 0, 0).endVertex();
            worldrenderer.pos(128, 128, 0).endVertex();
            worldrenderer.pos(0, 128, 0).endVertex();
            tessellator.draw();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            if (Config.feature.dungeons.dungeonsRotateMap && !finalScreen) {
                float angle = -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
                GlStateManager.translate(64, 64, 0);
                GlStateManager.rotate(angle + 180, 0, 0, 1);
                GlStateManager.translate(-64, -64, 0);
                float translateX = 64.0F - (float) playerPositionX;
                float translateY = 64.0F - (float) playerPositionY;
                GlStateManager.translate(translateX, translateY, 0);
            }
            mc.entityRenderer.getMapItemRenderer().renderMap(data, false);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    private void drawBorderMap() {
        GlStateManager.pushMatrix();
        ScaledResolution sr = new ScaledResolution(mc);
        int mapWidth = (int) (128 * Config.feature.dungeons.dungeonsMapScale);
        int mapHeight = (int) (128 * Config.feature.dungeons.dungeonsMapScale);
        float x = Config.feature.dungeons.dungeonsMapPos.getAbsX(sr, mapWidth);
        float y = Config.feature.dungeons.dungeonsMapPos.getAbsY(sr, mapHeight);

        // Adjust for centering
        if (Config.feature.dungeons.dungeonsMapPos.isCenterX()) {
            x -= (float) mapWidth / 2;
        }
        if (Config.feature.dungeons.dungeonsMapPos.isCenterY()) {
            y -= (float) mapHeight / 2;
        }

        GlStateManager.disableTexture2D();
        String[] colorParts = Config.feature.dungeons.dungeonsMapBorderColor.split(":");
        float alpha = Float.parseFloat(colorParts[1]) / 255f;
        float red = Float.parseFloat(colorParts[2]) / 255f;
        float green = Float.parseFloat(colorParts[3]) / 255f;
        float blue = Float.parseFloat(colorParts[4]) / 255f;
        GlStateManager.color(red, green, blue, alpha);
        GL11.glLineWidth(2.0f);

        float x1 = x;
        float y1 = y;
        float x2 = x1 + mapWidth;
        float y2 = y1 + mapHeight;

        worldrenderer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x1, y1, 0.0D).endVertex();
        worldrenderer.pos(x2, y1, 0.0D).endVertex();
        worldrenderer.pos(x2, y2, 0.0D).endVertex();
        worldrenderer.pos(x1, y2, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawMarkers(Map<String, Vec4b> mapDecorations) {
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();
        int mapWidth = (int) (128 * Config.feature.dungeons.dungeonsMapScale);
        int mapHeight = (int) (128 * Config.feature.dungeons.dungeonsMapScale);
        float x = Config.feature.dungeons.dungeonsMapPos.getAbsX(sr, mapWidth);
        float y = Config.feature.dungeons.dungeonsMapPos.getAbsY(sr, mapHeight);

        // Adjust for centering
        if (Config.feature.dungeons.dungeonsMapPos.isCenterX()) {
            x -= (float) mapWidth / 2;
        }
        if (Config.feature.dungeons.dungeonsMapPos.isCenterY()) {
            y -= (float) mapHeight / 2;
        }

        float x1 = x;
        float y1 = y;
        float x2 = x1 + mapWidth;
        float y2 = y1 + mapHeight;

        int scissorX = (int) (x1 * scaleFactor);
        int scissorY = (int) (y1 * scaleFactor);
        int scissorWidth = (int) ((x2 - x1) * scaleFactor);
        int scissorHeight = (int) ((y2 - y1) * scaleFactor);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, mc.displayHeight - (scissorY + scissorHeight), scissorWidth, scissorHeight);

        int colorIndex = 0;
        for (Map.Entry<String, Vec4b> entry : mapDecorations.entrySet()) {
            GlStateManager.pushMatrix();
            byte iconType = entry.getValue().func_176110_a();
            if (iconType == 3) iconType = 0;

            double markerX = entry.getValue().func_176112_b() / 2.0F + 64.0F;
            double markerY = entry.getValue().func_176113_c() / 2.0F + 64.0F;
            float playerAngle = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

            if (iconType == 1) {
                playerPositionX = markerX;
                playerPositionY = markerY;
            }

            if (!Config.feature.dungeons.dungeonsRotateMap || finalScreen) {
                GlStateManager.translate((x + markerX * Config.feature.dungeons.dungeonsMapScale),
                        (y + markerY * Config.feature.dungeons.dungeonsMapScale), 0.0);
            } else if (iconType == 1) {
                GlStateManager.translate((x + 64.0F * Config.feature.dungeons.dungeonsMapScale),
                        (y + 64.0F * Config.feature.dungeons.dungeonsMapScale), 0.0);
            } else if (iconType == 0) {
                float relativeX = (float) (markerX - 64);
                float relativeY = (float) (markerY - 64);
                relativeX += 64.0F - (float) playerPositionX;
                relativeY += 64.0F - (float) playerPositionY;
                float angleRad = (float) Math.toRadians(-playerAngle);
                float rotatedX = (float) (relativeX * Math.cos(angleRad) - relativeY * Math.sin(angleRad));
                float rotatedY = (float) (relativeX * Math.sin(angleRad) + relativeY * Math.cos(angleRad));

                GlStateManager.translate((x + (64.0F - rotatedX) * Config.feature.dungeons.dungeonsMapScale),
                        (y + (64.0F - rotatedY) * Config.feature.dungeons.dungeonsMapScale), 0.0);
            }

            float angle = 180F;
            if ((!Config.feature.dungeons.dungeonsRotateMap || finalScreen) && iconType == 1) angle = playerAngle;
            if (iconType == 0) angle = (float) (entry.getValue().func_176111_d() * 360) / 16.0F;
            if (Config.feature.dungeons.dungeonsRotateMap && !finalScreen && iconType == 0)
                angle = angle + 180 - playerAngle;

            GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
            if (iconType == 1) GlStateManager.scale(Config.feature.dungeons.dungeonsMapScale * 4 * playerMarkerScale,
                    Config.feature.dungeons.dungeonsMapScale * 4 * playerMarkerScale, 3.0F * playerMarkerScale);
            if (iconType == 0) GlStateManager.scale(Config.feature.dungeons.dungeonsMapScale * 4 * othersMarkerScale,
                    Config.feature.dungeons.dungeonsMapScale * 4 * othersMarkerScale, 3.0F * othersMarkerScale);

            float g = (float) (iconType % 4) / 4.0F;
            float h = (float) (iconType / 4) / 4.0F;
            float l = (float) (iconType % 4 + 1) / 4.0F;
            float m = (float) (iconType / 4 + 1) / 4.0F;

            GlStateManager.translate(-0.125F, 0.125F, 0.0F);
            mc.getTextureManager().bindTexture(mapIconsTexture);

            if (iconType == 0) {
                switch (colorIndex) {
                    case 0:
                        GlStateManager.color(0.0F, 0.0F, 1.0F, 1.0F);
                        break; // Blue
                    case 1:
                        GlStateManager.color(1.0F, 1.0F, 0.0F, 1.0F);
                        break; // Yellow
                    case 2:
                        GlStateManager.color(1.0F, 0.5F, 0.0F, 1.0F);
                        break; // Orange
                    case 3:
                        GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
                        break; // Red
                }
                if (mapDecorations.size() > 1)
                    colorIndex = (colorIndex + 1) % (mapDecorations.size() - 1);
                else colorIndex = 0;
            }

            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            float eachDecorationZOffset = -0.001F;
            worldrenderer.pos(-1.0D, 1.0D, eachDecorationZOffset).tex(g, h).endVertex();
            worldrenderer.pos(1.0D, 1.0D, eachDecorationZOffset).tex(l, h).endVertex();
            worldrenderer.pos(1.0D, -1.0D, eachDecorationZOffset).tex(l, m).endVertex();
            worldrenderer.pos(-1.0D, -1.0D, eachDecorationZOffset).tex(g, m).endVertex();
            tessellator.draw();

            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}