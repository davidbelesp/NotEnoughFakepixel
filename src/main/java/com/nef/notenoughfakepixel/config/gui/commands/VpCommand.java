package com.nef.notenoughfakepixel.config.gui.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterCommand;

@RegisterCommand
public class VpCommand extends SimpleCommand {

    @Override
    public String getName() {
        return "vp";
    }

    @Override
    public String getUsage() {
        return "/vp <name>";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            String name = String.join(" ", args);
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewprofile " + name);
        }
    }

}