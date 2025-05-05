package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.StringUtils;
import org.ginafro.notenoughfakepixel.utils.Utils;
import org.lwjgl.Sys;

import java.awt.*;

public class CustomBar {
    public int currentHealth = 1,maxHealth = 1;
    public int currentMana= 1,maxMana= 1;
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent e){
        if(e.type == RenderGameOverlayEvent.ElementType.HEALTH || e.type == RenderGameOverlayEvent.ElementType.ARMOR || e.type == RenderGameOverlayEvent.ElementType.FOOD){
            e.setCanceled(true);
            if(ScoreboardUtils.currentGamemode.isSkyblock() && NotEnoughFakepixel.feature.overlays.statOverlay) {
                renderCustomHealth(e.resolution);
                renderCustomMana(e.resolution);
            }
        }
    }


    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e){
        if(e.type != 2) return;
        String[] actionBar = e.message.getUnformattedText().split(" {5}");
        if(actionBar.length == 3){
            String health = StringUtils.cleanColor(actionBar[0]);
            if(health.endsWith("❤")){
                String[] healthValues = health.replace("❤","").split("/");
                currentHealth = Integer.parseInt(healthValues[0]);
                maxHealth = Integer.parseInt(healthValues[1]);
            }
            String mana = StringUtils.cleanColor(actionBar[2]);
            if(mana.contains("Mana")){
                int index = mana.indexOf("✎");
                String[] manaValues = mana.substring(0,index).split("/");
                currentMana = Integer.parseInt(manaValues[0]);
                maxMana = Integer.parseInt(manaValues[1]);
            }
        }
        if(e.isCancelable() && NotEnoughFakepixel.feature.overlays.statOverlay){
            e.setCanceled(true);
        }
    }

    private void renderCustomHealth(ScaledResolution sr) {
        int w = sr.getScaledWidth();
        int h = sr.getScaledHeight();

        int left = w / 2 - (int)((5.5 * 20) * Utils.getScale());
        int top = h - (int)(40 * Utils.getScale());
        float percentage = (float) currentHealth / maxHealth;

        int barWidth = (int)(Utils.getScale() * 75);
        int barHeight = (int)(5 * Utils.getScale());
        Gui.drawRect(left, top, left + barWidth, top + barHeight, new Color(0, 0, 0, 102).getRGB()); // Background
        Gui.drawRect(left, top, left + (int)(barWidth * percentage), top + barHeight, new Color(255, 0, 0, 180).getRGB()); // Foreground
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        int l = left + ((barWidth - fr.getStringWidth(currentHealth + "/" + maxHealth))/2);
        int t = top - fr.FONT_HEIGHT;
        fr.drawString(currentHealth + "/" + maxHealth ,l,t,new Color(255,0,0,180).getRGB());
    }


    private void renderCustomMana(ScaledResolution sr) {
            int w = sr.getScaledWidth();
            int h = sr.getScaledHeight();

            int left = w / 2 + (int)(40 * Utils.getScale());
            int top = h - (int)(40 * Utils.getScale());
            float percentage = (float) currentMana / maxMana;

            int barWidth = (int)(Utils.getScale() * 75);
            int barHeight = (int)(5 * Utils.getScale());
            Gui.drawRect(left, top, left + barWidth, top + barHeight, new Color(0, 0, 0, 102).getRGB());
            Gui.drawRect(left, top, left + (int)(barWidth * percentage), top + barHeight, new Color(51, 255, 255, 180).getRGB());
            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            int l = left + ((barWidth - fr.getStringWidth(currentMana + "/" + maxMana))/2);
            int t = top - fr.FONT_HEIGHT;
            fr.drawString(currentMana + "/" + maxMana ,l,t,new Color(51,255,255,180).getRGB());
        }

}
