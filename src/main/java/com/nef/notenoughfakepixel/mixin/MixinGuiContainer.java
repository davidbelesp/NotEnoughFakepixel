package com.nef.notenoughfakepixel.mixin;


import com.nef.notenoughfakepixel.NotEnoughFakepixel;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.events.GuiContainerBackgroundDrawnEvent;
import com.nef.notenoughfakepixel.events.SlotClickEvent;
import com.nef.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import com.nef.notenoughfakepixel.utils.ReforgePair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;


@Mixin(value = GuiContainer.class, priority = 500)
public class MixinGuiContainer extends GuiScreen {

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;color(FFFF)V", ordinal = 1))
    private void drawBackground(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        new GuiContainerBackgroundDrawnEvent(((GuiContainer) (Object) this), partialTicks).post();
    }

    @Inject(method = "drawSlot", at = @At("RETURN"))
    public void drawSlotRet(Slot slotIn, CallbackInfo ci) {
        SlotLocking.getInstance().drawSlot(slotIn);
    }


    @Shadow
    private Slot theSlot;

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGradientRect(IIIIII)V"))
    public void drawScreen_drawGradientRect(
            GuiContainer container,
            int left,
            int top,
            int right,
            int bottom,
            int startColor,
            int endColor
    ) {
        if (startColor == 0x80ffffff && endColor == 0x80ffffff &&
                theSlot != null && SlotLocking.getInstance().isSlotLocked(theSlot)) {
            int col = 0x80ff8080;
            drawGradientRect(left, top, right, bottom, col, col);
        } else {
            drawGradientRect(left, top, right, bottom, startColor, endColor);
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(CallbackInfo ci) {
        if (theSlot != null && SlotLocking.getInstance().isSlotLocked(theSlot)) {
            SlotLocking.getInstance().setRealSlot(theSlot);
            theSlot = null;
        } else if (theSlot == null) {
            SlotLocking.getInstance().setRealSlot(null);
        }
    }

    @Inject(method = "checkHotbarKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;handleMouseClick(Lnet/minecraft/inventory/Slot;III)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void checkHotbarKeys_Slotlock(int keyCode, CallbackInfoReturnable<Boolean> cir, int i) {
        if (SlotLocking.getInstance().isSlotIndexLocked(i)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "handleMouseClick", at = @At(value = "HEAD"), cancellable = true)
    public void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (slotIn == null) return;
        GuiContainer $this = (GuiContainer) (Object) this;
        SlotClickEvent event = new SlotClickEvent($this, slotIn, slotId, clickedButton, clickType);
        event.post();
        if (event.isCanceled()) {
            ci.cancel();
            return;
        }
        if (event.usePickblockInstead) {
            $this.mc.playerController.windowClick(
                    $this.inventorySlots.windowId,
                    slotId, 2, 3, $this.mc.thePlayer
            );
            ci.cancel();
        }
    }

    private ReforgePair reforgeToRender;

    @Inject(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemOverlayIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void drawSlot(Slot slotIn, CallbackInfo ci, int x, int y, ItemStack item, boolean flag, boolean flag1,
                          ItemStack itemstack1, String s) {
        if (!Config.feature.qol.qolReforgeHelper) return;

        if (slotIn.inventory.getDisplayName().getUnformattedText().equals("Reforge Item") && slotIn.slotNumber == 13) {
            if (item != null && item.hasDisplayName()) {
                String[] nameParts = item.getDisplayName().split(" ");
                if (nameParts.length > 2) {
                    String reforge = NotEnoughFakepixel.instance.getUtils().stripColor(nameParts[0]);
                    String displayReforge;
                    String lockedReforge = NotEnoughFakepixel.instance.getUtils().getLockedEnchantment();
                    if (lockedReforge.length() > 0 && reforge.toLowerCase().contains(lockedReforge.toLowerCase())) {
                        displayReforge = EnumChatFormatting.RED + reforge;
                    } else {
                        displayReforge = EnumChatFormatting.YELLOW + reforge;
                    }
                    x -= 28;
                    y += 22;
                    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
                    float halfStringWidth = fr.getStringWidth(displayReforge) / 2;
                    reforgeToRender = new ReforgePair(x - halfStringWidth, y, displayReforge);
                }
            }
        }
        if (slotIn.slotNumber == 53) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            if (reforgeToRender != null) {
                FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
                fr.drawStringWithShadow(reforgeToRender.getEnchant(), reforgeToRender.getX(), reforgeToRender.getY(), new Color(255, 255, 255, 255).getRGB());
                reforgeToRender = null;
            }
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }
    }
}
