package org.ginafro.notenoughfakepixel.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NefCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "nef";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> options = new ArrayList<>();
        if (args == null || args.length == 0) {
            options.add("help");
            options.addAll(getCategoryNames());
            options.add("default");
        } else if (args.length == 1) {
            // Space char case
            if (args[0].isEmpty()) {
                options.add("help");
                options.addAll(getCategoryNames());
                options.add("default");
                return options;
            // Command half written
            } else {
                if ("help".startsWith(args[0]) && !"help".equals(args[0])) {
                    options.add("help");
                }
                if ("default".startsWith(args[0]) && !"default".equals(args[0])) {
                    options.add("default");
                }
                for (String category : getCategoryNames()) {
                    if (category.startsWith(args[0]) && !category.equals(args[0])) {
                        options.add(category);
                    }
                }
            }
        } else if (args.length == 2) {
            if (getCategoryNames().contains(args[0])) {
                if (args[1].isEmpty()) {
                    options.add("help");
                    options.addAll(getFeatureNames(args[0]));
                } else {
                    if ("help".startsWith(args[1]) && !"help".equals(args[1])) {
                        options.add("help");
                    }
                    for (String feature : getFeatureNames(args[0])) {
                        if (feature.startsWith(args[1]) && !feature.equals(args[1])) {
                            options.add(feature);
                        }
                    }
                }
            }
        }
        return options;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String helpDisplay = EnumChatFormatting.YELLOW + "Commands:\n"
                + EnumChatFormatting.WHITE + "/nef <category>" + EnumChatFormatting.GRAY + " : Enable/disable one entire category\n"
                + EnumChatFormatting.WHITE + "/nef <category> help" + EnumChatFormatting.GRAY + " : Display all the features from given category\n"
                + EnumChatFormatting.WHITE + "/nef <category> <feature>" + EnumChatFormatting.GRAY + " : Change the value from given feature\n"
                + EnumChatFormatting.WHITE + "/nef default" + EnumChatFormatting.GRAY + " : Reset all settings to default\n"
                + EnumChatFormatting.WHITE + "/nef help" + EnumChatFormatting.GRAY + " : Display all commands from NEF\n\n"
                + EnumChatFormatting.WHITE + "Possible categories: qol, dungeons, fishing, diana, slayer, experimentation, mining, crimson.";
        if(args == null || args.length == 0){
            //System.out.println("\n\n\n\n\n\n\n"+System.getProperty("os.name"));
            // POJAV version
            if (Configuration.isPojav()) {
                sender.addChatMessage(new ChatComponentText(helpDisplay));
            // PC & others
            } else {
                NotEnoughFakepixel.config.openGui();
            }
        } else if(args.length == 1){
            String category = args[0];

            if (category.equals("help")) {
                sender.addChatMessage(new ChatComponentText(helpDisplay));
            } else if (!category.equals("default")) {
                if (getBooleanVariable("_"+category) == null) return;
                changeBooleanVariable(sender, "_"+category);
                setCategoryVariables(sender, category, getBooleanVariable("_"+category));
            } else {
                setDefault(sender);
            }
        } else if (args.length == 2) {
            String category = args[0];
            String feature = args[1];

            if (feature.equalsIgnoreCase("help")) {
                displayCategoryVariables(sender, category);
                return;
            }
            if (getBooleanVariable("_"+category) == null) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown category: " + category + ". Use \\nef help to display a list of categories available."));
                return;
            }
            updateCategoryVariable(sender, category, category+feature.substring(0, 1).toUpperCase()+feature.substring(1), getBooleanVariable("_"+category));
        } else {
            sender.addChatMessage(new ChatComponentText(helpDisplay));
        }
    }

    // Helper method to update variables based on category
    private boolean updateCategoryVariable(ICommandSender sender, String categoryName, String variableName, boolean categoryEnabled) {
        try {
            if (!categoryEnabled) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Category " + categoryName + " is not enabled"));
                return false;
            }
            java.lang.reflect.Field field = Configuration.class.getDeclaredField(variableName);
            if (field.getType() == boolean.class) {
                boolean newValue = !field.getBoolean(null);
                field.setBoolean(null, newValue);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Feature " + getDifference(categoryName, variableName).substring(0,1).toLowerCase()+getDifference(categoryName, variableName).substring(1) + " " + formatBoolean(newValue)));
                return true;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Feature " + getDifference(categoryName, variableName).substring(0,1).toLowerCase()+getDifference(categoryName, variableName).substring(1) + " not found in category " + categoryName + ". Use \\nef "+categoryName+" help to display a list of available features."));
            return false;
        }
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Feature " + getDifference(categoryName, variableName).substring(0,1).toLowerCase()+getDifference(categoryName, variableName).substring(1) + " not found in category " + categoryName + ". Use \\nef "+categoryName+" help to display a list of available features."));
        return false;
    }

    // Helper method to display all variables in a category
    private void displayCategoryVariables(ICommandSender sender, String category) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Available features in " + category + " category:"));

        try {
            for (java.lang.reflect.Field field : Configuration.class.getDeclaredFields()) {
                if (field.getType() == boolean.class && field.getName().startsWith(category)) {
                    if (Configuration.isPojav() && field.getName().endsWith("Overlay")) continue;
                    field.setAccessible(true);
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "- " + getDifference(category, field.getName()) + " " + formatBoolean((Boolean)field.get(null))));
                }
            }
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Error retrieving variables for category " + category));
        }
    }

    // Helper method to change the value of all boolean variables in a category
    private void setCategoryVariables(ICommandSender sender, String category, boolean value) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Category " + category + " and its features " + formatBoolean(value)));

        try {
            boolean newValue = false;
            boolean isFirst = true; // Determine new value based on the first variable in the category

            for (java.lang.reflect.Field field : Configuration.class.getDeclaredFields()) {
                if (field.getType() == boolean.class && field.getName().startsWith(category)) {
                    if (Configuration.isPojav() && field.getName().endsWith("Overlay")) continue;
                    if (isFirst) {
                        field.setAccessible(true);
                        newValue = value;
                        isFirst = false;
                    }

                    field.setAccessible(true);
                    field.set(null, newValue); // Update the variable value
                }
            }

            if (isFirst) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No variables found for category " + category));
            }
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Error toggling variables for category " + category));
        }
    }

    // Helper method to retrieve a boolean variable by name from the Configuration class
    private Boolean getBooleanVariable(String variableName) {
        try {
            // Iterate through all fields in the Configuration class
            for (java.lang.reflect.Field field : Configuration.class.getDeclaredFields()) {
                // Check if the field matches the variable name and is of type boolean
                if (field.getType() == boolean.class && field.getName().equalsIgnoreCase(variableName)) {
                    if (Configuration.isPojav() && field.getName().endsWith("Overlay")) continue;
                    field.setAccessible(true); // Allow access to private/protected fields
                    return (Boolean) field.get(null); // Return the value of the field
                }
            }
        } catch (Exception e) {
            System.err.println("Error retrieving boolean variable: " + variableName);
            e.printStackTrace();
        }
        return null; // Return null if the variable is not found or an error occurs
    }

    // Helper method to set a boolean variable by name in the Configuration class
    private Boolean changeBooleanVariable(ICommandSender sender, String variableName) {
        try {
            // Iterate through all fields in the Configuration class
            for (java.lang.reflect.Field field : Configuration.class.getDeclaredFields()) {
                // Check if the field matches the variable name and is of type boolean
                if (field.getType() == boolean.class && field.getName().equalsIgnoreCase(variableName)) {
                    if (Configuration.isPojav() && field.getName().endsWith("Overlay")) continue;
                    field.setAccessible(true); // Allow access to private/protected fields
                    // Toggle the current value of the boolean variable
                    boolean currentValue = (Boolean) field.get(null);
                    field.set(null, !currentValue); // Set the new value (toggle it)
                    return true; // Return success
                }
            }
            // If no matching field is found, notify the sender
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Variable '"
                    + variableName + "' not found."));
        } catch (Exception e) {
            // Handle exceptions and notify the sender
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED
                    + "An error occurred while updating the variable '" + variableName + "'."));
            e.printStackTrace();
        }
        return false; // Return failure if the operation fails
    }

    private List<String> getCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        try {
            for (java.lang.reflect.Field field : Configuration.class.getDeclaredFields()) {
                if (field.getType() == boolean.class && field.getName().startsWith("_") && !field.getName().startsWith("_debug") && !field.getName().startsWith("_info") && !field.getName().startsWith("_general")) {
                    if (Configuration.isPojav() && field.getName().endsWith("Overlay")) continue;
                    categoryNames.add(field.getName().substring(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(categoryNames);
        return categoryNames;
    }

    private List<String> getFeatureNames(String category) {
        List<String> featureNames = new ArrayList<>();
        try {
            for (java.lang.reflect.Field field : Configuration.class.getDeclaredFields()) {
                if (field.getType() == boolean.class && field.getName().startsWith(category)) {
                    if (Configuration.isPojav() && field.getName().endsWith("Overlay")) continue;
                    featureNames.add(getDifference(category, field.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(featureNames);
        return featureNames;
    }

    private String formatBoolean(boolean value) {
        return value ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
    }

    private void setDefault(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Configuration changed to default"));
        // QOL
        Configuration._qol = true;
        Configuration.qolDisableHyperionExplosions = true;
        Configuration.qolDisableThunderlordBolt = true;
        Configuration.qolFullbright = true;
        Configuration.qolNoHurtCam = true;
        Configuration.qolCropsHeight = false;
        Configuration.qolDisablePotionEffects = true;
        Configuration.qolDisableRain = true;
        Configuration.qolShortcutWardrobe = true;
        Configuration.qolShortcutPets = true;
        Configuration.qolShortcutWarps = true;
        Configuration.qolShowEnchantLevel = true;
        Configuration.qolMiddleClickChests = true;
        Configuration.qolShowPetEquipped = true;
        Configuration.qolDisableWatchdogInfo = true;
        Configuration.qolDisableFriendJoin = false;
        Configuration.qolChatCleaner = false;
        Configuration.qolDisableSellingRanks = false;
        Configuration.qolDisableZombieRareDrops = true;
        Configuration.qolScrollableTooltips = true;
        Configuration.qolDisableJerryChineGunSounds = true;
        Configuration.qolDisableAoteSounds = false;
        Configuration.qolDisableMidaStaffAnimation = false;
        Configuration.qolDmgCommas = true;
        Configuration.qolDmgFormatter = true;
        Configuration.qolDisableEnderManTeleport = true;
        Configuration.qolVisualCooldowns = true;
        Configuration.qolItemRarity = true;
        Configuration.qolHideDyingMobs = true;
        Configuration.qolBlockPlacingItems = true;
        // Dungeons
        Configuration._dungeons = true;
        Configuration.dungeonsAutoReady = true;
        Configuration.dungeonsAutoCloseChests = true;
        Configuration.dungeonsStarredMobs = 0;
        Configuration.dungeonsBatMobs = true;
        Configuration.dungeonsFelMob = true;
        Configuration.dungeonsThreeWeirdos = true;
        Configuration.dungeonsMap = true;
        Configuration.dungeonsCustomGui = true;
        Configuration.dungeonsTerminalStartsWithSolver = true;
        Configuration.dungeonsTerminalClickInOrderSolver = true;
        Configuration.dungeonsTerminalSelectColorsSolver = true;
        Configuration.dungeonsTerminalWaypoints = true;
        Configuration.dungeonsSpiritLeapGUI = true;
        Configuration.dungeonsBloodReady = true;
        Configuration.dungeonsScoreOverlay = true;
        Configuration.dungeonsSPlusNotifier = true;
        Configuration.dungeonsMuteIrrelevantMessages = true;
        Configuration.dungeonsStarredMobsEsp = true;
        Configuration.dungeonsRotateMap = true;
        Configuration.dungeonsTerminalMazeSolver = true;
        Configuration.dungeonsTerminalCorrectPanesSolver = true;
        Configuration.dungeonsTerminalHideIncorrect = true;
        Configuration.dungeonsPreventMissclicks = true;
        Configuration.dungeonsHideTooltips = true;
        Configuration.dungeonsFirstDeviceSolver = true;
        Configuration.dungeonsThirdDeviceSolver = true;
        Configuration.dungeonsItemSecretsDisplay = true;
        Configuration.dungeonsItemSecretsBig = true;
        Configuration.dungeonsIsPaul = true;
        Configuration.dungeonsKeyTracers = true;
        Configuration.dungeonsSalvageItemsPrevention = true;
        Configuration.dungeonsClearedNotifier = true;
        Configuration.dungeonsSPlusMessage = true;
        Configuration.dungeonsWaterSolver = true;
        // Fishing
        Configuration._fishing = true;
        Configuration.fishingLegendaryCreatures = true;
        Configuration.fishingGreatCatch = true;
        Configuration.fishingTrophyFish = true;
        // Diana
        Configuration.dianaShowWaypointsBurrows = true;
        Configuration.dianaShowLabelsWaypoints = true;
        Configuration.dianaShowTracersWaypoints = true;
        Configuration.dianaGaiaConstruct = true;
        Configuration.dianaSiamese = true;
        Configuration.dianaMinosInquisitorAlert = true;
        Configuration.dianaWaypointSounds = true;
        Configuration.dianaDisableDianaExplosionSounds = false;
        Configuration.dianaCancelCooldownSpadeMessage = true;
        // Slayer
        Configuration._slayer = true;
        Configuration.slayerMinibosses = true;
        Configuration.slayerMinibossSound = true;
        Configuration.slayerMinibossTitle = true;
        Configuration.slayerBosses = true;
        Configuration.slayerShowBeaconPath = true;
        Configuration.slayerFirePillarDisplay = true;
        Configuration.slayerBlazeAttunements = true;
        // Experimentation Table
        Configuration._experimentation = true;
        Configuration.experimentationChronomatronSolver = true;
        Configuration.experimentationUltraSequencerSolver = true;
        Configuration.experimentationPreventMissclicks = true;
        Configuration.experimentationHideTooltips = true;
        // Chocolate Factory
        Configuration.chocolateChocolateShowBestUpgrade = true;
        Configuration.chocolateChocolateEggWaypoints = true;
        // Mining
        Configuration._mining = true;
        Configuration.miningAbilityNotifier = true;
        Configuration.miningDisableDonEspresso = true;
        Configuration.miningDrillFix = true;
        Configuration.miningPuzzlerSolver = true;
        Configuration.miningShowGhosts = true;
        Configuration.miningDrillFuel = true;
        Configuration.miningMithrilPowder = true;
        Configuration.miningAbilityCooldown = true;
        Configuration.miningOverlay = true;
        // Crimson
        Configuration._crimson = true;
        Configuration.crimsonBladesoulNotifier = true;
        Configuration.crimsonMageOutlawNotifier = true;
        Configuration.crimsonAshfangNotifier = true;
        Configuration.crimsonBarbarianDukeXNotifier = true;
        Configuration.crimsonAshfangWaypoint = true;
        Configuration.crimsonGravityOrbWaypoint = true;
        Configuration.crimsonAshfangHitboxes = true;
        Configuration.crimsonAshfangMuteChat = true;
        Configuration.crimsonAshfangMuteSound = true;
        Configuration.crimsonAshfangHurtSound = true;
        Configuration.crimsonAshfangOverlay = true;

        if (Configuration.isPojav()) Configuration.hiddenOverlays();
    }

    private String getDifference(String category, String fieldName) {
        if (fieldName.startsWith(category)) {
            String difference = fieldName.substring(category.length());
            return difference.substring(0, 1).toLowerCase() + difference.substring(1);
        }
        return fieldName; // Return the full fieldName if it doesn't start with category
    }


}
