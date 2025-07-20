package org.ginafro.notenoughfakepixel;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.alerts.Alerts;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.config.gui.commands.Commands;
import org.ginafro.notenoughfakepixel.envcheck.registers.ModEventRegistrar;
import org.ginafro.notenoughfakepixel.features.cosmetics.CosmeticsManager;
import org.ginafro.notenoughfakepixel.features.cosmetics.impl.Bandana;
import org.ginafro.notenoughfakepixel.features.cosmetics.loader.OBJLoader;
import org.ginafro.notenoughfakepixel.features.skyblock.overlays.inventory.equipment.EquipmentOverlay;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.Aliases;
import org.ginafro.notenoughfakepixel.features.skyblock.qol.CustomAliases.CustomAliases;
import org.ginafro.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.Utils;

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
       try {
           if (System.getProperty("java.runtime.name", "").toLowerCase().contains("android")) { 
               throw new RuntimeException("No Pojav L")
           }
       } catch (Exception e) {
           // garbage code yay!
       }
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        objLoader = new OBJLoader();

        //registerCosmetics();
        //CapeManager.loadCapesFromGitHub();

        createDirectoryIfNotExists(nefFolder);
        createDirectoryIfNotExists(Config.configDirectory);
        createDirectoryIfNotExists(storageDirectory);
        SlotLocking.getInstance().loadConfig();

        Config.init();
        Runtime.getRuntime().addShutdownHook(new Thread(Config::saveConfig));

        EquipmentOverlay.loadData();

        new Aliases();

        Commands.init();
        Alerts.load();
        CustomAliases.load();

        ModEventRegistrar.registerModEvents();
        ModEventRegistrar.registerKeybinds();
        ModEventRegistrar.registerCommands();
        //InvManager.save();
        //InvManager.load();

        SlotLocking.getInstance().saveConfig();
    }

    private void createDirectoryIfNotExists(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


    public static void resetLockedSlots() {
        SlotLocking.getInstance().resetSlotLocking();
    }

    public static GuiScreen openGui;
    public static long lastOpenedGui;
    public static String th = "default";
    public static ResourceLocation bg = new ResourceLocation("notenoughfakepixel:backgrounds/" + th + "/background.png");

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

        ScoreboardUtils.parseScoreboard();
    }


}
