package com.nef.notenoughfakepixel.features.skyblock.diana;

import net.minecraft.entity.Entity;

import java.util.Arrays;
import java.util.List;

// set

public class SiameseLynx {
    private final List<Entity> entities;
    private Entity entity1;
    private Entity entity2;
    private int siameseHit;
    private Entity hittable;

    public SiameseLynx(Entity entity) {
        this.siameseHit = 0;
        this.entity1 = entity;
        this.hittable = entity;
        entities = Arrays.asList(entity, null); // entity2
    }

    public void setEntity1(Entity entity) {
        this.entity1 = entity;
        entities.set(0, entity);
        if (entity == null) setHittable(this.entity2);
    }

    public void setEntity2(Entity entity) {
        this.entity2 = entity;
        //entities.add(1, entity);
        entities.set(1, entity);
        if (entity == null) setHittable(this.entity1);
    }

    public Entity getEntity1() {
        return entity1;
    }

    public Entity getEntity2() {
        return entity2;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addHit() {
        if (!isAnyDead()) {
            siameseHit = (siameseHit + 1) % 2;
        }
    }

    public void setHit(int hit) {
        siameseHit = hit;
    }

    public Entity getSiameseHittable() {
        return entities.get(siameseHit);
    }

    public boolean isAnyDead() {
        return entity1 == null || entity2 == null;
    }

    public void setHittable(Entity hittable) {
        this.hittable = hittable;
    }

    public Entity getHittable() {
        return hittable;
    }
}
