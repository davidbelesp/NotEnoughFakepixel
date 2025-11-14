package com.nef.notenoughfakepixel.features.cosmetics.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import com.nef.notenoughfakepixel.NotEnoughFakepixel;
import com.nef.notenoughfakepixel.features.cosmetics.CosmeticsHandler;
import com.nef.notenoughfakepixel.features.cosmetics.loader.Obj;

import java.io.IOException;

public class Bandana extends CosmeticsHandler {

    private Obj model;

    public Bandana() {
        super("Bandana", CosmeticsType.HAT);
        try {
            model = NotEnoughFakepixel.instance.getObjLoader().loadModel(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("notenoughfakepixel:cosmetics/bandana/bandana.obj")).getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Entity entityIn, ModelRenderer modelRenderer) {
        GlStateManager.pushMatrix();
        if (entityIn.isSneaking()) {
            GlStateManager.translate(0.0, 0.06, 0.0);
        }

        if (modelRenderer.rotateAngleZ != 0.0F) {
            GlStateManager.rotate(modelRenderer.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
        }

        if (modelRenderer.rotateAngleY != 0.0F) {
            GlStateManager.rotate(modelRenderer.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        }

        if (modelRenderer.rotateAngleX != 0.0F) {
            GlStateManager.rotate(modelRenderer.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.translate(-0f, 0.00f, 0.010);
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.scale(0.068F, 0.068F, 0.068F);
        GlStateManager.color(255, 255, 255, 255);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("notenoughfakepixel:cosmetics/bandana/bandana_01.png"));
        NotEnoughFakepixel.instance.getObjLoader().render(model);
        GlStateManager.popMatrix();
    }
}
