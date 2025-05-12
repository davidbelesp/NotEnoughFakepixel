package org.ginafro.notenoughfakepixel.features.skyblock.slayers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.Gamemode;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;

@RegisterEvents
public class AutoOpenMaddox {

    static String maddoxCommand = "/openmaddox";
    static long lastMaddoxTime = 0;

    @SubscribeEvent
    public void onChatRecieve(ClientChatReceivedEvent e){
        if(NotEnoughFakepixel.feature.slayer.slayerMaddoxCalling == 2) return;
        if(ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK) return;
        List<IChatComponent> siblings = e.message.getSiblings();
        for(IChatComponent sibling : siblings){
            if(sibling.getUnformattedText().contains("[OPEN MENU]")) {
                lastMaddoxTime = System.currentTimeMillis() / 1000;
                if(NotEnoughFakepixel.feature.slayer.slayerMaddoxCalling == 0) Minecraft.getMinecraft().thePlayer.sendChatMessage(maddoxCommand);
                else if(NotEnoughFakepixel.feature.slayer.slayerMaddoxCalling == 1) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "Open chat then click anywhere on-screen to open Maddox"));
            }
        }
    }

    @SubscribeEvent
    public void onDraw(GuiScreenEvent.DrawScreenEvent e){
        if(System.currentTimeMillis() / 1000 - lastMaddoxTime < 10){
            GuiScreen.drawRect(0,0,Minecraft.getMinecraft().displayWidth,Minecraft.getMinecraft().displayHeight,new Color(0,0,0,56).getRGB());
            String m = "Click to Open Maddox Batphone";
            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            fr.drawString(m,Minecraft.getMinecraft().displayWidth - (fr.getStringWidth(m)/2),Minecraft.getMinecraft().displayHeight / 2 , -1);

        }
    }

    @SubscribeEvent
    public void onMouseInputPost(GuiScreenEvent.MouseInputEvent.Post event) {
        if(ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK) return;
        if (Mouse.getEventButton() == 0 && event.gui instanceof GuiChat) {
            if(NotEnoughFakepixel.feature.slayer.slayerMaddoxCalling == 1 && System.currentTimeMillis() / 1000 - lastMaddoxTime < 10) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage(maddoxCommand);
            }
        }
    }

}
