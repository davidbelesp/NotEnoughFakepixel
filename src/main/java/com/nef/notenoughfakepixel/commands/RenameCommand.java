package com.nef.notenoughfakepixel.commands;

import com.nef.notenoughfakepixel.config.gui.commands.SimpleCommand;
import com.nef.notenoughfakepixel.env.registers.RegisterCommand;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

@RegisterCommand
public class RenameCommand extends SimpleCommand {

    public static Map<String, String> renamedItems = new HashMap<>();

    @Override
    public String getName() {
        return "rename";
    }

    @Override
    public String getUsage() {
        return "/rename <name>";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        processCommand(sender, args);
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!SkyblockData.isSkyblock()) return;
        if (args.length < 1) {
            throw new CommandException("Usage: " + getUsage());
        }

        String newName = String.join(" ", args);
        newName = newName.replace("&", "§");

        // Getting hand item
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

        ItemUtils.renameItem(held, newName);
        renamedItems.put(heldUUID, newName);
    }

}