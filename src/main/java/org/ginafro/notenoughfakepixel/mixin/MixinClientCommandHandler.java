package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import org.ginafro.notenoughfakepixel.config.gui.commands.SimpleCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientCommandHandler.class)
public class MixinClientCommandHandler {

    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void requireSlash(ICommandSender sender, String input, CallbackInfoReturnable<Integer> cir) {
        if (input == null) return;
        String msg = input.trim();
        if (msg.isEmpty() || msg.charAt(0) == '/') return;

        int sp = msg.indexOf(' ');
        String first = (sp == -1 ? msg : msg.substring(0, sp));

        if (SimpleCommand.isSlashOnly(first)) {
            cir.setReturnValue(0);
            cir.cancel();
        }
    }



}
