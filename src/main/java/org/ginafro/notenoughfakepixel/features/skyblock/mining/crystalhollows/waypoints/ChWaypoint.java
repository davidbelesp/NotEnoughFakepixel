package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor(force = true)
public class ChWaypoint {
    public final double x;
    public final double y;
    public final double z;
    public final String id;
    public final String name;

    private Boolean temporary;
    private Integer colorRgb;
    public Boolean toggled;

    public static ChWaypoint of(double x, double y, double z, String name) {
        return new ChWaypoint(x, y, z, UUID.randomUUID().toString(), name,
                Boolean.FALSE, null, Boolean.TRUE);
    }

    public static ChWaypoint ofTemp(double x, double y, double z, String name, boolean temporary) {
        return new ChWaypoint(x, y, z, UUID.randomUUID().toString(), name, temporary, null, Boolean.TRUE);
    }

    public boolean isTemporarySafe() {
        return temporary != null && temporary;
    }

    // Returns true if toggled is null or true
    public boolean isToggledSafe() {
        return toggled == null || toggled;
    }

    public int getColorRgbOrDefault() {
        Integer c = colorRgb;
        if (c != null) return c;
        return ColorUtils.getColor(Config.feature.mining.crystalWaypointColor).getRGB();
    }

    public void setColorRgb(int rgb) {
        this.colorRgb = rgb;
    }
}
