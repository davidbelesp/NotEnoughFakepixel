    package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.score;

import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.TextHud;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import org.ginafro.notenoughfakepixel.utils.ChatUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.DungeonFloor;

import java.util.List;

public class ScoreOverlay extends TextHud {

    private static int chatDisplaySeconds = 40;
    private long readyTime = Long.MAX_VALUE;

    public ScoreOverlay() {
        super(true,
                0,
                128,
                1, true, true,
                4f, 2000, 1250,
                new OneColor(0, 0, 0, 150),
                false, 2,
                new OneColor(0, 0, 0, 127));
    }

    @Override
    protected boolean shouldShow() {
        if (!super.shouldShow()) return false;
        if (!DungeonManager.checkEssentials()) return false;
        return Configuration.dungeonsScoreOverlay;
    }

    @Override
    protected void getLines(List<String> lines, boolean example) {
        lines.add(getRankingDisplay());
        lines.add(getVirtualRankingDisplay());
        if (!Configuration.dungeonsMinimalScoreOverlay) {
          lines.add("");
          lines.add(getSkillDisplay());
          lines.add(getExplorationDisplay());
          lines.add(getSpeedDisplay());
          lines.add(getBonusDisplay());
          lines.add("");
          lines.add(getSecretDisplay()); 
        }
    }

    private String getRankingDisplay() {
        int totalScore = ScoreManager.getTotalScore();
        if (DungeonManager.isFinalStage() && ScoreManager.getExplorationClearScore() != 60) return EnumChatFormatting.RED + "UNKNOWN SCORE";
        //if (DungeonManager.isFinalStage() && ) totalScore = (int) (0.7 * ScoreManager.getTotalScore());
        String returnString = "\u00a77Total score: ";
        if (totalScore < 100) returnString = returnString + EnumChatFormatting.RED + totalScore + EnumChatFormatting.RED + " (D)";
        else if (totalScore < 160) returnString = returnString + EnumChatFormatting.RED + totalScore + EnumChatFormatting.BLUE + " (C)";
        else if (totalScore < 230) returnString = returnString + EnumChatFormatting.RED + totalScore + EnumChatFormatting.GREEN + " (B)";
        else if (totalScore < 269.5f) returnString = returnString + EnumChatFormatting.YELLOW + totalScore + EnumChatFormatting.LIGHT_PURPLE + " (A)";
        else if (totalScore < 300) returnString = returnString + EnumChatFormatting.YELLOW + totalScore + EnumChatFormatting.GOLD + " (S)";
        else returnString = returnString + EnumChatFormatting.GREEN + totalScore + EnumChatFormatting.GOLD + " (S+)";
        return returnString;
    }

    private String getVirtualRankingDisplay() {
        int virtualScore = ScoreManager.getSkillScore() + 60 + ScoreManager.getExplorationSecretScore() + ScoreManager.getSpeedScore() + ScoreManager.getBonusScore();
        if (DungeonManager.isFinalStage() && ScoreManager.getExplorationClearScore() != 60) return "";
        if (ScoreManager.getExplorationClearScore() == 60) return "";
        String returnString = "\u00a77Virtual score: ";
        if (virtualScore < 100) returnString = returnString + EnumChatFormatting.RED + virtualScore + EnumChatFormatting.RED + " (D)";
        else if (virtualScore < 160) returnString = returnString + EnumChatFormatting.RED + virtualScore + EnumChatFormatting.BLUE + " (C)";
        else if (virtualScore < 230) returnString = returnString + EnumChatFormatting.RED + virtualScore + EnumChatFormatting.GREEN + " (B)";
        else if (virtualScore < 269.5f) returnString = returnString + EnumChatFormatting.YELLOW + virtualScore + EnumChatFormatting.LIGHT_PURPLE + " (A)";
        else if (virtualScore < 300) returnString = returnString + EnumChatFormatting.YELLOW + virtualScore + EnumChatFormatting.GOLD + " (S)";
        else returnString = returnString + EnumChatFormatting.GREEN + virtualScore + EnumChatFormatting.GOLD + " (S+)";
        return returnString;
    }

    private String getSkillDisplay() {
        EnumChatFormatting enumChatFormatting;
        if (DungeonManager.isFinalStage() && ScoreManager.getExplorationClearScore() != 60) return "\u00a77Exploration: " + EnumChatFormatting.RED + "Not all puzzles done";
        int skillScore = ScoreManager.getSkillScore();
        if (skillScore == 100) enumChatFormatting = EnumChatFormatting.GREEN;
        else if (skillScore >= 94) enumChatFormatting = EnumChatFormatting.YELLOW;
        else enumChatFormatting = EnumChatFormatting.RED;
        return "\u00a77Skill: " + enumChatFormatting + skillScore;
    }

    private String getExplorationDisplay() {
        EnumChatFormatting enumChatFormatting;
        int explorationScore = ScoreManager.getExplorationScore();
        if (explorationScore == 100) enumChatFormatting = EnumChatFormatting.GREEN;
        else if (explorationScore >= 90) enumChatFormatting = EnumChatFormatting.YELLOW;
        else enumChatFormatting = EnumChatFormatting.RED;
        return "\u00a77Exploration: " + enumChatFormatting + explorationScore;
    }

    private String getSpeedDisplay() {
        EnumChatFormatting enumChatFormatting;
        int speedScore = ScoreManager.getSpeedScore();
        if (speedScore == 100) enumChatFormatting = EnumChatFormatting.GREEN;
        else if (speedScore >= 90) enumChatFormatting = EnumChatFormatting.YELLOW;
        else enumChatFormatting = EnumChatFormatting.RED;
        return "\u00a77Speed: " + enumChatFormatting + speedScore;
    }

    private String getBonusDisplay() {
        int threshold = Configuration.dungeonsIsPaul ? 15 : 5;
        EnumChatFormatting enumChatFormatting;
        int bonusScore = ScoreManager.getBonusScore();
        if (bonusScore >= threshold) enumChatFormatting = EnumChatFormatting.GREEN;
        else if ((threshold == 15 && bonusScore >= 10) || (threshold == 5 && bonusScore > 0)) enumChatFormatting = EnumChatFormatting.YELLOW;
        else enumChatFormatting = EnumChatFormatting.RED;
        return "\u00a77Bonus: " + enumChatFormatting + bonusScore;
    }

    private static String getSecretDisplay() {
        StringBuilder returnString = new StringBuilder("\u00a77Secrets: ");
        int secretPercentage = ScoreManager.getSecretPercentage();
        int requiredSecretNeeded = ScoreManager.getRequiredSecretNeeded();
        int secretRequirement = DungeonFloor.getFloor(ScoreboardUtils.currentFloor.name()).getSecretPercentage();

        returnString.append(secretPercentage == 0 ? "\u00a7c0% \u00a77/ " :
                (secretPercentage >= requiredSecretNeeded && requiredSecretNeeded != -1 ? "\u00a7a" : "\u00a7c") + secretPercentage + "% \u00a77/ ");

        if (requiredSecretNeeded == -1) {
            return returnString + "\u00a7c S+ UNREACHABLE!";
        }
        returnString.append(secretPercentage >= requiredSecretNeeded ? "\u00a7a" : "\u00a7c").append(requiredSecretNeeded).append("% \u00a77/ ");

        returnString.append(secretRequirement == 0 ? "\u00a7c?%" :
                (secretPercentage >= requiredSecretNeeded ? "\u00a7a" : "\u00a7c") + secretRequirement + "%");

        return returnString.toString();
    }

    // POJAV FAKE OVERLAY ON CHAT

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!Configuration.isPojav()) return;
        if (!DungeonManager.checkEssentials()) return;

        if (ScoreManager.currentSeconds > 0 && ScoreManager.currentSeconds <= 8) {
            readyTime = System.currentTimeMillis() + (chatDisplaySeconds * 1000L); // Reset the timer on dungeon load
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime >= readyTime) {
            readyTime = currentTime + (chatDisplaySeconds * 1000L); // Schedule the next announcement

            ChatUtils.notifyChat(EnumChatFormatting.WHITE + "----- Dungeon Report -----");
            ChatUtils.notifyChat(getRankingDisplay());
            ChatUtils.notifyChat(getVirtualRankingDisplay());
            ChatUtils.notifyChat("");
            ChatUtils.notifyChat(getSkillDisplay());
            ChatUtils.notifyChat(getExplorationDisplay());
            ChatUtils.notifyChat(getSpeedDisplay());
            ChatUtils.notifyChat(getBonusDisplay());
            ChatUtils.notifyChat("");
            ChatUtils.notifyChat(getSecretDisplay());
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Configuration.isPojav()) return;
        if (!DungeonManager.checkEssentials()) return;
        if (event.message.getUnformattedText().equals("[NPC] Mort: Good luck.")) {
            readyTime = System.currentTimeMillis() + (chatDisplaySeconds * 1000L); // Reset the timer on dungeon load
        } else if (event.message.getUnformattedText().contains("> EXTRA STATS <")) {
            readyTime = Long.MAX_VALUE; // Reset the timer on dungeon end
        }
    }

    @SubscribeEvent()
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!Configuration.isPojav()) return;
        if (!DungeonManager.checkEssentials()) return;
        readyTime = Long.MAX_VALUE; // Reset the timer on dungeon unload
    }

}
