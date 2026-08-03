package com.nef.notenoughfakepixel.features.skyblock.garden;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.RenderUtils;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RegisterEvents
public class PestEsp {

    private static final List<String> PEST_NAMES = Arrays.asList(
            "Fly",
            "Cricket",
            "Locust",
            "Rat",
            "Mosquito",
            "Earthworm",
            "Mite",
            "Moth",
            "Slug",
            "Beetle",
            "Firefly",
            "Dragonfly",
            "Praying Mantis",
            "Field Mouse"
    );

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (SkyblockData.getCurrentLocation() != Location.GARDEN) return;
        if (Config.feature == null || Config.feature.garden == null
                || !Config.feature.garden.enable || !Config.feature.garden.pestEsp.enabled) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        Color boxColor = ColorUtils.getColor(Config.feature.garden.pestEsp.boxColor);
        Color tracerColor = ColorUtils.getColor(Config.feature.garden.pestEsp.tracerColor);
        float boxWidth = Math.max(1.0F, Config.feature.garden.pestEsp.boxLineWidth);
        int tracerWidth = Math.max(1, Math.round(Config.feature.garden.pestEsp.tracerLineWidth));

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!isPest(entity)) continue;

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks;
            AxisAlignedBB box = new AxisAlignedBB(x - 0.5D, y - 1.0D, z - 0.5D, x + 0.5D, y, z + 0.5D);

            renderBox(box, boxColor, boxWidth, event.partialTicks,
                    Config.feature.garden.pestEsp.boxThroughWalls);

            if (Config.feature.garden.pestEsp.tracers) {
                Entity viewer = mc.getRenderViewEntity();
                if (viewer != null) {
                    double eyeX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * event.partialTicks;
                    double eyeY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * event.partialTicks
                            + viewer.getEyeHeight();
                    double eyeZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * event.partialTicks;
                    RenderUtils.draw3DLine(
                            x, y - 0.5D, z,
                            eyeX, eyeY, eyeZ,
                            tracerColor, tracerWidth,
                            !Config.feature.garden.pestEsp.tracerThroughWalls,
                            event.partialTicks);
                }
            }
        }
    }

    private static boolean isPest(Entity entity) {
        if (entity == null || entity instanceof EntityPlayer) return false;

        String displayName = entity.getDisplayName() == null
                ? ""
                : entity.getDisplayName().getUnformattedText();
        String entityName = entity.getName() == null ? "" : entity.getName();
        String searchableText = (displayName + " " + entityName).toLowerCase(Locale.ROOT);

        for (String pestName : PEST_NAMES) {
            if (searchableText.contains(pestName.toLowerCase(Locale.ROOT))) return true;
        }
        return false;
    }

    private static void renderBox(AxisAlignedBB box, Color color, float lineWidth,
                                  float partialTicks, boolean throughWalls) {
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        if (throughWalls) GlStateManager.disableDepth();
        RenderUtils.drawOutlinedBoundingBox(box, color, lineWidth, partialTicks);
        if (throughWalls && depthEnabled) GlStateManager.enableDepth();
    }
}
