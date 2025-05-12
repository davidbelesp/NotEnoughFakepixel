package org.ginafro.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.SoundUtils;
import org.ginafro.notenoughfakepixel.variables.MobDisplayTypes;

import java.awt.*;

@RegisterEvents
public class MiscDungFeatures {

    private final Minecraft mc = Minecraft.getMinecraft();

    private static String displayText = "";
    private static long endTime = 0;

    @SubscribeEvent(receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

        if (!ScoreboardUtils.currentLocation.isDungeon()) return;

        if (message.startsWith("[BOSS] The Watcher: That will be enough for now.")) {
            if (NotEnoughFakepixel.feature.dungeons.dungeonsBloodReady) {
                showCustomOverlay(EnumChatFormatting.RED + "BLOOD READY!", 2000);
                if (mc.theWorld != null) {
                    SoundUtils.playSound(mc.thePlayer.getPosition(), "note.pling", 2.0F, 1.0F);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc Blood Ready!");
                }
            }
        }
        if (message.startsWith("A Spirit Bear has appeared!")) {
            if (mc.theWorld != null && NotEnoughFakepixel.feature.dungeons.dungeonsSpiritBow) {
                SoundUtils.playSound(mc.thePlayer.getPosition(), "mob.enderdragon.growl", 2.0F, 1.0F);
            }
        }
    }

    private void showCustomOverlay(String text, int durationMillis) {
        displayText = text;
        endTime = System.currentTimeMillis() + durationMillis;
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (System.currentTimeMillis() > endTime) return;

        FontRenderer fr = mc.fontRendererObj;

        int screenWidth = event.resolution.getScaledWidth();
        int screenHeight = event.resolution.getScaledHeight();

        GlStateManager.pushMatrix();
        GlStateManager.scale(4.0F, 4.0F, 4.0F);
        int textWidth = fr.getStringWidth(displayText);
        int x = (screenWidth / 8) - (textWidth / 2);
        int y = (screenHeight / 8) - 10;
        fr.drawStringWithShadow(displayText, x, y, 0xFF5555);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null) {
            displayText = "";
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        WorldClient world = Minecraft.getMinecraft().theWorld;

        if (!ScoreboardUtils.currentLocation.isDungeon()) return;

        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof EntityArmorStand) {
                EntityArmorStand armorStand = (EntityArmorStand) entity;
                if (armorStand.getName().contains("Spirit Bow")) {
                    if (!NotEnoughFakepixel.feature.dungeons.dungeonsSpiritBow) return;
                    RenderUtils.draw3DLine(new Vec3(entity.posX,entity.posY+0.5,entity.posZ),
                            Minecraft.getMinecraft().thePlayer.getPositionEyes(event.partialTicks),
                            Color.RED,
                            8,
                            true,
                            event.partialTicks
                    );
                }
            }
            if (entity instanceof EntityWither) {
                String name = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getName());
                if ((name.equals("Maxor") || name.equals("Storm") || name.equals("Goldor") || name.equals("Necron"))) {
                    if (!NotEnoughFakepixel.feature.dungeons.dungeonsWithersBox) return;

                    Color color = ColorUtils.getColor(NotEnoughFakepixel.feature.dungeons.dungeonsWithersBoxColor);
                    GlStateManager.disableDepth();
                    GlStateManager.disableCull();
                    RenderUtils.renderEntityHitbox(
                            entity,
                            event.partialTicks,
                            color,
                            MobDisplayTypes.WITHER
                    );
                    GlStateManager.enableDepth();
                    GlStateManager.enableCull();
                }
            }
        }
    }
}

