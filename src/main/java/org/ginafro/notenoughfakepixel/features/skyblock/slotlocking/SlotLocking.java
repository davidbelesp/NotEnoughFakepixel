package org.ginafro.notenoughfakepixel.features.skyblock.slotlocking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.config.gui.core.config.KeybindHelper;
import org.ginafro.notenoughfakepixel.config.gui.core.util.render.RenderUtils;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;
import org.ginafro.notenoughfakepixel.events.ReplaceItemEvent;
import org.ginafro.notenoughfakepixel.events.SlotClickEvent;
import org.ginafro.notenoughfakepixel.mixin.accesors.AccessorGuiContainer;
import org.ginafro.notenoughfakepixel.utils.*;
import org.ginafro.notenoughfakepixel.variables.Gamemode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RegisterEvents
public class SlotLocking {
    private static final SlotLocking INSTANCE = new SlotLocking();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final LockedSlot DEFAULT_LOCKED_SLOT = new LockedSlot();

    private final ResourceLocation LOCK = new ResourceLocation("notenoughfakepixel:skyblock/lock.png");
    private final ResourceLocation BOUND = new ResourceLocation("notenoughfakepixel:skyblock/bound.png");
    public String currentlyOpenChestName = "";
    public String lastOpenChestName = "";

    public static SlotLocking getInstance() {
        return INSTANCE;
    }

    public static class LockedSlot {
        public boolean locked = false;
        public int boundTo = -1;
    }

    public static class SlotLockData {
        public LockedSlot[] lockedSlots = new LockedSlot[40];
        public LockedSlot[] riftLockedSlots = new LockedSlot[40];
    }

    public static class SlotLockProfile {
        public SlotLockData[] slotLockData = new SlotLockData[1];
    }

    public static class SlotLockingConfig {
        public HashMap<String, SlotLockProfile> profileData = new HashMap<>();
    }

    private SlotLockingConfig config = new SlotLockingConfig();
    private boolean lockKeyHeld = false;
    private Slot pairingSlot = null;

    @Setter
    @Getter
    private Slot realSlot = null;

    public void loadConfig() {
        config = CustomConfigHandler.loadConfig();
        if (config == null) {
            config = new SlotLockingConfig();
        }
    }

    public void changedSlot(int slotNumber) {
        int pingModifier = Config.feature.sl.slotLockSwapDelay;
        if (pingModifier == 0) {
            return;
        }
        if (!isSlotIndexLocked(slotNumber)) {
            return;
        }
        long currentTimeMilis = System.currentTimeMillis();

        for (int i = 0; i < slotChanges.length; i++) {
            if (i != slotNumber && slotChanges[i] != 0 && (slotChanges[i] + (long) pingModifier) > currentTimeMilis) {
                slotChanges[i] = 0;
            }
        }
        slotChanges[slotNumber] = currentTimeMilis;
        saveConfig();
    }

    public boolean isSwapedSlotLocked() {
        int pingModifier = Config.feature.sl.slotLockSwapDelay;
        if (pingModifier == 0) {
            return false;
        }
        long currentTimeMilis = System.currentTimeMillis();

        for (int i = 0; i < slotChanges.length; i++) {
            if (slotChanges[i] != 0 && isSlotIndexLocked(i) && (slotChanges[i] + (long) pingModifier) > currentTimeMilis) {
                return true;
            }
        }
        return false;
    }

    private final long[] slotChanges = new long[9];

    public void saveConfig() {
        CustomConfigHandler.saveConfig(config);
        loadConfig();
    }

    @SubscribeEvent
    public void onOpen(GuiOpenEvent e) {
        if (e.gui instanceof GuiChest) {
            GuiChest chest = (GuiChest) e.gui;
            ContainerChest container = (ContainerChest) chest.inventorySlots;

            currentlyOpenChestName = container.getLowerChestInventory().getDisplayName().getUnformattedText();
            lastOpenChestName = currentlyOpenChestName;
        } else {
            currentlyOpenChestName = "";
        }
    }

    private static final Pattern WINDOW_REGEX = Pattern.compile(".+ Backpack (?:✦ )?\\(Slot #(\\d+)\\)");
    private static final Pattern ECHEST_WINDOW_REGEX = Pattern.compile("Ender Chest \\((\\d+)/(\\d+)\\)");

    private LockedSlot[] getDataForProfile() {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK ||
                !Config.feature.sl.enableSlotLocking)
            return null;

        if (Config.feature.sl.disableInStorage) {
            if (currentlyOpenChestName.trim().equals("Storage")) return null;

            currentlyOpenChestName = StringUtils.stripControlCodes(currentlyOpenChestName);

            Matcher matcher = WINDOW_REGEX.matcher(currentlyOpenChestName);
            Matcher matcherEchest = ECHEST_WINDOW_REGEX.matcher(currentlyOpenChestName);

            if (matcher.matches() || matcherEchest.matches()) {
                return null;
            }
        }

        String profileName = "generic";
        loadConfig();
        SlotLockProfile profile = config.profileData.computeIfAbsent(
                profileName,
                k -> new SlotLockProfile()
        );

        if (profile.slotLockData[0] == null) {
            profile.slotLockData[0] = new SlotLockData();
        }

        return profile.slotLockData[0].lockedSlots;
    }

    private LockedSlot getLockedSlot(LockedSlot[] lockedSlots, int index) {
        if (lockedSlots == null) {
            return DEFAULT_LOCKED_SLOT;
        }

        LockedSlot slot = lockedSlots[index];

        if (slot == null) {
            return DEFAULT_LOCKED_SLOT;
        }

        return slot;
    }

    private Slot getFocusedSlot(GuiContainer container) {
        final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        final int scaledWidth = scaledresolution.getScaledWidth();
        final int scaledHeight = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * scaledWidth / Minecraft.getMinecraft().displayWidth;
        int mouseY = scaledHeight - Mouse.getY() * scaledHeight / Minecraft.getMinecraft().displayHeight - 1;
        try {
            return ((AccessorGuiContainer) container).doGetSlotAtPosition(mouseX, mouseY);
        } catch (Exception ignored) {
            Logger.log("Error loading focused slot in SlotLocking.java:getFocusedSlot:216");
            return null;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void keyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK ||
                !Config.feature.sl.enableSlotLocking) {
            return;
        }
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) {
            return;
        }
        GuiContainer container = (GuiContainer) Minecraft.getMinecraft().currentScreen;

        int key = Config.feature.sl.slotLockKey;
        if (!lockKeyHeld && KeybindHelper.isKeyPressed(key) && !Keyboard.isRepeatEvent()) {
            Slot slot = getFocusedSlot(container);
            if (slot != null && slot.getSlotIndex() != 8 && slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) {
                int slotNum = slot.getSlotIndex();
                if (slotNum >= 0 && slotNum <= 39) {
                    boolean isHotbar = slotNum < 9;
                    boolean isInventory = !isHotbar && slotNum < 36;
                    boolean isArmor = !isHotbar && !isInventory;

                    if (isInventory || isArmor) {
                        pairingSlot = slot;
                    } else {
                        pairingSlot = null;
                    }

                    LockedSlot[] lockedSlots = getDataForProfile();

                    if (lockedSlots != null) {
                        if (lockedSlots[slotNum] == null) {
                            lockedSlots[slotNum] = new LockedSlot();
                        }
                        lockedSlots[slotNum].locked = !lockedSlots[slotNum].locked;
                        lockedSlots[slotNum].boundTo = -1;

                        if (Config.feature.sl.slotLockSound) {
                            float vol = Config.feature.sl.slotLockSoundVol / 100f;
                            if (vol > 0) {
                                if (vol > 1) vol = 1;
                                final float volF = vol;
                                final boolean locked = lockedSlots[slotNum].locked;

                                ISound sound = new PositionedSound(new ResourceLocation("random.orb")) {{
                                    volume = volF;
                                    pitch = locked ? 0.943f : 0.1f;
                                    repeat = false;
                                    repeatDelay = 0;
                                    attenuationType = ISound.AttenuationType.NONE;
                                }};

                                float oldLevel = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS);
                                Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.PLAYERS, 1);
                                Minecraft.getMinecraft().getSoundHandler().playSound(sound);
                                Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.PLAYERS, oldLevel);
                            }
                        }

                        if (isHotbar && lockedSlots[slotNum].locked) {
                            for (int i = 9; i <= 39; i++) {
                                if (lockedSlots[i] != null && lockedSlots[i].boundTo == slotNum) {
                                    lockedSlots[i].boundTo = -1;
                                }
                            }
                        }
                    }
                }
            }
            saveConfig();
        }
        lockKeyHeld = KeybindHelper.isKeyDown(key);
        if (!lockKeyHeld) {
            pairingSlot = null;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void mouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK ||
                !Config.feature.sl.enableSlotLocking) {
            return;
        }
        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) {
            return;
        }
        GuiContainer container = (GuiContainer) Minecraft.getMinecraft().currentScreen;

        if (Config.feature.sl.enableSlotBinding && lockKeyHeld && pairingSlot != null) {
            final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
            final int scaledWidth = scaledresolution.getScaledWidth();
            final int scaledHeight = scaledresolution.getScaledHeight();
            int mouseX = Mouse.getX() * scaledWidth / Minecraft.getMinecraft().displayWidth;
            int mouseY = scaledHeight - Mouse.getY() * scaledHeight / Minecraft.getMinecraft().displayHeight - 1;

            Slot slot = ((AccessorGuiContainer) container).doGetSlotAtPosition(mouseX, mouseY);
            if (slot != null && slot.getSlotIndex() != 8 && slot.inventory == Minecraft.getMinecraft().thePlayer.inventory) {
                int slotNum = slot.getSlotIndex();
                if (slotNum >= 0 && slotNum <= 39) {
                    boolean isHotbar = slotNum < 9;
                    boolean isInventory = !isHotbar && slotNum < 36;
                    boolean isArmor = !isHotbar && !isInventory;

                    int pairingNum = pairingSlot.getSlotIndex();
                    if (isHotbar && slotNum != pairingNum) {
                        LockedSlot[] lockedSlots = getDataForProfile();
                        if (lockedSlots != null) {
                            if (lockedSlots[slotNum] == null) {
                                lockedSlots[slotNum] = new LockedSlot();
                            }
                            if (!lockedSlots[slotNum].locked) {
                                if (lockedSlots[pairingNum] == null) {
                                    lockedSlots[pairingNum] = new LockedSlot();
                                }

                                lockedSlots[pairingNum].boundTo = slotNum;
                                lockedSlots[pairingNum].locked = false;

                                lockedSlots[slotNum].boundTo = pairingNum;
                            }
                        }
                    }
                }
            } else {
                int pairingNum = pairingSlot.getSlotIndex();
                LockedSlot[] lockedSlots = getDataForProfile();
                if (lockedSlots != null && lockedSlots[pairingNum] != null) {
                    if (lockedSlots[pairingNum].boundTo >= 0) {
                        lockedSlots[lockedSlots[pairingNum].boundTo] = null;
                    }
                    lockedSlots[pairingNum] = null;
                }
            }
            saveConfig();
        }
    }

    public void toggleLock(int lockIndex) {
        if (lockIndex == 8) return;
        LockedSlot[] lockedSlots = getDataForProfile();

        if (lockedSlots != null) {
            if (lockedSlots[lockIndex] == null) {
                lockedSlots[lockIndex] = new LockedSlot();
            }
            lockedSlots[lockIndex].locked = !lockedSlots[lockIndex].locked;
            lockedSlots[lockIndex].boundTo = -1;

            if (Config.feature.sl.slotLockSound) {
                float vol = Config.feature.sl.slotLockSoundVol / 100f;
                if (vol > 0) {
                    if (vol > 1) vol = 1;
                    final float volF = vol;
                    final boolean locked = lockedSlots[lockIndex].locked;

                    ISound sound = new PositionedSound(new ResourceLocation("random.orb")) {{
                        volume = volF;
                        pitch = locked ? 0.943f : 0.1f;
                        repeat = false;
                        repeatDelay = 0;
                        attenuationType = ISound.AttenuationType.NONE;
                    }};

                    float oldLevel = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS);
                    Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.PLAYERS, 1);
                    Minecraft.getMinecraft().getSoundHandler().playSound(sound);
                    Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.PLAYERS, oldLevel);
                }
            }

            if (lockIndex < 9 && lockedSlots[lockIndex].locked) {
                for (int i = 9; i <= 39; i++) {
                    if (lockedSlots[i] != null && lockedSlots[i].boundTo == lockIndex) {
                        lockedSlots[i].boundTo = -1;
                    }
                }
            }
        }
        saveConfig();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void drawScreenEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Config.feature.sl.enableSlotBinding || event.isCanceled() || pairingSlot == null || !lockKeyHeld) {
            setTopHalfBarrier = false;
            return;
        }

        LockedSlot[] lockedSlots = getDataForProfile();
        LockedSlot lockedSlot = getLockedSlot(lockedSlots, pairingSlot.getSlotIndex());
        if (lockedSlot == null || lockedSlot.boundTo >= 0 && lockedSlot.boundTo < 8) {
            setTopHalfBarrier = false;
            return;
        }

        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) {
            setTopHalfBarrier = false;
            return;
        }
        AccessorGuiContainer container = (AccessorGuiContainer) Minecraft.getMinecraft().currentScreen;

        int x1 = container.getGuiLeft() + pairingSlot.xDisplayPosition + 8;
        int y1 = container.getGuiTop() + pairingSlot.yDisplayPosition + 8;
        int x2 = event.mouseX;
        int y2 = event.mouseY;

        if (x2 > x1 - 8 && x2 < x1 + 8 && y2 > y1 - 8 && y2 < y1 + 8) {
            setTopHalfBarrier = false;
            return;
        }

        drawLinkArrow(x1, y1, x2, y2);
        setTopHalfBarrier = true;
    }

    private void drawLinkArrow(int x1, int y1, int x2, int y2) {
        GlStateManager.color(0x33 / 255f, 0xee / 255f, 0xdd / 255f, 1f);
        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        GlStateManager.translate(0, 0, 500);
        drawLine(x1, y1, x2, y2);
        GlStateManager.translate(0, 0, -500);

        GlStateManager.enableTexture2D();
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        Vector2f vec = new Vector2f(x2 - x1, y2 - y1);
        vec.normalise(vec);
        Vector2f side = new Vector2f(vec.y, -vec.x);

        GL11.glLineWidth(1f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        int lines = 2; // Reduced from 6 to 2
        for (int i = 0; i < lines; i++) {
            worldrenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            worldrenderer.pos(x1 - side.x + side.x * i / lines, y1 - side.y + side.y * i / lines, 0.0D).endVertex();
            worldrenderer.pos(x2 - side.x + side.x * i / lines, y2 - side.y + side.y * i / lines, 0.0D).endVertex();
            tessellator.draw();
        }
    }

    @SubscribeEvent
    public void onWindowClick(SlotClickEvent slotClickEvent) {
        LockedSlot locked = getLockedSlot(slotClickEvent.slot);
        if (locked == null) {
            return;
        }
        if (locked.locked ||
                (slotClickEvent.clickType == 2 && SlotLocking.getInstance().isSlotIndexLocked(slotClickEvent.clickedButton))) {
            slotClickEvent.setCanceled(true);
            return;
        }
        if (Config.feature.sl.enableSlotBinding
                && slotClickEvent.clickType == 1 &&
                locked.boundTo != -1) {
            Slot boundSlot = slotClickEvent.guiContainer.inventorySlots.getSlotFromInventory(
                    Minecraft.getMinecraft().thePlayer.inventory,
                    locked.boundTo
            );

            if (boundSlot == null) {
                return;
            }

            LockedSlot boundLocked = getLockedSlot(boundSlot);

            if (!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) {
                return;
            }
            GuiContainer container = (GuiContainer) Minecraft.getMinecraft().currentScreen;
            int size = container.inventorySlots.inventorySlots.size();

            int from, to;
            int id = slotClickEvent.slot.getSlotIndex();
            int idChest = id + (size - 45); // adjust the id of the clicked slot to align with the current inventory's number of slots

            if (idChest >= 9 && 0 <= locked.boundTo && locked.boundTo < 8 && !boundLocked.locked) {
                from = idChest;
                to = locked.boundTo;
                if (boundLocked == DEFAULT_LOCKED_SLOT) {
                    LockedSlot[] lockedSlots = getDataForProfile();
                    lockedSlots[locked.boundTo] = new LockedSlot();
                    lockedSlots[locked.boundTo].boundTo = idChest;
                } else {
                    boundLocked.boundTo = id;
                }
            } else if (0 <= id && id < 8 && locked.boundTo >= 9 && locked.boundTo <= 39) {
                if (boundLocked.locked || boundLocked.boundTo != id) {
                    locked.boundTo = -1;
                    return;
                } else {
                    from = boundSlot.slotNumber;
                    to = id;
                }
            } else {
                return;
            }
            if (from == 39) from = 5;
            if (from == 38) from = 6;
            if (from == 37) from = 7;
            if (from == 36) from = 8;
            Minecraft.getMinecraft().playerController.windowClick(
                    slotClickEvent.guiContainer.inventorySlots.windowId,
                    from, to, 2, Minecraft.getMinecraft().thePlayer
            );
            slotClickEvent.setCanceled(true);
        } else if (Config.feature.sl.enableSlotBinding && locked.boundTo != -1 &&
                Config.feature.sl.bindingAlsoLocks) {
            slotClickEvent.setCanceled(true);
        }
        saveConfig();
    }

    public void drawSlot(Slot slot) {
        LockedSlot locked = getLockedSlot(slot);
        if (locked == null) return;

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (!(screen instanceof GuiContainer)) return;
        GuiContainer container = (GuiContainer) screen;

        boolean isBound = Config.feature.sl.enableSlotBinding && slot.canBeHovered() &&
                locked.boundTo >= 0 && locked.boundTo <= 39;

        if (locked.locked) {
            renderLockIcon(slot);
        } else if (isBound) {
            renderBoundIcon(slot, container);
        } else if (Config.feature.sl.enableSlotBinding && slot.getSlotIndex() < 8 &&
                pairingSlot != null && lockKeyHeld) {
            renderHotbarHighlight(slot, container);
        }
    }

    private void renderLockIcon(Slot slot) {
        GlStateManager.translate(0, 0, 400);
        Minecraft.getMinecraft().getTextureManager().bindTexture(LOCK);
        GlStateManager.color(1, 1, 1, 0.5f);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        RenderUtils.drawTexturedRect(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, 0, 1, 0, 1, GL11.GL_NEAREST);
        GlStateManager.depthMask(true);
        GlStateManager.translate(0, 0, -400);
    }

    private void renderBoundIcon(Slot slot, GuiContainer container) {
        final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        final int scaledWidth = scaledresolution.getScaledWidth();
        final int scaledHeight = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * scaledWidth / Minecraft.getMinecraft().displayWidth;
        int mouseY = scaledHeight - Mouse.getY() * scaledHeight / Minecraft.getMinecraft().displayHeight - 1;

        LockedSlot locked = getLockedSlot(slot);
        Slot boundSlot = container.inventorySlots.getSlotFromInventory(
                Minecraft.getMinecraft().thePlayer.inventory,
                locked.boundTo
        );
        if (boundSlot == null) return;

        boolean hoverOverSlot = ((AccessorGuiContainer) container).doIsMouseOverSlot(slot, mouseX, mouseY);

        if (hoverOverSlot || slot.getSlotIndex() >= 9) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(BOUND);
            GlStateManager.color(1, 1, 1, 0.7f);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            RenderUtils.drawTexturedRect(
                    slot.xDisplayPosition,
                    slot.yDisplayPosition,
                    16,
                    16,
                    0,
                    1,
                    0,
                    1,
                    GL11.GL_NEAREST
            );
            GlStateManager.depthMask(true);
        }

        if (hoverOverSlot) {
            LockedSlot boundLocked = getLockedSlot(boundSlot);
            if (boundLocked == null || boundLocked.locked ||
                    (boundSlot.getSlotIndex() >= 9 && boundLocked.boundTo != slot.getSlotIndex())) {
                locked.boundTo = -1;
                return;
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(BOUND);
            GlStateManager.color(1, 1, 1, 0.7f);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            RenderUtils.drawTexturedRect(
                    boundSlot.xDisplayPosition,
                    boundSlot.yDisplayPosition,
                    16,
                    16,
                    0,
                    1,
                    0,
                    1,
                    GL11.GL_NEAREST
            );
            GlStateManager.depthMask(true);

            int maxIter = 100;
            float x1 = slot.xDisplayPosition + 8;
            float y1 = slot.yDisplayPosition + 8;
            float x2 = boundSlot.xDisplayPosition + 8;
            float y2 = boundSlot.yDisplayPosition + 8;
            Vector2f vec = new Vector2f(x2 - x1, y2 - y1);
            vec.normalise(vec);

            while (x1 > slot.xDisplayPosition && x1 < slot.xDisplayPosition + 16 &&
                    y1 > slot.yDisplayPosition && y1 < slot.yDisplayPosition + 16) {
                if (maxIter-- < 50) break;
                x1 += vec.x;
                y1 += vec.y;
            }
            while (x2 > boundSlot.xDisplayPosition && x2 < boundSlot.xDisplayPosition + 16 &&
                    y2 > boundSlot.yDisplayPosition && y2 < boundSlot.yDisplayPosition + 16) {
                if (maxIter-- < 0) break;
                x2 -= vec.x;
                y2 -= vec.y;
            }

            GlStateManager.translate(0, 0, 200);
            drawLinkArrow((int) x1, (int) y1, (int) x2, (int) y2);
            GlStateManager.translate(0, 0, -200);
        }
    }

    private void renderHotbarHighlight(Slot slot, GuiContainer container) {
        final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        final int scaledWidth = scaledresolution.getScaledWidth();
        final int scaledHeight = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * scaledWidth / Minecraft.getMinecraft().displayWidth;
        int mouseY = scaledHeight - Mouse.getY() * scaledHeight / Minecraft.getMinecraft().displayHeight - 1;

        int x1 = ((AccessorGuiContainer) container).getGuiLeft() + pairingSlot.xDisplayPosition;
        int y1 = ((AccessorGuiContainer) container).getGuiTop() + pairingSlot.yDisplayPosition;

        if (mouseX <= x1 || mouseX >= x1 + 16 || mouseY <= y1 || mouseY >= y1 + 16) {
            Gui.drawRect(
                    slot.xDisplayPosition,
                    slot.yDisplayPosition,
                    slot.xDisplayPosition + 16,
                    slot.yDisplayPosition + 16,
                    0x80ffffff
            );
        }
    }

    public LockedSlot getLockedSlot(Slot slot) {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK ||
                !Config.feature.sl.enableSlotLocking)
            return null;
        if (slot == null) {
            return null;
        }
        if (slot.inventory != Minecraft.getMinecraft().thePlayer.inventory) {
            return null;
        }
        int index = slot.getSlotIndex();
        if (index < 0 || index > 39) {
            return null;
        }
        return getLockedSlotIndex(index);
    }

    public LockedSlot getLockedSlotIndex(int index) {
        if (ScoreboardUtils.currentGamemode != Gamemode.SKYBLOCK ||
                !Config.feature.sl.enableSlotLocking) {
            return null;
        }

        LockedSlot[] lockedSlots = getDataForProfile();
        if (lockedSlots == null) {
            return null;
        }
        return getLockedSlot(lockedSlots, index);
    }

    public boolean isSlotLocked(Slot slot) {
        LockedSlot locked = getLockedSlot(slot);
        return locked != null &&
                (locked.locked || (Config.feature.sl.bindingAlsoLocks && locked.boundTo != -1));
    }

    public boolean isSlotIndexLocked(int index) {
        LockedSlot locked = getLockedSlotIndex(index);

        return locked != null &&
                (locked.locked || (Config.feature.sl.bindingAlsoLocks && locked.boundTo != -1));
    }

    boolean setTopHalfBarrier = false;

    private boolean shouldShowBarrier(int slotNumber, IInventory inventory) {
        if (!(inventory instanceof InventoryPlayer))
            return false;
        if (slotNumber < 9)
            return false;
        if (pairingSlot != null && (slotNumber == pairingSlot.slotNumber || isArmourSlot(
                slotNumber,
                pairingSlot.slotNumber
        )))
            return false;
        return setTopHalfBarrier;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void afterRenderInventory(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiContainer)) return;
        GuiContainer gui = (GuiContainer) event.gui;
        Slot slot = getFocusedSlot(gui);
        if (slot == null || !shouldShowBarrier(slot.getSlotIndex(), slot.inventory))
            return;
        final ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        final int scaledWidth = scaledresolution.getScaledWidth();
        final int scaledHeight = scaledresolution.getScaledHeight();
        int mouseX = Mouse.getX() * scaledWidth / Minecraft.getMinecraft().displayWidth;
        int mouseY = scaledHeight - Mouse.getY() * scaledHeight / Minecraft.getMinecraft().displayHeight - 1;
        GuiUtils.drawHoveringText(
                Arrays.asList(
                        "§cYou cannot bind slots to anything except the hotbar.",
                        "§cBinding to a normal inventory slot would require cheats.",
                        "§cAnd you are not a cheater, are you?",
                        "§cWe certainly hope you are not."),
                mouseX,
                mouseY,
                scaledWidth,
                scaledHeight,
                Math.min(200, event.gui.width - mouseX - 20),
                Minecraft.getMinecraft().fontRendererObj
        );
    }

    @SubscribeEvent
    public void barrierInventory(ReplaceItemEvent event) {
        if (!shouldShowBarrier(event.getSlotNumber(), event.getInventory())) return;
        ItemStack stack = new ItemStack(Blocks.barrier);
        ItemUtils.getOrCreateTag(stack).setBoolean(
                "NEUHIDETOOLIP",
                true
        );
        event.replaceWith(stack);
    }

    boolean isArmourSlot(int eventSlotNumber, int pairingSlotNumber) {
        if (eventSlotNumber == 39 && pairingSlotNumber == 5) return true;
        if (eventSlotNumber == 38 && pairingSlotNumber == 6) return true;
        if (eventSlotNumber == 37 && pairingSlotNumber == 7) return true;
        return eventSlotNumber == 36 && pairingSlotNumber == 8;
    }

    public void resetSlotLocking() {
        String profileName = "generic";
        SlotLockProfile slotLockProfile = config.profileData.get(profileName);
        if (slotLockProfile != null) {
            slotLockProfile.slotLockData[0] = new SlotLockData();
        }
    }
}