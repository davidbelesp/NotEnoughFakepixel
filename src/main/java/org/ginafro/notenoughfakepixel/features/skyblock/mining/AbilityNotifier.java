package org.ginafro.notenoughfakepixel.features.skyblock.mining;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.config.features.Mining;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.Location;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class AbilityNotifier {

    private static long lastUsed = -1;
    private static long canUse = -1;
    private static boolean notifyScheduled = false;

    private static final String READY_MESSAGE = EnumChatFormatting.GREEN + "Ready";


    @SubscribeEvent
    public void onChat(@NotNull ClientChatReceivedEvent e){
        if (checkEssentials()) return;

        Matcher matcher = Pattern.compile("You used your (.+) Pickaxe Ability!").matcher(e.message.getUnformattedText());
        if (matcher.find()) {
            lastUsed = System.currentTimeMillis();
            canUse = lastUsed + 120000;
            notifyScheduled = true;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e){
        if(checkEssentials()) return;
        if(!NotEnoughFakepixel.feature.mining.miningAbilityNotifier) return;
        if( canUse == -1 || lastUsed == -1) return;
        if( canUse > System.currentTimeMillis() || !notifyScheduled) return;

        reminder();
    }

    // This will show a message when 0 seconds are left
    public static void reminder(){
        Minecraft.getMinecraft().ingameGUI.displayTitle(EnumChatFormatting.GOLD + "Mining Ability Ready", "", 2, 70, 2);
        notifyScheduled = false;
    }

    public static String cdSecondsRemaining(){
        if(lastUsed == -1 || canUse == -1) return READY_MESSAGE;
        if(canUse < System.currentTimeMillis()) {
            return READY_MESSAGE;
        }
        return EnumChatFormatting.RED + String.valueOf((canUse - System.currentTimeMillis()) / 1000) + "s";
    }

    private static boolean checkEssentials(){
        return  (Minecraft.getMinecraft().thePlayer == null) ||
                (!ScoreboardUtils.currentGamemode.isSkyblock()) ||
                (!ScoreboardUtils.currentLocation.equals(Location.DWARVEN));
    }


}
