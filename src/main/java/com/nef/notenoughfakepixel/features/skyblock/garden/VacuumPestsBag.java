package com.nef.notenoughfakepixel.features.skyblock.garden;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.utils.ColorUtils;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Renders the number of pests stored in a vacuum as a vanilla-style stack count. */
public final class VacuumPestsBag {
    private static final Set<String> VACUUM_IDS = new HashSet<>(Arrays.asList(
            "SKYMART_VACUUM",
            "SKYMART_TURBO_VACUUM",
            "INFINI_VACUUM_HOOVERIUS",
            "INFINI_VACUUM",
            "SKYMART_HYPER_VACUUM"
    ));

    private static final Pattern VACUUM_BAG_LINE = Pattern.compile(
            "^\\s*Vacuum Bag:\\s*([\\d,]+)\\s+Pests\\s*$",
            Pattern.CASE_INSENSITIVE
    );

    private VacuumPestsBag() {
    }

    public static void render(ItemStack item, int x, int y) {
        if (item == null || Config.feature == null || Config.feature.garden == null
                || Config.feature.garden.vacuumPestsBag == null
                || !Config.feature.garden.vacuumPestsBag.enabled) {
            return;
        }

        int pestCount = getPestCount(item);
        if (pestCount < 0) {
            return;
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        String text = Integer.toString(pestCount);
        int textWidth = fontRenderer.getStringWidth(text);
        Color color = ColorUtils.getColor(Config.feature.garden.vacuumPestsBag.color);

        // Match RenderItem.renderItemOverlayIntoGUI exactly: bottom-right of the slot.
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        fontRenderer.drawStringWithShadow(text, x + 17 - textWidth, y + 9, color.getRGB() & 0xFFFFFF);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private static int getPestCount(ItemStack item) {
        if (!VACUUM_IDS.contains(ItemUtils.getInternalName(item))) {
            return -1;
        }

        List<String> loreLines = ItemUtils.getLoreLines(item);
        for (String loreLine : loreLines) {
            String plainLine = StringUtils.stripControlCodes(loreLine);
            Matcher matcher = VACUUM_BAG_LINE.matcher(plainLine);
            if (!matcher.matches()) {
                continue;
            }

            try {
                return Integer.parseInt(matcher.group(1).replace(",", ""));
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }
        return -1;
    }
}
