package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.StringUtils;
import org.ginafro.notenoughfakepixel.utils.Utils;
import org.ginafro.notenoughfakepixel.variables.Gamemode;

import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class DamageCommas {

    private static final WeakHashMap<EntityLivingBase, ChatComponentText> replacementMap = new WeakHashMap<>();

    private static final EnumChatFormatting[] coloursHypixel = {
            EnumChatFormatting.WHITE,
            EnumChatFormatting.YELLOW,
            EnumChatFormatting.GOLD,
            EnumChatFormatting.RED,
            EnumChatFormatting.RED,
            EnumChatFormatting.WHITE
    };

    private static final char STAR = '✧';
    private static final char OVERLOAD_STAR = '✯';
    private static final Pattern PATTERN_CRIT = Pattern.compile(
            "§f" + STAR + "((?:§.\\d(?:§.,)?)+)§." + STAR + "(.*)");
    private static final Pattern PATTERN_NO_CRIT = Pattern.compile("(§.)([\\d+,]*)(.*)");
    private static final Pattern OVERLOAD_PATTERN = Pattern.compile("(§.)" + OVERLOAD_STAR + "((?:§.[\\d,])+)(§.)" + OVERLOAD_STAR + "§r");

    public static IChatComponent replaceName(EntityLivingBase entity) {
        IChatComponent name = entity.getDisplayName();

        if (!entity.hasCustomName()) return name;
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK) return name;

        if (false) return replaceForCommas(entity, name);
        else return name;
    }

    public static IChatComponent replaceForCommas(EntityLivingBase entity, IChatComponent name) {
        // This will replace for an abbreviation of the number.
        if (replacementMap.containsKey(entity)) {
            ChatComponentText component = replacementMap.get(entity);
            if (component == null) return name;
            return component;
        }

        String formatted = name.getFormattedText();

        boolean crit = false;
        String numbers;
        String prefix;
        String suffix;

        Matcher matcherCrit = PATTERN_CRIT.matcher(formatted);
        Matcher matcherOverload = OVERLOAD_PATTERN.matcher(formatted);
        if (matcherCrit.matches()) {
            crit = true;
            numbers = StringUtils.cleanColor(matcherCrit.group(1)).replace(",", "");
            prefix = "§f" + STAR;
            suffix = "§f" + STAR + matcherCrit.group(2);
        } else if (matcherOverload.matches()) {
            crit = true;
            numbers = StringUtils.cleanColor(matcherOverload.group(2)).replace(",", "");
            prefix = matcherOverload.group(1) + OVERLOAD_STAR;
            suffix = matcherOverload.group(3) + OVERLOAD_STAR + "§r";
        } else {
            Matcher matcherNoCrit = PATTERN_NO_CRIT.matcher(formatted);
            if (matcherNoCrit.matches()) {
                numbers = matcherNoCrit.group(2).replace(",", "");
                prefix = matcherNoCrit.group(1);
                suffix = "§r" + matcherNoCrit.group(3);
            } else {
                replacementMap.put(entity, null);
                return name;
            }
        }

        StringBuilder newFormatted = new StringBuilder();

        try {
            int number = Integer.parseInt(numbers);

            if (number > 999) {
                newFormatted.append(formatNumber(number));
            } else {
                return name;
            }
        } catch (NumberFormatException e) {
            replacementMap.put(entity, null);
            return name;
        }

        if (crit) {
            StringBuilder newFormattedCrit = new StringBuilder();

            int colourIndex = 0;
            for (char c : newFormatted.toString().toCharArray()) {
                if (c == ',') {
                    newFormattedCrit.append(EnumChatFormatting.GRAY);
                } else {
                    newFormattedCrit.append(coloursHypixel[colourIndex++ % coloursHypixel.length]);
                }
                newFormattedCrit.append(c);
            }

            newFormatted = newFormattedCrit;
        }

        ChatComponentText finalComponent = new ChatComponentText(prefix + newFormatted + suffix);

        replacementMap.put(entity, finalComponent);
        return finalComponent;
    }

    private static String formatNumber(int number) {
        if (false) {
            return Utils.shortNumberFormat(number, 0);
        } else {
            return Utils.commaFormat(number);
        }
    }

}
