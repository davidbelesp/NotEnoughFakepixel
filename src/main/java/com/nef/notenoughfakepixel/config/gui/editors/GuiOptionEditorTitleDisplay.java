package com.nef.notenoughfakepixel.config.gui.editors;

import com.nef.notenoughfakepixel.config.gui.annotations.ConfigTitleDisplay;
import com.nef.notenoughfakepixel.env.VersionChecker;
import io.github.notenoughupdates.moulconfig.common.IFontRenderer;
import io.github.notenoughupdates.moulconfig.common.RenderContext;
import io.github.notenoughupdates.moulconfig.common.text.StructuredText;
import io.github.notenoughupdates.moulconfig.gui.GuiOptionEditor;
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

/**
 * Displays a version, custom text, or the installed mod name.
 */
public class GuiOptionEditorTitleDisplay extends GuiOptionEditor {

    private final ConfigTitleDisplay display;

    public GuiOptionEditorTitleDisplay(ProcessedOption option, ConfigTitleDisplay display) {
        super(option);
        this.display = display;
    }

    @Override
    public int getHeight() {
        return getPanelHeight();
    }

    @Override
    public void render(RenderContext context, int x, int y, int width) {
        int panelHeight = getPanelHeight();
        float scale = getTextScale();
        context.drawDarkRect(x, y, width, panelHeight, true);

        StructuredText title = StructuredText.of(getDisplayText());
        IFontRenderer fontRenderer = context.getMinecraft().getDefaultFontRenderer();
        int scaledHeight = Math.round(fontRenderer.getHeight() * scale);

        context.pushMatrix();
        context.translate(x + width / 2f, y + (panelHeight - scaledHeight) / 2f);
        context.scale(scale);
        int textWidth = fontRenderer.getStringWidth(title);
        context.drawString(fontRenderer, title, -textWidth / 2, 0, display.color(), display.shadow());
        context.popMatrix();
    }

    private int getPanelHeight() {
        switch (display.size()) {
            case SMALL:
                return 20;
            case MEDIUM:
                return 30;
            case BIG:
            default:
                return 40;
        }
    }

    private float getTextScale() {
        switch (display.size()) {
            case SMALL:
                return 1f;
            case MEDIUM:
                return 1.5f;
            case BIG:
            default:
                return 2f;
        }
    }

    private String getDisplayText() {
        switch (display.type()) {
            case TEXT:
                return display.text();
            case MODNAME:
                ModContainer mod = Loader.instance().getIndexedModList().get("notenoughfakepixel");
                return mod != null && mod.getName() != null ? mod.getName() : "NotEnoughFakepixel";
            case VERSION:
            default:
                return "v" + VersionChecker.getModVersion();
        }
    }
}
