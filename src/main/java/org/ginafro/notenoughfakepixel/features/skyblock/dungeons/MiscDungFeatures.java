package org.ginafro.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.RenderEntityModelEvent;
import org.ginafro.notenoughfakepixel.utils.*;
import org.ginafro.notenoughfakepixel.variables.MobDisplayTypes;

import java.awt.*;

@RegisterEvents
public class MiscDungFeatures {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent(receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

        if (!TablistParser.currentLocation.isDungeon()) return;

        if (message.startsWith("[BOSS] The Watcher: That will be enough for now.")) {
            if (Config.feature.dungeons.dungeonsBloodReady) {
                TitleUtils.showTitle(EnumChatFormatting.RED + "BLOOD READY!", 2000);
                if (mc.theWorld != null) {
                    SoundUtils.playSound(mc.thePlayer.getPosition(), "note.pling", 2.0F, 1.0F);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc Blood Ready!");
                }
            }
        }
        if (message.startsWith("A Spirit Bear has appeared!")) {
            if (mc.theWorld != null && Config.feature.dungeons.dungeonsSpiritBow) {
                SoundUtils.playSound(mc.thePlayer.getPosition(), "mob.enderdragon.growl", 2.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        WorldClient world = Minecraft.getMinecraft().theWorld;

        if (!TablistParser.currentLocation.isDungeon()) return;

        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof EntityArmorStand) {
                EntityArmorStand armorStand = (EntityArmorStand) entity;
                if (armorStand.getName().contains("Spirit Bow")) {
                    if (!Config.feature.dungeons.dungeonsSpiritBow) return;
                    RenderUtils.draw3DLine(new Vec3(entity.posX, entity.posY + 0.5, entity.posZ),
                            Minecraft.getMinecraft().thePlayer.getPositionEyes(event.partialTicks),
                            Color.RED,
                            8,
                            true,
                            event.partialTicks
                    );
                }
            }
        }
    }
    @SubscribeEvent
    public void render(RenderEntityModelEvent e) {
        EntityLivingBase entity = e.getEntity();
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (entity instanceof EntityWither) {
            String name = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getName());
            if ((name.equals("Maxor") || name.equals("Storm") || name.equals("Goldor") || name.equals("Necron"))) {
                if (!Config.feature.dungeons.dungeonsWithersBox) return;

                Color color = ColorUtils.getColor(Config.feature.dungeons.dungeonsWithersBoxColor);
                GlStateManager.disableDepth();
                GlStateManager.disableCull();
                if (Configuration.isPojav()) {
                    EntityHighlightUtils.renderEntityOutline(e, color);
                } else {
                    OutlineUtils.outlineEntity(e, 4.0f, color, true);
                }
                GlStateManager.enableDepth();
                GlStateManager.enableCull();
            }
        }
    }
}

