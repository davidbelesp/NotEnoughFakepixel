package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.ginafro.notenoughfakepixel.config.gui.core.util.StringUtils;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterCommand;
import org.ginafro.notenoughfakepixel.utils.NumberUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;
import scala.actors.threadpool.Arrays;

import java.util.List;

@RegisterCommand
public class CrystalWaypointCommand extends CommandBase {

    @Override public String getCommandName() { return "chw"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/chw new <name>"; }
    @Override public int getRequiredPermissionLevel() { return 0; }

    private final String PREFIX = "§6[§eChWaypoints§6] §r";

    @Override public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!TablistParser.currentLocation.equals(Location.CRYSTAL_HOLLOWS)) throw new WrongUsageException("You can only use waypoints in Crystal Hollows");
        if (args.length >= 1 && "new".equalsIgnoreCase(args[0])) {
            if (args.length < 2) throw new WrongUsageException("Usage: " + getCommandUsage(sender));

            String name = StringUtils.joinStrings(args, 1);
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer == null) throw new CommandException("You are not online");

            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;

            ChWaypoint wp = ChWaypoint.of(x, y, z, name);
            CrystalWaypoints.getInstance().addWaypoint(wp);

            notify(sender, PREFIX + "§aWaypoint created: §c%s §7(§c%.1f§7, §a%.1f§7, §9%.1f§7)", name, x, y, z);
            return;
        }
        if (args.length >= 1 && "list".equalsIgnoreCase(args[0])) {
            List<ChWaypoint> list = CrystalWaypoints.getInstance().getAll();
            if (list.isEmpty()) {
                sender.addChatMessage(new ChatComponentText(PREFIX + "§7No waypoints yet."));
            } else {
                for (ChWaypoint w : list) WaypointChatMessages.sendWaypointLine(sender, w);
            }
            return;
        }

        if (args.length >= 1 && "del".equalsIgnoreCase(args[0])) {
            if (args.length < 2) throw new WrongUsageException("Usage: /chw del <id>");
            boolean ok = CrystalWaypoints.getInstance().removeById(args[1]);
            notify(sender, ok ? "Deleted." : "ID not found.");
            return;
        }

        if ("copy".equalsIgnoreCase(args[0])) {
            if (args.length < 2) throw new WrongUsageException("Usage: /chw copy <id>");
            ChWaypoint w = CrystalWaypoints.getInstance().findById(args[1]);
            if (w == null) {
                sender.addChatMessage(new ChatComponentText(PREFIX + "§7ID not found."));
                return;
            }
            String coords = WaypointChatMessages.formatCoordsEs(w);
            GuiScreen.setClipboardString(coords);
            sender.addChatMessage(new ChatComponentText(PREFIX + "§aCopied to clipboard: §f" + coords));
            return;
        }

        if ("share".equalsIgnoreCase(args[0])) {
            if (args.length < 2) throw new WrongUsageException("Usage: /chw share <id>");
            ChWaypoint w = CrystalWaypoints.getInstance().findById(args[1]);
            if (w == null) {
                sender.addChatMessage(new ChatComponentText(PREFIX + "§cID not found."));
                return;
            }

            // share message: CHW:<name>:<x>:<y>:<z>
            String msgToShare = String.format("CHW:%s:%.1f:%.1f:%.1f", w.getName(), w.x, w.y, w.z);
            if (sender instanceof EntityPlayerSP) {
                ((EntityPlayerSP) sender).sendChatMessage(msgToShare);
            }

            return;
        }

        if ("add".equalsIgnoreCase(args[0])) {
            if (args.length < 5) throw new WrongUsageException("Usage: /chw add <Nombre...> <x> <y> <z>");
            int n = args.length;
            String sx = args[n - 3], sy = args[n - 2], sz = args[n - 1];
            String name = StringUtils.joinRange(args, 1, n - 3);

            double x = NumberUtils.parseDoubleFlexible(sx);
            double y = NumberUtils.parseDoubleFlexible(sy);
            double z = NumberUtils.parseDoubleFlexible(sz);

            ChWaypoint wp = new ChWaypoint(x, y, z, java.util.UUID.randomUUID().toString(), name);
            CrystalWaypoints.getInstance().addWaypoint(wp);
            sender.addChatMessage(new ChatComponentText("§aWaypoint added from chat: §e" + name + " §7(" + WaypointChatMessages.formatCoordsEs(x,y,z) + ")"));
            return;
        }

        if ("copycoords".equalsIgnoreCase(args[0])) {
            if (args.length < 4) throw new WrongUsageException("Uso: /chw copycoords <x> <y> <z>");
            String sx = args[1].replace('.', ',');
            String sy = args[2].replace('.', ',');
            String sz = args[3].replace('.', ',');
            String text = "x" + sx + ", y" + sy + ", z" + sz;

            net.minecraft.client.gui.GuiScreen.setClipboardString(text);
            sender.addChatMessage(new ChatComponentText("§aCopiado al portapapeles: §f" + text));
            return;
        }

        throw new WrongUsageException(getCommandUsage(sender));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) return Arrays.asList(new String[]{"new", "list"});
        return null;
    }

    public static void notify(ICommandSender sender, String format, Object... args) {
        String msg = String.format(format, args);
        sender.addChatMessage(new ChatComponentText(msg));
    }

}
