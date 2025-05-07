package org.ginafro.notenoughfakepixel.features.capes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class CapePreviewRenderer {

    private static final Minecraft mc = Minecraft.getMinecraft();


    public static void drawCape(int x, int y, int scale, Cape cape) {
        ResourceLocation capeTexture = CapeManager.getCapeTexture(cape);

        if (capeTexture == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        CapeModel capeModel = new CapeModel();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.translate(x, y, 50F);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(180F, 0F, 0F, 0F);
        GlStateManager.rotate(180F, 0F, 0f, 0F);

        mc.getTextureManager().bindTexture(capeTexture);
        capeModel.renderCape(0.0625F);
        GlStateManager.popMatrix();

    }
}
