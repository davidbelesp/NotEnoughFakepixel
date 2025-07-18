package org.ginafro.notenoughfakepixel.features.skyblock.enchanting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.lwjgl.input.Mouse;

@RegisterEvents
public class PreventMissclicks {

    // timestamp of the last *allowed* click
    private long lastTimeClicked = System.currentTimeMillis();
    // minimum millis between allowed clicks
    private final long cooldownMs = 500L;

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        // only care about presses
        if (!Mouse.getEventButtonState()) return;

        // only when one of our solvers is active *and* currently resolving
        boolean isChrono = EnchantingSolvers.currentSolverType == EnchantingSolvers.SolverTypes.CHRONOMATRON
            && Config.feature.experimentation.experimentationChronomatronSolver 
            && EnchantingSolvers.resolving;

        boolean isUltra = EnchantingSolvers.currentSolverType == EnchantingSolvers.SolverTypes.ULTRASEQUENCER
            && Config.feature.experimentation.experimentationUltraSequencerSolver
            && EnchantingSolvers.resolving;

        if (!isChrono && !isUltra) return;
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiChest)) return;

        long now = System.currentTimeMillis();
        // if we're still in cooldown, block *all* clicks
        if (now - lastTimeClicked < cooldownMs) {
            event.setCanceled(true);
            return;
        }
        // this click is outside the cooldown window → accept & advance the timer
        lastTimeClicked = now;

        GuiChest chestGui = (GuiChest) Minecraft.getMinecraft().currentScreen;
        if (chestGui.getSlotUnderMouse() == null) return;
        int slotIndex = chestGui.getSlotUnderMouse().getSlotIndex();

        // --- Chronomatron logic ---
        if (isChrono && !EnchantingSolvers.chronomatronOrder.isEmpty()) {
            int target = EnchantingSolvers.chronomatronOrder.get(0);
            boolean valid = slotIndex == target
                         || slotIndex == target + 9
                         || (slotIndex == target + 18
                             && !TablistParser.currentOpenChestName.contains("Transcendent")
                             && !TablistParser.currentOpenChestName.contains("Metaphysical"));

            if (valid) {
                // allowed click, let it through
                return;
            }
            // clicked wrong slot → block if the user opted in
            if (Config.feature.experimentation.experimentationPreventMissclicks) {
                event.setCanceled(true);
            }
            return;
        }

        // --- Ultrasequencer logic ---
        if (isUltra) {
            for (EnchantingSolvers.UltrasequencerSlot slot : EnchantingSolvers.ultrasequencerSlots) {
                ItemStack itemInSlot = chestGui.inventorySlots.getInventory().get(slotIndex);
                // correct slot + correct order
                if (slot.slot == chestGui.getSlotUnderMouse()
                        && EnchantingSolvers.slotToClickUltrasequencer == slot.quantity) {
                    if (EnchantingSolvers.ultrasequencerSlots.size() == EnchantingSolvers.slotToClickUltrasequencer) {
                        EnchantingSolvers.roundUltraSequencerSolver++;
                    }
                    EnchantingSolvers.slotToClickUltrasequencer++;
                    return;
                }
                // skip dyes or empty
                if (itemInSlot == null || itemInSlot.getItem() == Items.dye) {
                    continue;
                }
            }
            // clicked a non-solver slot → block if the user opted in
            if (Config.feature.experimentation.experimentationPreventMissclicks) {
                event.setCanceled(true);
            }
        }
    }
}
