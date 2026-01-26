package com.nef.notenoughfakepixel.features.skyblock.crimson;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ChatUtils;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class BossNotifier {
    private final static String countdownSound = "random.orb";
    private final static String titleSoundMageOutlawReady = "mob.wither.spawn";
    private final static String titleTextBossReady = " Ready";
    private final boolean[] bladesoulScheduled = new boolean[6];
    private final boolean[] mageOutlawScheduled = new boolean[6];
    @Getter
    private static final boolean[] ashfangScheduled = new boolean[6];
    private static final boolean[] barbarianDukeXScheduled = new boolean[6];

    private long mageOutlawLastKill = -1;
    private long mageOutlawReady = -1;
    private long ashfangLastKill = -1;
    private long ashfangReady = -1;
    private long barbarianDukeXLastKill = -1;
    private long barbarianDukeXReady = -1;
    private long bladesoulLastKill = -1;
    private long bladesoulReady = -1;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (Crimson.checkEssentials()) return;
        if (Config.feature.crimson.crimsonBladesoulNotifier) {
            playCountdown("Bladesoul", bladesoulReady, bladesoulLastKill, bladesoulScheduled);
        }
        if (Config.feature.crimson.crimsonMageOutlawNotifier) {
            playCountdown("Mage Outlaw", mageOutlawReady, mageOutlawLastKill, mageOutlawScheduled);
        }
        if (Config.feature.crimson.crimsonAshfangNotifier) {
            playCountdown("Ashfang", ashfangReady, ashfangLastKill, ashfangScheduled);
        }
        if (Config.feature.crimson.crimsonBarbarianDukeXNotifier) {
            playCountdown("Barbarian Duke X", barbarianDukeXReady, barbarianDukeXLastKill, barbarianDukeXScheduled);
        }
    }

    @SubscribeEvent
    public void onChat(@NotNull ClientChatReceivedEvent e) {
        if (Crimson.checkEssentials()) return;
        int spawnBossMilliseconds = 125000;

        if (Config.feature.crimson.crimsonBladesoulNotifier) {
            Matcher matcher = Pattern.compile("BLADESOUL DOWN!").matcher(e.message.getUnformattedText());
            if (matcher.find()) {
                bladesoulLastKill = System.currentTimeMillis();
                bladesoulReady = bladesoulLastKill + spawnBossMilliseconds;
                Arrays.fill(bladesoulScheduled, true);
                return;
            }
        }

        if (Config.feature.crimson.crimsonMageOutlawNotifier) {
            Matcher matcher = Pattern.compile("MAGE OUTLAW DOWN!").matcher(e.message.getUnformattedText());
            if (matcher.find()) {
                mageOutlawLastKill = System.currentTimeMillis();
                mageOutlawReady = mageOutlawLastKill + spawnBossMilliseconds;
                Arrays.fill(mageOutlawScheduled, true);
                return;
            }
        }

        if (Config.feature.crimson.crimsonAshfangNotifier) {
            Matcher matcher = Pattern.compile("ASHFANG DOWN!").matcher(e.message.getUnformattedText());
            if (matcher.find()) {
                ashfangLastKill = System.currentTimeMillis();
                ashfangReady = ashfangLastKill + spawnBossMilliseconds;
                Arrays.fill(ashfangScheduled, true);
                return;
            }
        }

        if (Config.feature.crimson.crimsonBarbarianDukeXNotifier) {
            Matcher matcher = Pattern.compile("BARBARIAN DUKE X DOWN!").matcher(e.message.getUnformattedText());
            if (matcher.find()) {
                barbarianDukeXLastKill = System.currentTimeMillis();
                barbarianDukeXReady = barbarianDukeXLastKill + spawnBossMilliseconds;
                Arrays.fill(barbarianDukeXScheduled, true);
            }
        }
    }

    @SubscribeEvent()
    public void onWorldUnload(WorldEvent.Unload event) {
        if (Crimson.checkEssentials()) return;
        if (Config.feature.crimson.crimsonBladesoulNotifier) {
            Arrays.fill(bladesoulScheduled, false);
            bladesoulReady = -1;
            bladesoulLastKill = -1;
        }
        if (Config.feature.crimson.crimsonMageOutlawNotifier) {
            Arrays.fill(mageOutlawScheduled, false);
            mageOutlawReady = -1;
            mageOutlawLastKill = -1;
        }
        if (Config.feature.crimson.crimsonAshfangNotifier) {
            Arrays.fill(ashfangScheduled, false);
            ashfangReady = -1;
            ashfangLastKill = -1;
        }
        if (Config.feature.crimson.crimsonBarbarianDukeXNotifier) {
            Arrays.fill(barbarianDukeXScheduled, false);
            barbarianDukeXReady = -1;
            barbarianDukeXLastKill = -1;
        }
    }

    private void playCountdown(String boss, long timeBossReady, long bossLastKill, boolean[] bossScheduled) {
        if (timeBossReady != -1 && bossLastKill != -1) {
            // timer passed
            if (bossScheduled[0] && System.currentTimeMillis() > timeBossReady) {
                notifyTitle(boss);
                bossScheduled[0] = false;
            } else if (bossScheduled[1] && System.currentTimeMillis() > timeBossReady - 123000) {
                notifyChat(boss, 120);
                bossScheduled[1] = false;
            } else if (bossScheduled[2] && System.currentTimeMillis() > timeBossReady - 60000) {
                notifyChat(boss, 60);
                bossScheduled[2] = false;
            } else if (bossScheduled[3] && System.currentTimeMillis() > timeBossReady - 30000) {
                notifyChat(boss, 30);
                bossScheduled[3] = false;
            } else if (bossScheduled[4] && System.currentTimeMillis() > timeBossReady - 10000) {
                notifyChat(boss, 10);
                bossScheduled[4] = false;
            } else if (bossScheduled[5] && System.currentTimeMillis() > timeBossReady - 5000) {
                notifyChat(boss, 5);
                bossScheduled[5] = false;
            }
        }
    }

    private void notifyChat(String boss, int seconds) {
        String joinText = " spawning in ";
        if (seconds == 120) {
            ChatUtils.notifyChat(EnumChatFormatting.YELLOW + boss + joinText + "2 minutes");
        } else if (seconds == 60) {
            ChatUtils.notifyChat(EnumChatFormatting.YELLOW + boss + joinText + "1 minute");
        } else {
            ChatUtils.notifyChat(EnumChatFormatting.YELLOW + boss + joinText + seconds + " seconds");
        }
        SoundUtils.playSound(new int[]{Minecraft.getMinecraft().thePlayer.getPosition().getX(),
                Minecraft.getMinecraft().thePlayer.getPosition().getY(),
                Minecraft.getMinecraft().thePlayer.getPosition().getZ()}, countdownSound, 2.0f, 1.0f
        );
    }

    private void notifyTitle(String boss) {
        Minecraft.getMinecraft().ingameGUI.displayTitle(EnumChatFormatting.GOLD + boss + titleTextBossReady, "", 2, 25, 2);
        SoundUtils.playSound(new int[]{Minecraft.getMinecraft().thePlayer.getPosition().getX(),
                Minecraft.getMinecraft().thePlayer.getPosition().getY(),
                Minecraft.getMinecraft().thePlayer.getPosition().getZ()}, titleSoundMageOutlawReady, 2.0f, 1.0f);
        switch (boss) {
            case "Bladesoul":
                bladesoulScheduled[0] = false;
                break;
            case "Mage Outlaw":
                mageOutlawScheduled[0] = false;
                break;
            case "Ashfang":
                ashfangScheduled[0] = false;
                break;
            case "Barbarian Duke X":
                barbarianDukeXScheduled[0] = false;
                break;
        }
    }

    private static void playCountdownSound(int times) {
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(4);
        for (int i = 0; i < times; i++) {
            exec.schedule(new Runnable() {
                public void run() {
                    SoundUtils.playSound(Minecraft.getMinecraft().thePlayer.getPosition(), countdownSound, 2.0f, 1.0f + (float) (times * 2) / 10);
                }
            }, i, TimeUnit.SECONDS);
        }
    }

}