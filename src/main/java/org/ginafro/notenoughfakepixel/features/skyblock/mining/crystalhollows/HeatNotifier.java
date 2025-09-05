package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;

@RegisterEvents
public class HeatNotifier {

    private int tickCount = 0;
    private boolean notified = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        tickCount++;
        if (event.phase == TickEvent.Phase.END && event.side.isClient() && (tickCount % 120 == 0)) {
            tickCount = 0;
            if (!Config.feature.mining.crystalHeatNotifier) return;
            if (ScoreboardUtils.heat > Config.feature.mining.crystalHeatLevel && !notified) {
                notified = true;
                Minecraft.getMinecraft().ingameGUI.displayTitle(EnumChatFormatting.RED + "HEAT", "", 2, 70, 2);
            }
            if (ScoreboardUtils.heat < Config.feature.mining.crystalHeatLevel && notified) {
                notified = false;
            }
        }
    }

}
