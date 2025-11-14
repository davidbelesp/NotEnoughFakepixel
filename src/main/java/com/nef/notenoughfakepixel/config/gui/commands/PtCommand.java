package com.nef.notenoughfakepixel.config.gui.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterCommand;

@RegisterCommand
public class PtCommand extends SimpleCommand {

    @Override
    public String getName() {
        return "pt";
    }

    @Override
    public String getUsage() {
        return "/pt <name>";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            String name = String.join(" ", args);
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/p transfer " + name);
        }
    }
}
