package org.ginafro.notenoughfakepixel;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.*;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.ginafro.notenoughfakepixel.config.pages.AshfangOverlayPage;
import org.ginafro.notenoughfakepixel.config.pages.MiningOverlayPage;
import org.ginafro.notenoughfakepixel.config.pages.ScoreOverlayPage;
import org.ginafro.notenoughfakepixel.features.skyblock.mining.RemoveGhostInvis;

public class Configuration extends Config {

    // Categories
    private transient static final String DUNGEONS = "Dungeons";
    private transient static final String MINING = "Mining";
    private transient static final String FISHING = "Fishing";
    private transient static final String QUALITY_OF_LIFE = "Quality of Life";
    private transient static final String SLAYER = "Slayer";
    private transient static final String DIANA = "Diana";
    private transient static final String CRIMSON = "Crimson";
    private transient static final String EXPERIMENTATION = "Experimentation Table";
    private transient static final String CHOCOLATEFACTORY = "Chocolate Factory";

    public Configuration(){
        super(new Mod("NotEnoughFakepixel", ModType.UTIL_QOL, "assets/notenoughfakepixel/logo.png"), "config.json");
        initialize();

        //this.addDependency("debug", "Cant be enabled with debug2", () -> !debug2);
        //this.addDependency("debug2", "Cant be enabled with debug", () -> !debug);

        this.addListener("dmgFormatter", () -> {
            if (qolDmgFormatter) {
                qolDmgCommas = true;
            }
        });

        this.addListener("showGhosts", () -> {
            if (!miningShowGhosts) {
                RemoveGhostInvis.resetGhostInvis();
            }
        });

        this.addListener("disableRain", () -> {
            if (qolDisableRain) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText("[\u00a7eNEF\u00a7r]\u00a7c You might need to change lobby for the rain to disappear.")
                );
            }
        });

        //this.addListener("_debug", () -> _debug2 = false);
        //this.addListener("_debug2", () -> _debug = false);

        if (System.getProperty("os.name").contains("Android") || System.getProperty("os.name").contains("Linux")) {
            hiddenOverlays();
        }

        save();
    }

    public static boolean isPojav() {
        return (System.getProperty("os.name").contains("Android") || System.getProperty("os.name").contains("Linux"));
    }

    public static void hiddenOverlays() {
        dungeonsScoreOverlay = false;
        miningOverlay = false;
        crimsonAshfangOverlay = false;
    }

    @Override
    public boolean supportsProfiles() {
        return true;
    }

    @Header(text = "General" , size = 2)
    public boolean _general = true;
    @Dropdown(name = "Theme" , size = 2 , options = {"Default" , "Dark" , "Ocean"})
    public static int theme = 1;
    @Switch(name = "Debug" , description = "Enable debug mode")
    public static boolean debug = false;

    // Quality Of Life

    @Header(text = "General" , category = QUALITY_OF_LIFE, size = 2)
    public static boolean _qol = true;
    //@Switch(name = "Custom Chat Filters" , description = "Add your own chat filters, by - /addfilter", category = QUALITY_OF_LIFE)
    //public static boolean qolCustomFilters = true;

    @Switch(name = "Fairy Soul Waypoints" , category = QUALITY_OF_LIFE, subcategory = "Fairy Souls")
    public static boolean fairySoulWaypoints = true;
    @Color(name = "Fairy Soul Waypoints Color",  category = QUALITY_OF_LIFE, subcategory = "Fairy Souls")
    public static OneColor fairySoulWaypointsColor = new OneColor(0,0,0);

    @Switch(name = "Fullbright" , category = QUALITY_OF_LIFE)
    public static boolean qolFullbright = true;
    @Switch(name = "1.12 Crops height" , category = QUALITY_OF_LIFE)
    public static boolean qolCropsHeight = false;
    @Switch(name = "Disable rain", category = QUALITY_OF_LIFE, description = "Disables rain rendering")
    public static boolean qolDisableRain = true;
    @Switch(name = "Disable Potion Effects in Inventory" , category = QUALITY_OF_LIFE)
    public static boolean qolDisablePotionEffects = true;
    @Switch(name = "Show Enchant Level", category = QUALITY_OF_LIFE, description = "Show enchant level of an book on its icon")
    public static boolean qolShowEnchantLevel = true;
    @Switch(name = "Middle click on terminals and Enchanting", category = QUALITY_OF_LIFE, description = "Middle clicks on terminals and enchanting gui.")
    public static boolean qolMiddleClickChests = true;
    @Switch(name = "Visual Cooldowns", description = "Makes the durability of the weapon being used as cooldown timer for better info", category = QUALITY_OF_LIFE)
    public static boolean qolVisualCooldowns = true;
    @Switch(name = "Item Rarity Display", description = "Show visual circle on the item about its rarity.", category = QUALITY_OF_LIFE)
    public static boolean qolItemRarity = true;
    @Switch(name = "Disable Enderman Teleport", category = QUALITY_OF_LIFE)
    public static boolean qolDisableEnderManTeleport = true;

    @Switch(name = "Block placing items", category = QUALITY_OF_LIFE)
    public static boolean qolBlockPlacingItems = true;

    @Switch(name = "Wardrobe Shortcut" , category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static boolean qolShortcutWardrobe = true;
    @KeyBind(name = "Wardrobe Shortcut Key", category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static OneKeyBind qolWardrobeKeyBind = new OneKeyBind(UKeyboard.KEY_R);
    @Switch(name = "Pets Shortcut" , category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static boolean qolShortcutPets = true;
    @KeyBind(name = "Pets Shortcut Key", category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static OneKeyBind qolPetsKeyBind = new OneKeyBind(UKeyboard.KEY_P);
    @Switch(name = "Warps Shortcuts" , category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static boolean qolShortcutWarps = true;
    @KeyBind(name = "Warp Island Shortcut Key", category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static OneKeyBind qolShortcutWarpIs = new OneKeyBind(UKeyboard.KEY_F7);
    @KeyBind(name = "Warp Hub Shortcut Key", category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static OneKeyBind qolShortcutWarpHub = new OneKeyBind(UKeyboard.KEY_F8);
    @KeyBind(name = "Warp Dungeon Hub Shortcut Key", category = QUALITY_OF_LIFE, subcategory = "Shortcuts")
    public static OneKeyBind qolShortcutWarpDh = new OneKeyBind(UKeyboard.KEY_F9);

    @Switch(name = "Show Pet Equipped", category = QUALITY_OF_LIFE, subcategory = "Pets")
    public static boolean qolShowPetEquipped = true;
    @Color(name = "Pet Equipped Color", category = QUALITY_OF_LIFE, subcategory = "Pets")
    public static OneColor qolPetEquippedColor = new OneColor(190, 255, 190);

    @Switch(name = "Disable Watchdog & Info messages", category = QUALITY_OF_LIFE, subcategory = "Chat")
    public static boolean qolDisableWatchdogInfo = true;
    @Switch(name = "Disable Friend > joined/left message", category = QUALITY_OF_LIFE, subcategory = "Chat")
    public static boolean qolDisableFriendJoin = false;
    //@Switch(name = "Chat Cleaner", category = "Quality of Life", subcategory = "Chat")
    public static boolean qolChatCleaner = false;
    @Switch(name = "Disable zombie rare drops messages", category = QUALITY_OF_LIFE, subcategory = "Chat", description = "Disable drops carrot, potato and poisonous potato chat messages.")
    public static boolean qolDisableZombieRareDrops = true;
    @Switch(name = "Disable 'Selling ranks' messages", category = QUALITY_OF_LIFE, subcategory = "Chat")
    public static boolean qolDisableSellingRanks = false;
    @Switch(name = "Scrollable tooltips" , category = QUALITY_OF_LIFE, subcategory = "Items", description = "Scroll through the item lore")
    public static boolean qolScrollableTooltips = true;

    @Switch(name = "No Hurt Camera", category = QUALITY_OF_LIFE, subcategory = "Sounds & Animations")
    public static boolean qolNoHurtCam = true;
    @Switch(name = "Disable Jerry-chine Gun sounds", category = QUALITY_OF_LIFE, subcategory = "Sounds & Animations", description = "Disable Jerry-chine gun sounds.")
    public static boolean qolDisableJerryChineGunSounds = true;
    @Switch(name = "Disable AOTE teleport sounds", category = QUALITY_OF_LIFE, subcategory = "Sounds & Animations", description = "Disable Aspect of the End teleport sounds.")
    public static boolean qolDisableAoteSounds = false;
    @Switch(name = "Disable Hyperion Explosion", category = QUALITY_OF_LIFE, subcategory = "Sounds & Animations")
    public static boolean qolDisableHyperionExplosions = true;
    @Switch(name = "Disable Thunderlord Bolt", category = QUALITY_OF_LIFE, subcategory = "Sounds & Animations")
    public static boolean qolDisableThunderlordBolt = true;
    @Switch(name = "Minimum Midas Staff animation and sounds", category = QUALITY_OF_LIFE, subcategory = "Sounds & Animations", description = "Low-render gold blocks and set sound only at right click.")
    public static boolean qolDisableMidaStaffAnimation = false;
    @Switch(name = "Hide Flaming Fists" , category = QUALITY_OF_LIFE, subcategory = "Sounds & Animations")
    public static boolean qolHideFlamingFists = false;

    @Switch(name = "Hide dead mobs" , category = "Quality of Life", subcategory = "Sounds & Animations")
    public static boolean qolHideDyingMobs = true;

    @Switch(name = "Damage Commas", category = QUALITY_OF_LIFE, subcategory = "Damage Formatter")
    public static boolean qolDmgCommas = true;
    @Switch(name = "Damage Formatter", category = QUALITY_OF_LIFE, subcategory = "Damage Formatter", description = "Formats the damage. (ie. 167k instead of 167000)")
    public static boolean qolDmgFormatter = true;

    // Dungeons

    @Header(text = "Dungeons", category = DUNGEONS, size = 2)
    public static boolean _dungeons = true;

    @Switch(name = "Is Paul Active", category = DUNGEONS, subcategory = "QOL", description = "Check/uncheck this if Paul is active as mayor with EZPZ perk. Needed for correct score calculation.")
    public static boolean dungeonsIsPaul = false;
    @Switch(name = "Auto Close Chests", category = DUNGEONS, subcategory = "QOL", description = "Automatically closes chests in dungeons.")
    public static boolean dungeonsAutoCloseChests = true;
    @Switch(name = "Auto Ready Dungeon", category = DUNGEONS, subcategory = "QOL", description = "Automatically ready up in dungeons.")
    public static boolean dungeonsAutoReady = true;
    @Text(name = "Auto Ready Name (if nicked)", category = DUNGEONS, subcategory = "QOL", description = "The name this will search for when you are nicked")
    public static String dungeonsAutoReadyName = "your nicked name";
    @Switch(name = "Wither & Blood Keys Tracers", category = DUNGEONS, subcategory = "QOL", description = "Show tracer on wither and blood keys.")
    public static boolean dungeonsKeyTracers = true;
    @Switch(name = "Mute Irrelevant Messages", category = DUNGEONS, subcategory = "QOL", description = "Mutes bosses and crowd dialogs on chat.")
    public static boolean dungeonsMuteIrrelevantMessages = true;
    @Switch(name = "Salvage Items Saver", category = DUNGEONS, subcategory = "QOL", description = "Prevents you from salvaging important and legendary+ items.")
    public static boolean dungeonsSalvageItemsPrevention = true;
    @Switch(name = "Show Correct Livid", category = DUNGEONS, subcategory = "QOL")
    public static boolean dungeonsLividFinder = true;
    @Switch(name = "Show opened chests in Croesus", category = DUNGEONS, subcategory = "QOL")
    public static boolean dungeonsShowOpenedChests = true;
    @Switch(name = "Custom Spirit Leap GUI", category = DUNGEONS, subcategory = "QOL")
    public static boolean dungeonsSpiritLeapGUI = true;
    @Switch(name = "Announces Leaped to Player in party chat!", category = DUNGEONS, subcategory = "QOL")
    public static boolean dungeonsLeapAnnounce = true;


    @Switch(name = "Announces when Blood room is done spawning", category = DUNGEONS, subcategory = "QOL")
    public static boolean dungeonsBloodReady = true;

    @Switch(name = "Dungeons Map", category = DUNGEONS, subcategory = "Dungeon Map")
    public static boolean dungeonsMap = true;
    @Color(name = "Dungeons Map Border Color", category = DUNGEONS , subcategory = "Dungeon Map")
    public static OneColor dungeonsMapBorderColor = new OneColor(0,0,0);
    @Slider(name = "Dungeons Map Scale" ,category = DUNGEONS, subcategory = "Dungeon Map" , min = 0.1f,max=10f)
    public static float dungeonsMapScale = 1.0f;
    @Slider(name = "Dungeons Map Offset X" ,category = DUNGEONS, subcategory = "Dungeon Map" , min = 0.0f,max=1800f)
    public static float dungeonsMapOffsetX = 0.0f;
    @Slider(name = "Dungeons Map Offset Y" ,category = DUNGEONS, subcategory = "Dungeon Map" , min = 0.0f,max=1250)
    public static float dungeonsMapOffsetY = 0.0f;
    @Switch(name = "Dungeons Map Rotation" , category = DUNGEONS, subcategory = "Dungeon Map")
    public static boolean dungeonsRotateMap = true;

    @Switch(name = "Three Weirdos Solver" , category = DUNGEONS, subcategory = "Puzzles")
    public static boolean dungeonsThreeWeirdos = true;
    @Switch(name = "Water Solver" , category = DUNGEONS, subcategory = "Puzzles")
    public static boolean dungeonsWaterSolver = true;

    @Switch(name = "Fel Mobs Display", category = DUNGEONS, subcategory = "Starred Mobs")
    public static boolean dungeonsFelMob = true;
    @Color(name = "Fel Mob Color", category = DUNGEONS, subcategory = "Starred Mobs")
    public static OneColor dungeonsFelColor = new OneColor(92, 154, 255);
    @Switch(name = "Bat Mobs Display", category = DUNGEONS, subcategory = "Starred Mobs")
    public static boolean dungeonsBatMobs = true;
    @Color(name = "Bat Mob Color", category = DUNGEONS, subcategory = "Starred Mobs")
    public static OneColor dungeonsBatColor = new OneColor(92, 154, 255);
    @Switch(name = "Starred Mobs Display", category = DUNGEONS, subcategory = "Starred Mobs")
    public static boolean dungeonsStarredMobs = true;
    @Color(name = "Starred Mobs Color", category = DUNGEONS, subcategory = "Starred Mobs")
    public static OneColor dungeonsStarredBoxColor = new OneColor(92, 154, 255);
    @Switch(name = "Starred Mobs Esp", category = DUNGEONS, subcategory = "Starred Mobs", description = "Render starred mobs hitboxes through walls.")
    public static boolean dungeonsStarredMobsEsp = true;
    @Color(name = "Withermancer Color", category = DUNGEONS, subcategory = "Starred Mobs")
    public static OneColor dungeonsWithermancerColor = new OneColor(169, 169, 169);
    @Color(name = "Zombie Commander Color", category = DUNGEONS, subcategory = "Starred Mobs")
    public static OneColor dungeonsZombieCommanderColor = new OneColor(255, 0, 0);
    @Color(name = "Skeleton Master Color", category = DUNGEONS, subcategory = "Starred Mobs")
    public static OneColor dungeonsSkeletonMasterColor = new OneColor(255, 100, 0);
    @Color(name = "Stormy Color", category = DUNGEONS, subcategory = "Starred Mobs")
    public static OneColor dungeonsStormyColor = new OneColor(173, 216, 230);

    @Switch(name = "Custom Terminal GUI", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonTerminalCustomGui = true;
    @Slider(name = "Custom Terminal Scale", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices", min = 1.0f, max = 5.0f)
    public static float dungeonTerminalScale = 2.0f;
    @Switch(name = "Terminal Starts With Solver", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsTerminalStartsWithSolver = true;
    @Switch(name = "Terminal Select Colors Solver", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsTerminalSelectColorsSolver = true;
    @Switch(name = "Terminal Click In Order Solver", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsTerminalClickInOrderSolver = true;
    @Switch(name = "Terminal Maze Solver", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsTerminalMazeSolver = true;
    @Switch(name = "Terminal Correct Panes Solver", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsTerminalCorrectPanesSolver = true;
    @Switch(name = "Hide Terminal Incorrect Slots", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsTerminalHideIncorrect = true;
    @Switch(name = "Prevent Terminal Missclicks", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsPreventMissclicks = true;
    @Switch(name = "Hide Tooltips", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsHideTooltips = true;
    @Switch(name = "First Device Solver", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsFirstDeviceSolver = true;
    @Switch(name = "Third Device Solver", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsThirdDeviceSolver = true;
    @Color(name = "Correct Color", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static OneColor dungeonsCorrectColor = new OneColor(0,255,0);
    //public static OneColor dungeonsCorrectColor = new OneColor(128,255,255);
    @Color(name = "Alternative Color", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static OneColor dungeonsAlternativeColor = new OneColor(255,255,0);

    @Switch(name = "Highlight Uncompleted Terminals", category = DUNGEONS, subcategory = "Floor 7 Terminals and Devices")
    public static boolean dungeonsTerminalWaypoints = true;

    @Switch(name = "Score Overlay" ,category = DUNGEONS, subcategory = "Score & Secrets")
    public static boolean dungeonsScoreOverlay = true;
    @Switch(name = "Minimal Score Overlay" ,category = DUNGEONS, subcategory = "Score & Secrets")
    public static boolean minimalScoreOverlay = false;
    @Switch(name = "S+ Notifier", category = DUNGEONS, subcategory = "Score & Secrets", description = "Shows an S+ in screen when virtually reached S+ (% secrets + X% completion).")
    public static boolean dungeonsSPlusNotifier = true;
    @Switch(name = "S+ Message on Chat", category = DUNGEONS, subcategory = "Score & Secrets", description = "Send a message chat when dungeon is about to be done.")
    public static boolean dungeonsSPlusMessage = true;
    @Text(name = "Custom S+ Message on Chat" , category = DUNGEONS, subcategory = "Score & Secrets")
    public static String dungeonsSPlusCustom = "";
    @Switch(name = "Dungeon Cleared Notifier" , category = DUNGEONS, subcategory = "Score & Secrets", description = "Shows a message in screen + party message chat when 100% completion.")
    public static boolean dungeonsClearedNotifier = true;
    @Switch(name = "Show Item Secrets and Wither Essences", category = DUNGEONS, subcategory = "Score & Secrets", description = "Show hitbox through walls of item secrets.")
    public static boolean dungeonsItemSecretsDisplay = true;
    @Color(name = "Item Secrets Color", category = DUNGEONS, subcategory = "Score & Secrets")
    public static OneColor dungeonsItemSecretsColor = new OneColor(255, 255, 0);
    @Switch(name = "Make Item Secrets Big", category = DUNGEONS, subcategory = "Score & Secrets")
    public static boolean dungeonsItemSecretsBig = true;
    @Slider(name = "Item Secrets Scale", category = DUNGEONS, subcategory = "Score & Secrets", description = "Change the size of secret items.", min = 0.1f,max=5f)
    public static float dungeonsScaleItemDrop = 3.5f;
    @Page(name = "Score Overlay Options", category = DUNGEONS, subcategory = "Score & Secrets", location = PageLocation.BOTTOM)
    public static ScoreOverlayPage dungeonsScoreOverlayPage = new ScoreOverlayPage();


    // Diana
    @Header(text = DIANA, category = DIANA, size = 2)
    public static boolean _diana = true;
    @Switch(name = "Show Waypoints on Burrows", category = DIANA, subcategory = "Waypoints", description = "IMPORTANT: this feature needs \"Video Settings, Particles\" active. Show waypoints on burrows when certain amount of burrow particles are detected.")
    public static boolean dianaShowWaypointsBurrows = true;
    @Color(name = "Empty Burrow Color", category = DIANA, subcategory = "Waypoints")
    public static OneColor dianaEmptyBurrowColor = new OneColor(0, 0, 255);
    @Switch(name = "Show Labels on Waypoints", category = DIANA, subcategory = "Waypoints", description = "IMPORTANT: this feature needs burrow waypoints active. Show labels on burrows when a waypoint is added, indicating the type of burrow and distance to it.")
    public static boolean dianaShowLabelsWaypoints = true;
    @Color(name = "Mob Burrow Color", category = DIANA, subcategory = "Waypoints")
    public static OneColor dianaMobBurrowColor = new OneColor(255, 255, 255);
    @Switch(name = "Show Tracers on Waypoints", category = DIANA, subcategory = "Waypoints", description = "IMPORTANT: this feature needs burrow waypoints active. Show tracers on burrows when a waypoint is added.")
    public static boolean dianaShowTracersWaypoints = true;
    @Color(name = "Treasure Burrow Color", category = DIANA, subcategory = "Waypoints")
    public static OneColor dianaTreasureBurrowColor = new OneColor(255, 0, 0);
    @Switch(name = "Track Gaia Hits", category = DIANA, subcategory = "Mobs", description = "Turn off/on hitbox that represents when Gaia Construct can be damaged.")
    public static boolean dianaGaiaConstruct = true;
    @Color(name = "Gaia Hittable Color", category = DIANA, subcategory = "Mobs")
    public static OneColor dianaGaiaHittableColor = new OneColor(0, 255, 0);
    @Switch(name = "Show Hittable Siamese", category = DIANA, subcategory = "Mobs", description = "Turn off/on hitbox that represents which siamese can be damaged.")
    public static boolean dianaSiamese = true;
    @Color(name = "Gaia Un-hittable Color", category = DIANA, subcategory = "Mobs")
    public static OneColor dianaGaiaUnhittableColor = new OneColor(255, 0, 0);
    // Minos inquisitor /pc feature
    @Switch(name = "Minos Inquisitor alert on party chat", category = DIANA, subcategory = "Mobs", description = "Enable alerts on party chat when Minos Inquisitor dug. Receive alerts from other players and show inquisitor waypoints.")
    public static boolean dianaMinosInquisitorAlert = true;
    @Color(name = "Siamese Hittable Color", category = DIANA, subcategory = "Mobs")
    public static OneColor dianaSiameseHittableColor = new OneColor(0, 255, 0);
    @Switch(name = "Waypoint Sounds", category = DIANA, subcategory = "Sounds", description = "Turn off/on waypoints creation sounds.")
    public static boolean dianaWaypointSounds = true;
    @Switch(name = "Disable Explosion Sounds", category = DIANA, subcategory = "Sounds", description = "Disable explosion sounds generated by digging a burrow.")
    public static boolean dianaDisableDianaExplosionSounds = false;
    @Switch(name = "Disable Ancestral Spade cooldown message", category = DIANA, subcategory = "Misc", description = "Mute ability cooldown message on chat.")
    public static boolean dianaCancelCooldownSpadeMessage = true;


    //@Switch(name = "Disable harp sounds" , category = DIANA, subcategory = "Sounds", description = "Turn off/on harp sounds.")
    //public static boolean disableDianaHarpSounds = true;


    // Slayer
    @Header(text = SLAYER , category = SLAYER , size = 2)
    public static boolean _slayer = true;
    @Switch(name = "Slayer Minibosses Display" , category = SLAYER , subcategory = "Slayer Mobs", description = "Draws a box around slayer minibosses.")
    public static boolean slayerMinibosses = true;

    @Switch(name = "Miniboss spawn title" , category = SLAYER, subcategory = "Slayer Mobs", description = "Shows a title on the screen after a Miniboss is spawned.")
    public static boolean slayerMinibossTitle = true;
    @Switch(name = "Miniboss sound notification" , category = SLAYER, subcategory = "Slayer Mobs", description = "Plays a sound on Miniboss spawning.")
    public static boolean slayerMinibossSound = true;

    @Color(name = "Slayer Minibosses Color", category = SLAYER, subcategory = "Slayer Mobs", description = "Color of the slayer minibosses.")
    public static OneColor slayerColor = new OneColor(92, 154, 255);
    @Switch(name = "Slayer Bosses Display" , category = SLAYER, subcategory = "Slayer Mobs", description = "Draws a box around slayer bosses.")
    public static boolean slayerBosses = true;
    @Color(name = "Slayer Bosses Color", category = SLAYER, subcategory = "Slayer Mobs", description = "Color of the slayer bosses.")
    public static OneColor slayerBossColor = new OneColor(92, 154, 255);
    @Dropdown(name = "Faster Maddox Calling", category = SLAYER, subcategory = "Quality of Life",options = {"Auto Open","Semi Auto","Disabled"})
    public static int slayerMaddoxCalling = 1;
    @Switch(name = "Show Beacon Waypoint" , category = SLAYER, subcategory = "Voidgloom Seraph", description = ".")
    public static boolean slayerShowBeaconPath = true;
    @Color(name = "Beacon Color", category = SLAYER, subcategory = "Voidgloom Seraph", description = "Color of the beacon's waypoint.")
    public static OneColor slayerBeaconColor = new OneColor(128, 0, 128);
    @Switch(name = "Display Blaze Pillar Title", category = SLAYER, subcategory = "Inferno Demonlord", description = "Displays a text on the screen when a pillar is nearby.")
    public static boolean slayerFirePillarDisplay = true;

    @Switch(name = "Blaze Attunements display" , category = SLAYER , subcategory = "Inferno Demonlord")
    public static boolean slayerBlazeAttunements = true;

    // Enchanting
    @Header(text = "Experimentation Table", category = EXPERIMENTATION , size = 2)
    public static boolean _experimentation = true;
    @Switch(name = "Chronomatron Solver", category = EXPERIMENTATION, description = "Enables Chronomatron solver.")
    public static boolean experimentationChronomatronSolver = true;
    @Switch(name = "Ultrasequencer solver", category = EXPERIMENTATION, description = "Enables Ultrasequencer solver.")
    public static boolean experimentationUltraSequencerSolver = true;
    @Switch(name = "Hide Tooltips", category = EXPERIMENTATION, description = "Hide tooltips when doing experiments.")
    public static boolean experimentationHideTooltips = true;
    //@Switch(name = "Superpairs Solver", category = EXPERIMENTATION, description = "Enables Superpairs solver.")
    //public static boolean experimentationSuperpairsSolver = true;
    @Switch(name = "Prevent Misclicks", category = EXPERIMENTATION, description = "IMPORTANT: this feature doesn't work properly if your connection isn't stable or server has lag. Prevents clicking wrong answers when doing experiments.")
    public static boolean experimentationPreventMissclicks = true;

    // Chocolate Factory
    @Header(text = CHOCOLATEFACTORY, category = CHOCOLATEFACTORY, size = 2)
    public static boolean _chocolate = true;
    @Switch(name = "Show Waypoints on Chocolate Eggs", category = CHOCOLATEFACTORY, description = "Show waypoints on chocolate eggs on Hoppity's Hunt event.")
    public static boolean chocolateChocolateEggWaypoints = true;
    @Color(name = "Chocolate Eggs Waypoints Color", category = CHOCOLATEFACTORY, description = "Color of chocolate eggs' waypoints.")
    public static OneColor chocolateChocolateEggWaypointsColor = new OneColor(210,105,30);
    @Switch(name = "Show Best Upgrade", category = CHOCOLATEFACTORY)
    public static boolean chocolateChocolateShowBestUpgrade = true;

    // Crimson
    @Header(text = CRIMSON, category = CRIMSON, size = 2)
    public static boolean _crimson = true;
    @Switch(name = "Bladesoul notifier", category = CRIMSON, subcategory = "Bosses notifier", description = "Notifies you when Bladesoul boss spawns.")
    public static boolean crimsonBladesoulNotifier = true;
    @Switch(name = "Mage Outlaw notifier", category = CRIMSON, subcategory = "Bosses notifier", description = "Notifies you when Mage Outlaw boss spawns.")
    public static boolean crimsonMageOutlawNotifier = true;
    @Switch(name = "Ashfang notifier", category = CRIMSON, subcategory = "Bosses notifier", description = "Notifies you when Ashfang boss spawns.")
    public static boolean crimsonAshfangNotifier = true;
    @Switch(name = "Barbarian Duke X notifier", category = CRIMSON, subcategory = "Bosses notifier", description = "Notifies you when Barbarian Duke X boss spawns.")
    public static boolean crimsonBarbarianDukeXNotifier = true;
    @Switch(name = "Ashfang waypoint", category = CRIMSON, subcategory = "Ashfang", description = "Show a waypoint on Ashfang for launching Blazing Souls easier.")
    public static boolean crimsonAshfangWaypoint = true;
    @Color(name = "Ashfang waypoint color", category = CRIMSON , subcategory = "Ashfang", description = "Color of Ashfang's waypoint.")
    public static OneColor crimsonAshfangWaypointColor = new OneColor(0, 255, 0);
    @Switch(name = "Gravity Orb waypoint", category = CRIMSON, subcategory = "Ashfang", description = "Show a waypoint on Gravity Orb to lure Ashfang Followers easier.")
    public static boolean crimsonGravityOrbWaypoint = true;
    @Color(name = "Gravity orb waypoint color", category = CRIMSON , subcategory = "Ashfang", description = "Color of Gravity Orb's waypoint.")
    public static OneColor crimsonBlazingSoulWaypointColor = new OneColor(255, 255, 0);
    @Switch(name = "Ashfang hitboxes", category = CRIMSON, subcategory = "Ashfang", description = "Show colors on different Ashfang's minions (Follower, Acolyte, Underling) for better recognition.")
    public static boolean crimsonAshfangHitboxes = true;
    @Switch(name = "Ashfang mute chat", category = CRIMSON, subcategory = "Ashfang", description = "Mute irrelevant chat messages generated by Ashfang's minions.")
    public static boolean crimsonAshfangMuteChat = true;
    @Switch(name = "Ashfang mute sound", category = CRIMSON, subcategory = "Ashfang", description = "Mute annoying sounds generated by Ashfang's minions.")
    public static boolean crimsonAshfangMuteSound = true;
    @Switch(name = "Ashfang hurt sound", category = CRIMSON, subcategory = "Ashfang", description = "Make a sound when Ashfang is hit by a Blazing Soul.")
    public static boolean crimsonAshfangHurtSound = true;
    @Switch(name = "Ashfang overlay", category = CRIMSON, subcategory = "Ashfang", description = "Overlay that shows the HP of Ashfang, as well as Blazing Souls alive and needed for killing the boss.")
    public static boolean crimsonAshfangOverlay = true;
    @Page(name = "Ashfang Overlay Options", category = CRIMSON, subcategory = "Ashfang", location = PageLocation.BOTTOM)
    public static AshfangOverlayPage crimsonAshfangOverlayPage = new AshfangOverlayPage();

    // Mining
    @Header(text = MINING, category = MINING, size = 2)
    public static boolean _mining = true;
    @Switch(name = "Enable Mining Ability Notifier", category = MINING, subcategory = "", description = "Notifies you when your mining ability is ready.")
    public static boolean miningAbilityNotifier = true;
    @Switch(name = "Disable Don Espresso messages", category = MINING, subcategory = "", description = "Disables Don Espresso event messages.")
    public static boolean miningDisableDonEspresso = true;
    @Switch(name = "Fix Drill Animation Reset", category = MINING, subcategory = "", description = "Fixes drill animation resetting when the fuel updates.")
    public static boolean miningDrillFix = true;
    @Switch(name = "Puzzler Solver", category = MINING, subcategory = "", description = "Solves the Puzzler block.")
    public static boolean miningPuzzlerSolver = true;
    @Switch(name = "Remove Ghost Invisibility", category = MINING, subcategory = "", description = "Removes the invisibility of the ghosts")
    public static boolean miningShowGhosts = true;

    @Switch(name = "Drill Fuel Overlay" , category = MINING, subcategory = "Mining Overlay", description = "Shows the drill fuel in overlay.")
    public static boolean miningDrillFuel = true;
    @Switch(name = "Mithril Powder Overlay" , category = MINING, subcategory = "Mining Overlay", description = "Shows the mithril powder in overlay.")
    public static boolean miningMithrilPowder = true;
    @Switch(name = "Ability Cooldown Overlay" , category = MINING, subcategory = "Mining Overlay", description = "Shows the ability cooldown in overlay.")
    public static boolean miningAbilityCooldown = true;
    @Switch(name = "Commission overlay" , category = MINING, subcategory = "Mining Overlay", description = "Shows the commission in overlay.")
    public static boolean miningOverlay = true;
    @Page(name = "Mining Overlay Options" , category = MINING, subcategory = "Mining Overlay", location = PageLocation.BOTTOM)
    public static MiningOverlayPage miningOverlayPage = new MiningOverlayPage();

    // Fishing
    @Header(text = FISHING, category = FISHING, size = 2)
    public static boolean _fishing = true;
    @Switch(name = "Notify Legendary Creatures", category = FISHING, description = "Notifies you when a legendary creature is catched.")
    public static boolean fishingLegendaryCreatures = true;
    //@Switch(name = "Notify on Great Catch (not working?)", category = FISHING)
    public static boolean fishingGreatCatch = true;
    @Switch(name = "Notify on Trophy Fish", category = FISHING)
    public static boolean fishingTrophyFish = true;

    // SKYBLOCK

//    @Header(text = "Skyblock" , category = "Skyblock" , size = 2)
//    public boolean skyblock = true;
//    @Switch(name = "Jacob/Auction Timer" , category = "Skyblock")
    public static boolean jATimer = true;

    //@Switch(name = "Storage GUI Overlay", category = "Skyblock", subcategory = "GUI Overlays")
    public static boolean storageOverlay = false;

}
