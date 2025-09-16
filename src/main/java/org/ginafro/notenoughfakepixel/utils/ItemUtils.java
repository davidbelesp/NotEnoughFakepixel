package org.ginafro.notenoughfakepixel.utils;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.ginafro.notenoughfakepixel.variables.Rarity;
import org.ginafro.notenoughfakepixel.variables.Skins;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class ItemUtils {

    public static String getInternalName(ItemStack item) {
        if (!item.hasTagCompound()) return "";
        if (!item.getTagCompound().hasKey("ExtraAttributes")) return "";

        NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
        if (!extraAttributes.hasKey("id")) return "";

        return extraAttributes.getString("id");
    }

    public static @NotNull NBTTagCompound getExtraAttributes(ItemStack itemStack) {
        NBTTagCompound tag = getOrCreateTag(itemStack);
        NBTTagCompound extraAttributes = tag.getCompoundTag("ExtraAttributes");
        tag.setTag("ExtraAttributes", extraAttributes);
        return extraAttributes;
    }

    public static int getExtraAttributesIntTag(ItemStack item, String tag) {
        NBTTagCompound extraAttributes = getExtraAttributes(item);
        if (!extraAttributes.hasKey(tag)) return -1;
        return extraAttributes.getInteger(tag);
    }

    public static String getExtraAttributesStringTag(ItemStack item, String tag) {
        NBTTagCompound extraAttributes = getExtraAttributes(item);
        if (!extraAttributes.hasKey(tag)) return "";
        return extraAttributes.getString(tag);
    }

    public static List<ItemStack> getAllCustomSkulls(Map<String, String> skullIcons) {
        List<ItemStack> skulls = new ArrayList<>();
        for (Map.Entry<String, String> entry : skullIcons.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue().replace("skull:", "").trim();
            skulls.add(createSkullWithTexture(name, value));
        }
        return skulls;
    }

    public static String getSkullTexture(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) return "";
        if (!itemStack.getTagCompound().hasKey("SkullOwner")) return "";

        NBTTagCompound skullOwner = itemStack.getTagCompound().getCompoundTag("SkullOwner");
        if (!skullOwner.hasKey("Properties")) return "";

        NBTTagCompound properties = skullOwner.getCompoundTag("Properties");
        if (!properties.hasKey("textures")) return "";

        NBTTagList textures = properties.getTagList("textures", 10);
        if (textures.tagCount() == 0) return "";

        NBTTagCompound texture = textures.getCompoundTagAt(0);
        if (!texture.hasKey("Value")) return "";

        return texture.getString("Value");
    }

    public static ItemStack createSkullWithTexture(String name, String textureHash) {
        ItemStack skull = new ItemStack(Items.skull, 1, 3); // 3 = player head

        NBTTagCompound skullTag = new NBTTagCompound();
        NBTTagCompound skullOwner = new NBTTagCompound();

        skullOwner.setString("Id", UUID.randomUUID().toString());

        // Create texture compound
        NBTTagCompound textures = new NBTTagCompound();
        NBTTagList texturesList = new NBTTagList();
        NBTTagCompound valueTag = new NBTTagCompound();

        String fullTexture = "eyJ0aW1lc3RhbXAiOjAsInByb2ZpbGVJZCI6IiIsInByb2ZpbGVOYW1lIjoiIiwidGV4dHVyZXMiOns" +
                "iU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL" +
                "y" + textureHash + "\"}}}";

        valueTag.setString("Value", Base64.getEncoder().encodeToString(fullTexture.getBytes()));
        texturesList.appendTag(valueTag);
        textures.setTag("textures", texturesList);

        skullOwner.setTag("Properties", textures);
        skullTag.setTag("SkullOwner", skullOwner);
        skull.setTagCompound(skullTag);
        skull.setStackDisplayName(name);
        return skull;
    }

    public static NBTTagCompound getOrCreateTag(ItemStack is) {
        if (is.hasTagCompound()) return is.getTagCompound();
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        is.setTagCompound(nbtTagCompound);
        return nbtTagCompound;
    }

    public static void addLoreLine(ItemStack item, String line) {
        if (!item.hasTagCompound()) return;
        if (!item.getTagCompound().hasKey("display")) return;
        if (!item.getTagCompound().getCompoundTag("display").hasKey("Lore")) return;

        NBTTagCompound displayTag = item.getTagCompound().getCompoundTag("display");
        NBTTagList lore = displayTag.getTagList("Lore", 8);

        lore.appendTag(new NBTTagString(line));
    }

    public static String getLoreLine(ItemStack item, String matcher) {
        if (!item.hasTagCompound()) return null;
        if (!item.getTagCompound().hasKey("display")) return null;
        if (!item.getTagCompound().getCompoundTag("display").hasKey("Lore")) return null;

        NBTTagCompound displayTag = item.getTagCompound().getCompoundTag("display");
        NBTTagList lore = displayTag.getTagList("Lore", 8);

        for (int i = 0; i < lore.tagCount(); i++) {
            String line = lore.getStringTagAt(i);
            if (line.contains(matcher)) return line;
        }

        return null;
    }

    public static Rarity getRarity(ItemStack item) {
        if (item == null || !item.hasTagCompound()) return Rarity.NONE;

        List<String> loreLines = getLoreLines(item);
        if (loreLines.isEmpty()) return Rarity.NONE;

        String clean = ColorUtils.cleanColor(loreLines.get(loreLines.size() - 1));
        return Optional.ofNullable(Rarity.fromString(clean)).orElse(Rarity.NONE);
    }

    public static List<String> getLoreLines(ItemStack item) {
        if (item == null || !item.hasTagCompound()) return Collections.emptyList();

        NBTTagCompound displayTag = item.getTagCompound().getCompoundTag("display");
        if (displayTag == null || !displayTag.hasKey("Lore")) return Collections.emptyList();

        NBTTagList lore = displayTag.getTagList("Lore", 8);
        List<String> loreLines = new ArrayList<>();

        for (int i = 0; i < lore.tagCount(); i++) {
            loreLines.add(lore.getStringTagAt(i));
        }

        return loreLines;
    }

    public static String getLoreLine(ItemStack item, Pattern matcher) {
        List<String> lore = getLoreLines(item);
        if (lore.isEmpty()) return null;

        return lore.stream()
                .filter(line -> matcher.matcher(line).matches())
                .findFirst()
                .orElse(null);
    }

    public static boolean hasSkinValue(String value, ItemStack item) {
        if (item == null) return false;
        if (!item.hasTagCompound()) return false;
        if (!item.getTagCompound().hasKey("SkullOwner")) return false;
        NBTTagCompound skullOwner = item.getTagCompound().getCompoundTag("SkullOwner");
        if (!skullOwner.hasKey("Properties")) return false;
        NBTTagCompound properties = skullOwner.getCompoundTag("Properties");
        if (!properties.hasKey("textures")) return false;
        NBTTagList textures = properties.getTagList("textures", 10);
        for (int i = 0; i < textures.tagCount(); i++) {
            NBTTagCompound texture = textures.getCompoundTagAt(i);
            if (texture.hasKey("Value") && texture.getString("Value").equals(value)) return true;
        }
        return false;
    }

    public static boolean hasSkinValue(Skins skin, ItemStack item) {
        if (item == null) return false;
        if (!item.hasTagCompound()) return false;
        if (!item.getTagCompound().hasKey("SkullOwner")) return false;
        NBTTagCompound skullOwner = item.getTagCompound().getCompoundTag("SkullOwner");
        if (!skullOwner.hasKey("Properties")) return false;
        NBTTagCompound properties = skullOwner.getCompoundTag("Properties");
        if (!properties.hasKey("textures")) return false;
        NBTTagList textures = properties.getTagList("textures", 10);
        for (int i = 0; i < textures.tagCount(); i++) {
            NBTTagCompound texture = textures.getCompoundTagAt(i);
            if (texture.hasKey("Value")) {
                if (texture.getString("Value").equals(skin.getHttp()) || texture.getString("Value").equals(skin.getHttps())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSkyblockItem(ItemStack item) {
        if (item == null) return false;
        if (!item.hasTagCompound()) return false;
        if (!item.getTagCompound().hasKey("ExtraAttributes")) return false;
        NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
        return extraAttributes.hasKey("id");
    }

    public static boolean isMenuItem(ItemStack item) {
        return item.getDisplayName().trim().isEmpty() && Item.getItemFromBlock(Blocks.stained_glass_pane) == item.getItem() && item.getItemDamage() == 15;
    }
}
