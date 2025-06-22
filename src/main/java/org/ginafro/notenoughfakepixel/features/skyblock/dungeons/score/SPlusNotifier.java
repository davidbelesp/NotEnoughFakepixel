package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.score;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.DungeonManager;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.SoundUtils;
import org.ginafro.notenoughfakepixel.utils.TitleUtils;
import org.ginafro.notenoughfakepixel.variables.DungeonFloor;

@RegisterEvents
public class SPlusNotifier {

    private static boolean remindedSPlus = true;
    private static boolean remindedUnreachable = true;

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!DungeonManager.checkEssentials() ||
                e.phase == TickEvent.Phase.END ||
                Minecraft.getMinecraft().thePlayer == null ||
                Minecraft.getMinecraft().theWorld == null) return;
        if (ScoreManager.currentSeconds > 0 && ScoreManager.currentSeconds <= 8) {
            remindedSPlus = false;
            remindedUnreachable = false;
        }
        reminderSPlus();
        reminderUnreachable();
    }

    public static void reminderSPlus() {
        if (remindedSPlus) return;
        if (!Config.feature.dungeons.dungeonsSPlusNotifier && !Config.feature.dungeons.dungeonsSPlusMessage) return;

        int currentScore = ScoreManager.getSkillScore() + ScoreManager.getExplorationClearScore() + ScoreManager.getSpeedScore() + ScoreManager.getBonusScore();
        int virtualSecretScore = Math.min(40, ScoreManager.getSecretPercentage() * 40 /
                DungeonFloor.getFloor(ScoreboardUtils.currentFloor.name()).getSecretPercentage());
        int virtualTotalScore = currentScore + virtualSecretScore;

        if (virtualTotalScore >= 300) {
            if (Config.feature.dungeons.dungeonsSPlusNotifier) {
                SoundUtils.playSound(mc.thePlayer.getPosition(), "note.pling", 2.0F, 2.0F);
                TitleUtils.showTitle(EnumChatFormatting.RED + "300 Score!", 2000);
            }
            if (Config.feature.dungeons.dungeonsSPlusMessage) {
                String customMessage = Config.feature.dungeons.dungeonsSPlusCustom.trim();
                if (!customMessage.isEmpty()) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc " + customMessage);
                } else {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc 300 Score!");
                }
            }
            remindedSPlus = true;
        }
    }

    public void reminderUnreachable() {
        if (remindedUnreachable) return;
        if (!Config.feature.dungeons.dungeonsSPlusNotifier && !Config.feature.dungeons.dungeonsSPlusMessage) return;

        if (ScoreManager.getRequiredSecretNeeded() == -1) {
            if (Config.feature.dungeons.dungeonsSPlusMessage) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/pc [NEF] S+ may not be reached by secrets only, do crypts or restart");
            }
            remindedUnreachable = true;
        }
    }
}
