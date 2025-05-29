package org.ginafro.notenoughfakepixel.features.skyblock.dungeons;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.variables.Skins;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterEvents
public class M7RelicWaypoints {

    public enum RelicColor {
        PURPLE, GREEN, RED, ORANGE, BLUE
    }

    private static final Map<RelicColor, BlockPos> relicPositions = new HashMap<>();
    private static final Map<RelicColor, BlockPos> cauldronPositions = new HashMap<>();
    public boolean isFinalPhase = false;
    private Set<RelicColor> pickedRelics = new HashSet<>();

    static {
        relicPositions.put(RelicColor.PURPLE, new BlockPos(64, 8, 140));
        relicPositions.put(RelicColor.GREEN, new BlockPos(28, 6, 102));
        relicPositions.put(RelicColor.RED, new BlockPos(28, 6, 67));
        relicPositions.put(RelicColor.ORANGE, new BlockPos(100, 6, 64));
        relicPositions.put(RelicColor.BLUE, new BlockPos(99, 6, 102));

        cauldronPositions.put(RelicColor.PURPLE, new BlockPos(62, 7, 49));
        cauldronPositions.put(RelicColor.GREEN, new BlockPos(57, 7, 52));
        cauldronPositions.put(RelicColor.RED, new BlockPos(59, 7, 50));
        cauldronPositions.put(RelicColor.ORANGE, new BlockPos(65, 7, 50));
        cauldronPositions.put(RelicColor.BLUE, new BlockPos(67, 7, 52));
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!DungeonManager.checkEssentialsF7()) return;
        if (!isFinalPhase) return;
        if (!Config.feature.dungeons.m7Relics) return;
        float partialTicks = event.partialTicks;

        for (RelicColor color : RelicColor.values()) {

            if (!pickedRelics.contains(color)) {
                BlockPos pos = relicPositions.get(color);
                int rgb = getColorRGB(color);
                RenderUtils.highlightBlock(pos, new Color(rgb), true, partialTicks);
                RenderUtils.renderBeaconBeam(pos, rgb, 1.0f, partialTicks);
            }
            else {
                if (hasRelicInInventory(color)) {
                    BlockPos cauldronPos = cauldronPositions.get(color);
                    int rgb = getColorRGB(color);
                    RenderUtils.renderBoundingBox(cauldronPos, rgb, partialTicks);
                }
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText().toLowerCase();

       if (message.contains("picked the corrupted")) {
            for (RelicColor color : RelicColor.values()) {
                String colorStr = color.name().toLowerCase();
                if (message.contains(colorStr + " relic!")) {
                    pickedRelics.add(color);
                    break;
                }
            }
        }

        if (event.message.getUnformattedText().contains("The Catacombs... are no more")) {
            isFinalPhase = true;
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        pickedRelics.clear();
    }

    private int getColorRGB(RelicColor color) {
        switch (color) {
            case PURPLE: return 0x800080;
            case GREEN: return 0x008000;
            case RED: return 0xFF0000;
            case ORANGE: return 0xFFA500;
            case BLUE: return 0x0000FF;
            default: return 0xFFFFFF;
        }
    }

    private boolean hasRelicInInventory(RelicColor color) {
        Skins skin = getSkinForColor(color);
        if (skin == null) return false;
        String targetValue = skin.getValue();

        for (int i = 0; i < 36; i++) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i);
            if (isRelicSkull(stack, targetValue)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRelicSkull(ItemStack stack, String targetValue) {
        if (stack == null ||
                stack.getItem() != Items.skull ||
                stack.getMetadata() != 3) {  // 3 = player skull
            return false;
        }

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("SkullOwner", 10)) return false;

        NBTTagCompound skullOwner = tag.getCompoundTag("SkullOwner");
        if (!skullOwner.hasKey("Properties", 10)) return false;

        NBTTagCompound properties = skullOwner.getCompoundTag("Properties");
        NBTTagList textures = properties.getTagList("textures", 10);  // 10 = compound type
        if (textures.tagCount() == 0) return false;

        NBTTagCompound texture = textures.getCompoundTagAt(0);
        String value = texture.getString("Value");
        return value.equals(targetValue);
    }

    private Skins getSkinForColor(RelicColor color) {
        switch (color) {
            case PURPLE: return Skins.PURPLE_RELIC;
            case GREEN: return Skins.GREEN_RELIC;
            case RED: return Skins.RED_RELIC;
            case ORANGE: return Skins.ORANGE_RELIC;
            case BLUE: return Skins.BLUE_RELIC;
            default: return null;
        }
    }
}