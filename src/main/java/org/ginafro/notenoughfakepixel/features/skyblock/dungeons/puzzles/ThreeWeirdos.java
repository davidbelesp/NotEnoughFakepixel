package org.ginafro.notenoughfakepixel.features.skyblock.dungeons.puzzles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.Configuration;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.variables.MobDisplayTypes;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreeWeirdos {

    private static String[] answers = {
            "The reward is not in my chest!",
            "At least one of them is lying, and the reward is not in ",
            "My chest doesn't have the reward we are all telling the truth.",
            "My chest has the reward and I'm telling the truth!",
            "The reward isn't in any of our chests.",
            "Both of them are telling the truth. Also, "
    };

    private static Pattern threeWeirdosPattern = Pattern.compile("(§r§e\\[NPC] §r§c)(.+)(§r§f:)(.+)");
    private static Pattern threeWeirdosSolved = Pattern.compile("§r§a§lPUZZLE SOLVED! (?<player>.+) §r§ewasn't fooled by three weirdos!(.+)");

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean foundResponse = false;
    private String correctName = "";
    private BlockPos riddleChest = null;

    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent e) {
        if (!Configuration.dungeonsThreeWeirdos) return;
        if (mc.thePlayer == null) return;
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!ScoreboardUtils.currentLocation.isDungeon()) return;

        if (e.message.getFormattedText().startsWith("§c")) return;

        Matcher matcher = threeWeirdosPattern.matcher(e.message.getFormattedText());
        if (matcher.find()) {
            String name = matcher.group(2);
            String message = matcher.group(4);
            for (String answer : answers) {
                if (message.contains(answer)) {
                    mc.thePlayer.addChatMessage(new ChatComponentText("§a[Riddle Solver] §r§c" + name + "§r§f has the blessing!"));
                    e.setCanceled(true);
                    foundResponse = true;
                    correctName = name;
                    findRiddleChest(name);
                    return;
                }
            }
        } else if (threeWeirdosSolved.matcher(e.message.getFormattedText()).matches()) {
            foundResponse = false;
            correctName = "";
        }
    }

    private void findRiddleChest(String npcName) {
        if (riddleChest == null) {
            List<Entity> entities = mc.theWorld.loadedEntityList;
            for (Entity entity : entities) {
                if (entity == null || !entity.hasCustomName()) continue;
                if (entity.getCustomNameTag().contains(npcName)) {
                    BlockPos npcLocation = new BlockPos(entity.posX, 69, entity.posZ);
                    riddleChest = checkAdjacentForChest(npcLocation);
                    if (riddleChest == null) {
                        System.out.print("Could not find correct riddle chest.");
                    }
                    break;
                }
            }
        }
    }

    private BlockPos checkAdjacentForChest(BlockPos npcLocation) {
        if (mc.theWorld.getBlockState(npcLocation.north()).getBlock() == Blocks.chest) {
            return npcLocation.north();
        } else if (mc.theWorld.getBlockState(npcLocation.east()).getBlock() == Blocks.chest) {
            return npcLocation.east();
        } else if (mc.theWorld.getBlockState(npcLocation.south()).getBlock() == Blocks.chest) {
            return npcLocation.south();
        } else if (mc.theWorld.getBlockState(npcLocation.west()).getBlock() == Blocks.chest) {
            return npcLocation.west();
        }
        return null;
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!Configuration.dungeonsThreeWeirdos) return;
        if (mc.theWorld == null) return;
        if (!foundResponse) return;
        if (correctName.isEmpty()) return;
        highlightNpc(correctName, event.partialTicks);
        if (riddleChest != null) {
            drawFilled3DBox(new AxisAlignedBB(riddleChest.getX() - 0.05, riddleChest.getY(), riddleChest.getZ() - 0.05,
                            riddleChest.getX() + 1.05, riddleChest.getY() + 1, riddleChest.getZ() + 1.05),
                    0x00FF00, false, true, event.partialTicks);
        }
    }

    public void highlightNpc(String correctName, float partialTicks) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity.getDisplayName().getUnformattedText().contains(correctName)) {
                RenderUtils.renderEntityHitbox(entity, partialTicks, new Color(90, 255, 90, 198), MobDisplayTypes.NONE);
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        correctName = "";
        foundResponse = false;
        riddleChest = null;
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        riddleChest = null;
    }

    public static void drawFilled3DBox(AxisAlignedBB aabb, int colourInt, boolean translucent, boolean depth, float partialTicks) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        Color colour = new Color(colourInt);

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableCull();

        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }

        // Prevent Z-fighting
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(1.0f, 1.0f);

        GlStateManager.color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, colour.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        // Bottom
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        // Top
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        // West
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        // East
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        // North
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
        // South
        worldRenderer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
        worldRenderer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();
        Tessellator.getInstance().draw();

        // Restore state
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);

        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }
}
