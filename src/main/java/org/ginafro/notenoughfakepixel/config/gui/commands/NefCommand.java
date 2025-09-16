package org.ginafro.notenoughfakepixel.config.gui.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import org.apache.commons.lang3.StringUtils;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.config.gui.config.ConfigEditor;
import org.ginafro.notenoughfakepixel.config.gui.core.GuiScreenElementWrapper;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterCommand;
import org.ginafro.notenoughfakepixel.utils.ListUtils;

import java.util.List;

@RegisterCommand
public class NefCommand extends SimpleCommand {

    @Override
    public String getName() {
        return "nef";
    }

    @Override
    public String getUsage() {
        return "/nef <category?>";
    }

    @Override
    public List<String> getAliases() {
        return ListUtils.of("notenoughfakepixel");
    }

    @Override
    public void execute(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            Config.screenToOpen = new GuiScreenElementWrapper(new ConfigEditor(Config.feature));
        }
        Config.screenToOpen = new GuiScreenElementWrapper(new ConfigEditor(Config.feature, StringUtils.join(args, " ")));
    }
}
