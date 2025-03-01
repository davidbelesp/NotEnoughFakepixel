package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.IChatComponent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.mobs.StarredMobDisplay;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.DamageCommas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.ginafro.notenoughfakepixel.events.RenderEntityModelEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> {

    @Shadow
    protected ModelBase mainModel;

    @Shadow
    protected FloatBuffer brightnessBuffer;

    @Final
    @Shadow
    private static DynamicTexture textureBrightness;

    StarredMobDisplay starredMobDisplay = new StarredMobDisplay();
    Set<EntityLivingBase> entities = starredMobDisplay.getCurrentEntities();

    @Redirect(method = "renderName*", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/entity/EntityLivingBase;getDisplayName()Lnet/minecraft/util/IChatComponent;"))
    public IChatComponent renderName_getDisplayName(EntityLivingBase entity) {
        if (entity instanceof EntityArmorStand) {
            return DamageCommas.replaceName(entity);
        } else {
            return entity.getDisplayName();
        }
    }

    @Inject(method = "setBrightness", at = @At(value = "HEAD"), cancellable = true)
    private <T extends EntityLivingBase> void setBrightness(T entity, float partialTicks, boolean combineTextures, CallbackInfoReturnable<Boolean> cir) {
        if (entities.contains(entity)) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
            glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
            glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
            glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
            glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
            this.brightnessBuffer.position(0);
            Color color = new Color(
                    Configuration.dungeonsStarredBoxColor.getRed(),
                    Configuration.dungeonsStarredBoxColor.getGreen(),
                    Configuration.dungeonsStarredBoxColor.getBlue(),
                    Configuration.dungeonsStarredBoxColor.getAlpha()
            );
            brightnessBuffer.put(color.getRed() / 255f);
            brightnessBuffer.put(color.getGreen() / 255f);
            brightnessBuffer.put(color.getBlue() / 255f);
            brightnessBuffer.put(color.getAlpha() / 255f);
            this.brightnessBuffer.flip();
            glTexEnv(8960, 8705, this.brightnessBuffer);
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(textureBrightness.getGlTextureId());
            glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
            glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
            glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
            glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;renderLayers(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onPostRenderLayers(T entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        float limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
        float limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
        float ageInTicks = entity.ticksExisted + partialTicks;
        float headYaw = entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * partialTicks;
        float headPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        float scaleFactor = 0.0625F;

        if (MinecraftForge.EVENT_BUS.post(new RenderEntityModelEvent(
                entity, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scaleFactor, mainModel
        ))) {
            // Cancel further rendering if needed
        }
    }
}