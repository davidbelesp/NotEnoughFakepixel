package com.nef.notenoughfakepixel.features.skyblock.mining;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.variables.Colors;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class DrillFuelParsing {

    public static int lastRead = 0;

    public static int fuel = 0;
    public static int fuelMax = 0;

    // §7Fuel: §218,660§8/25k <
    public static Matcher fuelMatcher = Pattern.compile("§7Fuel: §2([\\d,]+)§8/(\\d+)k").matcher("");

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!Config.feature.mining.miningDrillFuel) return;
        if (lastRead < 120) {
            lastRead++;
            return;
        }
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (!SkyblockData.getCurrentLocation().equals(Location.DWARVEN) && !SkyblockData.getCurrentLocation().equals(Location.CRYSTAL_HOLLOWS)) return;

        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (heldItem == null) return;

        if (!ItemUtils.getInternalName(heldItem).contains("_DRILL_") && !ItemUtils.getInternalName(heldItem).equals("DIVAN_DRILL")) return;

        int fuelTemp = ItemUtils.getExtraAttributesIntTag(heldItem, "fuel");
        if (fuelTemp != -1) fuel = fuelTemp;

        String fuelLore = ItemUtils.getLoreLine(heldItem, "Fuel:");
        if (fuelLore == null) return;

        fuelMatcher.reset(fuelLore);
        if (fuelMatcher.find()) {
            fuelMax = Integer.parseInt(fuelMatcher.group(2).replace(",", "")) * 1000;
        }

        lastRead = 0;
        // Getting the item in hand, and checking its fuel
    }

    public static String getString() {
        // GREEN when fuel is above 50%
        // YELLOW when fuel is above 25%
        // RED when fuel is below 25%
        Colors color = fuel > fuelMax * 0.5 ? Colors.GREEN : fuel > fuelMax * 0.25 ? Colors.YELLOW : Colors.RED;
        return Colors.GRAY + "Drill Fuel: "
                + color + fuel +
                Colors.GRAY + "/" + fuelMax;
    }
}
