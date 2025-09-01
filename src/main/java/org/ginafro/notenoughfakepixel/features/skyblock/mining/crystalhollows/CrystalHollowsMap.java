package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.Logger;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@RegisterEvents
public class CrystalHollowsMap extends Gui {

    @Getter private static final int MARGIN_PX = 6;
    private static final ResourceLocation MAP_ZONES_TEX = new ResourceLocation("notenoughfakepixel:crystalhollows/map.png");
    private static final ResourceLocation MAP_GEMS_TEX = new ResourceLocation("notenoughfakepixel:crystalhollows/map_gems.png");

    private static final ResourceLocation PLAYER_ARROW_TEX =
            new ResourceLocation("notenoughfakepixel:crystalhollows/map_arrow.png");
    private static final ResourceLocation PLAYER_ARROW_OUT_TEX =
            new ResourceLocation("notenoughfakepixel:crystalhollows/map_point.png");

    private static final int ARROW_SRC_W = 16;
    private static final int ARROW_SRC_H = 16;

    // Arrow size in screen (pixels)
    private static final int ARROW_DST = 12;
    private static final float EDGE_EPS = 1e-3f;

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (!Config.feature.mining.miningCrystalMap) return;
        if (!(event instanceof RenderGameOverlayEvent.Post)) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) return;
        if (TablistParser.currentLocation != Location.CRYSTAL_HOLLOWS) return;
        if (Config.feature.mining.miningOverlayHideOnChat && mc.currentScreen instanceof GuiChat) return;
        if (mc.thePlayer == null || mc.gameSettings.keyBindPlayerList.isKeyDown() || mc.gameSettings.showDebugInfo) return;

        ScaledResolution sr = new ScaledResolution(mc);

        final int x = (Config.feature.mining.crystalMapPos.getAbsX(sr, Config.feature.mining.miningCrystalMapWidth)) - (Config.feature.mining.miningCrystalMapWidth/2);
        final int y = (Config.feature.mining.crystalMapPos.getAbsY(sr, Config.feature.mining.miningCrystalMapWidth)) - (Config.feature.mining.miningCrystalMapWidth/2);

        final int w = Config.feature.mining.miningCrystalMapWidth;

        // Draw map
        drawMap(x, y, w);

        // Draw player position
        drawPlayer(x, y, w);

        mc.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawMap(int x, int y, int w) {
        ResourceLocation mapTex = Objects.equals(Config.feature.mining.miningCrystalMapType, "Gemstones") ? MAP_GEMS_TEX : MAP_ZONES_TEX;

        final float texW = 256f, texH = 256f;
        float u = 0f, v = 0f;
        final int srcW = 256, srcH = 256;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        try {
            mc.getTextureManager().bindTexture(mapTex);

            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            drawScaledCustomSizeModalRect(x, y, u, v, srcW, srcH, w, w, texW, texH);

        } catch (Exception ex) {
            Logger.logErrorConsole("Failed to render Crystal Hollows map overlay." + ex.getMessage());
        } finally {
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private void drawPlayer(int x, int y, int w) {
        final double px = mc.thePlayer.posX;
        final double pz = mc.thePlayer.posZ;
        
        final ResourceLocation arrowTex = getResourceLocation(px, pz);
        
        final float sx = worldToScreenX(px, x, w);
        final float sy = worldToScreenY(pz, y, w);

        final float yaw = mc.thePlayer.rotationYaw;
        final float angle = 180f - yaw;

        final float centerNudge = (ARROW_DST & 1) != 0 ? 0.5f : 0.0f;
        final float half = ARROW_DST * 0.5f;

        mc.getTextureManager().bindTexture(arrowTex);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        GlStateManager.translate(sx + centerNudge, sy + centerNudge, 0f);
        GlStateManager.rotate(-angle, 0f, 0f, 1f);

        GlStateManager.translate(-half, -half, 0f);

        Gui.drawScaledCustomSizeModalRect(
                0, 0,
                0f, 0f,
                ARROW_SRC_W, ARROW_SRC_H,
                ARROW_DST, ARROW_DST,
                ARROW_SRC_W, ARROW_SRC_H
        );


        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static @NotNull ResourceLocation getResourceLocation(double px, double pz) {
        final float tXraw = (float)((px - WORLD_MIN_X) / (WORLD_MAX_X - WORLD_MIN_X));
        final float tZraw = (float)((pz - WORLD_MIN_Z) / (WORLD_MAX_Z - WORLD_MIN_Z));
        final boolean isOut = (tXraw < 0f || tXraw > 1f || tZraw < 0f || tZraw > 1f);
        final boolean atEdge = !isOut && (
                tXraw <= EDGE_EPS || tXraw >= 1f - EDGE_EPS ||
                        tZraw <= EDGE_EPS || tZraw >= 1f - EDGE_EPS
        );

        return (isOut) ? PLAYER_ARROW_OUT_TEX : PLAYER_ARROW_TEX;
    }

    private static final double WORLD_MIN_X = 202;
    private static final double WORLD_MIN_Z = 202;
    private static final double WORLD_MAX_X = 823;
    private static final double WORLD_MAX_Z = 823;

    private static float worldToScreenX(double worldX, float hudX, float hudW) {
        float t = (float)((worldX - WORLD_MIN_X) / (WORLD_MAX_X - WORLD_MIN_X));
        if (t < 0) t = 0; else if (t > 1) t = 1;
        return hudX + t * hudW;
    }

    private static float worldToScreenY(double worldZ, float hudY, float hudH) {
        float t = (float)((worldZ - WORLD_MIN_Z) / (WORLD_MAX_Z - WORLD_MIN_Z));
        if (t < 0) t = 0; else if (t > 1) t = 1;
        return hudY + t * hudH;
    }

}
