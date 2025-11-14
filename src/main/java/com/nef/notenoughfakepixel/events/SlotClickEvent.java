package com.nef.notenoughfakepixel.events;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

@Cancelable
public class SlotClickEvent extends Event {

    public boolean post() {
        MinecraftForge.EVENT_BUS.post(this);
        return isCancelable() && isCanceled();
    }

    public void cancel() {
        setCanceled(true);
    }

    public final @NotNull GuiContainer guiContainer;
    public final @NotNull Slot slot;
    public final int slotId;
    public int clickedButton;
    /**
     * Click types (along with the default keybind):
     *
     * <ul>
     * 	 <li>0 : mouse click (either LMB or RMB)</li>
     * 	 <li>1 : Shift mouse click</li>
     * 	 <li>2 : hotbar keybind (0-9) -> see clickedButton</li>
     * 	 <li>3 : pick block (middle mouse button)</li>
     * 	 <li>4 : drop block (Q)</li>
     * </ul>
     */
    public int clickType;
    public boolean usePickblockInstead = false;

    public SlotClickEvent(GuiContainer guiContainer, Slot slot, int slotId, int clickedButton, int clickType) {
        this.guiContainer = guiContainer;
        this.slot = slot;
        this.slotId = slotId;
        this.clickedButton = clickedButton;
        this.clickType = clickType;
    }

    public void usePickblockInstead() {
        usePickblockInstead = true;
    }

    @Override
    public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
    }
}
