package com.nef.notenoughfakepixel.events;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CustomEvent extends Event {

    public boolean post() {
        MinecraftForge.EVENT_BUS.post(this);
        return isCancelable() && isCanceled();
    }

    public void cancel() {
        setCanceled(true);
    }

}
