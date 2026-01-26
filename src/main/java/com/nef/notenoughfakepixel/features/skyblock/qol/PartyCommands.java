package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.PacketReadEvent;
import com.nef.notenoughfakepixel.utils.RandomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class PartyCommands {

    private static final Pattern partyRegex = Pattern.compile("^Party > (?:\\[[^]]*])?\\s*(\\w{1,16}):\\s*(.+)$");

    private static final String[] responses = {
            "It is certain", "It is decidedly so", "Without a doubt", "Yes definitely",
            "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes",
            "Signs point to yes", "Reply hazy try again", "Ask again later", "Better not tell you now",
            "Cannot predict now", "Concentrate and ask again", "Don't count on it", "My reply is no",
            "My sources say no", "Outlook not so good", "Very doubtful"
    };

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = RandomUtils.getInstance();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private long prevTime = 0L;
    public double averageTps = 20.0;

    @SubscribeEvent
    public void onPacket(PacketReadEvent event) {
        if (event.packet instanceof S03PacketTimeUpdate) {
            long currentTime = System.currentTimeMillis();
            if (prevTime != 0L) {
                double tps = 20000.0 / (currentTime - prevTime + 1);
                averageTps = Math.max(0.0, Math.min(20.0, tps));
            }
            prevTime = currentTime;
        }
    }

    @SubscribeEvent
    public void onClientChat(ClientChatReceivedEvent event) {
        String message = StringUtils.stripControlCodes(event.message.getFormattedText());

        Matcher matcher = partyRegex.matcher(message);
        if (!matcher.matches()) return;

        String content = matcher.group(2);
        if (content == null) return;

        String response = null;
        switch (content.toLowerCase()) {
            case "!dice":
                response = String.valueOf(random.nextInt(6) + 1);
                break;
            case "!cf":
                response = random.nextBoolean() ? "heads" : "tails";
                break;
            case "!tps":
                response = "Current TPS: " + (int) averageTps;
                break;
        }

        if (response != null && mc.thePlayer != null) {
            final String finalResponse = response;
            scheduler.schedule(() -> {
                if (mc.thePlayer != null) {
                    mc.thePlayer.sendChatMessage("/pc " + finalResponse);
                }
            }, 1500, TimeUnit.MILLISECONDS);
        }
    }
}