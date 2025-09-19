package org.ginafro.notenoughfakepixel.serverdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ginafro.notenoughfakepixel.events.handlers.RepoHandler;

import java.util.*;
import java.util.stream.Collectors;

@Data public class AccessoriesData {

    public static AccessoriesData INSTANCE = new AccessoriesData();

    public static boolean finalPage = false;
    public static boolean show = false;

    public static int totalMp = 0;
    public static int maxMp = 0;
    public static int maxMpRec = 0;
    private static int bonuses = 0;

    @AllArgsConstructor @Data
    public static class Accessory {
        public String rarity;
        public String name;
    }

    private Set<Accessory> repoAccessories;
    private Set<Accessory> currentAccessories;

    @Data private static class RepoWrapper {
        private Set<Accessory> accessories = Collections.emptySet();
        private int bonuses = 0;
    }

    private static final RepoWrapper STUB = new RepoWrapper();

    public AccessoriesData() {
        RepoWrapper wrapper = RepoHandler.getData("accessories", RepoWrapper.class, STUB);
        this.repoAccessories = wrapper.getAccessories() != null
                ? wrapper.getAccessories()
                : Collections.emptySet();
        this.currentAccessories = new HashSet<>();
        bonuses = wrapper.getBonuses();
    }

    public List<Accessory> getMissingAccessories() {
        return repoAccessories.stream()
                .filter(acc -> {
                    for (Accessory current : currentAccessories) {
                        if (current.getName().equalsIgnoreCase(acc.getName())) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public void addAccessory(Accessory accessory) {
        if (currentAccessories.stream().noneMatch(acc -> acc.getName().equalsIgnoreCase(accessory.getName()))) {
            currentAccessories.add(accessory);
        }
    }

    public void clearAccessories() {
        currentAccessories.clear();
    }

    public static void calculateMp(){
        calculateTotalMp();
        calculateMaxMp();
        calculateMaxRecomb();
    }

    private static int getMpByRarity(String rarity) {
        return getMpByRarity(rarity, false);
    }

    private static int getMpByRarity(String rarity, boolean withRecomb) {
        rarity = rarity.trim();
        if (withRecomb) {
            switch (rarity.toLowerCase()) {
                case "special":
                case "common": return 5;
                case "uncommon": return 8;
                case "rare": return 12;
                case "epic": return 16;
                case "legendary": return 22;
                case "mythic":
                case "divine": return 28;
                default: return 0;
            }
        }
        switch (rarity.toLowerCase()) {
            case "special":
            case "common": return 3;
            case "uncommon": return 5;
            case "rare": return 8;
            case "epic": return 12;
            case "legendary": return 16;
            case "mythic": return 22;
            case "divine": return 28;
            default: return 0;
        }
    }

    public static void calculateTotalMp() {
        totalMp = INSTANCE.getCurrentAccessories().stream()
                .mapToInt(acc -> getMpByRarity(acc.getRarity()))
                .sum();
    }

    public static void calculateMaxMp() {
        maxMp = INSTANCE.getRepoAccessories().stream()
                .mapToInt(acc -> getMpByRarity(acc.getRarity()))
                .sum();
        maxMp += bonuses;
    }

    private static void calculateMaxRecomb() {
        maxMpRec = INSTANCE.getRepoAccessories().stream()
                .mapToInt(acc -> getMpByRarity(acc.getRarity(), true))
                .sum();
        maxMpRec += bonuses;
    }

    public static String getColorLevel(int max) {
        int total = totalMp;
        if (total >= max) return "§b";
        if (total >= 2 * max / 3) return "§a";
        if (total >= max / 3) return "§e";
        return "§c";
    }

}
