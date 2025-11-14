package com.nef.notenoughfakepixel.events;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

@Setter
@Getter
public class RenderEntityModelEvent extends Event {

    private EntityLivingBase entity;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float headYaw;
    private float headPitch;
    private float scaleFactor;
    private ModelBase model;

    public RenderEntityModelEvent(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scaleFactor, ModelBase model) {
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.headYaw = headYaw;
        this.headPitch = headPitch;
        this.scaleFactor = scaleFactor;
        this.model = model;
    }

}