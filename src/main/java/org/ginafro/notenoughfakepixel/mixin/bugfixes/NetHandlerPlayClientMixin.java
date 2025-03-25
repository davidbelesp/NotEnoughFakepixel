package org.ginafro.notenoughfakepixel.mixin.bugfixes;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

/// Thanks to Patcher for providing this mixin
/// [...](https://github.com/Sk1erLLC/Patcher/blob/4ce6e196e5ad1339f8a0ab96eb5680c2f6464583/src/main/java/club/sk1er/patcher/mixins/bugfixes/network/NetHandlerPlayClientMixin_SignChatSpam.java)

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {
    //#if MC==10809
    @Redirect(
            method = "handleUpdateSign",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0)
    )
    private void removeSignDebugMessage(EntityPlayerSP instance, IChatComponent component) {
        // No-op
    }
    //#endif
}
