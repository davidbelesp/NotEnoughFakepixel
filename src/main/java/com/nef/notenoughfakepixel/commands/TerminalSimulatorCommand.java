package com.nef.notenoughfakepixel.commands;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterCommand;
import com.nef.notenoughfakepixel.features.skyblock.dungeons.terminals.TerminalSimulator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

@RegisterCommand
public class TerminalSimulatorCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "termsim";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/termsim";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Config.screenToOpen = new TerminalSimulator();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}