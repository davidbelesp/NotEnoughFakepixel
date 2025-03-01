package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.events.PacketReadEvent;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscFeatures {
    ArrayList<Block> flowerPlaceable = new ArrayList<>(Arrays.asList(
            Blocks.grass,
            Blocks.dirt,
            Blocks.flower_pot,
            Blocks.tallgrass,
            Blocks.double_plant
    ));

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        // Existing code for blocking unwanted placements…
        if (!ScoreboardUtils.currentGamemode.isSkyblock() || mc.thePlayer != event.entityPlayer) return;
        ItemStack item = event.entityPlayer.getHeldItem();
        if (item == null) return;

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Block block = mc.theWorld.getBlockState(event.pos).getBlock();

            if (flowerPlaceable.contains(block)) {
                if (Configuration.qolBlockPlacingItems && item.getDisplayName().contains("Flower of Truth")) {
                    event.setCanceled(true);
                }
                if (Configuration.qolBlockPlacingItems && item.getDisplayName().contains("Spirit Sceptre")) {
                    event.setCanceled(true);
                }
            }

            if (Configuration.qolBlockPlacingItems && item.getItem() == Item.getItemFromBlock(Blocks.hopper) && item.getDisplayName().contains("Weird Tuba")) {
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
    public void onRightClick(PlayerInteractEvent event) {
        if (!(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR ||
                event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK))
            return;

        if (mc.thePlayer != event.entityPlayer)
            return;

        EntityPlayer player = event.entityPlayer;
        if (player == null || !player.isSneaking() || player.getHeldItem() == null)
            return;

        String cleanName = EnumChatFormatting.getTextWithoutFormattingCodes(player.getHeldItem().getDisplayName());
        if (!(cleanName.contains("Aspect of the Void") || cleanName.contains("Aspect of the End")))
            return;

        EtherWarpData data = getEtherWarpData(player);
        if (!data.hasEther || data.range <= 0)
            return;

        Vec3 startVec = player.getPositionEyes(1.0F);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 endVec = startVec.addVector(
                lookVec.xCoord * data.range,
                lookVec.yCoord * data.range,
                lookVec.zCoord * data.range
        );

        List<Block> excludedBlocks = Arrays.asList(
                Blocks.torch, Blocks.flowing_water, Blocks.standing_sign,
                Blocks.wall_sign, Blocks.snow_layer, Blocks.double_plant,
                Blocks.redstone_torch, Blocks.wooden_button, Blocks.stone_button,
                Blocks.carpet, Blocks.tallgrass, Blocks.red_flower, Blocks.yellow_flower
        );

        BlockPos target = raycastBlocks(player.worldObj, startVec, endVec, excludedBlocks);
        if (target == null)
            return;

        boolean isAirAbove1 = player.worldObj.isAirBlock(target.up());
        boolean isAirAbove2 = player.worldObj.isAirBlock(target.up(2));
        if (!isAirAbove1 || !isAirAbove2)
            return;

        // Store the teleport destination
        teleportTarget = target;
        teleportStartTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (teleportTarget == null) return;

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
    }

    @SubscribeEvent
    public void onPacketRead(PacketReadEvent event) {
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
                        mc.theWorld.playSound(
                                soundPacket.getX(), soundPacket.getY(), soundPacket.getZ(),
                                soundName, soundPacket.getVolume(), soundPacket.getPitch(), false
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
        switch (Configuration.qolEtherwarpSound) {
            case 1: return "mob.blaze.hit";
            case 2: return "note.pling";
            case 3: return "random.orb";
            case 4: return "mob.enderdragon.hit";
            case 5: return "mob.cat.meow";
            default: return "mob.endermen.portal"; // Fallback to original if not set
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderLivingSpecialsPre(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (Configuration.qolHideDyingMobs) {
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
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (Configuration.qolHideDyingMobs) {
            EntityLivingBase entity = event.entity;
            if (entity.getHealth() <= 0 || entity.isDead) {
                event.setCanceled(true);
            }
        }
    }

    private static class EtherWarpData {
        public boolean hasEther;
        public int range;

        public EtherWarpData(boolean hasEther, int range) {
            this.hasEther = hasEther;
            this.range = range;
        }
    }

    /**
     * Renders the bounding box around a valid Ether Warp target block if:
     *  1. Player is sneaking
     *  2. Player is holding an item with "Ether" in its lore
     */
    private BlockPos raycastBlocks(World world, Vec3 start, Vec3 end, List<Block> excludedBlocks) {
        Vec3 direction = end.subtract(start).normalize();
        double maxDistance = start.distanceTo(end);

        for (double d = 0.0; d <= maxDistance; d += 0.05) {
            Vec3 currentVec = start.addVector(
                    direction.xCoord * d,
                    direction.yCoord * d,
                    direction.zCoord * d
            );
            BlockPos pos = new BlockPos(MathHelper.floor_double(currentVec.xCoord),
                    MathHelper.floor_double(currentVec.yCoord),
                    MathHelper.floor_double(currentVec.zCoord));

            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            // Skip air and excluded blocks
            if (block != Blocks.air && !excludedBlocks.contains(block)) {
                return pos;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.thePlayer == null) return;
        EntityPlayerSP player = mc.thePlayer;

        if (!player.isSneaking()) return;

        EtherWarpData data = getEtherWarpData(player);
        if (!data.hasEther || data.range <= 0) return;

        Vec3 startVec = player.getPositionEyes(1.0F);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 endVec = startVec.addVector(
                lookVec.xCoord * data.range,
                lookVec.yCoord * data.range,
                lookVec.zCoord * data.range
        );

        List<Block> excludedBlocks = Arrays.asList(
                Blocks.torch, Blocks.flowing_water, Blocks.standing_sign,
                Blocks.wall_sign, Blocks.snow_layer, Blocks.double_plant,
                Blocks.redstone_torch, Blocks.wooden_button, Blocks.stone_button,
                Blocks.carpet, Blocks.tallgrass, Blocks.red_flower, Blocks.yellow_flower
        );

        BlockPos target = raycastBlocks(mc.theWorld, startVec, endVec, excludedBlocks);
        if (target == null) {
            return;
        }

        // Check for two air blocks above the target
        boolean isAirAbove1 = mc.theWorld.isAirBlock(target.up());
        boolean isAirAbove2 = mc.theWorld.isAirBlock(target.up(2));

        if (isAirAbove1 && isAirAbove2) {
            if (Configuration.qolEtherwarpOverlay) {
                renderFilledBoundingBox(target, new Color(Configuration.qolEtherwarpOverlayColor.getRGB(), true));
            }
        }
    }

    private EtherWarpData getEtherWarpData(EntityPlayer player) {
        ItemStack stack = player.getHeldItem();
        if (stack == null) {
            return new EtherWarpData(false, 0);
        }

        String displayName = stack.getDisplayName();
        String cleanName = EnumChatFormatting.getTextWithoutFormattingCodes(displayName);
        boolean isCorrectItem = cleanName.contains("Aspect of the Void") || cleanName.contains("Aspect of the End");
        if (!isCorrectItem) {
            return new EtherWarpData(false, 0);
        }

        // Check the lore for "Ether" and parse the range
        List<String> loreLines = stack.getTooltip(player, false);
        boolean hasEther = false;
        int range = 0;
        Pattern rangePattern = Pattern.compile(".*?(\\d+) blocks away.*");

        for (String line : loreLines) {
            String cleanLine = EnumChatFormatting.getTextWithoutFormattingCodes(line);

            if (cleanLine.contains("Ether")) {
                hasEther = true;
            }

            Matcher matcher = rangePattern.matcher(cleanLine);
            if (matcher.matches()) {
                try {
                    range = Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException ignored) {
                    // Ignore invalid number formats
                }
            }
        }

        return new EtherWarpData(hasEther, range);
    }

    private void renderFilledBoundingBox(BlockPos pos, Color color) {
        double x = pos.getX() - mc.getRenderManager().viewerPosX;
        double y = pos.getY() - mc.getRenderManager().viewerPosY;
        double z = pos.getZ() - mc.getRenderManager().viewerPosZ;

        AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1).expand(0.002, 0.002, 0.002);
        drawFilledBoundingBox(box, color);
    }

    private void drawFilledBoundingBox(AxisAlignedBB box, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableCull(); // Render all faces

        float red = color.getRed() / 255.0F;
        float green = color.getGreen() / 255.0F;
        float blue = color.getBlue() / 255.0F;
        float alpha = color.getAlpha() / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Bottom Face
        worldrenderer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();

        // Top Face
        worldrenderer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();

        // North Face (Z-)
        worldrenderer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();

        // South Face (Z+)
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();

        // East Face (X+)
        worldrenderer.pos(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();

        // West Face (X-)
        worldrenderer.pos(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).endVertex();

        tessellator.draw();

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
