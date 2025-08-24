package org.ginafro.notenoughfakepixel.features.skyblock.qol;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.RenderUtils;
import org.ginafro.notenoughfakepixel.utils.ScoreboardUtils;
import org.ginafro.notenoughfakepixel.utils.TablistParser;
import org.ginafro.notenoughfakepixel.variables.Location;

import java.awt.*;
import java.util.*;
import java.util.List;

@RegisterEvents
public class RelicWaypoints {
    private static final List<BlockPos> RELIC_LOCATIONS = Arrays.asList(
            new BlockPos(-217, 58, -304),
            new BlockPos(-206, 63, -301),
            new BlockPos(-384, 89, -225),
            new BlockPos(-178, 136, -297),
            new BlockPos(-188, 80, -345),
            new BlockPos(-147, 83, -334),
            new BlockPos(-303, 71, -317),
            new BlockPos(-300, 51, -254),
            new BlockPos(-275, 64, -272),
            new BlockPos(-272, 48, -291),
            new BlockPos(-348, 65, -202),
            new BlockPos(-284, 49, -234),
            new BlockPos(-328, 50, -238),
            new BlockPos(-274, 100, -178),
            new BlockPos(-311, 69, -251),
            new BlockPos(-354, 73, -284),
            new BlockPos(-236, 51, -239),
            new BlockPos(-296, 37, -269),
            new BlockPos(-317, 69, -272),
            new BlockPos(-183, 51, -252),
            new BlockPos(-254, 57, -279),
            new BlockPos(-342, 122, -253),
            new BlockPos(-300, 50, -218),
            new BlockPos(-313, 58, -250),
            new BlockPos(-372, 89, -242),
            new BlockPos(-225, 70, -316),
            new BlockPos(-342, 89, -221),
            new BlockPos(-355, 86, -213)
    );

    private final Set<BlockPos> hiddenRelics = Collections.synchronizedSet(new HashSet<>());

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!Config.feature.qol.qolRelicWaypoints || TablistParser.currentLocation != Location.SPIDERS_DEN) {
            return;
        }

        for (BlockPos pos : RELIC_LOCATIONS) {
            if (hiddenRelics.contains(pos)) {
                continue;
            }

            RenderUtils.highlightBlock(pos, Color.WHITE, true, event.partialTicks);

            GlStateManager.disableCull();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            RenderUtils.renderBeaconBeam(pos, Color.WHITE.getRGB(), 1.0f, event.partialTicks);

            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (!Config.feature.qol.qolRelicWaypoints || TablistParser.currentLocation != Location.SPIDERS_DEN) {
            return;
        }

        String message = event.message.getUnformattedText();
        if (message.endsWith("/28 Relics)") || message.contains("You've already found this relic!")) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer == null) {
                return;
            }

            BlockPos playerPos = mc.thePlayer.getPosition();
            BlockPos nearestRelic = null;
            double minDistanceSq = Double.MAX_VALUE;

            for (BlockPos relicPos : RELIC_LOCATIONS) {
                if (hiddenRelics.contains(relicPos)) {
                    continue;
                }

                double distanceSq = relicPos.distanceSq(playerPos);
                if (distanceSq < minDistanceSq) {
                    minDistanceSq = distanceSq;
                    nearestRelic = relicPos;
                }
            }

            // hide the nearest relic
            if (nearestRelic != null) {
                hiddenRelics.add(nearestRelic);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(ClientDisconnectionFromServerEvent event) {
        hiddenRelics.clear();
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        hiddenRelics.clear();
    }
}