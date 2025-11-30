package com.nef.notenoughfakepixel.config.gui.commands;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.config.ConfigEditor;
import com.nef.notenoughfakepixel.config.gui.core.GuiScreenElementWrapper;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterCommand;
import com.nef.notenoughfakepixel.utils.ListUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import org.apache.commons.lang3.StringUtils;

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
