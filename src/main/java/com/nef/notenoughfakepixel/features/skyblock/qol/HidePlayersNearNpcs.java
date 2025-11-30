package com.nef.notenoughfakepixel.features.skyblock.qol;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterEvents;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.StringUtils;
import com.nef.notenoughfakepixel.variables.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.UUID;

@RegisterEvents
public class HidePlayersNearNpcs {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final double HIDE_RADIUS_IN  = 3.0D;
    private static final double HIDE_RADIUS_OUT = 4.0D;
    private static final double HIDE_RADIUS_IN_SQ  = HIDE_RADIUS_IN  * HIDE_RADIUS_IN;
    private static final double HIDE_RADIUS_OUT_SQ = HIDE_RADIUS_OUT * HIDE_RADIUS_OUT;
    private static final int    NPC_KEEP_ALIVE_TICKS = 60;
    private static final int    HIDE_STABILITY_TICKS = 5;

    private static final Map<UUID, TrackedNpc> npcMap = new java.util.HashMap<>(128);

    private static final Map<UUID, HideState> hideStateByPlayer = new java.util.HashMap<>(256);

    private static int lastComputedTick = -1;
    private static final java.util.HashMap<UUID, Boolean> hideDecisionCache = new java.util.HashMap<>(256);

    public static boolean isNpc(Entity entity) {
        if (!(entity instanceof EntityOtherPlayerMP)) return false;
        final EntityLivingBase base = (EntityLivingBase) entity;

        return entity.getUniqueID().version() == 4
                && StringUtils.startsWithFast(base.getName(), "§e§l");
    }

    public static void trackNpc(EntityOtherPlayerMP npc) {
        final UUID id = npc.getUniqueID();
        final TrackedNpc t = npcMap.get(id);
        final int tick = mc.theWorld.getTotalWorldTime() != 0 ? (int) mc.theWorld.getTotalWorldTime() : (int) (System.nanoTime() & 0x7FFFFFFF);

        if (t == null) {
            npcMap.put(id, new TrackedNpc(npc.posX, npc.posY, npc.posZ, tick));
        } else {
            t.x = npc.posX;
            t.y = npc.posY;
            t.z = npc.posZ;
            t.lastSeenTick = tick;
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(net.minecraftforge.client.event.RenderPlayerEvent.Pre e) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (!SkyblockData.getCurrentGamemode().isSkyblock()) return;
        if (!Config.feature.qol.qolHidePlayerNearNpcs) return;
        if (SkyblockData.getCurrentLocation() == Location.PRIVATE_ISLAND || SkyblockData.getCurrentLocation() == Location.DUNGEON) return;

        final Entity entity = e.entity;
        if (!(entity instanceof EntityOtherPlayerMP)) return;
        if (isNpc(entity)) return;

        final EntityOtherPlayerMP other = (EntityOtherPlayerMP) entity;

        final int worldTick = (int) mc.theWorld.getTotalWorldTime();
        if (worldTick != lastComputedTick) {
            cleanupStaleNpcs(worldTick);
            hideDecisionCache.clear();
            lastComputedTick = worldTick;
        }

        final UUID id = other.getUniqueID();
        Boolean cached = hideDecisionCache.get(id);
        if (cached == null) {
            cached = shouldHideWithHysteresis(other, worldTick);
            hideDecisionCache.put(id, cached);
        }

        if (cached.booleanValue()) {
            e.setCanceled(true);
        }
    }

    public static boolean isCurrentlyHidden(UUID id) {
        final Boolean b = hideDecisionCache.get(id);
        return b != null && b.booleanValue();
    }

    public static boolean checkForMixin(Entity entity) {
        return entity instanceof EntityOtherPlayerMP
                && !HidePlayersNearNpcs.isNpc(entity)
                && HidePlayersNearNpcs.isCurrentlyHidden(entity.getUniqueID());
    }

    // ---------- Distance + hysteresis + stability logic ----------
    private static boolean shouldHideWithHysteresis(EntityOtherPlayerMP player, int worldTick) {
        final UUID id = player.getUniqueID();
        HideState state = hideStateByPlayer.computeIfAbsent(id, k -> new HideState(false, worldTick));

        final double minDistSq = minDistanceSqToAnyNpc(player.posX, player.posY, player.posZ);

        boolean targetHidden = state.hidden;
        if (state.hidden) {
            if (minDistSq > HIDE_RADIUS_OUT_SQ && worldTick - state.lastFlipTick >= HIDE_STABILITY_TICKS) {
                targetHidden = false;
            }
        } else {
            if (minDistSq <= HIDE_RADIUS_IN_SQ && worldTick - state.lastFlipTick >= HIDE_STABILITY_TICKS) {
                targetHidden = true;
            }
        }

        if (targetHidden != state.hidden) {
            state.hidden = targetHidden;
            state.lastFlipTick = worldTick;
        }
        return state.hidden;
    }

    private static double minDistanceSqToAnyNpc(double x, double y, double z) {
        if (npcMap.isEmpty()) return Double.POSITIVE_INFINITY;
        double best = Double.POSITIVE_INFINITY;
        for (TrackedNpc t : npcMap.values()) {
            final double dx = x - t.x;
            final double dy = y - t.y;
            final double dz = z - t.z;
            final double d2 = dx * dx + dy * dy + dz * dz;
            if (d2 < best) best = d2;
            if (best <= HIDE_RADIUS_IN_SQ) return best;
        }
        return best;
    }

    // ---------- Cleaning ----------
    private static void cleanupStaleNpcs(int worldTick) {
        if (npcMap.isEmpty()) return;
        final int cutoff = worldTick - NPC_KEEP_ALIVE_TICKS;
        final java.util.Iterator<Map.Entry<UUID, TrackedNpc>> it = npcMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, TrackedNpc> e = it.next();
            TrackedNpc t = e.getValue();
            if (t.lastSeenTick < cutoff) {
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public void onWorldJoin(net.minecraftforge.event.entity.EntityJoinWorldEvent e) {
        if (e.world.isRemote && e.entity == mc.thePlayer) {
            npcMap.clear();
            hideStateByPlayer.clear();
            hideDecisionCache.clear();
            lastComputedTick = -1;
        }
    }

    @SubscribeEvent
    public void onWorldUnload(net.minecraftforge.event.world.WorldEvent.Unload e) {
        if (e.world.isRemote) {
            npcMap.clear();
            hideStateByPlayer.clear();
            hideDecisionCache.clear();
            lastComputedTick = -1;
        }
    }

    @SubscribeEvent
    public void onDeath(net.minecraftforge.event.entity.living.LivingDeathEvent e) {
        if (!(e.entity instanceof EntityOtherPlayerMP)) return;
        final UUID id = e.entity.getUniqueID();
        npcMap.remove(id);
        hideStateByPlayer.remove(id);
        hideDecisionCache.remove(id);
    }

    // ---------- Aux ----------
    private static final class TrackedNpc {
        double x, y, z;
        int lastSeenTick;
        TrackedNpc(double x, double y, double z, int tick) {
            this.x = x; this.y = y; this.z = z; this.lastSeenTick = tick;
        }
    }
    private static final class HideState {
        boolean hidden;
        int lastFlipTick;
        HideState(boolean hidden, int lastFlipTick) {
            this.hidden = hidden;
            this.lastFlipTick = lastFlipTick;
        }
    }


}
