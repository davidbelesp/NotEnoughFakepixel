package org.ginafro.notenoughfakepixel.features.skyblock.overlays.stats;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.envcheck.registers.RegisterEvents;

import java.awt.Color;

@RegisterEvents
public class StatBars {

    public enum BarLength {
        TINY(new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_0_base.png")
                ,new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_0_fill.png"),
                34
        ),
        SMALL(new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_1_base.png")
                ,new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_1_fill.png"),
                50
        ),
        MEDIUM(new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_2_base.png")
                ,new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_2_fill.png"),
                80
        ),
        LARGE(new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_3_base.png")
                ,new ResourceLocation("notenoughfakepixel","skyblock/stats/bars/size_3_fill.png"),
                182
        );

        public final ResourceLocation base;
        public final ResourceLocation fill;
        public final int width;

        BarLength(ResourceLocation base, ResourceLocation fill, int width){
            this.base = base;
            this.fill = fill;
            this.width = width;
        }
    }

    public enum BarType {
        HEALTH(new ResourceLocation("notenoughfakepixel","skyblock/stats/heart.png"),new float[]{1f,0f,0f,1f,0.84f,0.074f}),
        MANA(new ResourceLocation("notenoughfakepixel","skyblock/stats/mana.png"), new float[]{0.0745f,0.905f,1f}),
        EXP(new ResourceLocation("notenoughfakepixel","skyblock/stats/exp.png"), new float[]{0.7843f,1f,0.5607f}),
        SPEED(new ResourceLocation("notenoughfakepixel","skyblock/stats/speed.png"), new float[]{1f,1f,1f}),
        DEFENCE(new ResourceLocation("notenoughfakepixel","skyblock/stats/defense.png"), new float[]{1f,1f,1f});

        public ResourceLocation logo;
        public float[] color;
        BarType(ResourceLocation logo,float[] color){
            this.logo = logo;
            this.color = color;
        }

        public int getX(){
            if(this == HEALTH){
                return Config.feature.overlays.posHealth.getRawX();
            }
            if(this == MANA){
                return Config.feature.overlays.posMana.getRawX();
            }
            if(this == SPEED){
                return Config.feature.overlays.posSpeed.getRawX();
            }
            if(this == EXP){
                return Config.feature.overlays.posExp.getRawX();
            }
            else{
                return Config.feature.overlays.posDefense.getRawX();
            }
        }

        public int getY(){
            if(this == HEALTH){
                return Config.feature.overlays.posHealth.getRawY();
            }
            if(this == MANA){
                return Config.feature.overlays.posMana.getRawY();
            }
            if(this == SPEED){
                return Config.feature.overlays.posSpeed.getRawY();
            }
            if(this == EXP){
                return Config.feature.overlays.posExp.getRawY();
            }
            else{
                return Config.feature.overlays.posDefense.getRawY();
            }
        }

        public StatValue getStatValue(){
            if(this == HEALTH){
                return new StatValue(health,maxHealth,overflowHealth);
            }
            if(this == MANA){
                return new StatValue(mana,maxMana,overflowMana);
            }
            if(this == SPEED){
                return new StatValue(speed,400);
            }
            if(this == EXP){
                return new StatValue(expLevel,0,exp);
            }
            if(this == DEFENCE){
                return new StatValue(defence);
            }
            return new StatValue(0,0);
        }
    }

    public static class StatValue {
        public float value1,value2,value3;

        public StatValue(float v1,float v2){
            value1 = v1;
            value2 = v2;
        }
        public StatValue(float v1,float v2,float v3){
            value1 = v1;
            value2 = v2;
            value3 = v3;
        }

        public StatValue(float v1){
            value1 = v1;
        }
    }

    public BarLength getBarLength(BarType type){
        if(type == BarType.HEALTH){
            return getLength(Config.feature.overlays.barLengthH);
        }
        if(type == BarType.MANA){
            return getLength(Config.feature.overlays.barLengthM);
        }
        if(type == BarType.SPEED){
            return getLength(Config.feature.overlays.barLengthS);
        }
        if(type == BarType.EXP){
            return getLength(Config.feature.overlays.barLengthE);
        }else{
            return getLength(Config.feature.overlays.barLengthD);
        }
    }

    public BarLength getLength(int i){
        if(i == 0) return BarLength.TINY;
        if(i == 1) return BarLength.SMALL;
        if(i == 2) return BarLength.MEDIUM;
        if(i == 3) return BarLength.LARGE;
        else return BarLength.SMALL;
    }


    @Setter
    @Getter
    private static int expLevel, health,maxHealth,mana,maxMana,overflowMana,overflowHealth,defence,speed = 100;
    private static float exp;
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e){
        if(e.type == 2){
            updateStats(e.message);
            if(Config.feature.overlays.disableActionBar && e.message.getUnformattedText().contains("Defense")){
                e.setCanceled(true);
            }
        }
    }


    private void updateStats(IChatComponent message) {
        String text1 = message.getUnformattedText();
        if(text1.contains("❤") && text1.contains("❇") && text1.contains("✎")){
            String text = text1.replace("❤","").replace("❇","").replace("✎","");
            String[] stats = text.split(" {5}");
            String heal = StringUtils.stripControlCodes(stats[0].trim());
            String def = StringUtils.stripControlCodes(stats[1].trim());
            String man = StringUtils.stripControlCodes(stats[2].trim());
            defence = Integer.parseInt(def.replace("Defense","").trim());
            maxHealth = Integer.parseInt(heal.split("/")[1].trim());
            health = Integer.parseInt(heal.split("/")[0].trim());
            overflowHealth = Math.max(0,health - maxHealth);
            String[] manas = man.replace(" Mana","").split(" ");
            mana = Integer.parseInt(manas[0].split("/")[0].trim());
            maxMana = Integer.parseInt(manas[0].split("/")[1].trim());
            if(man.contains("ʬ")){
                overflowMana = Integer.parseInt(manas[1].replace("ʬ","").trim());
            }
            exp = Minecraft.getMinecraft().thePlayer.experience;
            expLevel = Minecraft.getMinecraft().thePlayer.experienceLevel;
        }
    }

    @SubscribeEvent
    public void onDraw(RenderGameOverlayEvent e){
        if(maxHealth > 0 && maxMana > 0 && Config.feature.overlays.disableIcons) {
            if (e.type == RenderGameOverlayEvent.ElementType.EXPERIENCE || e.type == RenderGameOverlayEvent.ElementType.ARMOR || e.type == RenderGameOverlayEvent.ElementType.HEALTH || e.type == RenderGameOverlayEvent.ElementType.HEALTHMOUNT || e.type == RenderGameOverlayEvent.ElementType.FOOD) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e){
        if (e.type != RenderGameOverlayEvent.ElementType.HOTBAR || !Config.feature.overlays.statOverlay) {
            return;
        }
        if(maxMana != 0 || maxHealth != 0){
            for(BarType type : BarType.values()){
                drawBar(type.getX(),type.getY(),type,getBarLength(type),type.getStatValue());
            }
        }
    }


    private void drawBar(int x,int y,BarType type, BarLength barLength,StatValue value) {
        GlStateManager.pushMatrix();
        GlStateManager.resetColor();
        GlStateManager.color(1f,1f,1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(type.logo);
        Gui.drawModalRectWithCustomSizedTexture(x - 11,y - 1,0f,0f,9,9,9,9);
        float filled = barLength.width;
        if(value.value2 != 0){
            filled = Math.min(barLength.width,((value.value1/value.value2) * barLength.width));
        }
        if(type == BarType.EXP){
            filled = Math.min(barLength.width,(BarType.EXP.getStatValue().value3 * barLength.width));
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(barLength.base);
        Gui.drawModalRectWithCustomSizedTexture(x,y,0,0,barLength.width,7,barLength.width,7);
        Minecraft.getMinecraft().getTextureManager().bindTexture(barLength.fill);
        GlStateManager.color(type.color[0],type.color[1],type.color[2]);
        Gui.drawModalRectWithCustomSizedTexture(x,y,0,0,(int)filled,7,barLength.width,7);
        if(value.value3 != 0 && type != BarType.EXP && type.color.length > 3){
            float fill = Math.min(barLength.width,((value.value3/value.value2) * barLength.width));
            GlStateManager.resetColor();
            GlStateManager.color(type.color[3],type.color[4],type.color[5]);
            Gui.drawModalRectWithCustomSizedTexture(x,y,0,0,(int)fill,7,barLength.width,7);
        }
        String text = String.valueOf(type.getStatValue().value1);
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        float xPos = x + (float) barLength.width / 2 - (float) font.getStringWidth(text) / 2;
        int color = new Color((int)type.color[0] * 255,(int)type.color[1] * 255,(int)type.color[2] * 255).getRGB();
        font.drawStringWithShadow(text,xPos,y,color);
        GlStateManager.popMatrix();
    }
}
