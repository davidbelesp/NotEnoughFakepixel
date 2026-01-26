package com.nef.notenoughfakepixel.features.skyblock.qol.customaliases;

import com.google.gson.GsonBuilder;
import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.features.skyblock.qol.Aliases;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@RegisterEvents
public class CustomAliases {

    public static List<CustomAliases.Alias> aliases = new ArrayList<>();
    public static HashMap<String, Pattern> patterns = new HashMap<>();
    private static final Set<Integer> pressed = new HashSet<>();
    public static final String configFile = "/nefalias.json";
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onInput(InputEvent.KeyInputEvent e) {
        for (Alias alias : aliases) {
            if (alias.key <= 0) continue;
            final int k = alias.key;
            final boolean down = Keyboard.isKeyDown(k);
            if (down && !pressed.contains(k)) {
                if (mc.thePlayer != null) mc.thePlayer.sendChatMessage(alias.command);
                pressed.add(k);
            } else if (!down) {
                pressed.remove(k);
            }
        }
    }

    public CustomAliases() {
        load();
        registerAliases();
    }

    public static void unregisterAlias(Alias alias) {
        if (alias != null) {
            ClientCommandHandler.instance.getCommands().remove(alias.alias);
        }
    }

    private static File getConfigFile() {
        final File dir = Config.configDirectory;
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, configFile);
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent e) {
        registerAliases();
    }

    public static void registerAliases() {
        for (Alias a : aliases) {
            if (a != null && a.toggled && a.alias != null && a.command != null) {
                // Register only if not already registered
                if (!ClientCommandHandler.instance.getCommands().containsKey(a.alias)) {
                    ClientCommandHandler.instance.registerCommand(new Aliases.AliasCommand(a.alias, a.command));
                }
            }
        }
    }

    public static void save() {
        final File file = getConfigFile();
        try (FileWriter writer = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                    .toJson(aliases, writer);
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed to save aliases to " + file.getAbsolutePath());
        }
    }

    public static void load() {
        final File file = getConfigFile();
        if (!file.exists()) {
            System.out.println("No aliases file found at " + file.getAbsolutePath() + ", starting empty");
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            List<CustomAliases.Alias> loaded = new GsonBuilder().create().fromJson(
                    reader, new com.google.gson.reflect.TypeToken<List<CustomAliases.Alias>>(){}.getType()
            );
            aliases.clear();
            if (loaded != null) aliases.addAll(loaded);

            patterns.clear();
            for (CustomAliases.Alias a : aliases) {
                if (a != null && a.command != null) {
                    patterns.put(a.alias, Pattern.compile(a.command));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Failed to load aliases from " + file.getAbsolutePath());
        }
    }

    public static class Alias {
        public String location;
        public String command;
        public String alias;
        public int key;
        public boolean toggled;

        public Alias(String location, String command, String a, boolean toggled) {
            this.location = location;
            this.command = command;
            this.alias = a;
            this.toggled = toggled;
            key = 0;
        }

        public Alias(String location, String command, String a, boolean toggled, int key) {
            this.location = location;
            this.command = command;
            this.alias = a;
            this.toggled = toggled;
            this.key = key;
        }

        public void toggle() {
            toggled = !toggled;
        }
    }
}
