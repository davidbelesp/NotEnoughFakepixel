package com.nef.notenoughfakepixel.utils;

import com.nef.notenoughfakepixel.config.gui.Config;

import java.io.File;

public enum CustomConfigFiles {

    FAIRY_SOULS(Config.configDirectory + File.separator + "gainedsouls.json"),
    SLOT_LOCKING(Config.configDirectory + File.separator + "slotlocking.json"),
    EQUIPMENTS(Config.configDirectory + File.separator + "equipments.json"),
    STORAGE_FOLDER(Config.configDirectory + File.separator + "storage");
    public String path;

    CustomConfigFiles(String s) {
        path = s;
    }

}
