package com.nef.notenoughfakepixel.utils;

import com.nef.notenoughfakepixel.variables.Rarity;
import com.nef.notenoughfakepixel.variables.Skins;
import com.nef.notenoughfakepixel.variables.StackingEnchant;
import lombok.Data;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class ItemUtils {

    public static String[] getLoreFromNBT(NBTTagCompound tag) {
        return ItemUtils.getLore(tag).toArray(new String[0]);
    }

    public static @NotNull List<@NotNull String> getLore(@Nullable NBTTagCompound tagCompound) {
        if (tagCompound == null) {
            return Collections.emptyList();
        }
        NBTTagList tagList = tagCompound.getCompoundTag("display").getTagList("Lore", 8);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            list.add(tagList.getStringTagAt(i));
        }
        return list;
    }

    public static String getInternalName(ItemStack item) {
        if (item == null) return "";
        if (!item.hasTagCompound()) return "";
        if (!item.getTagCompound().hasKey("ExtraAttributes")) return "";

        NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
        if (!extraAttributes.hasKey("id")) return "";

        return extraAttributes.getString("id");
    }

    public static String getItemUUID(ItemStack item) {
        if (item == null) return "";
        if (!item.hasTagCompound()) return "";
        if (!item.getTagCompound().hasKey("ExtraAttributes")) return "";

        NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
        if (!extraAttributes.hasKey("uuid")) return "";

        return extraAttributes.getString("uuid");
    }

    public static void renameItem(ItemStack item, String newName) {
        if (item == null) return;
        NBTTagCompound tag = getOrCreateTag(item);
        NBTTagCompound displayTag = tag.getCompoundTag("display");
        displayTag.setString("Name", newName);
        tag.setTag("display", displayTag);
        item.setTagCompound(tag);
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

        String lastLine = loreLines.get(loreLines.size() - 1);

        lastLine = StringUtils.stripFormattingFastRarity(lastLine);
        return Rarity.fromString(lastLine);
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

    @Data public static class AdminStatus {
        boolean isAdmin;
        String reason;
        String detail;

        public AdminStatus(String reason, String detail) {
            this.reason = reason;
            this.detail = detail;
            this.isAdmin = true;
        }

        public AdminStatus() {
            this.isAdmin = false;
            this.reason = "";
            this.detail = "";
        }
    }

    public static AdminStatus isAdminItem(ItemStack item) {
        if (item == null) return new AdminStatus();
        // Checking if custom item / custom pet
        if (getInternalName(item).trim().equals("CUSTOM_ITEM")) return new AdminStatus("Custom Item", "");
        if (getInternalName(item).trim().contains("PET_CUSTOM")) return new AdminStatus("Custom Pet", "");

        if (!item.hasTagCompound()) return new AdminStatus();
        if (!item.getTagCompound().hasKey("ExtraAttributes")) return new AdminStatus();
        NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");

        // Checking origin
        if (extraAttributes.hasKey("origin")) {
            if (extraAttributes.getString("origin").contains("ALL_ITEMS_GUI_ACTOR")) return new AdminStatus("", "");
            if (extraAttributes.getString("origin").contains("random")) return new AdminStatus("Gambled item", "");
        }

        // Checking candy count
        if (extraAttributes.hasKey("candy")) {
            if (extraAttributes.getInteger("candy") < 0) return new AdminStatus("Candies", "");
            if (extraAttributes.getInteger("candy") > 10) return new AdminStatus("Candies", "");
        }

        // Checking abnormal stats
        if (extraAttributes.hasKey("stars")) {
           if (extraAttributes.getInteger("stars") > 5) return new AdminStatus("Stars", "");
           if (extraAttributes.getInteger("stars") < 0) return new AdminStatus("Stars", "");
        }

        if (extraAttributes.hasKey("master_stars")) {
            if (extraAttributes.getInteger("master_stars") > 5) return new AdminStatus("Master Stars", "");
            if (extraAttributes.getInteger("master_stars") < 0) return new AdminStatus("Master Stars", "");;
        }

        // Checking abnormal bids
        if (extraAttributes.hasKey("bid")) {
            if (extraAttributes.getInteger("bid") > 10) return new AdminStatus("Edited tags", "");
            if (extraAttributes.getInteger("bid") < 0) return new AdminStatus("Edited tags", "");;
        }

        // Checking abnormal dates
        if (extraAttributes.hasKey("timestamp")) {
            long timestamp = extraAttributes.getLong("timestamp");
            if (timestamp < 1640995200L) return new AdminStatus("Invalid date", "");;
        }

        // Checking invalid level enchantments
        if (extraAttributes.hasKey("enchantments")) {
            NBTTagCompound enchantments = extraAttributes.getCompoundTag("enchantments");
            for (String key : enchantments.getKeySet()) {
                int level = enchantments.getInteger(key);
                if (level < 0 || level > 10) {
                    return new AdminStatus(StringUtils.capitalizeName(key), String.format("%d", level));
                }
            }
        }

        // Checking abnormal xp amount
        if (extraAttributes.hasKey("exp")) {
            double xp = extraAttributes.getDouble("exp");
            if (xp > 1.5E9) return new AdminStatus("Leveled up", "");;
        }

        return new AdminStatus();
    }

    public static String getAdminName(ItemStack item) {
        if (!item.hasTagCompound()) return "";
        if (!item.getTagCompound().hasKey("ExtraAttributes")) return "";
        NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");

        if (extraAttributes.hasKey("origin")) {
            if (extraAttributes.getString("origin").contains("ALL_ITEMS_GUI_ACTOR")) {
                return extraAttributes.getString("origin").replace("ALL_ITEMS_GUI_ACTOR_", "");
            }
        }

        return "";
    }

    public static boolean hasStackingCounter(ItemStack item) {
        if (item == null) return false;
        if (!item.hasTagCompound()) return false;
        if (!item.getTagCompound().hasKey("ExtraAttributes")) return false;
        NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
        return extraAttributes.hasKey("stacking_enchant_counter");
    }

    public static int getStackingCounter(ItemStack item) {
        NBTTagCompound extraAttributes = getExtraAttributes(item);
        return extraAttributes.getInteger("stacking_enchant_counter");
    }

    public static StackingEnchant getStackingEnchant(ItemStack item) {
        List<String> loreLines = getLoreLines(item);
        for (String line : loreLines) {
            if (line.trim().contains("Cultivating")) {
                return StackingEnchant.CULTIVATING;
            } else if (line.trim().contains("Compact")) {
                return StackingEnchant.COMPACT;
            } else if (line.trim().contains("Expertise")) {
                return StackingEnchant.EXPERTISE;
            } else if (line.trim().contains("Champion")) {
                return StackingEnchant.CHAMPION;
            } else if (line.trim().contains("Toxophilite")) {
                return StackingEnchant.TOXOPHILITE;
            }
        }
        return StackingEnchant.NONE;
    }

}