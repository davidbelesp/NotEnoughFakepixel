package com.nef.notenoughfakepixel.commands;

import com.nef.notenoughfakepixel.config.gui.utils.Utils;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

@RegisterCommand
public class CopyCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "copytoclipboard";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/copytoclipboard <text>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) return;

        String text = String.join(" ", args);
        Utils.copyToClipboard(text);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}

