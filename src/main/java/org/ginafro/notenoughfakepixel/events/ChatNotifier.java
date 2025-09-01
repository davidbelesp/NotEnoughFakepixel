package org.ginafro.notenoughfakepixel.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public abstract class ChatNotifier {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.message == null) return;
        String message = event.message.getFormattedText();
        if (message.contains(getMessage())) {
            if (shouldNotify()) notifyUser();
        }
    }

    private void notifyUser() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;
        player.playSound("random.orb", 1.0f, 1.0f);
        Minecraft.getMinecraft().ingameGUI.displayTitle(EnumChatFormatting.GREEN + notifyMessage(), "", 2, 70, 2);
    }

    public abstract boolean shouldNotify();
    public abstract String getMessage();
    public abstract String notifyMessage();
    public EnumChatFormatting getColor() {
        return EnumChatFormatting.GREEN;
    }

}
