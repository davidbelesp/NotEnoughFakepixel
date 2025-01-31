package org.ginafro.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.MobDisplayTypes;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreeWeirdos {

    private static String[] answers = {
            "The reward is not in my chest!",
            "At least one of them is lying, and the reward is not in ",
            "My chest doesn't have the reward we are all telling the truth.",
            "My chest has the reward and I'm telling the truth!",
            "The reward isn't in any of our chests.",
            "Both of them are telling the truth. Also, "
    };

    private static Pattern threeWeirdosPattern = Pattern.compile("(§r§e\\[NPC] §r§c)(.+)(§r§f:)(.+)");
    private static Pattern threeWeirdosSolved = Pattern.compile("§r§a§lPUZZLE SOLVED! (?<player>.+) §r§ewasn't fooled by three weirdos!(.+)");

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean foundResponse = false;
    private String correctName = "";

    @SubscribeEvent
    public void onChatRecieve(ClientChatReceivedEvent e){
       if (!Configuration.dungeonsThreeWeirdos) return;
       if (mc.thePlayer == null) return;
       if (!ScoreboardUtils.currentGamemode.isSkyblock());
       if (!ScoreboardUtils.currentLocation.isDungeon()) return;


       if (e.message.getFormattedText().startsWith("§c")) return;

       Matcher matcher = threeWeirdosPattern.matcher(e.message.getFormattedText());
       if (matcher.find()){
           String name = matcher.group(2);
           String message = matcher.group(4);
           for (String answer : answers) {
               if (message.contains(answer)) {
                   mc.thePlayer.addChatMessage(new ChatComponentText("§a[Riddle Solver] §r§c" + name + "§r§f has the blessing!"));
                   e.setCanceled(true);
                   foundResponse = true;
                   correctName = name;
                   return;
               }
           }
       } else if (threeWeirdosSolved.matcher(e.message.getFormattedText()).matches()){
           foundResponse = false;
           correctName = "";
       }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Configuration.dungeonsThreeWeirdos) return;
        if (mc.theWorld == null) return;
        if (!foundResponse) return;
        if (correctName.isEmpty()) return;
        highlightNpc(correctName, event.partialTicks);
    }

    public void highlightNpc(String correctName, float partialTicks){
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity.getDisplayName().getUnformattedText().contains(correctName)){
                RenderUtils.renderEntityHitbox(entity, partialTicks, new Color(90,255,90,198), MobDisplayTypes.NONE, false);
            }
        }
    }

    @SubscribeEvent()
    public void onWorldUnload(WorldEvent.Unload event) {
        correctName = "";
        foundResponse = false;
    }

}
