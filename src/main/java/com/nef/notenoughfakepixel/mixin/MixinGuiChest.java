package com.nef.notenoughfakepixel.mixin;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import com.nef.notenoughfakepixel.NotEnoughFakepixel;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(GuiChest.class)
public abstract class MixinGuiChest extends GuiContainer {
    private Boolean inInventory = false;
    private GuiTextField textField = null;

    @Shadow
    private IInventory lowerChestInventory;

    public MixinGuiChest(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (textField != null) {
            textField.drawTextBox();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        String guiName = lowerChestInventory.getDisplayName().getUnformattedText();
        if (guiName.equals("Reforge Item") && Config.feature.qol.qolReforgeHelper) {
            inInventory = true;
            int xPos = guiLeft - 140;
            int yPos = guiTop + 80;
            textField = new GuiTextField(2, this.fontRendererObj, xPos, yPos, 120, 20);
            textField.setMaxStringLength(50);
            textField.setFocused(true);
            String lockedReforge = NotEnoughFakepixel.instance.getUtils().getLockedEnchantment().toLowerCase();
            if (lockedReforge != null) {
                textField.setText(lockedReforge);
            }
        } else {
            inInventory = false;
            textField = null;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (inInventory) {
            if (keyCode != this.mc.gameSettings.keyBindInventory.getKeyCode()) {
                super.keyTyped(typedChar, keyCode);
            }
            if (textField != null) {
                textField.textboxKeyTyped(typedChar, keyCode);
                NotEnoughFakepixel.instance.getUtils().setLockedEnchantment(textField.getText());
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        if (inInventory && NotEnoughFakepixel.instance.getUtils().getLockedEnchantment().length() > 0) {
            if (slotIn != null && slotIn.getHasStack() && slotIn.getSlotIndex() == 22) {
                Slot itemSlot = inventorySlots.getSlot(13);
                if (itemSlot != null && itemSlot.getHasStack()) {
                    ItemStack item = itemSlot.getStack();
                    if (item.hasDisplayName()) {
                        String[] nameParts = item.getDisplayName().split(" ");
                        if (nameParts.length > 2) {
                            String reforge = NotEnoughFakepixel.instance.getUtils().stripColor(nameParts[0]);
                            if (reforge.toLowerCase().contains(NotEnoughFakepixel.instance.getUtils().getLockedEnchantment().toLowerCase())) {
                                SoundUtils.playGlobalSound("random.orb", 1, 0.1F);
                                return;
                            }
                        }
                    }
                }
            }
        }
        super.handleMouseClick(slotIn, slotId, clickedButton, clickType);
    }
}