package com.nef.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;
import com.nef.notenoughfakepixel.utils.StringUtils;
import com.nef.notenoughfakepixel.variables.Gamemode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RegisterEvents
public class VisualCooldowns {

    public HashMap<ItemStack, Integer> cooldowns = new HashMap<>();
    public long lastUpdatedtime = 0;
    public long currentTime = 0;

    @SubscribeEvent
    public void onDraw(RenderGameOverlayEvent.Text e) {
        if (!Config.feature.qol.qolVisualCooldowns) return;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int xPos = sr.getScaledWidth() - 20;
        int yPos = 16;
        for (ItemStack stack : cooldowns.keySet()) {
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos);
            Minecraft.getMinecraft().fontRendererObj.drawString(String.valueOf(cooldowns.get(stack)), xPos - 16, yPos + 3, -1);
            if (stack == null) return;
            int x = xPos + 12 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(stack.getDisplayName());
            Minecraft.getMinecraft().fontRendererObj.drawString(net.minecraft.util.StringUtils.stripControlCodes(stack.getDisplayName()), x, yPos + 17, -1);
            yPos += 20;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdatedtime > 1000) {
            lastUpdatedtime = System.currentTimeMillis();
            Iterator<Map.Entry<ItemStack, Integer>> iterator = cooldowns.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ItemStack, Integer> entry = iterator.next();
                int newCooldown = entry.getValue() - 1;
                if (newCooldown <= 0) {
                    iterator.remove();
                } else {
                    entry.setValue(newCooldown);
                }
            }
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK) return;
        if (e.message.getUnformattedText().startsWith("This ability is on cooldown for")) {
            int cooldownTime = 0;
            for (String s : e.message.getUnformattedText().split(" ")) {
                if (StringUtils.isNumeric(s)) {
                    cooldownTime = Integer.parseInt(s);
                    break;
                }
            }
            ItemStack item = Minecraft.getMinecraft().thePlayer.getHeldItem();
            //System.out.println(item);
            for (ItemStack stack : cooldowns.keySet()) {
                if (stack.getDisplayName().equals(item.getDisplayName())) {
                    cooldowns.replace(item, cooldownTime);
                    return;
                }
            }
            cooldowns.put(item, cooldownTime);
            //System.out.println(cooldowns);
        }
    }

}
