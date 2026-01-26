package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.env.registers.RegisterEvents;
import com.nef.notenoughfakepixel.events.PacketReadEvent;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.SoundUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@RegisterEvents
public class MiscFeatures {

    ArrayList<Block> flowerPlaceable = new ArrayList<>(Arrays.asList(
            Blocks.grass,
            Blocks.dirt,
            Blocks.flower_pot,
            Blocks.tallgrass,
            Blocks.double_plant
    ));

    List<ItemStack> armour = new ArrayList<>();

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock() || mc.thePlayer != event.entityPlayer) return;
        ItemStack item = event.entityPlayer.getHeldItem();
        if (item == null) return;

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Block block = mc.theWorld.getBlockState(event.pos).getBlock();

            if (flowerPlaceable.contains(block)) {
                if (Config.feature.qol.qolBlockPlacingItems && item.getDisplayName().contains("Flower of Truth")) {
                    event.setCanceled(true);
                }
                if (Config.feature.qol.qolBlockPlacingItems && item.getDisplayName().contains("Spirit Sceptre")) {
                    event.setCanceled(true);
                }
                if (Config.feature.qol.qolBlockPlacingItems && item.getDisplayName().contains("Bouquet of Lies")) {
                    event.setCanceled(true);
                }
            }

            if (Config.feature.qol.qolBlockPlacingItems && item.getDisplayName().contains("Superboom TNT")) {
                event.setCanceled(true);
            }

            if (Config.feature.qol.qolBlockPlacingItems && item.getDisplayName().contains("Infinityboom TNT")) {
                event.setCanceled(true);
            }

            if (Config.feature.qol.qolBlockPlacingItems && item.getItem() == Item.getItemFromBlock(Blocks.hopper) && item.getDisplayName().contains("Weird Tuba")) {
                event.setCanceled(true);
            }
        }
    }

    private BlockPos teleportTarget = null;
    private long teleportStartTime = 0;
    private Long landingTime = null;
    private static final long TELEPORT_GRACE_PERIOD_MS = 5000;
    private static final long SOUND_WINDOW_MS = 1000;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (teleportTarget == null) return;
        try {
            EntityPlayerSP player = mc.thePlayer;
            if (player == null) return;

            double targetX = teleportTarget.getX() + 0.5;
            double targetY = teleportTarget.getY() + 1;
            double targetZ = teleportTarget.getZ() + 0.5;

            if (System.currentTimeMillis() - teleportStartTime > TELEPORT_GRACE_PERIOD_MS) {
                teleportTarget = null;
                landingTime = null;
                return;
            }

            // Distance check
            double distance = player.getDistance(targetX, targetY, targetZ);
            if (distance < 1.0 && landingTime == null) {
                landingTime = System.currentTimeMillis();
            }
        } catch (Exception ignored) {}
    }

    @SubscribeEvent
    public void onPacketRead(PacketReadEvent event) {
        if (event.packet instanceof S0EPacketSpawnObject) {
            S0EPacketSpawnObject spawnPacket = (S0EPacketSpawnObject) event.packet;

            if (spawnPacket.getType() == 70 && Config.feature.qol.qolHideFallingBlocks) {
                event.setCanceled(true);
            }
        }
        if (event.packet instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect soundPacket = (S29PacketSoundEffect) event.packet;

            EntityPlayerSP player = mc.thePlayer;
            if (player == null) return;

            if ("mob.endermen.portal".equals(soundPacket.getSoundName())) {
                if (landingTime != null) {
                    long currentTime = System.currentTimeMillis();
                    long timeDifference = currentTime - landingTime;

                    if (Math.abs(timeDifference) <= SOUND_WINDOW_MS) {
                        event.setCanceled(true);
                        String soundName = getConfiguredSound();
                        SoundUtils.playSound(
                                (int) player.posX,
                                (int) player.posY,
                                (int) player.posZ,
                                soundName,
                                2.0f,
                                1.0f
                        );
                        landingTime = null;
                        teleportTarget = null;
                    }
                }
            }
        } else if (event.packet instanceof S08PacketPlayerPosLook) {
            if (teleportTarget != null && System.currentTimeMillis() - teleportStartTime <= TELEPORT_GRACE_PERIOD_MS) {
                landingTime = System.currentTimeMillis();
                teleportTarget = null;
            }
        }
    }

    private String getConfiguredSound() {
        switch (Config.feature.qol.qolEtherwarpSound) {
            case 1:
                return "mob.blaze.hit";
            case 2:
                return "note.pling";
            case 3:
                return "random.orb";
            case 4:
                return "mob.enderdragon.hit";
            case 5:
                return "mob.cat.meow";
            default:
                return "mob.endermen.portal"; // Fallback to original if not set
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderLivingSpecialsPre(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (Config.feature.qol.qolHideDyingMobs) {
            EntityLivingBase entity = event.entity;
            String name = entity.getDisplayName().getUnformattedText();

            Pattern pattern1 = Pattern.compile("^§.\\[§.Lv\\d+§.\\] §.+ (?:§.)+0§f/.+§c❤$");
            Pattern pattern2 = Pattern.compile("^.+ (?:§.)+0§c❤$");

            if (pattern1.matcher(name).matches() || pattern2.matcher(name).matches()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;

        if (Config.feature.qol.qolHideDyingMobs) {
            EntityLivingBase entity = event.entity;

            if (entity.getHealth() <= 0 || entity.isDead) {
                double playerX = Minecraft.getMinecraft().thePlayer.posX;
                double playerZ = Minecraft.getMinecraft().thePlayer.posZ;

                double teleportY = -64.0;
                entity.setPositionAndUpdate(playerX, teleportY, playerZ);

                event.setCanceled(true);
            }
        }

        if (Config.feature.qol.qolHidePlayerArmor) {
            if (event.entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entity;

                for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                    if (player.inventory.armorInventory[i] != null) {
                        armour.add(player.inventory.armorInventory[i].copy());
                        player.inventory.armorInventory[i] = null;
                    } else {
                        armour.add(null);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLivingSpecials(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;

        if (Config.feature.qol.qolHideDyingMobs) {
            EntityLivingBase entity = event.entity;

            if (entity.getHealth() <= 0 || entity.isDead) {
                double playerX = Minecraft.getMinecraft().thePlayer.posX;
                double playerZ = Minecraft.getMinecraft().thePlayer.posZ;

                double teleportY = -64.0;
                entity.setPositionAndUpdate(playerX, teleportY, playerZ);

                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatReceived(ClientChatReceivedEvent e) {
        if (e.type == 2) return;
        if (Config.feature.misc.qolCopyChatMsg) {

            String unformattedText = StringUtils.stripControlCodes(e.message.getUnformattedText());

            if (!unformattedText.replace(" ", "").isEmpty()) {
                ChatComponentText copyText = new ChatComponentText(EnumChatFormatting.DARK_GRAY + "✍");
                if (Config.feature.misc.copyChatString.equals("[COPY]")){
                    copyText = new ChatComponentText(EnumChatFormatting.AQUA + "" + EnumChatFormatting.BOLD + "[COPY]");
                } else if (Config.feature.misc.copyChatString.equals("Legacy Emoji")) {
                    copyText = new ChatComponentText(EnumChatFormatting.DARK_GRAY + "✍");
                }

                ChatStyle style = new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.AQUA + "Copy message")))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copytoclipboard " + unformattedText));
                copyText.setChatStyle(style);

                e.message.appendSibling(new ChatComponentText(EnumChatFormatting.RESET + " "));
                e.message.appendSibling(copyText);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Config.feature.misc.qolAlwaysSprint) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post<EntityLivingBase> event) {
        if (Config.feature.qol.qolHidePlayerArmor && SkyblockData.getCurrentGamemode().isSkyblock()) {
            if (event.entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entity;

                for (int i = 0; i < player.inventory.armorInventory.length; i++) {
                    player.inventory.armorInventory[i] = armour.get(i);
                }

                armour.clear();
            }
        }
    }

}