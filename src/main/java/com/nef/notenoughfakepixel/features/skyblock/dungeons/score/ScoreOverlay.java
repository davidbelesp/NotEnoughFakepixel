package com.nef.notenoughfakepixel.features.skyblock.dungeons.score;

import com.nef.notenoughfakepixel.Configuration;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ChatUtils;
import com.nef.notenoughfakepixel.variables.DungeonFloor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

@RegisterEvents
public class ScoreOverlay {

    private static final int chatDisplaySeconds = 40;
    private long readyTime = Long.MAX_VALUE;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (!shouldShow()) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int overlayWidth = getWidth(1.0f); // Base width, scaled later
        int overlayHeight = getHeight(1.0f); // Base height, scaled later
        float x = Config.feature.dungeons.scoreOverlayPos.getAbsX(sr, overlayWidth);
        float y = Config.feature.dungeons.scoreOverlayPos.getAbsY(sr, overlayHeight);
        draw(x, y, Config.feature.dungeons.scoreOverlayScale, false);
    }

    private boolean shouldShow() {
        if (!DungeonManager.checkEssentials()) return false;
        return Config.feature.dungeons.dungeonsScoreOverlay;
    }

    private void draw(float x, float y, float scale, boolean example) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, scale);

        List<String> lines = new ArrayList<>();
        getLines(lines, example);

        // Parse background color
        String[] colorParts = Config.feature.dungeons.scoreOverlayBackgroundColor.split(":");
        int alpha = Integer.parseInt(colorParts[1]);
        int red = Integer.parseInt(colorParts[2]);
        int green = Integer.parseInt(colorParts[3]);
        int blue = Integer.parseInt(colorParts[4]);
        int bgColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

        // Calculate text dimensions (unscaled)
        int textWidth = 0;
        for (String line : lines) {
            int lineWidth = mc.fontRendererObj.getStringWidth(line);
            if (lineWidth > textWidth) textWidth = lineWidth;
        }
        textWidth += 4; // 2px padding on each side
        int textHeight = lines.size() * 11; // 11px per line (8px font + 3px spacing)

        // Draw background matching text dimensions
        Gui.drawRect(0, 0, textWidth, textHeight, bgColor);

        // Draw text
        for (int i = 0; i < lines.size(); i++) {
            mc.fontRendererObj.drawString(lines.get(i), 2, i * 11, -1); // 2px left padding
        }

        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);
        GlStateManager.popMatrix();
    }

    public void renderDummy() {
        ScaledResolution sr = new ScaledResolution(mc);
        int overlayWidth = getWidth(1.0f);
        int overlayHeight = getHeight(1.0f);
        float x = Config.feature.dungeons.scoreOverlayPos.getAbsX(sr, overlayWidth);
        float y = Config.feature.dungeons.scoreOverlayPos.getAbsY(sr, overlayHeight);
        draw(x, y, Config.feature.dungeons.scoreOverlayScale, true);
    }

    public int getWidth(float scale) {
        int baseWidth = mc.fontRendererObj.getStringWidth("Total score: 300 (S+)") + 4; // Base estimate
        return (int) (baseWidth * scale);
    }

    public int getHeight(float scale) {
        return (int) (110 * scale); // 10 lines × 11px
    }

    private void getLines(List<String> lines, boolean example) {
        if (example) {
            if (Config.feature.dungeons.dungeonsScoreSimple) {
                lines.add("\u00a77Total score: \u00a7a300\u00a76 (S+)");
                lines.add("");
                lines.add("\u00a77Secrets: \u00a7a100% \u00a7a/ 80% \u00a7a/ 100%");
            } else {
                lines.add("\u00a77Total score: \u00a7a300\u00a76 (S+)");
                lines.add("");
                lines.add("\u00a77Skill: \u00a7a100");
                lines.add("\u00a77Exploration: \u00a7a100");
                lines.add("\u00a77Speed: \u00a7a100");
                lines.add("\u00a77Bonus: \u00a7a5");
                lines.add("");
                lines.add("\u00a77Secrets: \u00a7a100% \u00a7a/ 80% \u00a7a/ 100%");
            }
        } else {
            if (Config.feature.dungeons.dungeonsScoreSimple) {
                lines.add(getRankingDisplay());
                lines.add("");
                lines.add(getSecretDisplay());
            } else {
                lines.add(getRankingDisplay());
                lines.add("");
                lines.add(getSkillDisplay());
                lines.add(getExplorationDisplay());
                lines.add(getSpeedDisplay());
                lines.add(getBonusDisplay());
                lines.add("");
                lines.add(getSecretDisplay());
            }
        }
    }

    private String getRankingDisplay() {
        int totalScore = ScoreManager.getTotalScore();
        if (DungeonManager.isFinalStage() && ScoreManager.getExplorationClearScore() != 60)
            return EnumChatFormatting.RED + "UNKNOWN SCORE";
        String returnString = "\u00a77Total score: ";
        if (totalScore < 100)
            returnString = returnString + EnumChatFormatting.RED + totalScore + EnumChatFormatting.RED + " (D)";
        else if (totalScore < 160)
            returnString = returnString + EnumChatFormatting.RED + totalScore + EnumChatFormatting.BLUE + " (C)";
        else if (totalScore < 230)
            returnString = returnString + EnumChatFormatting.RED + totalScore + EnumChatFormatting.GREEN + " (B)";
        else if (totalScore < 269.5f)
            returnString = returnString + EnumChatFormatting.YELLOW + totalScore + EnumChatFormatting.LIGHT_PURPLE + " (A)";
        else if (totalScore < 300)
            returnString = returnString + EnumChatFormatting.YELLOW + totalScore + EnumChatFormatting.GOLD + " (S)";
        else returnString = returnString + EnumChatFormatting.GREEN + totalScore + EnumChatFormatting.GOLD + " (S+)";
        return returnString;
    }

    private String getSkillDisplay() {
        EnumChatFormatting enumChatFormatting;
        if (DungeonManager.isFinalStage() && ScoreManager.getExplorationClearScore() != 60)
            return "\u00a77Exploration: " + EnumChatFormatting.RED + "Not all puzzles done";
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
        int threshold = Config.feature.dungeons.dungeonsIsPaul ? 15 : 5;
        EnumChatFormatting enumChatFormatting;
        int bonusScore = ScoreManager.getBonusScore();
        if (bonusScore >= threshold) enumChatFormatting = EnumChatFormatting.GREEN;
        else if ((threshold == 15 && bonusScore >= 10) || (threshold == 5 && bonusScore > 0))
            enumChatFormatting = EnumChatFormatting.YELLOW;
        else enumChatFormatting = EnumChatFormatting.RED;
        return "\u00a77Bonus: " + enumChatFormatting + bonusScore;
    }

    private static String getSecretDisplay() {
        StringBuilder returnString = new StringBuilder("\u00a77Secrets: ");
        int secretPercentage = ScoreManager.getSecretPercentage();
        int requiredSecretNeeded = ScoreManager.getRequiredSecretNeeded();
        int secretRequirement = DungeonFloor.getFloor(SkyblockData.getCurrentFloor().name()).getSecretPercentage();

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

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!Configuration.isPojav()) return;
        if (!DungeonManager.checkEssentials()) return;

        if (ScoreManager.currentSeconds > 0 && ScoreManager.currentSeconds <= 8) {
            readyTime = System.currentTimeMillis() + (chatDisplaySeconds * 1000L);
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime >= readyTime) {
            readyTime = currentTime + (chatDisplaySeconds * 1000L);

            ChatUtils.notifyChat(EnumChatFormatting.WHITE + "----- Dungeon Report -----");
            ChatUtils.notifyChat(getRankingDisplay());
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
        String msg = event.message.getUnformattedText();
        if (msg.equals("[NPC] Mort: Good luck.")) {
            readyTime = System.currentTimeMillis() + (chatDisplaySeconds * 1000L);
        } else if (msg.contains("> EXTRA STATS <")) {
            readyTime = Long.MAX_VALUE;
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!Configuration.isPojav()) return;
        if (!DungeonManager.checkEssentials()) return;
        readyTime = Long.MAX_VALUE;
    }
}