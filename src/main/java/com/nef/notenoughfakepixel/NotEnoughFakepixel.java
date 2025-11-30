package com.nef.notenoughfakepixel;

import com.nef.notenoughfakepixel.alerts.Alerts;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.ModEventRegistrar;
import com.nef.notenoughfakepixel.events.handlers.RepoHandler;
import com.nef.notenoughfakepixel.features.cosmetics.CosmeticsManager;
import com.nef.notenoughfakepixel.features.cosmetics.impl.Bandana;
import com.nef.notenoughfakepixel.features.cosmetics.loader.OBJLoader;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.treasure.TreasureTriangulator;
import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.CrystalWaypoints;
import com.nef.notenoughfakepixel.features.skyblock.overlays.inventory.equipment.EquipmentOverlay;
import com.nef.notenoughfakepixel.features.skyblock.qol.customaliases.CustomAliases;
import com.nef.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import com.nef.notenoughfakepixel.nefrepo.NefRepo;
import com.nef.notenoughfakepixel.utils.Utils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;

@Mod(modid = "notenoughfakepixel", useMetadata = true)
public class NotEnoughFakepixel {

    public static NotEnoughFakepixel instance;

    public static final File nefFolder = new File(Minecraft.getMinecraft().mcDataDir, "NotEnoughFakepixel");

    @Getter
    private final Utils utils = new Utils();
    public static File storageDirectory = new File("config/Notenoughfakepixel/storage");

    @Getter
    private OBJLoader objLoader;

    public void registerCosmetics() {
        CosmeticsManager.registerCosmetics(new Bandana());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        objLoader = new OBJLoader();

        //registerCosmetics();
        //CapeManager.loadCapesFromGitHub();

        createDirectoryIfNotExists(Config.configDirectory);

        Config.init();
        Runtime.getRuntime().addShutdownHook(new Thread(Config::saveConfig));

        EquipmentOverlay.loadData();

        Alerts.load();

        ModEventRegistrar.registerModEvents();
        ModEventRegistrar.registerKeybinds();
        ModEventRegistrar.registerCommands();

        // Load this after register for instance
        SlotLocking.getInstance().loadConfig(new File(Config.configDirectory, "slotlocking.json"));

        // REPO
        NefRepo.init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownHook, "Shutdown Hook"));

        // Ch waypoints
        CrystalWaypoints.getInstance().load();
    }

    private void createDirectoryIfNotExists(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void shutdownHook() {
        // Shutdown tasks
        RepoHandler.shutdown();
        TreasureTriangulator.getInstance().shutdown();

        // Save configs
        try {
            CrystalWaypoints.getInstance().saveIfDirty();
            SlotLocking.getInstance().saveConfig();
            CustomAliases.save();
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public static void resetLockedSlots() {
        SlotLocking.getInstance().resetSlotLocking();
    }

    public static GuiScreen openGui;
    public static long lastOpenedGui;
    public static String th = "default";

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            openGui = null;
            return;
        }

        if (openGui != null) {
            if (mc.thePlayer.openContainer != null) {
                mc.thePlayer.closeScreen();
            }
            mc.displayGuiScreen(openGui);
            openGui = null;
            lastOpenedGui = System.currentTimeMillis();
        }

    }


}