package com.nef.notenoughfakepixel.config.gui;

import io.github.notenoughupdates.moulconfig.gui.GuiElement;
import io.github.notenoughupdates.moulconfig.gui.GuiScreenElementWrapper;

/**
 * MoulConfig screen wrapper that persists NEF settings whenever the screen closes.
 */
public class NefGuiScreenElementWrapper extends GuiScreenElementWrapper {

    public NefGuiScreenElementWrapper(GuiElement element) {
        super(element);
    }

    @Override
    public void onGuiClosed() {
        Config.saveConfig();
    }
}
