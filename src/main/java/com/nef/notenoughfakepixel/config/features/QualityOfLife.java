package com.nef.notenoughfakepixel.config.features;

import com.google.gson.annotations.Expose;
import com.nef.notenoughfakepixel.config.position.Position;
import io.github.notenoughupdates.moulconfig.annotations.*;
import org.lwjgl.input.Keyboard;

public class QualityOfLife {

    @Expose
    @ConfigOption(name = "NEF Alerts", desc = "Create custom alerts when a message is written in chat.")
    @ConfigEditorButton(runnableId = 24, buttonText = "Edit")
    public String nefAlerts = "";

    @Expose
    @ConfigOption(name = "NEF Aliases", desc = "Create custom aliases to run a command with an alias")
    @ConfigEditorButton(runnableId = 25, buttonText = "Edit")
    public String nefAlias = "";

    @Expose
    //@ConfigOption(name = "NEF Capes", desc = "Choose from a variety of capes provided by nef")
    //@ConfigEditorButton(runnableId = "nefCapes", buttonText = "Choose")
    public String nefCapes = "";

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
    @ConfigOption(name = "Disable Server Announcements", desc = "Disable Fakepixel announcement/promo spam messages (help, store, discord, report, social media, etc.).")
    @ConfigEditorBoolean
    public boolean qolDisableServerAnnouncements = true;

    @Expose
    @ConfigOption(name = "Disable Game Invite Spam", desc = "Disable server-wide game invitation messages (e.g. 'invites you to play BedWars Solo').")
    @ConfigEditorBoolean
    public boolean qolDisableGameInvites = true;

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

    @Expose
    @ConfigOption(name = "Enable Smooth AOTE", desc = "Enables smooth AOTE teleport animation.")
    @ConfigEditorBoolean
    public boolean enableSmoothAote = false;

    @Expose
    @Category(name = "Visual Tweaks", desc = "Settings for visual enhancements.")
    public VisualTweaks visualTweaks = new VisualTweaks();

    @Expose
    @Category(name = "Pingless Cactus", desc = "Client-side cactus breaking prediction for Cactus Knives.")
    public PinglessCactusSettings pinglessCactus = new PinglessCactusSettings();

    @Expose
    @Category(name = "Etherwarp Settings", desc = "Settings for etherwarp features.")
    public EtherwarpSettings etherwarpSettings = new EtherwarpSettings();

    @Expose
    @Category(name = "Shortcuts", desc = "Settings for shortcut keybinds.")
    public Shortcuts shortcuts = new Shortcuts();

    @Expose
    @Category(name = "Sound Tweaks", desc = "Settings for sound modifications.")
    public SoundTweaks soundTweaks = new SoundTweaks();

    @Expose
    @Category(name = "Item Animation", desc = "Settings for item animation adjustments.")
    public ItemAnimation itemAnimation = new ItemAnimation();

    @Expose
    @Category(name = "Item Salvaging", desc = "Settings for item salvaging.")
    public ItemSalvaging itemSalvaging = new ItemSalvaging();

    @Expose
    @Category(name = "Player Size Settings", desc = "Settings for player size adjustments.")
    public PlayerSizeSettings playerSizeSettings = new PlayerSizeSettings();

    @Expose
    @Category(name = "Item Data", desc = "Adds up item data to item tooltips.")
    public ItemTooltipSettings itemTooltipSettings = new ItemTooltipSettings();

    @Expose
    @Category(name = "End Nodes Settings", desc = "Settings for end nodes highlighter.")
    public EndNodesSettings endNodesSettings = new EndNodesSettings();

    @Expose
    @Category(name = "Dark Auction Settings", desc = "Settings for Dark Auction features.")
    public DarkAuctionSettings darkAuctionSettings = new DarkAuctionSettings();

    public void resetWardrobeSlotKeybinds() {
        shortcuts.qolWardrobeKey = Keyboard.KEY_R;
        shortcuts.qolWardrobeKey1 = Keyboard.KEY_1;
        shortcuts.qolWardrobeKey2 = Keyboard.KEY_2;
        shortcuts.qolWardrobeKey3 = Keyboard.KEY_3;
        shortcuts.qolWardrobeKey4 = Keyboard.KEY_4;
        shortcuts.qolWardrobeKey5 = Keyboard.KEY_5;
        shortcuts.qolWardrobeKey6 = Keyboard.KEY_6;
        shortcuts.qolWardrobeKey7 = Keyboard.KEY_7;
        shortcuts.qolWardrobeKey8 = Keyboard.KEY_8;
        shortcuts.qolWardrobeKey9 = Keyboard.KEY_9;
    }

    public void resetItemValues() {
        itemAnimation.customSize = 0f;
        itemAnimation.customX = 0f;
        itemAnimation.customY = 0f;
        itemAnimation.customZ = 0f;
        itemAnimation.customRoll = 0f;
        itemAnimation.customPitch = 0f;
        itemAnimation.customYaw = 0f;
        itemAnimation.doesScaleSwing = true;
        itemAnimation.ignoreHaste = true;
        itemAnimation.customSpeed = 0f;
    }

    public static class PinglessCactusSettings {

        @Expose
        @ConfigOption(name = "Pingless Cactus", desc = "Remove cactus client-side immediately when using a Cactus Knife.")
        @ConfigEditorBoolean
        public boolean enabled = true;
    }

    public static class VisualTweaks {

            @Expose
            @ConfigOption(name = "Fullbright", desc = "Enable fullbright.")
            @ConfigEditorBoolean
            public boolean qolFullbright = true;

            @Expose
            @ConfigOption(name = "Disable Rain", desc = "Disables rain rendering.")
            @ConfigEditorBoolean
            public boolean qolDisableRain = true;

            @Expose
            @ConfigOption(name = "Item Rarity Display", desc = "Show visual circle indicating item rarity.")
            @ConfigEditorBoolean
            public boolean qolItemRarity = true;

            @Expose
            @ConfigOption(name = "Item Rarity Opacity", desc = "Adjust how visible item rarity background is.")
            @ConfigEditorSlider(minValue = 0f, maxValue = 1.0f, minStep = 0.05f)
            public float qolItemRarityOpacity = 0.6f;

            @Expose
            @ConfigOption(name = "Golden enchants", desc = "Changes the color of the maxed enchants.")
            @ConfigEditorBoolean
            public boolean qolGoldenEnchants = true;

            @Expose
            @ConfigOption(name = "Hide falling blocks", desc = "Hide falling blocks.")
            @ConfigEditorBoolean
            public boolean qolHideFallingBlocks = false;

            @Expose
            @ConfigOption(name = "Hide Player Armor", desc = "Hide player armor.")
            @ConfigEditorBoolean
            public boolean qolHidePlayerArmor = false;

            @Expose
            @ConfigOption(name = "Hide Players near NPCS", desc = "Hide players near NPCS.")
            @ConfigEditorBoolean
            public boolean qolHidePlayerNearNpcs = false;
    }

    public static class EtherwarpSettings {

            @Expose
            @ConfigOption(name = "Etherwarp Zoom", desc = "Zooms in when using etherwarp.")
            @ConfigEditorBoolean
            public boolean qolEtherwarpZoom = true;

            @Expose
            @ConfigOption(name = "Etherwarp Overlay", desc = "Show etherwarp overlay.")
            @ConfigEditorBoolean
            public boolean qolEtherwarpOverlay = true;

            @Expose
            @ConfigOption(name = "Etherwarp Text Helper", desc = "Shows text helper when etherwarping.")
            @ConfigEditorBoolean
            public boolean qolEtherwarpText = true;

            @Expose
            @ConfigOption(name = "Etherwarp Overlay Color", desc = "Color of the etherwarp overlay.")
            @ConfigEditorColour
            public String qolEtherwarpOverlayColor = "0:100:0:255:0";

            @Expose
            @ConfigOption(name = "Etherwarp Overlay Failed Color", desc = "Color of the failed etherwarp overlay.")
            @ConfigEditorColour
            public String qolEtherwarpFailedOverlayColor = "0:100:0:255:0";

            @Expose
            @ConfigOption(name = "Etherwarp Sound", desc = "Sound played on etherwarp.")
            @ConfigEditorDropdown(values = {"Default", "mob.blaze.hit", "note.pling", "random.orb", "mob.enderdragon.hit", "mob.cat.meow"})
            public int qolEtherwarpSound = 0;
    }

    public static class Shortcuts {

            @Expose
            @ConfigOption(name = "Wardrobe Shortcut", desc = "Enable wardrobe shortcut.")
            @ConfigEditorBoolean
            public boolean qolShortcutWardrobe = false;

            @Expose
            @ConfigOption(name = "Wardrobe Slots Shortcut", desc = "Enable wardrobe slots {1 - 9} shortcuts.")
            @ConfigEditorBoolean
            public boolean qolShortcutSlotsWardrobe = false;

            @Expose
            @ConfigOption(name = "Wardrobe Shortcut Key", desc = "Keybind for wardrobe shortcut.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_R)
            public int qolWardrobeKey = Keyboard.KEY_R;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 1 Key", desc = "Keybind for wardrobe slot 1.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_1)
            public int qolWardrobeKey1 = Keyboard.KEY_1;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 2 Key", desc = "Keybind for wardrobe slot 2.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_2)
            public int qolWardrobeKey2 = Keyboard.KEY_2;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 3 Key", desc = "Keybind for wardrobe slot 3.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_3)
            public int qolWardrobeKey3 = Keyboard.KEY_3;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 4 Key", desc = "Keybind for wardrobe slot 4.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_4)
            public int qolWardrobeKey4 = Keyboard.KEY_4;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 5 Key", desc = "Keybind for wardrobe slot 5.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_5)
            public int qolWardrobeKey5 = Keyboard.KEY_5;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 6 Key", desc = "Keybind for wardrobe slot 6.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_6)
            public int qolWardrobeKey6 = Keyboard.KEY_6;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 7 Key", desc = "Keybind for wardrobe slot 7.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_7)
            public int qolWardrobeKey7 = Keyboard.KEY_7;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 8 Key", desc = "Keybind for wardrobe slot 8.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_8)
            public int qolWardrobeKey8 = Keyboard.KEY_8;

            @Expose
            @ConfigOption(name = "Wardrobe Slot 9 Key", desc = "Keybind for wardrobe slot 9.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_9)
            public int qolWardrobeKey9 = Keyboard.KEY_9;

            @Expose
            @ConfigOption(name = "Equipment Shortcut", desc = "Enable Equipment shortcut.")
            @ConfigEditorBoolean
            public boolean qolShortcutEq = false;

            @Expose
            @ConfigOption(name = "Equipment Shortcut Key", desc = "Keybind for Equipment shortcut.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_P)
            public int qolEqKey = Keyboard.KEY_U;

            @Expose
            @ConfigOption(name = "Warps Shortcuts", desc = "Enable warps shortcuts.")
            @ConfigEditorBoolean
            public boolean qolShortcutWarps = false;

            @Expose
            @ConfigOption(name = "Warp Island Shortcut Key", desc = "Keybind for warp island.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F7)
            public int qolShortcutWarpIs = Keyboard.KEY_F7;

            @Expose
            @ConfigOption(name = "Warp Hub Shortcut Key", desc = "Keybind for warp hub.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F8)
            public int qolShortcutWarpHub = Keyboard.KEY_F8;

            @Expose
            @ConfigOption(name = "Warp Dungeon Hub Shortcut Key", desc = "Keybind for warp dungeon hub.")
            @ConfigEditorKeybind(defaultKey = Keyboard.KEY_F9)
            public int qolShortcutWarpDh = Keyboard.KEY_F9;
    }

    public static class SoundTweaks {

            @Expose
            @ConfigOption(name = "Disable Jerry-chine Gun Sounds", desc = "Disable Jerry-chine gun sounds.")
            @ConfigEditorBoolean
            public boolean qolDisableJerryChineGunSounds = true;

            @Expose
            @ConfigOption(name = "Disable AOTE Teleport Sounds", desc = "Disable Aspect of the End teleport sounds.")
            @ConfigEditorBoolean
            public boolean qolDisableAoteSounds = false;

            @Expose
            @ConfigOption(name = "Disable Hyperion Explosion", desc = "Disable Hyperion explosion effects.")
            @ConfigEditorBoolean
            public boolean qolDisableHyperionExplosions = true;

            @Expose
            @ConfigOption(name = "Disable Thunderlord Bolt", desc = "Disable Thunderlord bolt effects.")
            @ConfigEditorBoolean
            public boolean qolDisableThunderlordBolt = true;

            @Expose
            @ConfigOption(name = "Minimum Midas Staff Animation and Sounds", desc = "Reduce Midas Staff animation and sounds.")
            @ConfigEditorBoolean
            public boolean qolDisableMidaStaffAnimation = false;
    }

    public static class ItemAnimation {

            @Expose
            @ConfigOption(name = "Item Animation Toggle", desc = "Change the look of your held item.")
            @ConfigEditorBoolean
            public boolean customAnimations = false;

            @Expose
            @ConfigOption(name = "Item Animation Size", desc = "Scales the size of your currently held item. Default: 0")
            @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
            public float customSize = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Scale Swing", desc = "Also scale the size of the swing animation.")
            @ConfigEditorBoolean
            public boolean doesScaleSwing = true;

            @Expose
            @ConfigOption(name = "Item Animation X", desc = "Moves the held item. Default: 0")
            @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
            public float customX = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Y", desc = "Moves the held item. Default: 0")
            @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
            public float customY = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Z", desc = "Moves the held item. Default: 0")
            @ConfigEditorSlider(minValue = -1.5f, maxValue = 1.5f, minStep = 0.05f)
            public float customZ = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Yaw", desc = "Rotates your held item. Default: 0")
            @ConfigEditorSlider(minValue = -180f, maxValue = 180f, minStep = 1f)
            public float customYaw = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Pitch", desc = "Rotates your held item. Default: 0")
            @ConfigEditorSlider(minValue = -180f, maxValue = 180f, minStep = 1f)
            public float customPitch = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Roll", desc = "Rotates your held item. Default: 0")
            @ConfigEditorSlider(minValue = -180f, maxValue = 180f, minStep = 1f)
            public float customRoll = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Speed", desc = "Speed of the swing animation.")
            @ConfigEditorSlider(minValue = -2f, maxValue = 1f, minStep = 0.05f)
            public float customSpeed = 0f;

            @Expose
            @ConfigOption(name = "Item Animation Ignore Haste", desc = "Makes the chosen speed override haste modifiers.")
            @ConfigEditorBoolean
            public boolean ignoreHaste = true;

            @Expose
            @ConfigOption(name = "Item Animation Drinking Fix", desc = "Pick how to handle drinking animations.")
            @ConfigEditorDropdown(values = {"No fix", "Rotationless", "Fixed"})
            public int drinkingSelector = 2;

            @Expose
            @ConfigOption(name = "Item Animation Reset Item Values", desc = "Vanilla Look! Closes Settings GUI.")
            @ConfigEditorButton(runnableId = 26, buttonText = "Reset!")
            public String resetItemValuesButton = "";
    }

    public static class ItemSalvaging {

            @Expose
            @ConfigOption(name = "Mythic Prevention", desc = "Prevent salvaging MYTHIC items.")
            @ConfigEditorBoolean
            public boolean salvageMythicPrevention = true;

            @Expose
            @ConfigOption(name = "Legendary Prevention", desc = "Prevent salvaging LEGENDARY items.")
            @ConfigEditorBoolean
            public boolean salvageLegendaryPrevention = true;

            @Expose
            @ConfigOption(name = "Epic Prevention", desc = "Prevent salvaging EPIC items.")
            @ConfigEditorBoolean
            public boolean salvageEpicPrevention = false;
    }

    public static class PlayerSizeSettings {

            @Expose
            @ConfigOption(name = "Skytils's smolpeople", desc = "Enable smolpeople")
            @ConfigEditorBoolean
            public boolean smolPeople = false;
    }

    public static class ItemTooltipSettings {

            @Expose
            @ConfigOption(name = "Show Admin Tag on Admin Items", desc = "Shows §cADMIN §7tag on admin items description.")
            @ConfigEditorBoolean
            public boolean qolShowAdminTag = true;

            @Expose
            @ConfigOption(name = "Shows Stacking Counter", desc = "Shows stacking counter in the item description.")
            @ConfigEditorBoolean
            public boolean qolShowStackingCounter = true;
    }

    public static class EndNodesSettings {

            @Expose
            @ConfigOption(name = "End Node Highlighter", desc = "Highlight end nodes in the end.")
            @ConfigEditorBoolean
            public boolean qolEndNodeHighlighter = true;

            @Expose
            @ConfigOption(name = "End Node Color", desc = "Color of end node highlighter.")
            @ConfigEditorColour
            public String endNodeColor = "0:100:0:255:0";
    }

    public static class DarkAuctionSettings {

            @Expose
            @ConfigOption(name = "Dark Auction Timer", desc = "Shows timer to the next Dark Auction.")
            @ConfigEditorBoolean
            public boolean qolDAtimer = true;

            @Expose
            @ConfigOption(name = "Dark Auction Reminder", desc = "Notifies you when there is 1 minute left for Dark Auction.")
            @ConfigEditorBoolean
            public boolean qolDarkAhNotifier = false;

    }

    @Expose
    @Category(name = "Dark AH Timer Settings", desc = "Dark Auction timer settings.")
    public DarkAuctionTimerSettings darkAuctionTimerSettings = new DarkAuctionTimerSettings();

    public static class DarkAuctionTimerSettings {

            @Expose
            @ConfigOption(name = "Scale", desc = "Scale of the Dark AH Timer.")
            @ConfigEditorSlider(minValue = 0.5f, maxValue = 5.0f, minStep = 0.1f)
            public float darkAHTimerScale = 1.0f;

            @Expose
            @ConfigOption(name = "Position", desc = "Position of the Dark AH Timer.")
            @ConfigEditorButton(runnableId = 27, buttonText = "Edit")
            public String editDarkAHTimerPos = "";

            @Expose
            public Position darkAhTimerPos = new Position(0, 0, false, false);
    }
}

