package com.nef.notenoughfakepixel.events;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.minecraft.client.gui.inventory.GuiContainer;


@EqualsAndHashCode(callSuper = true)
@Value
public class GuiContainerBackgroundDrawnEvent extends CustomEvent {

    public GuiContainer container;
    public float partialTicks;

    public GuiContainerBackgroundDrawnEvent(GuiContainer container, float partialTicks) {
        this.container = container;
        this.partialTicks = partialTicks;
    }

    public GuiContainer getContainer() {
        return container;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

}
