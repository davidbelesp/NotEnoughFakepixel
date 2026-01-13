package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.commands.RenameCommand;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.StringUtils;
import com.nef.notenoughfakepixel.variables.Constants;
import com.nef.notenoughfakepixel.variables.StackingEnchant;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Handles modifications to item tooltips, specifically for adding custom
 * status tags like the "Admin Item" warning.
 * <p>
 * This class listens to Forge tooltip events to inject information
 * at specific positions within the tooltip list, ensuring custom tags appear
 * above technical information (like NBT or registry names) but below the item description.
 */
@RegisterEvents
public class ItemTooltipModifier {

    /**
     * Injects custom metadata tags into the item tooltip.
     * <p>
     * The method calculates an insertion index to place the tag appropriately:
     * <ul>

     * @param e The ItemTooltipEvent provided by the event bus.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onItemTooltipFirst(ItemTooltipEvent e) {
        ItemStack hoveredItem = e.itemStack;
        int insertAt = e.toolTip.size();

        // If not in Skyblock or not a Skyblock item, do nothing
        if (!SkyblockData.isSkyblock()) return;
        if (!ItemUtils.isSkyblockItem(hoveredItem)) return;

        // Calculating the correct insertion point
        for (String line : e.toolTip) {
            List<String> rarities = Arrays.asList(Constants.RARITIES);
            for (String rarity : rarities) {
                if (line.contains(rarity)) {
                    insertAt = e.toolTip.indexOf(line);
                    break;
                }
            }
        }


        // Admin Item Tag
        if (Config.feature.qol.qolShowAdminTag) {
            if (ItemUtils.isAdminItem(hoveredItem)) {
                String adminName = ItemUtils.getAdminName(hoveredItem);
                String insertText = "§c§l⚠ ADMIN ITEM ⚠";
                if (!adminName.isEmpty()) {
                    insertText += " §7(§c" + adminName + "§7)";
                }
                e.toolTip.add(insertAt, insertText);
            }
        }

        // Farming tools Counter
        if (Config.feature.qol.qolShowStackingCounter) {
            if (ItemUtils.hasStackingCounter(hoveredItem)) {
                int counter = ItemUtils.getStackingCounter(hoveredItem);
                String counterFormatted = String.format("%,d", counter);

                StackingEnchant type = ItemUtils.getStackingEnchant(hoveredItem);
                if (type == StackingEnchant.NONE) return;

                Integer nextLevelAt = StackingEnchant.getNextLevel(counter, type);

                if (nextLevelAt != null) {
                    String nextLevelFormatted = String.format("%,d", nextLevelAt);
                    e.toolTip.add(insertAt, "§6§l" + StringUtils.capitalizeName(type.name().toLowerCase()) + ": §e" + counterFormatted + " §7(Next Level at §a" + nextLevelFormatted + "§7)");
                } else {
                    e.toolTip.add(insertAt, "§6§l" + StringUtils.capitalizeName(type.name().toLowerCase()) + ": §e" + counterFormatted + " §7(MAX LEVEL)");
                }
            }
        }

        // Checking if item UUID is renamed already
        Map<String, String> renamedItems = RenameCommand.renamedItems;
        String itemUUID = ItemUtils.getItemUUID(hoveredItem);
        if (renamedItems.containsKey(itemUUID)) {
            ItemUtils.renameItem(hoveredItem, renamedItems.get(itemUUID));
        }
    }

}