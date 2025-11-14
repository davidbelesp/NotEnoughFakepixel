package com.nef.notenoughfakepixel.features.capes;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class CapeModel extends ModelBase {
    private final ModelRenderer cape;

    public CapeModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.cape = new ModelRenderer(this, 0, 0);
        this.cape.addBox(-5.0F, 0.0F, 0.0F, 10, 16, 1); // width, height, depth
    }

    public void renderCape(float scale) {
        this.cape.render(scale);
    }
}
