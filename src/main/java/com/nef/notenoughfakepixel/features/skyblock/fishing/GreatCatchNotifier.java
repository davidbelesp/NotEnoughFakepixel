package com.nef.notenoughfakepixel.features.skyblock.fishing;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@RegisterEvents
public class GreatCatchNotifier {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onChat(@NotNull ClientChatReceivedEvent e) {
        if (!Config.feature.fishing.fishingLegendaryCreatures) return;

        String message = e.message.getUnformattedText();
        String title = null;

        if (message.contains("The Sea Emperor arises")) {
            title = "The Sea Emperor";
        } else if (message.contains("The Water Hydra has come")) {
            title = "Water Hydra";
        } else if (message.contains("An Abyssal Miner breaks")) {
            title = "Abyssal Miner";
        } else if (message.contains("The spirit of a long lost Phantom ")) {
            title = "Phantom Fisher";
        } else if (message.contains("This can't be! The manifestation of death himself!")) {
            title = "Grim Reaper";
        } else if (message.contains("WOAH! A Plhlegblast appeared.")) {
            title = "Plhlegblast";
        } else if (message.contains("What is this creature!?")) {
            title = "Yeti";
        } else if (message.contains("A Reindrake forms from the depths.")) {
            title = "Reindrake";
        } else if (message.contains("Hide no longer, a Great White Shark")) {
            title = "Great White Shark";
        } else if (message.contains("You hear a massive rumble as")) {
            title = "Thunder";
        } else if (message.contains("You have angered a legendary creature.")) {
            title = "Lord Jawbus";
        }

        if (title != null) {
            mc.ingameGUI.displayTitle(EnumChatFormatting.GOLD + "Legendary Catch!", title, 2, 70, 2);
        }
    }

}
