package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.treasure;

import net.minecraft.block.BlockChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.*;
import org.ginafro.notenoughfakepixel.variables.Area;
import org.ginafro.notenoughfakepixel.variables.Location;

import java.awt.*;

@RegisterEvents
public class CrystalHollowsTreasureModule {

    private static final TreasureTriangulator TRI = TreasureTriangulator.getInstance();

    @SubscribeEvent
    public void onChatReceived(net.minecraftforge.client.event.ClientChatReceivedEvent e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        if (ScoreboardUtils.currentArea != Area.CH_MINES_OF_DIVAN) return;
        String msg = e.message.getFormattedText();
        if (msg.contains("TREASURE: ")) {
            try {
                String distanceStr = msg.split("TREASURE: ")[1].split("m")[0].replaceAll("[^0-9.]", "");
                double distance = Double.parseDouble(distanceStr);
                EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
                TRI.handleData(p.posX, p.posY, p.posZ, distance);
            } catch (Exception ex) {
                Logger.log("§cMetal Detector: parse parser failed");
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        TRI.reset();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        if (TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS)) TRI.reset();
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent e) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        if (!TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS)) return;
        if (e.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) &&
                e.world.getBlockState(e.pos).getBlock() instanceof BlockChest) {
            TRI.reset();
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Config.feature.mining.crysalMetalDetector) return;
        if (ScoreboardUtils.currentArea != Area.CH_MINES_OF_DIVAN) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        GridTrilateration.Int3 guess = TRI.getBestGuess();

        if (guess != null) {
            EntityPlayerSP p = mc.thePlayer;
            Color waypointColor = ColorUtils.getColor(Config.feature.mining.crystalDivanWaypointColor);
            RenderUtils.renderBeaconBeam(new BlockPos(guess.x, p.posY, guess.z), waypointColor.getRGB(), 1, event.partialTicks);
            RenderUtils.renderWaypointText("Guess", new BlockPos(guess.x, p.posY + 3, guess.z), event.partialTicks);
        }
    }

}
