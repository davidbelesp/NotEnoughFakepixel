package com.nef.notenoughfakepixel.features.skyblock.qol;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.ScoreboardUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class GoldenEnchants {
    public static final Map<String, String> t6Enchants;
    public static final Pattern t6EnchantPattern;

    static {
        Map<String, String> tempEnchants = new HashMap<>();

        // NORMAL enchantments
        tempEnchants.put("§9Aiming IV", "§6Aiming IV");
        tempEnchants.put("§9Dragon Tracer V", "§6Dragon Tracer V");
        tempEnchants.put("§9Angler VI", "§6Angler VI");
        tempEnchants.put("§9Aqua Affinity I", "§6Aqua Affinity I");
        tempEnchants.put("§9Bane of Arthropods VII", "§6Bane of Arthropods VII");
        tempEnchants.put("§9Big Brain V", "§6Big Brain V");
        tempEnchants.put("§9Blast Protection VII", "§6Blast Protection VII");
        tempEnchants.put("§9Blessing VI", "§6Blessing VI");
        tempEnchants.put("§9Caster VI", "§6Caster VI");
        tempEnchants.put("§9Cayenne V", "§6Cayenne V");
        tempEnchants.put("§9Chance V", "§6Chance V");
        tempEnchants.put("§9Cleave VI", "§6Cleave VI");
        tempEnchants.put("§9Counter-Strike V", "§6Counter-Strike V");
        tempEnchants.put("§9Critical VII", "§6Critical VII");
        tempEnchants.put("§9Cubism VI", "§6Cubism VI");
        tempEnchants.put("§9Charm V", "§6Charm V");
        tempEnchants.put("§9Corruption V", "§6Corruption V");
        tempEnchants.put("§9Delicate V", "§6Delicate V");
        tempEnchants.put("§9Depth Strider III", "§6Depth Strider III");
        tempEnchants.put("§9Divine Gift III", "§6Divine Gift III");
        tempEnchants.put("§9Dragon Hunter V", "§6Dragon Hunter V");
        tempEnchants.put("§9Efficiency X", "§6Efficiency X");
        tempEnchants.put("§9Ender Slayer VII", "§6Ender Slayer VII");
        tempEnchants.put("§9Execute VI", "§6Execute VI");
        tempEnchants.put("§9Experience IV", "§6Experience IV");
        tempEnchants.put("§9Feather Falling X", "§6Feather Falling X");
        tempEnchants.put("§9Fire Aspect III", "§6Fire Aspect III");
        tempEnchants.put("§9Fire Protection VII", "§6Fire Protection VII");
        tempEnchants.put("§9First Strike V", "§6First Strike V");
        tempEnchants.put("§9Flame II", "§6Flame II");
        tempEnchants.put("§9Fortune IV", "§6Fortune IV");
        tempEnchants.put("§9Frail VI", "§6Frail VI");
        tempEnchants.put("§9Frost Walker II", "§6Frost Walker II");
        tempEnchants.put("§9Ferocious Mana X", "§6Ferocious Mana X");
        tempEnchants.put("§9Giant Killer VII", "§6Giant Killer VII");
        tempEnchants.put("§9Growth VII", "§6Growth VII");
        tempEnchants.put("§9Harvesting VI", "§6Harvesting VI");
        tempEnchants.put("§9Hardened Mana X", "§6Hardened Mana X");
        tempEnchants.put("§9Impaling III", "§6Impaling III");
        tempEnchants.put("§9Infinite Quiver X", "§6Infinite Quiver X");
        tempEnchants.put("§9Knockback II", "§6Knockback II");
        tempEnchants.put("§9Lethality VI", "§6Lethality VI");
        tempEnchants.put("§9Life Steal V", "§6Life Steal V");
        tempEnchants.put("§9Looting V", "§6Looting V");
        tempEnchants.put("§9Luck VII", "§6Luck VII");
        tempEnchants.put("§9Luck of the Sea VI", "§6Luck of the Sea VI");
        tempEnchants.put("§9Lure VI", "§6Lure VI");
        tempEnchants.put("§9Magnet VI", "§6Magnet VI");
        tempEnchants.put("§9Mana Steal III", "§6Mana Steal III");
        tempEnchants.put("§9Mana Vampire X", "§6Mana Vampire X");
        tempEnchants.put("§9Overload V", "§6Overload V");
        tempEnchants.put("§9Piercing I", "§6Piercing I");
        tempEnchants.put("§9Piscary VI", "§6Piscary VI");
        tempEnchants.put("§9Power VII", "§6Power VII");
        tempEnchants.put("§9Pristine V", "§6Pristine V");
        tempEnchants.put("§9Projectile Protection VII", "§6Projectile Protection VII");
        tempEnchants.put("§9Prosecute VI", "§6Prosecute VI");
        tempEnchants.put("§9Protection VII", "§6Protection VII");
        tempEnchants.put("§9Punch II", "§6Punch II");
        tempEnchants.put("§9Rainbow II", "§6Rainbow II");
        tempEnchants.put("§9Rejuvenate V", "§6Rejuvenate V");
        tempEnchants.put("§9Replenish I", "§6Replenish I");
        tempEnchants.put("§9Respiration III", "§6Respiration III");
        tempEnchants.put("§9Respite V", "§6Respite V");
        tempEnchants.put("§9Scavenger V", "§6Scavenger V");
        tempEnchants.put("§9Sharpness VII", "§6Sharpness VII");
        tempEnchants.put("§9Silk Touch I", "§6Silk Touch I");
        tempEnchants.put("§9Smarty Pants V", "§6Smarty Pants V");
        tempEnchants.put("§9Smelting Touch I", "§6Smelting Touch I");
        tempEnchants.put("§9Smite VII", "§6Smite VII");
        tempEnchants.put("§9Snipe IV", "§SantSnipe IV");
        tempEnchants.put("§9Spiked Hook VI", "§6Spiked Hook VI");
        tempEnchants.put("§9Sugar Rush III", "§6Sugar Rush III");
        tempEnchants.put("§9Syphon V", "§6Syphon V");
        tempEnchants.put("§9Smoldering V", "§6Smoldering V");
        tempEnchants.put("§9Strong Mana X", "§6Strong Mana X");
        tempEnchants.put("§9Tabasco III", "§6Tabasco III");
        tempEnchants.put("§9Telekinesis I", "§6Telekinesis I");
        tempEnchants.put("§9Thorns III", "§6Thorns III");
        tempEnchants.put("§9Thunderbolt VI", "§6Thunderbolt VI");
        tempEnchants.put("§9Thunderlord VII", "§6Thunderlord VII");
        tempEnchants.put("§9Titan Killer VII", "§6Titan Killer VII");
        tempEnchants.put("§9Triple-Strike V", "§6Triple-Strike V");
        tempEnchants.put("§9True Protection I", "§6True Protection I");
        tempEnchants.put("§9Turbo-Cacti V", "§6Turbo-Cacti V");
        tempEnchants.put("§9Turbo-Cane V", "§6Turbo-Cane V");
        tempEnchants.put("§9Turbo-Carrot V", "§6Turbo-Carrot V");
        tempEnchants.put("§9Turbo-Cocoa V", "§6Turbo-Cocoa V");
        tempEnchants.put("§9Turbo-Melon V", "§6Turbo-Melon V");
        tempEnchants.put("§9Turbo-Mushrooms V", "§6Turbo-Mushrooms V");
        tempEnchants.put("§9Turbo-Potato V", "§6Turbo-Potato V");
        tempEnchants.put("§9Turbo-Pumpkin V", "§6Turbo-Pumpkin V");
        tempEnchants.put("§9Turbo-Warts V", "§6Turbo-Warts V");
        tempEnchants.put("§9Turbo-Wheat V", "§6Turbo-Wheat V");
        tempEnchants.put("§9Vampirism VI", "§6Vampirism VI");
        tempEnchants.put("§9Venomous VI", "§6Venomous VI");
        tempEnchants.put("§9Vicious V", "§6Vicious V");

        // STACKING enchantments
        tempEnchants.put("§9Compact X", "§6Compact X");
        tempEnchants.put("§9Cultivating X", "§6Cultivating X");
        tempEnchants.put("§9Expertise X", "§6Expertise X");
        tempEnchants.put("§9Hecatomb X", "§6Hecatomb X");
        tempEnchants.put("§9Champion X", "§6Champion X");

        t6Enchants = Collections.unmodifiableMap(tempEnchants);

        String patternString = "(" + String.join("|", tempEnchants.keySet().stream()
                .map(Pattern::quote)
                .toArray(String[]::new)) + ")";
        t6EnchantPattern = Pattern.compile(patternString);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (event.toolTip == null || event.itemStack == null) return;

        if (Config.feature.qol.qolGoldenEnchants) {
            for (int i = 0; i < event.toolTip.size(); i++) {
                String line = event.toolTip.get(i);
                if (line != null) {
                    event.toolTip.set(i, returnGoldenEnchants(line));
                }
            }
        }
    }

    public static String returnGoldenEnchants(String line) {
        if (line == null) return "";
        Matcher matcher = t6EnchantPattern.matcher(line);
        StringBuffer out = new StringBuffer();

        while (matcher.find()) {
            String replacement = t6Enchants.get(matcher.group(1));
            if (replacement != null) {
                matcher.appendReplacement(out, replacement);
            } else {
                matcher.appendReplacement(out, matcher.group(1));
            }
        }
        matcher.appendTail(out);

        return out.toString();
    }
}