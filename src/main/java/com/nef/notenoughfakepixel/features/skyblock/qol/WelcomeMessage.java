package com.nef.notenoughfakepixel.features.skyblock.qol;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ListUtils;
import com.nef.notenoughfakepixel.utils.RandomUtils;

import java.util.List;
import java.util.Random;

@RegisterEvents
public class WelcomeMessage {

    private final boolean notified = false;
    private final Random random = RandomUtils.getInstance();
    private final String initialString = "[NEF] ";

    private static final List<String> arrayWelcomeMessages = ListUtils.of(
            "Did you know that, 9 out of 10 experts recommend NEF? Number 10 plays hypixel (shame)",
            "Did you know that, 9 out of 10 bugs reported are related to pojav?",
            "Fun Fact: 9 out of 10 Fakepixel players recommend NEF. The 10th player fell into the void",
            "Alert! Fakepixel detected a skill issue. Please report in NEF's Discord immediately",
            "Watch Out: Every time you miss a dungeon puzzle with NEF, a villager sighs in disappointment",
            "NEF gives you dungeon powers, better don't die at Goldor's",
            "Attention: Your free trial of skill has expired. Please renew with NEF",
            "Did you know that statistically, you’re worse at Minecraft than you think?",
            "NEF detected. Injecting 100% more gaming skill… please wait… process failed",
            "NEF makes you better at Minecraft. Too bad it can’t fix your hairline",
            "It is said that in the past, people could play Fakepixel without NEF",
            "Did you know that, NEF is younger than you? But maybe... more useful",
            "Welcome to Fakepixel! Did you know NEF is the secret ingredient to your gaming happiness?",
            "Welcome back to Fakepixel, where NEF makes everything better!",
            "Did you know NEF was the original inspiration for the world's best gaming experiences?",
            "Once upon a time, players tried Fakepixel without NEF... and the world was less .fun",
            "The future of Fakepixel is NEF. Don’t worry, you’re on the right side of history",
            "We know you love NEF... and NEF also loves you!",
            "It is said that NEF was created for the legends of Fakepixel… and you're one of them!",
            "Once upon a time, one brave fighter played FakePixel without NEF. Wait, that's you",
            "Did you know that Fakepixel does have other gamemodes except Skyblock?",
            "@Iru98X wishes you a happy nef-gaming!",
            "Fun Fact: It took @_Whispering nearly 7 months to go from Helper to Moderator on Fakepixel",
            "Did you know that Samsung everyday day keeps Apple away? (@ClassyCoder)",
            "It always seems impossible until it’s done (@MG)",
            "It took @MG 3-days to figure out how to cancel chest gui rendering",
            "Who is that behind you?",
            "If you want to be beta-tester, remember that first NEF beta versions wiped some necron armors... scary!"
    );

    String epicString = "You discovered an epic message that gives you 200% extra penetration for the rest of your day!";
    String legendaryString = "You discovered a legendary message that will make you not bald for the rest of your life!";

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
//        if (!notified && event.message.getUnformattedText().equals("+10 Magic Find Bonus!")) {
//            notified = true;
//            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
//            String welcomeMessage = "Welcome " + player.getName() + "! ";
//            String randomMessage = arrayWelcomeMessages.get(random.nextInt(arrayWelcomeMessages.size()));
//            int numberSpecial = random.nextInt(1001);
//            if (numberSpecial == 1000) {
//                randomMessage = EnumChatFormatting.GOLD + "(LEGENDARY) " + EnumChatFormatting.BLUE + legendaryString;
//            } else if (numberSpecial > 900) {
//                randomMessage = EnumChatFormatting.DARK_PURPLE + "(EPIC) " + EnumChatFormatting.BLUE + epicString;
//            }
//            if (randomMessage.startsWith("Welcome")) welcomeMessage = "";
//            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
//            String finalRandomMessage = randomMessage;
//            String finalWelcomeMessage = welcomeMessage;
//            exec.schedule(() -> {
//                ChatUtils.notifyChat(EnumChatFormatting.BOLD.toString() + EnumChatFormatting.BLUE + initialString + finalWelcomeMessage + finalRandomMessage);
//                SoundUtils.playSound(new int[]{player.getPosition().getX(),
//                        player.getPosition().getY(),
//                        player.getPosition().getZ()}, "note.pling", 2.0f, 1.0f);
//            }, 1000, TimeUnit.MILLISECONDS);
//            exec.schedule(() -> SoundUtils.playSound(player.getPosition(), "note.pling", 2.0f, 1.6f), 1300, TimeUnit.MILLISECONDS);
//        }
    }
}
