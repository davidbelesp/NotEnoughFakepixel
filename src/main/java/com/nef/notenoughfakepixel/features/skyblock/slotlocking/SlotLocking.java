package com.nef.notenoughfakepixel.features.skyblock.slotlocking;

import com.nef.notenoughfakepixel.config.gui.Config;
import com.nef.notenoughfakepixel.config.gui.core.config.KeybindHelper;
import com.nef.notenoughfakepixel.config.gui.core.util.StringUtils;
import com.nef.notenoughfakepixel.config.gui.core.util.render.RenderUtils;
import com.nef.notenoughfakepixel.envcheck.registers.RegisterInstance;
import com.nef.notenoughfakepixel.events.ReplaceItemEvent;
import com.nef.notenoughfakepixel.events.SlotClickEvent;
import com.nef.notenoughfakepixel.mixin.accesors.AccessorGuiContainer;
import com.nef.notenoughfakepixel.serverdata.SkyblockData;
import com.nef.notenoughfakepixel.utils.ConfigHandler;
import com.nef.notenoughfakepixel.utils.ItemUtils;
import com.nef.notenoughfakepixel.utils.TablistParser;
import com.nef.notenoughfakepixel.variables.Resources;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SlotLocking {

    @RegisterInstance public static final SlotLocking INSTANCE = new SlotLocking();
    public static SlotLocking getInstance() { return INSTANCE; }

    // ----- CONSTANTS -----
    private static final int HOTBAR_SIZE = 9;
    private static final int INV_SIZE = 40; // 0-39
    private static final int INV_LAST = INV_SIZE - 1;
    private static final int HOTBAR_LAST = HOTBAR_SIZE - 1;
    private static final int ARMOR_START = 36; // 36..39
    private static final int SLOT_THROW = 8;   // vanilla drop key protected index
    private static final long[] EMPTY_CHANGES = new long[HOTBAR_SIZE];

    private static final Pattern WINDOW_REGEX = Pattern.compile(".+ Backpack (?:✦ )?\\(Slot #(\\d+)\\)");
    private static final Pattern ECHEST_WINDOW_REGEX = Pattern.compile("Ender Chest \\(Page (\\d+)\\)");

    // ----- DATA MODEL (left public for GSON) -----
    public static class LockedSlot {
        public boolean locked = false;
        public int boundTo = -1;
    }
    public static class SlotLockData {
        public LockedSlot[] lockedSlots = new LockedSlot[INV_SIZE];
    }
    public static class SlotLockProfile {
        public SlotLockData[] slotLockData = new SlotLockData[1];
    }
    public static class SlotLockingConfig {
        public HashMap<String, SlotLockProfile> profileData = new HashMap<>();
    }

    // ----- FIELDS -----
    private static final LockedSlot DEFAULT_LOCKED_SLOT = new LockedSlot();
    private SlotLockingConfig config = new SlotLockingConfig();

    private boolean lockKeyHeld = false;
    private Slot pairingSlot = null;

    // tracks recent slot index changes (hotbar only) to apply swap delay
    private final long[] slotChanges = new long[HOTBAR_SIZE];

    @Setter @Getter private Slot realSlot = null;

    // ----- LIFECYCLE/CONFIG -----
    public void loadConfig(File file) {
        SlotLockingConfig loaded = ConfigHandler.loadConfig(SlotLockingConfig.class, file, ConfigHandler.GSON);
        config = (loaded != null) ? loaded : new SlotLockingConfig();
    }
    public void saveConfig() {
        ConfigHandler.saveConfig(config, new File(Config.configDirectory, "slotlocking.json"), ConfigHandler.GSON);
    }
    public void resetSlotLocking() {
        String profile = currentProfileOrGeneric();
        SlotLockProfile p = config.profileData.get(profile);
        if (p != null) p.slotLockData[0] = new SlotLockData();
    }

    // ----- CONTEXT HELPERS -----
    private static Minecraft mc() { return Minecraft.getMinecraft(); }
    private static boolean bindingOn() { return Config.feature.sl.enableSlotBinding; }
    private static boolean lockAndOnSkyblock() { return SkyblockData.getCurrentGamemode().isSkyblock() && Config.feature.sl.enableSlotLocking; }

    private static GuiContainer currentContainer() {
        return (mc().currentScreen instanceof GuiContainer) ? (GuiContainer) mc().currentScreen : null;
    }

    private static boolean isValidIndex(int idx) { return idx >= 0 && idx <= INV_LAST; }
    private static boolean isHotbar(int idx) { return idx >= 0 && idx < HOTBAR_SIZE; }
    private static boolean isInventory(int idx) { return idx >= HOTBAR_SIZE && idx < ARMOR_START; }
    private static boolean isArmor(int idx) { return idx >= ARMOR_START && idx <= INV_LAST; }

    private static boolean isPlayerInventorySlot(Slot s) {
        return s != null && s.inventory == mc().thePlayer.inventory && isValidIndex(s.getSlotIndex());
    }

    private static String currentProfileOrGeneric() {
        String p = SkyblockData.getCurrentProfile();
        return (p == null) ? "generic" : p;
    }

    // mouse → scaled coords & hovered slot
    private static int[] getMouseScaled() {
        ScaledResolution sr = new ScaledResolution(mc());
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();
        int mx = Mouse.getX() * sw / mc().displayWidth;
        int my = sh - Mouse.getY() * sh / mc().displayHeight - 1;
        return new int[]{mx, my};
    }

    private static Slot getSlotAtMouse(GuiContainer container) {
        int[] m = getMouseScaled();
        return ((AccessorGuiContainer) container).doGetSlotAtPosition(m[0], m[1]);
    }

    // ----- PROFILE DATA ACCESS -----

    private LockedSlot[] getDataForProfile() {
        if (!lockAndOnSkyblock()) return null;

        if (Config.feature.sl.disableInStorage) {
            String raw = TablistParser.lastOpenChestName.trim();
            if ("Storage".equals(raw)) return null;
            String name = StringUtils.cleanColour(raw);
            if (WINDOW_REGEX.matcher(name).matches() || ECHEST_WINDOW_REGEX.matcher(name).matches()) return null;
        }

        String profile = currentProfileOrGeneric();
        SlotLockProfile prof = config.profileData.computeIfAbsent(profile, k -> new SlotLockProfile());
        if (prof.slotLockData[0] == null) prof.slotLockData[0] = new SlotLockData();
        return prof.slotLockData[0].lockedSlots;
    }

    private static LockedSlot getLockedOrDefault(LockedSlot[] arr, int idx) {
        if (arr == null || !isValidIndex(idx)) return DEFAULT_LOCKED_SLOT;
        LockedSlot s = arr[idx];
        return (s != null) ? s : DEFAULT_LOCKED_SLOT;
    }

    private static LockedSlot getOrCreateLocked(LockedSlot[] arr, int idx) {
        if (arr[idx] == null) arr[idx] = new LockedSlot();
        return arr[idx];
    }

    // ----- PUBLIC QUERIES -----

    public LockedSlot getLockedSlot(Slot slot) {
        if (!lockAndOnSkyblock() || slot == null || slot.inventory != mc().thePlayer.inventory) return null;
        int idx = slot.getSlotIndex();
        if (!isValidIndex(idx)) return null;
        return getLockedSlotIndex(idx);
    }

    public LockedSlot getLockedSlotIndex(int index) {
        if (!lockAndOnSkyblock()) return null;
        return getLockedOrDefault(getDataForProfile(), index);
    }

    public boolean isSlotLocked(Slot slot) {
        LockedSlot l = getLockedSlot(slot);
        return l != null && (l.locked || (Config.feature.sl.bindingAlsoLocks && l.boundTo != -1));
    }

    public boolean isSlotIndexLocked(int index) {
        LockedSlot l = getLockedSlotIndex(index);
        return l != null && (l.locked || (Config.feature.sl.bindingAlsoLocks && l.boundTo != -1));
    }

    // ----- SWAP DELAY TRACKING -----
    public void changedSlot(int slotNumber) {
        int delay = Config.feature.sl.slotLockSwapDelay;
        if (delay == 0 || !isSlotIndexLocked(slotNumber) || !isHotbar(slotNumber)) return;

        long now = System.currentTimeMillis();
        for (int i = 0; i < slotChanges.length; i++) {
            if (i != slotNumber && slotChanges[i] != 0 && slotChanges[i] + delay > now) {
                slotChanges[i] = 0;
            }
        }
        slotChanges[slotNumber] = now;
    }

    public boolean isSwapedSlotLocked() {
        int delay = Config.feature.sl.slotLockSwapDelay;
        if (delay == 0) return false;
        long now = System.currentTimeMillis();
        for (int i = 0; i < slotChanges.length; i++) {
            if (slotChanges[i] != 0 && isSlotIndexLocked(i) && slotChanges[i] + delay > now) return true;
        }
        return false;
    }

    // ----- input handling -----
    @SubscribeEvent(priority = EventPriority.LOW)
    public void keyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!lockAndOnSkyblock()) return;
        GuiContainer container = currentContainer();
        if (container == null) return;

        int key = Config.feature.sl.slotLockKey;
        if (!lockKeyHeld && KeybindHelper.isKeyPressed(key) && !Keyboard.isRepeatEvent()) {
            Slot foc = getSlotAtMouse(container);
            if (!isPlayerInventorySlot(foc)) return;

            int idx = foc.getSlotIndex();
            if (idx == SLOT_THROW) return; // avoid vanilla drop slot

            // decide pairing source (only if inventory/armor)
            pairingSlot = (isInventory(idx) || isArmor(idx)) ? foc : null;

            LockedSlot[] arr = getDataForProfile();
            if (arr != null) {
                LockedSlot ls = getOrCreateLocked(arr, idx);
                // toggle
                ls.locked = !ls.locked;
                ls.boundTo = -1;

                playToggleSound(ls.locked);

                // if hotbar got locked, unbind any inventories bound to it
                if (isHotbar(idx) && ls.locked) {
                    for (int i = HOTBAR_SIZE; i <= INV_LAST; i++) {
                        LockedSlot other = arr[i];
                        if (other != null && other.boundTo == idx) other.boundTo = -1;
                    }
                }
            }
        }
        lockKeyHeld = KeybindHelper.isKeyDown(key);
        if (!lockKeyHeld) pairingSlot = null;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void mouseEvent(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!lockAndOnSkyblock()) return;
        GuiContainer container = currentContainer();
        if (container == null) return;

        if (bindingOn() && lockKeyHeld && pairingSlot != null) {
            Slot hover = getSlotAtMouse(container);
            if (isPlayerInventorySlot(hover) && hover.getSlotIndex() != SLOT_THROW) {
                int targetIdx = hover.getSlotIndex();
                if (!isValidIndex(targetIdx)) return;

                boolean targetHotbar = isHotbar(targetIdx);
                int pairingIdx = pairingSlot.getSlotIndex();

                if (targetHotbar && targetIdx != pairingIdx) {
                    LockedSlot[] arr = getDataForProfile();
                    if (arr != null) {
                        LockedSlot target = getOrCreateLocked(arr, targetIdx);
                        if (!target.locked) {
                            LockedSlot src = getOrCreateLocked(arr, pairingIdx);
                            src.boundTo = targetIdx;
                            src.locked = false;
                            target.boundTo = pairingIdx;
                        }
                    }
                }
            } else {
                // mouse left pairing area → cancel temp binding preview
                int pairingIdx = pairingSlot.getSlotIndex();
                LockedSlot[] arr = getDataForProfile();
                if (arr != null && arr[pairingIdx] != null) {
                    if (arr[pairingIdx].boundTo >= 0) arr[arr[pairingIdx].boundTo] = null;
                    arr[pairingIdx] = null;
                }
            }
        }
    }

    public void toggleLock(int lockIndex) {
        if (lockIndex == SLOT_THROW) return;
        LockedSlot[] arr = getDataForProfile();
        if (arr == null) return;

        LockedSlot ls = getOrCreateLocked(arr, lockIndex);
        ls.locked = !ls.locked;
        ls.boundTo = -1;

        playToggleSound(ls.locked);

        if (isHotbar(lockIndex) && ls.locked) {
            for (int i = HOTBAR_SIZE; i <= INV_LAST; i++) {
                LockedSlot other = arr[i];
                if (other != null && other.boundTo == lockIndex) other.boundTo = -1;
            }
        }
    }

    // ----- CLICK HANDLING / BINDING MOVES -----
    @SubscribeEvent
    public void onWindowClick(SlotClickEvent e) {
        LockedSlot locked = getLockedSlot(e.slot);
        if (locked == null) return;

        // lock blocks and: swap-click (type 2) blocks if target hotbar index is locked
        if (locked.locked || (e.clickType == 2 && isSlotIndexLocked(e.clickedButton))) {
            e.setCanceled(true);
            return;
        }

        if (bindingOn() && e.clickType == 1 && locked.boundTo != -1) {
            GuiContainer container = currentContainer();
            if (container == null) return;

            Slot bound = container.inventorySlots.getSlotFromInventory(mc().thePlayer.inventory, locked.boundTo);
            if (bound == null) return;

            LockedSlot boundLocked = getLockedSlot(bound);
            int id = e.slot.getSlotIndex();
            int size = container.inventorySlots.inventorySlots.size();

            // adjust clicked id to chest index space
            int idChest = id + (size - 45);

            int from, to;
            if (idChest >= HOTBAR_SIZE && 0 <= locked.boundTo && locked.boundTo < HOTBAR_SIZE && !boundLocked.locked) {
                from = idChest; to = locked.boundTo;
                if (boundLocked == DEFAULT_LOCKED_SLOT) {
                    LockedSlot[] arr = getDataForProfile();
                    LockedSlot tmp = getOrCreateLocked(arr, locked.boundTo);
                    tmp.boundTo = idChest;
                } else {
                    boundLocked.boundTo = id;
                }
            } else if (0 <= id && id < HOTBAR_SIZE && locked.boundTo >= HOTBAR_SIZE && locked.boundTo <= INV_LAST) {
                if (boundLocked.locked || boundLocked.boundTo != id) {
                    locked.boundTo = -1;
                    return;
                } else {
                    from = bound.slotNumber; to = id;
                }
            } else {
                return;
            }

            // armor slots remap
            if (from == 39) from = 5;
            if (from == 38) from = 6;
            if (from == 37) from = 7;
            if (from == 36) from = 8;

            mc().playerController.windowClick(container.inventorySlots.windowId, from, to, 2, mc().thePlayer);
            e.setCanceled(true);
        } else if (bindingOn() && locked.boundTo != -1 && Config.feature.sl.bindingAlsoLocks) {
            e.setCanceled(true);
        }
    }

    // ----- DRAW HOOKS -----
    @SubscribeEvent(priority = EventPriority.LOW)
    public void drawScreenEvent(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!bindingOn() || event.isCanceled() || pairingSlot == null || !lockKeyHeld) {
            setTopHalfBarrier = false;
            return;
        }

        LockedSlot[] arr = getDataForProfile();
        LockedSlot locked = getLockedOrDefault(arr, pairingSlot.getSlotIndex());
        if (locked.boundTo >= 0 && locked.boundTo < HOTBAR_LAST) return;

        GuiContainer container = currentContainer();
        if (container == null) return;

        AccessorGuiContainer agc = (AccessorGuiContainer) container;

        int x1 = agc.getGuiLeft() + pairingSlot.xDisplayPosition + 8;
        int y1 = agc.getGuiTop() + pairingSlot.yDisplayPosition + 8;
        int x2 = event.mouseX;
        int y2 = event.mouseY;

        if (x2 > x1 - 8 && x2 < x1 + 8 && y2 > y1 - 8 && y2 < y1 + 8) return;

        drawLinkArrow(x1, y1, x2, y2);
        setTopHalfBarrier = true;
    }

    private void drawLinkArrow(int x1, int y1, int x2, int y2) {
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(
                GL11.GL_ENABLE_BIT
                        | GL11.GL_COLOR_BUFFER_BIT
                        | GL11.GL_LINE_BIT
                        | GL11.GL_TEXTURE_BIT
                        | GL11.GL_DEPTH_BUFFER_BIT
                        | GL11.GL_LIGHTING_BIT);

        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,       GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.depthMask(false);
        GlStateManager.translate(0, 0, 500);
        GlStateManager.color(0x33 / 255f, 0xee / 255f, 0xdd / 255f, 1f);
        GlStateManager.disableTexture2D();

        GL11.glLineWidth(1f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        drawLine(x1, y1, x2, y2);

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.depthMask(true);

        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        Vector2f vec = new Vector2f(x2 - x1, y2 - y1);
        vec.normalise(vec);
        Vector2f side = new Vector2f(vec.y, -vec.x);

        Tessellator t = Tessellator.getInstance();
        WorldRenderer w = t.getWorldRenderer();

        final int lines = 6;
        for (int i = 0; i < lines; i++) {
            w.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            w.pos(x1 - side.x + side.x * i / lines, y1 - side.y + side.y * i / lines, 0.0D).endVertex();
            w.pos(x2 - side.x + side.x * i / lines, y2 - side.y + side.y * i / lines, 0.0D).endVertex();
            t.draw();
        }
    }

    public void drawSlot(Slot slot) {
        LockedSlot locked = getLockedSlot(slot);
        if (locked == null) return;

        if (locked.locked) {
            // padlock overlay
            GlStateManager.translate(0, 0, 400);
            mc().getTextureManager().bindTexture(Resources.LOCK.getResource());
            GlStateManager.color(1, 1, 1, 0.5f);
            GlStateManager.depthMask(false);
            RenderUtils.drawTexturedRect(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, 0, 1, 0, 1, GL11.GL_NEAREST);
            GlStateManager.depthMask(true);
            GlStateManager.enableBlend();
            GlStateManager.translate(0, 0, -400);
            return;
        }

        if (!bindingOn()) return;

        final int idx = slot.getSlotIndex();
        if (slot.canBeHovered() && locked.boundTo >= 0 && locked.boundTo <= INV_LAST) {
            GuiContainer container = currentContainer();
            if (container == null) return;
            AccessorGuiContainer agc = (AccessorGuiContainer) container;

            int[] m = getMouseScaled();
            boolean hoverOver = agc.doIsMouseOverSlot(slot, m[0], m[1]);

            Slot boundSlot = container.inventorySlots.getSlotFromInventory(mc().thePlayer.inventory, locked.boundTo);
            if (boundSlot == null) return;

            if (hoverOver || idx >= HOTBAR_SIZE) {
                // draw bound icon on this slot
                drawBoundOverlay(slot);
                // re-render item overlay text
                rerenderItemOverlay(slot);
            } else if (pairingSlot != null && lockKeyHeld && idx < HOTBAR_SIZE) {
                // dim hotbar targets during pairing
                int x1 = agc.getGuiLeft() + pairingSlot.xDisplayPosition;
                int y1 = agc.getGuiTop() + pairingSlot.yDisplayPosition;
                if (!pointInRect(m[0], m[1], x1, y1, 16, 16)) {
                    Gui.drawRect(slot.xDisplayPosition, slot.yDisplayPosition, slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, 0x80ffffff);
                }
            }

            if (hoverOver) {
                LockedSlot boundLocked = getLockedSlot(boundSlot);
                if (boundLocked == null || boundLocked.locked || (boundSlot.getSlotIndex() >= HOTBAR_SIZE && boundLocked.boundTo != idx)) {
                    locked.boundTo = -1;
                    return;
                }

                drawBoundOverlay(boundSlot);
                rerenderItemOverlay(boundSlot);

                // draw line center-to-center trimmed to slot bounds
                int maxIter = 100;
                float x1 = slot.xDisplayPosition + 8, y1 = slot.yDisplayPosition + 8;
                float x2 = boundSlot.xDisplayPosition + 8, y2 = boundSlot.yDisplayPosition + 8;
                Vector2f dir = new Vector2f(x2 - x1, y2 - y1);
                dir.normalise(dir);

                while (insideSlot(x1, y1, slot) && maxIter-- > 50) { x1 += dir.x; y1 += dir.y; }
                while (insideSlot(x2, y2, boundSlot) && maxIter-- > 0) { x2 -= dir.x; y2 -= dir.y; }

                GlStateManager.translate(0, 0, 200);
                drawLinkArrow((int) x1, (int) y1, (int) x2, (int) y2);
                GlStateManager.translate(0, 0, -200);
            }
        } else if (idx < HOTBAR_SIZE && pairingSlot != null && lockKeyHeld) {
            GuiContainer container = currentContainer();
            if (container == null) return;
            AccessorGuiContainer agc = (AccessorGuiContainer) container;

            int[] m = getMouseScaled();
            int x1 = agc.getGuiLeft() + pairingSlot.xDisplayPosition;
            int y1 = agc.getGuiTop() + pairingSlot.yDisplayPosition;
            if (!pointInRect(m[0], m[1], x1, y1, 16, 16)) {
                Gui.drawRect(slot.xDisplayPosition, slot.yDisplayPosition, slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, 0x80ffffff);
            }
        }
    }

    private static boolean pointInRect(int px, int py, int rx, int ry, int w, int h) {
        return (px > rx && px < rx + w && py > ry && py < ry + h);
    }
    private static boolean insideSlot(float x, float y, Slot s) {
        return x > s.xDisplayPosition && x < s.xDisplayPosition + 16 && y > s.yDisplayPosition && y < s.yDisplayPosition + 16;
    }
    private static void drawBoundOverlay(Slot s) {
        mc().getTextureManager().bindTexture(Resources.BOUND.getResource());
        GlStateManager.color(1, 1, 1, 0.7f);
        GlStateManager.depthMask(false);
        RenderUtils.drawTexturedRect(s.xDisplayPosition, s.yDisplayPosition, 16, 16, 0, 1, 0, 1, GL11.GL_NEAREST);
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
    }
    private static void rerenderItemOverlay(Slot s) {
        ItemStack st = s.getStack();
        if (st != null) {
            mc().getRenderItem().renderItemOverlayIntoGUI(mc().fontRendererObj, st, s.xDisplayPosition, s.yDisplayPosition, null);
        }
    }

    // ----- SOUND -----
    private void playToggleSound(boolean locked) {
        if (!Config.feature.sl.slotLockSound) return;

        float vol = Math.min(1f, Math.max(0f, Config.feature.sl.slotLockSoundVol / 100f));
        if (vol <= 0f) return;

        ISound sound = new PositionedSound(new ResourceLocation("random.orb")) {{
            volume = vol;
            pitch = locked ? 0.943f : 0.1f;
            repeat = false;
            repeatDelay = 0;
            attenuationType = ISound.AttenuationType.NONE;
        }};

        float old = mc().gameSettings.getSoundLevel(SoundCategory.PLAYERS);
        mc().gameSettings.setSoundLevel(SoundCategory.PLAYERS, 1f);
        mc().getSoundHandler().playSound(sound);
        mc().gameSettings.setSoundLevel(SoundCategory.PLAYERS, old);
    }

    // ----- VISUAL BARRIERS (prevent binding on inv slots) -----
    boolean setTopHalfBarrier = false;

    private boolean shouldShowBarrier(int slotNumber, IInventory inv) {
        if (!(inv instanceof InventoryPlayer)) return false;
        if (slotNumber < HOTBAR_SIZE) return false;
        if (pairingSlot != null && (slotNumber == pairingSlot.slotNumber || isArmourSlot(slotNumber, pairingSlot.slotNumber))) return false;
        return setTopHalfBarrier;
    }

    @SubscribeEvent
    public void barrierInventory(ReplaceItemEvent event) {
        if (!shouldShowBarrier(event.getSlotNumber(), event.getInventory())) return;
        ItemStack stack = new ItemStack(Blocks.barrier);
        ItemUtils.getOrCreateTag(stack).setBoolean("NEFHIDETOOLTIP", true);
        event.replaceWith(stack);
    }

    boolean isArmourSlot(int eventSlotNumber, int pairingSlotNumber) {
        return (eventSlotNumber == 39 && pairingSlotNumber == 5)
                || (eventSlotNumber == 38 && pairingSlotNumber == 6)
                || (eventSlotNumber == 37 && pairingSlotNumber == 7)
                || (eventSlotNumber == 36 && pairingSlotNumber == 8);
    }

}