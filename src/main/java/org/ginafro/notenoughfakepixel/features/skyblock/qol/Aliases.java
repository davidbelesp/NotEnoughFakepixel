package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class Aliases {

    public Aliases() {

    }

    public static class AliasCommand extends CommandBase {
        private final String shortCommand;
        private final String fullCommand;

        public AliasCommand(String shortCommand, String fullCommand) {
            this.shortCommand = shortCommand;
            this.fullCommand = fullCommand;
        }

        @Override
        public String getCommandName() {
            return shortCommand;
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/" + shortCommand;
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            String fullCommandWithSlash = fullCommand.startsWith("/") ? fullCommand : "/" + fullCommand;
            Minecraft.getMinecraft().thePlayer.sendChatMessage(fullCommandWithSlash);
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }

        @Override
        public boolean canCommandSenderUseCommand(ICommandSender sender) {
            return true;
        }
    }

}