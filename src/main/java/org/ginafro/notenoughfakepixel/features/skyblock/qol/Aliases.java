package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterCommand;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;

@RegisterEvents
public class Aliases {

    public Aliases() {
        ClientCommandHandler.instance.registerCommand(new VpCommand());
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

    private static class VpCommand extends CommandBase {
        @Override
        public String getCommandName() {
            return "vp";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/vp <name>";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                String name = String.join(" ", args);
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/viewprofile " + name);
            }
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

    private static class PtCommand extends CommandBase {
        @Override
        public String getCommandName() {
            return "pt";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/pt <name>";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 0) {
                String name = String.join(" ", args);
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/p transfer " + name);
            }
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