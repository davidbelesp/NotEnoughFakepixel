package com.nef.notenoughfakepixel.features.skyblock.debug;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.utils.Logger;
import com.nef.notenoughfakepixel.variables.Colors;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.*;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

@RegisterEvents
public class CopyNbtData {

    private static final String INDENT = "    ";

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!Config.feature.debug.debug) return;

        int eventKey = Keyboard.getEventKey();
        if (eventKey != Keyboard.KEY_RCONTROL || !Keyboard.getEventKeyState()) return;

        if (!(event.gui instanceof GuiContainer)) return;

        GuiContainer guiContainer = (GuiContainer) event.gui;
        Slot currentSlot = guiContainer.getSlotUnderMouse();

        if (currentSlot != null && currentSlot.getHasStack()) {
            NBTTagCompound nbt = currentSlot.getStack().serializeNBT();
            copyNBTTagToClipboard(nbt, Colors.GREEN + "Item data was copied to clipboard!");
        }
    }

    public static void copyNBTTagToClipboard(NBTBase nbtTag, String message) {
        if (nbtTag == null) {
            Logger.log(Colors.RED, "NBT Tag is null, cannot copy to clipboard.");
            return;
        }

        StringBuilder sb = new StringBuilder(4096);
        prettyPrintRecursive(nbtTag, sb, 0);

        writeToClipboard(sb.toString(), message);
    }

    private static void prettyPrintRecursive(NBTBase nbt, StringBuilder sb, int depth) {
        int tagID = nbt.getId();

        switch (tagID) {
            case Constants.NBT.TAG_END:
                sb.append('}');
                break;

            case Constants.NBT.TAG_BYTE_ARRAY:
                appendByteArray((NBTTagByteArray) nbt, sb);
                break;

            case Constants.NBT.TAG_INT_ARRAY:
                appendIntArray((NBTTagIntArray) nbt, sb);
                break;

            case Constants.NBT.TAG_LIST:
                appendList((NBTTagList) nbt, sb, depth);
                break;

            case Constants.NBT.TAG_COMPOUND:
                appendCompound((NBTTagCompound) nbt, sb, depth);
                break;

            default:
                // Primitives (String, Int, Double, etc.)
                sb.append(nbt.toString());
                break;
        }
    }

    private static void appendCompound(NBTTagCompound compound, StringBuilder sb, int depth) {
        sb.append('{');

        if (compound.hasNoTags()) {
            sb.append('}');
            return;
        }

        sb.append(System.lineSeparator());
        Set<String> keys = compound.getKeySet();
        Iterator<String> iterator = keys.iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
            NBTBase currentTag = compound.getTag(key);

            appendIndent(sb, depth + 1);
            sb.append(key).append(": ");

            prettyPrintRecursive(currentTag, sb, depth + 1);

            if (key.contains("backpack_data") && currentTag instanceof NBTTagByteArray) {
                decodeBackpackData((NBTTagByteArray) currentTag, sb, depth + 1);
            }

            if (iterator.hasNext()) {
                sb.append(",").append(System.lineSeparator());
            }
        }

        sb.append(System.lineSeparator());
        appendIndent(sb, depth);
        sb.append('}');
    }

    private static void appendList(NBTTagList list, StringBuilder sb, int depth) {
        sb.append('[');
        int count = list.tagCount();

        for (int i = 0; i < count; i++) {
            prettyPrintRecursive(list.get(i), sb, depth);

            if (i < count - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
    }

    private static void appendByteArray(NBTTagByteArray nbt, StringBuilder sb) {
        sb.append('[');
        byte[] bytes = nbt.getByteArray();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(bytes[i]);
            if (i < bytes.length - 1) sb.append(", ");
        }
        sb.append(']');
    }

    private static void appendIntArray(NBTTagIntArray nbt, StringBuilder sb) {
        sb.append('[');
        int[] ints = nbt.getIntArray();
        for (int i = 0; i < ints.length; i++) {
            sb.append(ints[i]);
            if (i < ints.length - 1) sb.append(", ");
        }
        sb.append(']');
    }

    private static void decodeBackpackData(NBTTagByteArray tag, StringBuilder sb, int depth) {
        try {
            byte[] data = tag.getByteArray();
            NBTTagCompound backpackData = CompressedStreamTools.readCompressed(new ByteArrayInputStream(data));

            sb.append(",").append(System.lineSeparator());
            appendIndent(sb, depth);
            sb.append("(decoded): ");
            prettyPrintRecursive(backpackData, sb, depth);

        } catch (IOException e) {
            Logger.logError("Couldn't decompress backpack data into NBT, skipping!");
        }
    }

    private static void appendIndent(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append(INDENT);
        }
    }

    private static void writeToClipboard(String text, String successMessage) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection output = new StringSelection(text);
            clipboard.setContents(output, output);

            if (successMessage != null) {
                Logger.log(successMessage);
            }
        } catch (IllegalStateException | HeadlessException exception) {
            Logger.logError("Clipboard not available or system is headless!");
        }
    }
}