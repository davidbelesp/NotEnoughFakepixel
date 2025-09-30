package org.ginafro.notenoughfakepixel.features.skyblock.overlays.storage.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Saving Data
// Types: echest,backpack
public class StorageData {
    public HashMap<String, List<StorageContainer>> containers = new HashMap<>();

    public StorageContainer getContainer(String type,int containerNum){
        if(containers.containsKey(type)){
            if(!containers.get(type).isEmpty()){
                for(StorageContainer c : containers.get(type)){
                    if(c.containerNumber == containerNum) return c;
                }
            }
        }
        return null;
    }
    public void addContainer(String type,StorageContainer c){
        containers.computeIfAbsent(type, k -> new ArrayList<>()).add(c);
    }
}
