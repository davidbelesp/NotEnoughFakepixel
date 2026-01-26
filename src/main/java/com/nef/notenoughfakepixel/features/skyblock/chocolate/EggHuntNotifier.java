package com.nef.notenoughfakepixel.features.skyblock.chocolate;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.ChatNotifier;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ListUtils;

import java.util.List;

@RegisterEvents
public class EggHuntNotifier extends ChatNotifier {

    @Override
    public boolean shouldNotify() {
        return false;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public List<String> getMessages() {
        return ListUtils.of(
                "§r§d§lHOPPITY'S HUNT §r§dA §r§6Chocolate Breakfast Egg §r§dhas appeared!§r",
                "§r§d§lHOPPITY'S HUNT §r§dA §r§9Chocolate Lunch Egg §r§dhas appeared!§r",
                "§r§d§lHOPPITY'S HUNT §r§dA §r§aChocolate Dinner Egg §r§dhas appeared!§r"
        );
    }

    @Override
    public String notifyMessage() {
        return "New Egg Hunt Day!";
    }

    @Override
    public void afterDetection(String message) {
        if (SkyblockData.getSeason() != SkyblockData.Season.SPRING) return;
        if (
                ((SkyblockData.getSbHour() == 7 || SkyblockData.getSbHour() == 6) && SkyblockData.isAm()) ||
                        ((SkyblockData.getSbHour() == 2 || SkyblockData.getSbHour() == 1) && !SkyblockData.isAm())
        ) { ChocolateEggTimer.setGoalEpochMs(System.currentTimeMillis() + 350000);}
        else if (
                ((SkyblockData.getSbHour() == 9 || SkyblockData.getSbHour() == 8) && !SkyblockData.isAm())
        ) {
            ChocolateEggTimer.setGoalEpochMs(System.currentTimeMillis() + 500000);
            if (Config.feature.chocolateFactory.huntDayNotifier) {
                notifyUser();
            }

        }
    }

}
