package org.ginafro.notenoughfakepixel.features.skyblock.overlays.inventory.invbuttons;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.ItemUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

@Deprecated
public class InvManager {
//
//    public static ArrayList<InventoryButton> buttons = new ArrayList<>();
//    public static ArrayList<ItemStack> itemMap = new ArrayList<>();
//    public static boolean isEditor = false;
//    public static InventoryButton selected = null;
//
//    private static final HashMap<String, String> skullIcons = new HashMap<String, String>() {{
//        put("personal bank", "e36e94f6c34a35465fce4a90f2e25976389eb9709a12273574ff70fd4daa6852");
//        put("skyblock hub", "d7cc6687423d0570d556ac53e0676cb563bbdd9717cd8269bdebed6f6d4e7bf8");
//        put("private island", "c9c8881e42915a9d29bb61a16fb26d059913204d265df5b439b3d792acd56");
//        put("castle", "f4559d75464b2e40a518e4de8e6cf3085f0a3ca0b1b7012614c4cd96fed60378");
//        put("sirius shack", "7ab83858ebc8ee85c3e54ab13aabfcc1ef2ad446d6a900e471c3f33b78906a5b");
//        put("crypts", "25d2f31ba162fe6272e831aed17f53213db6fa1c4cbe4fc827f3963cc98b9");
//        put("spiders den", "c754318a3376f470e481dfcd6c83a59aa690ad4b4dd7577fdad1c2ef08d8aee6");
//        put("top of the nest", "9d7e3b19ac4f3dee9c5677c135333b9d35a7f568b63d1ef4ada4b068b5a25");
//        put("the end", "7840b87d52271d2a755dedc82877e0ed3df67dcc42ea479ec146176b02779a5");
//        put("the end dragons nest", "a1cd6d2d03f135e7c6b5d6cdae1b3a68743db4eb749faf7341e9fb347aa283b");
//        put("the park", "a221f813dacee0fef8c59f76894dbb26415478d9ddfc44c2e708a6d3b7549b");
//        put("the park jungle", "79ca3540621c1c79c32bf42438708ff1f5f7d0af9b14a074731107edfeb691c");
//        put("the park howling cave", "1832d53997b451635c9cf9004b0f22bb3d99ab5a093942b5b5f6bb4e4de47065");
//        put("gold mines", "73bc965d579c3c6039f0a17eb7c2e6faf538c7a5de8e60ec7a719360d0a857a9");
//        put("deep caverns", "569a1f114151b4521373f34bc14c2963a5011cdc25a6554c48c708cd96ebfc");
//        put("the barn", "4d3a6bd98ac1833c664c4909ff8d2dc62ce887bdcf3cc5b3848651ae5af6b");
//        put("mushroom desert", "6b20b23c1aa2be0270f016b4c90d6ee6b8330a17cfef87869d6ad60b2ffbf3b5");
//        put("dungeon hub", "9b56895b9659896ad647f58599238af532d46db9c1b0389b8bbeb70999dab33d");
//        put("dwarven mines", "51539dddf9ed255ece6348193cd75012c82c93aec381f05572cecf7379711b3b");
//        put("hotm heart of the mountain", "86f06eaa3004aeed09b3d5b45d976de584e691c0e9cade133635de93d23b9edb");
//        put("bazaar dude", "c232e3820897429157619b0ee099fec0628f602fff12b695de54aef11d923ad7");
//        put("museum", "438cf3f8e54afc3b3f91d20a49f324dca1486007fe545399055524c17941f4dc");
//        put("crystal hollows", "21dbe30b027acbceb612563bd877cd7ebb719ea6ed1399027dcee58bb9049d4a");
//        put("dwarven forge", "5cbd9f5ec1ed007259996491e69ff649a3106cf920227b1bb3a71ee7a89863f");
//        put("forgotton skull", "6becc645f129c8bc2faa4d8145481fab11ad2ee75749d628dcd999aa94e7");
//        put("crystal nucleus", "34d42f9c461cee1997b67bf3610c6411bf852b9e5db607bbf626527cfb42912c");
//        put("void sepulture", "eb07594e2df273921a77c101d0bfdfa1115abed5b9b2029eb496ceba9bdbb4b3");
//        put("crimson isle", "c3687e25c632bce8aa61e0d64c24e694c3eea629ea944f4cf30dcfb4fbce071");
//        put("trapper den", "6102f82148461ced1f7b62e326eb2db3a94a33cba81d4281452af4d8aeca4991");
//        put("arachne sanctuary", "35e248da2e108f09813a6b848a0fcef111300978180eda41d3d1a7a8e4dba3c3");
//        put("garden", "f4880d2c1e7b86e87522e20882656f45bafd42f94932b2c5e0d6ecaa490cb4c");
//        put("winter", "6dd663136cafa11806fdbca6b596afd85166b4ec02142c8d5ac8941d89ab7");
//        put("wizard tower", "838564e28aba98301dbda5fafd86d1da4e2eaeef12ea94dcf440b883e559311c");
//        put("dwarven mines base camp", "2461ec3bd654f62ca9a393a32629e21b4e497c877d3f3380bcf2db0e20fc0244");
//    }};
//
//    public static void drawButtons() {
//        if (!Config.feature.overlays.invButtons) return;
//        for (InventoryButton b : buttons) {
//            b.render();
//        }
//    }
//
//    public static void save() {
//        InventoryData data = new InventoryData(buttons);
//        File file = new File(NotEnoughFakepixel.nefFolder, "invbuttons.json");
//        try {
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            FileWriter writer = new FileWriter(file);
//            writer.write(gson.toJson(data));
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void load() {
//        File file = new File(NotEnoughFakepixel.nefFolder, "invbuttons.json");
//        try {
//            if (!file.exists()) {
//                file.createNewFile();
//                return;
//            }
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            FileReader reader = new FileReader(file);
//            InventoryData data = gson.fromJson(reader, InventoryData.class);
//            if (data == null) return;
//            buttons = data.getButtons();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NBTException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void click(int mouseX, int mouseY) {
//        if (!Config.feature.overlays.invButtons) return;
//        for (InventoryButton b : buttons) {
//            if (b.isHovered(mouseX, mouseY)) {
//                if (isEditor) {
//                    selected = b;
//                    break;
//                }
//                b.process();
//                break;
//            }
//        }
//    }
//
//    public static void addButton(InventoryButton b) {
//        buttons.add(b);
//    }
//
//    public static void addButtons(InventoryButton... b) {
//        buttons.addAll(Arrays.asList(b));
//    }
//
//
//    public static void removeButton(InventoryButton b) {
//        buttons.removeIf(button -> button.equals(b));
//    }
//
//    public static void loadItemStacks() {
//        itemMap.clear();
//        for (ResourceLocation rl : Item.itemRegistry.getKeys()) {
//            Item item = Item.itemRegistry.getObject(rl);
//            try {
//                ItemStack stack = new ItemStack(item);
//                if (!itemMap.contains(stack)) {
//                    itemMap.add(new ItemStack(item));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        itemMap.addAll(ItemUtils.getAllCustomSkulls(skullIcons));
//        itemMap.sort(Comparator.comparing(s -> s.getDisplayName().toLowerCase()));
//    }
}
