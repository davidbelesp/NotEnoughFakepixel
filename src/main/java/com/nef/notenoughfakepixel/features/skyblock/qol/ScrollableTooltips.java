package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// This class has been imported from Not Enough Updates.

@RegisterEvents
public class ScrollableTooltips {
    static List<String> lastRenderedTooltip = null;
    static int scrollOffset = 0;
    public static boolean didRenderTooltip = false;

    public static List<String> handleTextLineRendering(List<String> tooltip) {
        didRenderTooltip = true;
        if (!Objects.equals(tooltip, lastRenderedTooltip)) {
            lastRenderedTooltip = new ArrayList<>(tooltip);
            scrollOffset = 0;
            return tooltip;
        }
        lastRenderedTooltip = new ArrayList<>(tooltip);
        List<String> modifiableTooltip = new ArrayList<>(tooltip);
        for (int i = 0; i < scrollOffset && modifiableTooltip.size() > 1; i++) {
            modifiableTooltip.remove(0);
        }
        for (int i = 0; i < -scrollOffset && modifiableTooltip.size() > 1; i++) {
            modifiableTooltip.remove(modifiableTooltip.size() - 1);
        }
        return modifiableTooltip;
    }

    @SubscribeEvent
    public void onMouse(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Config.feature.misc.qolScrollableTooltips) return;
        if (Mouse.getEventDWheel() < 0) {
            scrollOffset = Math.max(
                    lastRenderedTooltip == null ? 0 : -Math.max(lastRenderedTooltip.size() - 1, 0)
                    , scrollOffset - 1
            );
        } else if (Mouse.getEventDWheel() > 0) {
            scrollOffset = Math.min(
                    lastRenderedTooltip == null ? 0 : Math.max(lastRenderedTooltip.size() - 1, 0),
                    scrollOffset + 1
            );
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            didRenderTooltip = false;
        } else if (!didRenderTooltip) {
            lastRenderedTooltip = null;
        }
    }
}
