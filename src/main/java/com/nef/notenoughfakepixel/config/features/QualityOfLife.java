package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.gui.core.config.Position;
import com.nef.notenoughfakepixel.config.gui.core.config.annotations.*;
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
    //@ConfigOption(name = "NEF Capes", desc = "Choose from a variety of capes provided by nef")
    //@ConfigEditorButton(runnableId = "nefCapes", buttonText = "Choose")
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
    @ConfigOption(name = "Golden enchants", desc = "Changes the color of the maxed enchants.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolGoldenEnchants = true;

    @Expose
    @ConfigOption(name = "Hide falling blocks", desc = "Hide falling blocks.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolHideFallingBlocks = false;

    @Expose
    @ConfigOption(name = "Hide Player Armor", desc = "Hide player armor.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolHidePlayerArmor = false;

    @Expose
    @ConfigOption(name = "Hide Players near NPCS", desc = "Hide players near NPCS.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 1)
    public boolean qolHidePlayerNearNpcs = false;

    // Etherwarp Subcategory
    @Expose
    @ConfigOption(name = "Etherwarp Settings", desc = "Settings for etherwarp features.")
    @ConfigEditorAccordion(id = 3)
    public boolean etherwarpAccordion = false;

    @Expose
    @ConfigOption(name = "Etherwarp Zoom", desc = "Zooms in when using etherwarp.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean qolEtherwarpZoom = true;

    @Expose
    @ConfigOption(name = "Etherwarp Overlay", desc = "Show etherwarp overlay.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean qolEtherwarpOverlay = true;

    @Expose
    @ConfigOption(name = "Etherwarp Text Helper", desc = "Shows text helper when etherwarping.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 3)
    public boolean qolEtherwarpText = true;

    @Expose
    @ConfigOption(name = "Etherwarp Overlay Color", desc = "Color of the etherwarp overlay.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 3)
    public String qolEtherwarpOverlayColor = "0:100:0:255:0";

    @Expose
    @ConfigOption(name = "Etherwarp Overlay Failed Color", desc = "Color of the failed etherwarp overlay.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 3)
    public String qolEtherwarpFailedOverlayColor = "0:100:0:255:0";

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
    @ConfigOption(name = "Wardrobe Slots Shortcut", desc = "Enable wardrobe slots {1 - 9} shortcuts.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean qolShortcutSlotsWardrobe = false;

    @Expose
    @ConfigOption(name = "Wardrobe Shortcut Key", desc = "Keybind for wardrobe shortcut.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_R)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey = Keyboard.KEY_R;

    @Expose
    @ConfigOption(name = "Wardrobe Slot 1 Key", desc = "Keybind for wardrobe slot 1.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_1)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey1 = Keyboard.KEY_1;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 2 Key", desc = "Keybind for wardrobe slot 2.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_2)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey2 = Keyboard.KEY_2;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 3 Key", desc = "Keybind for wardrobe slot 3.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_3)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey3 = Keyboard.KEY_3;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 4 Key", desc = "Keybind for wardrobe slot 4.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_4)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey4 = Keyboard.KEY_4;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 5 Key", desc = "Keybind for wardrobe slot 5.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_5)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey5 = Keyboard.KEY_5;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 6 Key", desc = "Keybind for wardrobe slot 6.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_6)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey6 = Keyboard.KEY_6;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 7 Key", desc = "Keybind for wardrobe slot 7.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_7)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey7 = Keyboard.KEY_7;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 8 Key", desc = "Keybind for wardrobe slot 8.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_8)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey8 = Keyboard.KEY_8;
    @Expose
    @ConfigOption(name = "Wardrobe Slot 9 Key", desc = "Keybind for wardrobe slot 9.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_9)
    @ConfigAccordionId(id = 4)
    public int qolWardrobeKey9 = Keyboard.KEY_9;

    public void resetWardrobeSlotKeybinds() {
        qolWardrobeKey = Keyboard.KEY_R;
        qolWardrobeKey1 = Keyboard.KEY_1;
        qolWardrobeKey2 = Keyboard.KEY_2;
        qolWardrobeKey3 = Keyboard.KEY_3;
        qolWardrobeKey4 = Keyboard.KEY_4;
        qolWardrobeKey5 = Keyboard.KEY_5;
        qolWardrobeKey6 = Keyboard.KEY_6;
        qolWardrobeKey7 = Keyboard.KEY_7;
        qolWardrobeKey8 = Keyboard.KEY_8;
        qolWardrobeKey9 = Keyboard.KEY_9;
    }

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
    @ConfigOption(name = "Equipment Shortcut", desc = "Enable Equipment shortcut.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 4)
    public boolean qolShortcutEq = false;

    @Expose
    @ConfigOption(name = "Equipment Shortcut Key", desc = "Keybind for Equipment shortcut.")
    @ConfigEditorKeybind(defaultKey = Keyboard.KEY_P)
    @ConfigAccordionId(id = 4)
    public int qolEqKey = Keyboard.KEY_U;

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

    // Item Salvaging Subcategory

    @Expose
    @ConfigOption(name = "Item Salvaging", desc = "Settings for item salvaging.")
    @ConfigEditorAccordion(id = 8)
    public boolean itemSalvagingAccordion = false;

    @Expose
    @ConfigOption(name = "Mythic Prevention", desc = "Prevent salvaging MYTHIC items.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 8)
    public boolean salvageMythicPrevention = true;

    @Expose
    @ConfigOption(name = "Legendary Prevention", desc = "Prevent salvaging LEGENDARY items.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 8)
    public boolean salvageLegendaryPrevention = true;

    @Expose
    @ConfigOption(name = "Epic Prevention", desc = "Prevent salvaging EPIC items.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 8)
    public boolean salvageEpicPrevention = false;

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
    public boolean qolMiddleClickChests = false;

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
    @ConfigOption(name = "Full Block Button", desc = "Make Buttons full blocks.")
    @ConfigEditorBoolean
    public boolean qolFullBlockButton = true;

    @Expose
    @ConfigOption(name = "Block Placing Items", desc = "Prevent items from being placed as blocks.")
    @ConfigEditorBoolean
    public boolean qolBlockPlacingItems = true;

    @Expose
    @ConfigOption(name = "Item Data", desc = "Adds up item data to item tooltips.")
    @ConfigEditorAccordion(id = 12)
    public boolean qolItemTooltipAccordion = false;

    @Expose
    @ConfigOption(name = "Show Admin Tag on Admin Items", desc = "Shows §cADMIN §7tag on admin items description.")
    @ConfigAccordionId(id = 12)
    @ConfigEditorBoolean
    public boolean qolShowAdminTag = true;

    @Expose
    @ConfigOption(name = "Shows Stacking Counter", desc = "Shows stacking counter in the item description.")
    @ConfigAccordionId(id = 12)
    @ConfigEditorBoolean
    public boolean qolShowStackingCounter = true;

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
    @ConfigOption(name = "Show Active Wardrobe slot", desc = "Show active wardrobe slot.")
    @ConfigEditorBoolean
    public boolean qolShowWardrobeSlot = true;

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
    @ConfigOption(name = "Show Relic waypoints", desc = "Arachne relic waypoints.")
    @ConfigEditorBoolean
    public boolean qolRelicWaypoints = false;

    @Expose
    @ConfigOption(name = "Reforge helper", desc = "Reforge helper.")
    @ConfigEditorBoolean
    public boolean qolReforgeHelper = true;

    // End Nodes Subcategory
    @Expose
    @ConfigOption(name = "End Nodes Settings", desc = "Settings for end nodes highlighter.")
    @ConfigEditorAccordion(id = 11)
    public boolean endNodesSettingsAccordion = false;

    @Expose
    @ConfigOption(name = "End Node Highlighter", desc = "Highlight end nodes in the end.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 11)
    public boolean qolEndNodeHighlighter = true;

    @Expose
    @ConfigOption(name = "End Node Color", desc = "Color of end node highlighter.")
    @ConfigEditorColour
    @ConfigAccordionId(id = 11)
    public String endNodeColor = "0:100:0:255:0";

    // Dark Auction Subcategory
    @Expose
    @ConfigOption(name= "Dark Auction Settings", desc = "Settings for Dark Auction features.")
    @ConfigEditorAccordion(id = 9)
    public boolean darkAuctionSettingsAccordion = false;

    @Expose
    @ConfigOption(name = "Dark Auction Timer", desc = "Shows timer to the next Dark Auction.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 9)
    public boolean qolDAtimer = true;

    @Expose
    @ConfigOption(name = "Dark Auction Reminder", desc = "Notifies you when there is 1 minute left for Dark Auction.")
    @ConfigEditorBoolean
    @ConfigAccordionId(id = 9)
    public boolean qolDarkAhNotifier = false;

    @Expose
    @ConfigOption(name = "Dark AH Timer Settings", desc = "Dark Auction timer settings.")
    @ConfigEditorAccordion(id = 10)
    @ConfigAccordionId(id = 9)
    public boolean darkAuctionAccordion = false;

    @Expose
    @ConfigOption(name = "Scale", desc = "Scale of the Dark AH Timer.")
    @ConfigAccordionId(id = 10)
    @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
    public float darkAHTimerScale = 1.0f;

    @Expose
    @ConfigOption(name = "Position", desc = "Position of the Dark AH Timer.")
    @ConfigEditorButton(runnableId = "editDarkAHTimerPos", buttonText = "Edit")
    @ConfigAccordionId(id = 10)
    public String editDarkAHTimerPos = "";

    @Expose
    public Position darkAhTimerPos = new Position(0, 0, true, true);

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