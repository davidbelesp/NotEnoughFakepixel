package com.nef.notenoughfakepixel.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nef.notenoughfakepixel.config.gui.commands.SimpleCommand;
import com.nef.notenoughfakepixel.env.registers.RegisterCommand;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.Logger;
import com.nef.notenoughfakepixel.variables.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@RegisterCommand
public class RenameCommand extends SimpleCommand {

    public static Map<String, String> renamedItems = new HashMap<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static File getSaveFile() {
        return new File(Minecraft.getMinecraft().mcDataDir, "config/NotEnoughFakepixel/renamed_items.json");
    }

    public static void load() {
        File file = getSaveFile();
        if (!file.exists()) return;
        try (Reader r = new FileReader(file)) {
            Type type = new TypeToken<HashMap<String, String>>(){}.getType();
            Map<String, String> loaded = GSON.fromJson(r, type);
            if (loaded != null) renamedItems = loaded;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        File file = getSaveFile();
        file.getParentFile().mkdirs();
        try (Writer w = new FileWriter(file)) {
            GSON.toJson(renamedItems, w);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() { return "rename"; }

    @Override
    public String getUsage() { return "/rename <name>"; }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        processCommand(sender, args);
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!SkyblockData.isSkyblock()) return;

        // No args: print help
        if (args.length < 1) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    Constants.PREFIX_INFO + "Usage: /rename <name>"
            ));
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    Constants.PREFIX_INFO + "Add colors using §r§7\\u00A7§a + color code§r§a. Examples:"
            ));
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    "  §7\\u00A7a§aGreen text   §7\\u00A7c§cRed text   §7\\u00A7e§eYellow text"
            ));
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    "  §7\\u00A7l§lBold   §7\\u00A7o§oItalic   §7\\u00A7n§nUnderline   §7\\u00A7k§kObfuscated"
            ));
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    Constants.PREFIX_INFO + "Example: §r/rename §a\\u00A7aMy Cool Sword"
            ));
            return;
        }

        ItemStack held = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if (held == null) {
            Logger.logErrorPlayers("You must be holding an item to rename it!");
            return;
        }

        String heldUUID = ItemUtils.getItemUUID(held);
        if (heldUUID == null || heldUUID.isEmpty()) {
            Logger.logErrorPlayers("This item cannot be renamed since it has no ID!");
            return;
        }

        String rawName = String.join(" ", args).replace("\\u00A7", "§").replace("&", "§");

        // Always kill italic unless the player explicitly used §o .
        // default to §b (aqua) if no color was specified at all.
        String newName;
        boolean playerChoseItalic = rawName.contains("§o") || rawName.contains("§O");
        if (playerChoseItalic) {
            newName = rawName; // respect their choice
        } else if (rawName.startsWith("\u00A7")) {
            newName = "§r" + rawName; // has color, prepend §r to kill inherited italic
        } else {
            newName = "§r§b" + rawName; // no formatting at all, default divine (aqua) + no italic
        }

        ItemUtils.renameItem(held, newName);
        renamedItems.put(heldUUID, newName);
        save();

        // Strip color codes for the confirmation message display
        String displayName = newName.replaceAll("§[0-9a-fk-orA-FK-OR]", "");

        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
                Constants.PREFIX_INFO + "Renamed item to §r\"" + newName + "§r\""
        ));
    }
}