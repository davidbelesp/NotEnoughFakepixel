package com.nef.notenoughfakepixel.config.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.nef.notenoughfakepixel.Configuration;
import com.nef.notenoughfakepixel.config.gui.config.ConfigEditor;
import com.nef.notenoughfakepixel.config.gui.core.GuiScreenElementWrapper;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterKeybind;
import com.nef.notenoughfakepixel.features.skyblock.slotlocking.SlotLocking;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RegisterEvents
public class Config {

    public static Configuration feature;
    private static File configFile;
    public static File configDirectory = new File("config/Notenoughfakepixel");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

    public static void init() {
        configFile = new File(configDirectory, "config.json");
        loadConfig();
    }

    private static void loadConfig() {
        if (configFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8))) {
                feature = gson.fromJson(reader, Configuration.class);
            } catch (Exception ignored) {
            }
        }
        if (feature == null) {
            feature = new Configuration();
            saveConfig();
        }
    }

    public static void saveConfig() {
        try {
            //noinspection ResultOfMethodCallIgnored
            configFile.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8))) {
                writer.write(gson.toJson(feature));
                SlotLocking.getInstance().saveConfig();
            }
        } catch (IOException ignored) {
        }
    }

    @RegisterKeybind
    public static final KeyBinding openGuiKey = new KeyBinding(
            "Open GUI",
            Keyboard.KEY_P,
            "NotEnoughFakepixel"
    );

    public static GuiScreen screenToOpen = null;
    private static int screenTicks = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;

        if (screenToOpen != null) {
            screenTicks++;
            if (screenTicks == 5) {
                Minecraft.getMinecraft().displayGuiScreen(screenToOpen);
                screenTicks = 0;
                screenToOpen = null;
            }
        }

        if (openGuiKey.isPressed() && Minecraft.getMinecraft().currentScreen == null) {
            screenToOpen = new GuiScreenElementWrapper(new ConfigEditor(feature));
        }
    }

}
