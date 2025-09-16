package org.ginafro.notenoughfakepixel.config.gui.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.*;

public abstract class SimpleCommand extends CommandBase {

    public abstract String getName();
    public abstract String getUsage();
    public abstract void execute(ICommandSender sender, String[] args) throws CommandException;

    private static final Set<String> SLASH_ONLY = new HashSet<>();

    protected SimpleCommand() {
        SLASH_ONLY.add(getName().toLowerCase(Locale.ROOT));
    }

    public List<String> getAliases() {
        return Collections.emptyList();
    };

    @Override
    public List<String> getCommandAliases() {
        return getAliases();
    }

    @Override
    public String getCommandName() {
        return getName();
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getUsage();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        execute(sender, args);
    }

    public static Set<String> getSlashOnlyNames() {
        return Collections.unmodifiableSet(SLASH_ONLY);
    }

    public static boolean isSlashOnly(String cmd) {
        return cmd != null && SLASH_ONLY.contains(cmd.toLowerCase(Locale.ROOT));
    }

}
