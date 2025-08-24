package org.ginafro.notenoughfakepixel.mixin;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.events.RenderEntityModelEvent;
import org.ginafro.notenoughfakepixel.features.skyblock.diana.Diana;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.mobs.LividDisplay;
import org.ginafro.notenoughfakepixel.features.skyblock.dungeons.mobs.StarredMobDisplay;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.DamageCommas;
import org.ginafro.notenoughfakepixel.features.skyblock.slayers.BlazeAttunements;
import org.ginafro.notenoughfakepixel.features.skyblock.slayers.SlayerMobsDisplay;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import static org.lwjgl.opengl.GL11.glTexEnv;
import static org.lwjgl.opengl.GL11.glTexEnvi;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> {

    @Shadow
    protected ModelBase mainModel;

    @Shadow
    protected FloatBuffer brightnessBuffer;

    @Final
    @Shadow
    private static DynamicTexture textureBrightness;

    private final StarredMobDisplay notEnoughFakepixel$starredMobDisplay = new StarredMobDisplay();
    private final Diana notEnoughFakepixel$diana = new Diana();
    private final LividDisplay notEnoughFakepixel$lividDisplay = new LividDisplay();
    private final SlayerMobsDisplay notEnoughFakepixel$slayerMobsDisplay = new SlayerMobsDisplay();
    private final BlazeAttunements notEnoughFakepixel$blaze = new BlazeAttunements();

    private final Set<EntityLivingBase> notEnoughFakepixel$starredEntities = notEnoughFakepixel$starredMobDisplay.getCurrentEntities();
    private final Set<EntityLivingBase> notEnoughFakepixel$inqEntities = notEnoughFakepixel$diana.getCurrentEntities();
    private final Set<EntityLivingBase> notEnoughFakepixel$lividEntities = notEnoughFakepixel$lividDisplay.getLividEntity();
    private final Set<EntityLivingBase> notEnoughFakepixel$slayerEntities = notEnoughFakepixel$slayerMobsDisplay.getSlayerEntity();
    private final Set<EntityLivingBase> notEnoughFakepixel$slayerMiniEntities = notEnoughFakepixel$slayerMobsDisplay.getSlayerMiniEntity();
    private final Set<EntityLivingBase> notEnoughFakepixel$blazeEntities = notEnoughFakepixel$blaze.getBlazeEntity();

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
    private void setBrightness(T entity, float partialTicks, boolean combineTextures, CallbackInfoReturnable<Boolean> cir) {
        if (shouldApplyBrightnessBoost(entity)) {
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
            Color color = getBrightnessColor(entity);
            if (color == null) {
                color = Color.WHITE;
            }
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

    private boolean shouldApplyBrightnessBoost(T entity) {
        // Starred Mobs
        if (notEnoughFakepixel$starredEntities.contains(entity)) {
            return true;
        }

        // Minos Inquisitor
        if (notEnoughFakepixel$inqEntities.contains(entity)) {
            return true;
        }

        // Livid
        if (notEnoughFakepixel$lividEntities.contains(entity)) {
            return true;
        }

        // Slayer Bosses and Minibosses
        if (notEnoughFakepixel$slayerEntities.contains(entity)) {
            return true;
        }
        if (notEnoughFakepixel$slayerMiniEntities.contains(entity)) {
            return true;
        }

        // Blaze Attunements
        if (notEnoughFakepixel$blazeEntities.contains(entity)) {
            return true;
        }

        // Dungeon Withers
        if (Config.feature.dungeons.dungeonsWithersBox && entity instanceof EntityWither) {
            String name = net.minecraft.util.EnumChatFormatting.getTextWithoutFormattingCodes(entity.getName());
            return name != null && (name.equals("Maxor") || name.equals("Storm") || name.equals("Goldor") || name.equals("Necron"));
        }

        return false;
    }

    private Color getBrightnessColor(T entity) {
        if (notEnoughFakepixel$starredEntities.contains(entity)) {
            return ColorUtils.getColor(Config.feature.dungeons.dungeonsStarredBoxColor);
        }

        if (notEnoughFakepixel$inqEntities.contains(entity)) {
            return ColorUtils.getColor(Config.feature.diana.dianaInqOutlineColor);
        }

        if (notEnoughFakepixel$lividEntities.contains(entity)) {
            return new Color(LividDisplay.LIVID_COLOUR);
        }

        if (notEnoughFakepixel$slayerEntities.contains(entity)) {
            return ColorUtils.getColor(Config.feature.slayer.slayerBossColor);
        }

        if (notEnoughFakepixel$slayerMiniEntities.contains(entity)) {
            return ColorUtils.getColor(Config.feature.slayer.slayerColor);

        }

        if (notEnoughFakepixel$blazeEntities.contains(entity)) {
            List<Entity> armorStands = entity.worldObj.getEntitiesWithinAABB(
                    EntityArmorStand.class,
                    entity.getEntityBoundingBox().offset(0, 2.0, 0).expand(1.0, 1.0, 1.0)
            );
            for (Entity armorStand : armorStands) {
                if (armorStand instanceof EntityArmorStand) {
                    String displayName = armorStand.getDisplayName().getUnformattedText();
                    Matcher matcher = BlazeAttunements.COLOR_PATTERN.matcher(displayName);
                    if (matcher.find()) {
                        String attunement = matcher.group().toUpperCase();
                        int colorInt = BlazeAttunements.getColorForAttunement(attunement);
                        if (colorInt != -1) {
                            return new Color(colorInt);
                        }
                    }
                }
            }
        }

        if (Config.feature.dungeons.dungeonsWithersBox && entity instanceof EntityWither) {
            String name = net.minecraft.util.EnumChatFormatting.getTextWithoutFormattingCodes(entity.getName());
            if (name != null && (name.equals("Maxor") || name.equals("Storm") || name.equals("Goldor") || name.equals("Necron"))) {
                return ColorUtils.getColor(Config.feature.dungeons.dungeonsWithersBoxColor);
            }
        }

        return null;
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