package org.ginafro.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import org.ginafro.notenoughfakepixel.config.gui.core.config.annotations.*;
import org.lwjgl.input.Keyboard;

public class QualityOfLife {

    @Expose
    @ConfigOption(name = "NEF Alerts", desc = "Create custom alerts when a message is written in chat.")
    @ConfigEditorButton(runnableId = "nefAlerts", buttonText = "Edit")
    public String nefAlerts = "";

    @Expose
    @ConfigOption(name = "NEF Aliases", desc = "Create custom aliases to run a command with an alias")
    @ConfigEditorButton(runnableId = "nefAlias", buttonText = "Edit")
    public String nefAlias = "";

    @Expose
    @ConfigOption(name = "NEF Capes", desc = "Choose from a variety of capes provided by nef")
    @ConfigEditorButton(runnableId = "nefCapes", buttonText = "Choose")
    public String nefCapes = "";

    // Visual Tweaks Subcategory
    @Expose
    @ConfigOption(name = "Visual Tweaks", desc = "Settings for visual enhancements.")
    @ConfigEditorAccordion(id = 1)
    public boolean visualTweaksAccordion = false;

    @Expose
    @ConfigOption(name = "Fullbright", desc = "Enable fullbright.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolFullbright = true;

    @Expose
    @ConfigOption(name = "Disable Rain", desc = "Disables rain rendering.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolDisableRain = true;

    @Expose
    @ConfigOption(name = "Item Rarity Display", desc = "Show visual circle indicating item rarity.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolItemRarity = true;

    @Expose
    @ConfigOption(name = "Item Rarity Opacity", desc = "Adjust how visible item rarity background is.")
    @ConfigEditorSlider(minValue = 0f, maxValue = 1.0f, minStep = 0.05f)
    @ConfigAccordionId(id = 1)
    public float qolItemRarityOpacity = 0.6f;

    @Expose
    @ConfigOption(name = "Hide Player Armor", desc = "Hide player armor.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolHidePlayerArmor = false;

    // Fairy Soul Subcategory
    @Expose
    @ConfigOption(name = "Fairy Soul Waypoints", desc = "Settings for fairy soul waypoints.")
    @ConfigEditorAccordion(id = 2)
    public boolean fairySoulAccordion = false;

    @Expose
    @ConfigOption(name = "Enable Fairy Soul Waypoints", desc = "Enable fairy soul waypoints.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 2)
    public boolean fairySoulWaypoints = false;

    @Expose
    @ConfigOption(name = "Fairy Soul Waypoints Color", desc = "Color of fairy soul waypoints.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 2)
    public String fairySoulWaypointsColor = "0:100:255:255:255";

    // Etherwarp Subcategory
    @Expose
    @ConfigOption(name = "Etherwarp Settings", desc = "Settings for etherwarp features.")
    @ConfigEditorAccordion(id = 3)
    public boolean etherwarpAccordion = false;

    @Expose
    @ConfigOption(name = "Etherwarp Overlay", desc = "Show etherwarp overlay.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean qolEtherwarpOverlay = true;

    @Expose
    @ConfigOption(name = "Etherwarp Overlay Color", desc = "Color of the etherwarp overlay.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 3)
    public String qolEtherwarpOverlayColor = "0:100:0:255:0";

    @Expose
    @ConfigOption(name = "Etherwarp Sound", desc = "Sound played on etherwarp.")
    @ConfigEditorDropdown(values = {"Default", "mob.blaze.hit", "note.pling", "random.orb", "mob.enderdragon.hit", "mob.cat.meow"})
    @ConfigAccordionId(id = 3)
    public int qolEtherwarpSound = 0;

    // Shortcuts Subcategory
    @Expose
    @ConfigOption(name = "Shortcuts", desc = "Settings for shortcut keybinds.")
    @ConfigEditorAccordion(id = 4)
    public boolean shortcutsAccordion = false;

    @Expose
    @ConfigOption(name = "Wardrobe Shortcut", desc = "Enable wardrobe shortcut.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean qolShortcutWardrobe = false;

    @Expose
    @ConfigOption(name = "Wardrobe Shortcut Key", desc = "Keybind for wardrobe shortcut.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_R)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey = Keyboard.KEY_R;

    @Expose
    @ConfigOption(name = "Pets Shortcut", desc = "Enable pets shortcut.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean qolShortcutPets = false;

    @Expose
    @ConfigOption(name = "Pets Shortcut Key", desc = "Keybind for pets shortcut.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_P)
    @ConfigAccordionId(id = 4)
    public int qolPetsKey = Keyboard.KEY_P;

    @Expose
    @ConfigOption(name = "Warps Shortcuts", desc = "Enable warps shortcuts.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean qolShortcutWarps = false;

    @Expose
    @ConfigOption(name = "Warp Island Shortcut Key", desc = "Keybind for warp island.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F7)
    @ConfigAccordionId(id = 4)
    public int qolShortcutWarpIs = Keyboard.KEY_F7;

    @Expose
    @ConfigOption(name = "Warp Hub Shortcut Key", desc = "Keybind for warp hub.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F8)
    @ConfigAccordionId(id = 4)
    public int qolShortcutWarpHub = Keyboard.KEY_F8;

    @Expose
    @ConfigOption(name = "Warp Dungeon Hub Shortcut Key", desc = "Keybind for warp dungeon hub.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F9)
    @ConfigAccordionId(id = 4)
    public int qolShortcutWarpDh = Keyboard.KEY_F9;

    // Sound Tweaks Subcategory
    @Expose
    @ConfigOption(name = "Sound Tweaks", desc = "Settings for sound modifications.")
    @ConfigEditorAccordion(id = 5)
    public boolean soundTweaksAccordion = false;

    @Expose
    @ConfigOption(name = "Disable Jerry-chine Gun Sounds", desc = "Disable Jerry-chine gun sounds.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 5)
    public boolean qolDisableJerryChineGunSounds = true;

    @Expose
    @ConfigOption(name = "Disable AOTE Teleport Sounds", desc = "Disable Aspect of the End teleport sounds.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 5)
    public boolean qolDisableAoteSounds = false;

    @Expose
    @ConfigOption(name = "Disable Hyperion Explosion", desc = "Disable Hyperion explosion effects.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 5)
    public boolean qolDisableHyperionExplosions = true;

    @Expose
    @ConfigOption(name = "Disable Thunderlord Bolt", desc = "Disable Thunderlord bolt effects.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 5)
    public boolean qolDisableThunderlordBolt = true;

    @Expose
    @ConfigOption(name = "Minimum Midas Staff Animation and Sounds", desc = "Reduce Midas Staff animation and sounds.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 5)
    public boolean qolDisableMidaStaffAnimation = false;

    // Item Animation Subcategory
    @Expose
    @ConfigOption(name = "Item Animation", desc = "Settings for item animation adjustments.")
    @ConfigEditorAccordion(id = 6)
    public boolean itemAnimationAccordion = false;

    @Expose
    @ConfigOption(name = "Item Animation Toggle", desc = "Change the look of your held item.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 6)
    public boolean customAnimations = false;

    @Expose
    @ConfigOption(name = "Item Animation Size", desc = "Scales the size of your currently held item. Default: 0")
    @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
    @ConfigAccordionId(id = 6)
    public float customSize = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Scale Swing", desc = "Also scale the size of the swing animation.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 6)
    public boolean doesScaleSwing = true;

    @Expose
    @ConfigOption(name = "Item Animation X", desc = "Moves the held item. Default: 0")
    @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
    @ConfigAccordionId(id = 6)
    public float customX = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Y", desc = "Moves the held item. Default: 0")
    @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
    @ConfigAccordionId(id = 6)
    public float customY = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Z", desc = "Moves the held item. Default: 0")
    @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
    @ConfigAccordionId(id = 6)
    public float customZ = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Yaw", desc = "Rotates your held item. Default: 0")
    @ConfigEditorSlider(minValue = -180f, maxValue = 180f, minStep = 1f)
    @ConfigAccordionId(id = 6)
    public float customYaw = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Pitch", desc = "Rotates your held item. Default: 0")
    @ConfigEditorSlider(minValue = -180f, maxValue = 180f, minStep = 1f)
    @ConfigAccordionId(id = 6)
    public float customPitch = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Roll", desc = "Rotates your held item. Default: 0")
    @ConfigEditorSlider(minValue = -180f, maxValue = 180f, minStep = 1f)
    @ConfigAccordionId(id = 6)
    public float customRoll = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Speed", desc = "Speed of the swing animation.")
    @ConfigEditorSlider(minValue = -2f, maxValue = 1f, minStep = 0.05f)
    @ConfigAccordionId(id = 6)
    public float customSpeed = 0f;

    @Expose
    @ConfigOption(name = "Item Animation Ignore Haste", desc = "Makes the chosen speed override haste modifiers.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 6)
    public boolean ignoreHaste = true;

    @Expose
    @ConfigOption(name = "Item Animation Drinking Fix", desc = "Pick how to handle drinking animations.")
    @ConfigEditorDropdown(values = {"No fix", "Rotationless", "Fixed"})
    @ConfigAccordionId(id = 6)
    public int drinkingSelector = 2;

    @Expose
    @ConfigOption(name = "Item Animation Reset Item Values", desc = "Vanilla Look! Closes Settings GUI.")
    @ConfigEditorButton(runnableId = "resetItemValues", buttonText = "Reset!")
    @ConfigAccordionId(id = 6)
    public String resetItemValuesButton = "";

    // Player Size Subcategory

    @Expose
    @ConfigOption(name = "Player Size Settings", desc = "Settings for player size adjustments.")
    @ConfigEditorAccordion(id = 7)
    public boolean playerSizeAccordion = false;
    @Expose
    @ConfigOption(name = "Skytils's smolpeople", desc = "Enable smolpeople")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 7)
    public boolean smolPeople = false;

    @Expose
    @ConfigOption(name = "1.12 Crops Height", desc = "Use 1.12 crops height.")
    @ConfigEditorBoolean
    public boolean qolCropsHeight = false;

    @Expose
    @ConfigOption(name = "Disable Block Breaking Particles", desc = "Disable block breaking particles.")
    @ConfigEditorBoolean
    public boolean qolHideBlockBreakingParticles = false;

    @Expose
    @ConfigOption(name = "Disable Potion Effects in Inventory", desc = "Disable potion effects in inventory.")
    @ConfigEditorBoolean
    public boolean qolDisablePotionEffects = true;

    @Expose
    @ConfigOption(name = "Middle Click on skyblock GUIs", desc = "Enable middle click on skyblock GUIs.")
    @ConfigEditorBoolean
    public boolean qolMiddleClickChests = true;

    @Expose
    @ConfigOption(name = "Visual Cooldowns", desc = "Use weapon durability as a cooldown timer.")
    @ConfigEditorBoolean
    public boolean qolVisualCooldowns = true;

    @Expose
    @ConfigOption(name = "Disable Enderman Teleport", desc = "Disable enderman teleportation.")
    @ConfigEditorBoolean
    public boolean qolDisableEnderManTeleport = true;

    @Expose
    @ConfigOption(name = "Full Block Lever", desc = "Make levers full blocks.")
    @ConfigEditorBoolean
    public boolean qolFullBlockLever = true;

    @Expose
    @ConfigOption(name = "Block Placing Items", desc = "Prevent items from being placed as blocks.")
    @ConfigEditorBoolean
    public boolean qolBlockPlacingItems = true;

    @Expose
    @ConfigOption(name = "Show Pet Equipped", desc = "Show equipped pet.")
    @ConfigEditorBoolean
    public boolean qolShowPetEquipped = true;

    @Expose
    @ConfigOption(name = "Pet Equipped Color", desc = "Color of equipped pet indicator.")
    @ConfigEditorColour
    public String qolPetEquippedColor = "0:190:0:255:0";

    @Expose
    @ConfigOption(name = "Show Unclaimed Contests", desc = "Show unclaimed Jacob rewards.")
    @ConfigEditorBoolean
    public boolean qolShowJacobRewards = true;

    @Expose
    @ConfigOption(name = "Disable Watchdog & Info Messages", desc = "Disable watchdog and info messages.")
    @ConfigEditorBoolean
    public boolean qolDisableWatchdogInfo = true;

    @Expose
    @ConfigOption(name = "Disable Friend Join/Left Messages", desc = "Disable friend join/left messages.")
    @ConfigEditorBoolean
    public boolean qolDisableFriendJoin = false;

    @Expose
    @ConfigOption(name = "Disable Zombie Rare Drops Messages", desc = "Disable zombie rare drops messages.")
    @ConfigEditorBoolean
    public boolean qolDisableZombieRareDrops = true;

    @Expose
    @ConfigOption(name = "Disable 'Selling Ranks' Messages", desc = "Disable 'selling ranks' messages.")
    @ConfigEditorBoolean
    public boolean qolDisableSellingRanks = false;

    @Expose
    @ConfigOption(name = "No Hurt Camera", desc = "Disable hurt camera effect.")
    @ConfigEditorBoolean
    public boolean qolNoHurtCam = true;

    @Expose
    @ConfigOption(name = "Hide Flaming Fists", desc = "Hide flaming fists.")
    @ConfigEditorBoolean
    public boolean qolHideFlamingFists = false;

    @Expose
    @ConfigOption(name = "Hide Dead Mobs", desc = "Hide dead mobs.")
    @ConfigEditorBoolean
    public boolean qolHideDyingMobs = true;

    @Expose
    @ConfigOption(name = "Damage Commas", desc = "Add commas to damage numbers.")
    @ConfigEditorBoolean
    public boolean qolDmgCommas = false;

    @Expose
    @ConfigOption(name = "Damage Formatter", desc = "Format damage numbers (e.g., 167k instead of 167000).")
    @ConfigEditorBoolean
    public boolean qolDmgFormatter = false;

    // Method to handle the reset button functionality
    public void resetItemValues() {
        customSize = 0f;
        customX = 0f;
        customY = 0f;
        customZ = 0f;
        customRoll = 0f;
        customPitch = 0f;
        customYaw = 0f;
        doesScaleSwing = true;
        ignoreHaste = true;
        customSpeed = 0f;
    }
}