package org.ginafro.notenoughfakepixel.features.skyblock.chocolate;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.NotEnoughFakepixel;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.utils.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class ChocolateFactory {

    private final Pattern upgradeCostPattern = Pattern.compile("(§.)(?<cost>[0-9,]+) Chocolate");
    @Getter
    @Setter
    private Pattern pattern = Pattern.compile("Id:\"([^\"]+)\"");
    private final ArrayList<String> eggIDS = new ArrayList<>();
    private final String eggLime = "e3da4593-afbb-38df-bf1e-b57e27a2e0e1";
    private final String eggBlue = "15785089-b2b0-38ac-b379-8af3d6253c62";
    private final String eggCake = "9e39f2f4-8038-3aac-97fd-d7420cdf4601";
    private final ArrayList <Waypoint> waypoints = new ArrayList<>();

    public ChocolateFactory() {
        eggIDS.add(eggLime);
        eggIDS.add(eggBlue);
        eggIDS.add(eggCake);
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event) {
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (!NotEnoughFakepixel.feature.chocolateFactory.chocolateChocolateEggWaypoints) return;
        checkForEggs();
        drawWaypoints(event.partialTicks);
        drawTags(event.partialTicks);
    }

    @SubscribeEvent()
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!NotEnoughFakepixel.feature.chocolateFactory.chocolateChocolateShowBestUpgrade || !(event.gui instanceof GuiChest)) return;

        TreeMap<Float, Slot> upgradeCosts = new TreeMap<>();
        GuiChest chest = (GuiChest) event.gui;
        Container container = chest.inventorySlots;

        String chestName = TablistParser.currentOpenChestName;
        if (chestName == null || !chestName.startsWith("Chocolate Factory")) return;

        int index = 0;
        ContainerChest containerChest = (ContainerChest) container;
        for (Slot slot : containerChest.inventorySlots) {
            if (slot.getSlotIndex() < 28 || slot.getSlotIndex() > 34) continue;
            index++;
            ItemStack item = slot.getStack();
            if (item != null && item.getItem() instanceof ItemSkull) {
                String upgradeCost = ItemUtils.getLoreLine(item, upgradeCostPattern);
                if (upgradeCost == null) continue;
                upgradeCost = StringUtils.cleanColor(upgradeCost).replaceAll(",", "").replaceAll(" Chocolate", "");

                float costRatio = Float.parseFloat(upgradeCost) / index;
                upgradeCosts.put(costRatio, slot);
            }
        }
        if (upgradeCosts.isEmpty()) return;
        float lowestValue = upgradeCosts.firstKey();
        Slot associatedSlot = upgradeCosts.get(lowestValue);

        RenderUtils.drawOnSlot(containerChest.inventorySlots.size(), associatedSlot.xDisplayPosition, associatedSlot.yDisplayPosition, new Color(0, 255, 0, 100).getRGB());
    }

    @SubscribeEvent
    public void onChat(@NotNull ClientChatReceivedEvent e){
        if (!NotEnoughFakepixel.feature.chocolateFactory.chocolateChocolateEggWaypoints) return;
        if (!ScoreboardUtils.currentGamemode.isSkyblock()) return;
        if (ChatUtils.middleBar.matcher(e.message.getFormattedText()).matches()) return;
        Matcher matcher = Pattern.compile("HOPPITY'S HUNT You found").matcher(e.message.getUnformattedText());
        Matcher matcher2 = Pattern.compile("HOPPITY'S HUNT A Chocolate .* Egg has appeared").matcher(e.message.getUnformattedText());
        Matcher matcher3 = Pattern.compile("You have already collected this Chocolate .* Egg! Try again when it respawns!").matcher(e.message.getUnformattedText());
        int[] playerCoords = new int[] {Minecraft.getMinecraft().thePlayer.getPosition().getX(), Minecraft.getMinecraft().thePlayer.getPosition().getY(), Minecraft.getMinecraft().thePlayer.getPosition().getZ()};
        if (matcher.find()) {
            Waypoint w = Waypoint.getClosestWaypoint(waypoints, playerCoords);
            if (w == null) return;
            w.setHidden(true);
        }
        if (matcher2.find()) {
            ArrayList<Waypoint> waypointsToRemove = new ArrayList<>();
            for (Waypoint w : waypoints) {
                if (w.isHidden() && Waypoint.distance(playerCoords,w.getCoordinates()) > 64) waypointsToRemove.add(w);
            }
            waypoints.removeAll(waypointsToRemove);
        }
        if (matcher3.find()) {
            Waypoint w = Waypoint.getClosestWaypoint(waypoints, playerCoords);
            if (w != null && Waypoint.distance(playerCoords,w.getCoordinates()) < 6) w.setHidden(true);
        }
    }

    @SubscribeEvent()
    public void onWorldUnload(WorldEvent.Unload event) {
        if (NotEnoughFakepixel.feature.chocolateFactory.chocolateChocolateEggWaypoints) waypoints.clear();
    }

    private void checkForEggs() {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        for (int i = 0; i < world.loadedEntityList.size(); i++) {
            Entity entity = world.loadedEntityList.get(i);
            if (entity == null) continue;
            if (entity.getName() == null) continue;
            if (entity instanceof EntityArmorStand) {
                ItemStack it = ((EntityArmorStand) entity).getEquipmentInSlot(4);
                if (it != null && it.getItem() == Items.skull) {
                    NBTTagCompound nbt = it.getTagCompound();
                    if(nbt != null && nbt.hasKey("SkullOwner") && nbt.getCompoundTag("SkullOwner").hasKey("Id")) {
                        String id = nbt.getCompoundTag("SkullOwner").getString("Id");
                        if (isEgg(id)) {
                            int[] entityCoords = new int[]{entity.getPosition().getX(), entity.getPosition().getY(), entity.getPosition().getZ()};
                            Waypoint waypoint = new Waypoint("EGG", entityCoords);
                            if (checkIfAdded(waypoint)) continue;
                            waypoints.add(waypoint);
                            SoundUtils.playSound(entityCoords,"random.pop", 4.0f, 2.5f);
                        }
                    }
                }
            }
        }
    }

    private void drawWaypoints(float partialTicks) {
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        for (Waypoint waypoint : waypoints) {
            if (waypoint == null || waypoint.isHidden()) continue;
            Color colorDrawWaypoint = ColorUtils.getColor(NotEnoughFakepixel.feature.chocolateFactory.chocolateChocolateEggWaypointsColor);
            colorDrawWaypoint = new Color(colorDrawWaypoint.getRed(), colorDrawWaypoint.getGreen(), colorDrawWaypoint.getBlue(), 150);
            AxisAlignedBB bb = new AxisAlignedBB(
                    waypoint.getCoordinates()[0] - viewerX,
                    waypoint.getCoordinates()[1] - viewerY + 1,
                    waypoint.getCoordinates()[2] - viewerZ,
                    waypoint.getCoordinates()[0] + 1 - viewerX,
                    waypoint.getCoordinates()[1] + 1 - viewerY + 150,
                    waypoint.getCoordinates()[2] + 1 - viewerZ
            ).expand(0.01f, 0.01f, 0.01f);
            GlStateManager.disableCull();
            RenderUtils.drawFilledBoundingBox(bb, 1f, colorDrawWaypoint);
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();
        }
    }

    private void drawTags(float partialTicks) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint == null || waypoint.isHidden()) continue;
            GlStateManager.disableCull();
            RenderUtils.drawTag("Egg",new double[]{waypoint.getCoordinates()[0],waypoint.getCoordinates()[1],waypoint.getCoordinates()[2]},Color.WHITE, partialTicks);
            GlStateManager.enableCull();
            GlStateManager.enableTexture2D();
        }
    }

    private boolean checkIfAdded(Waypoint waypoint) {
        return waypoints.stream().anyMatch(w -> Arrays.equals(w.getCoordinates(), waypoint.getCoordinates()));
    }

    private boolean isEgg(String id) {
        for (String egg : eggIDS) {
            if (egg.equals(id)) {
                return true;
            }
        }
        return false;
    }
}
