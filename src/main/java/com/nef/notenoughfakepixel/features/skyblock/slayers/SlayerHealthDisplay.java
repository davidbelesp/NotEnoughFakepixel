package com.nef.notenoughfakepixel.features.skyblock.slayers;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

@RegisterEvents
public class SlayerHealthDisplay {
    private final Minecraft mc = Minecraft.getMinecraft();
    private String displayText = "";
    private boolean isBoss = false;
    private final Position position; // Added position field

    private int ticks = 0;

    public SlayerHealthDisplay() {
        this.position = Config.feature.slayer.slayerBossHPPos; // Initialize position
    }

    public static final String[] SLAYER_BOSSES = {
            "Revenant Horror",
            "Atoned Horror",
            "Sven Packmaster",
            "Tarantula Broodfather",
            "Voidgloom Seraph",
            "Inferno Demonlord"
    };

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side != net.minecraftforge.fml.relauncher.Side.CLIENT || mc.theWorld == null) {
            return;
        }

        ticks++;
        if (ticks % 10 != 0) return;

        if (Config.feature.slayer.slayerBossHP) {
            List<String> sidebarLines = ScoreboardUtils.getScoreboardLines();
            isBoss = SkyblockData.isBossActive();

            // Find closest Slayer boss
            if (isBoss && mc.thePlayer != null) {
                List<Entity> entities = mc.theWorld.getLoadedEntityList();
                Entity closestBoss = null;
                double closestDistance = Double.MAX_VALUE;
                String bossNameFound = "";

                for (Entity entity : entities) {
                    if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
                        String entityName = entity.getCustomNameTag();
                        for (String bossName : SLAYER_BOSSES) {
                            if (entityName.contains(bossName)) {
                                double distance = mc.thePlayer.getDistanceToEntity(entity);
                                if (distance < closestDistance) {
                                    closestDistance = distance;
                                    closestBoss = entity;
                                    bossNameFound = entityName;
                                }
                                break;
                            }
                        }
                    }
                }
                displayText = (closestBoss != null) ? bossNameFound : "";
            } else {
                displayText = "";
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || displayText.isEmpty() || !isBoss) {
            return;
        }

        ScaledResolution resolution = event.resolution;
        FontRenderer fr = mc.fontRendererObj;
        float scale = 2.0F;

        GL11.glPushMatrix();

        GL11.glScalef(scale, scale, scale);
        int textWidth = fr.getStringWidth(displayText);
        int textHeight = fr.FONT_HEIGHT;

        int x = position.getAbsX(resolution, textWidth) / (int) scale;
        int y = position.getAbsY(resolution, textHeight) / (int) scale;

        fr.drawStringWithShadow(displayText, x, y, 0xFF5555);

        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        displayText = "";
        isBoss = false;
    }
}