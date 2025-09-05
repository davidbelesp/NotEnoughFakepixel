package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints;

import com.google.gson.*;
import org.ginafro.notenoughfakepixel.config.gui.Config;
import org.ginafro.notenoughfakepixel.utils.ColorUtils;

import java.lang.reflect.Type;
import java.util.UUID;

public final class ChWaypointAdapter implements JsonDeserializer<ChWaypoint>, JsonSerializer<ChWaypoint> {

    @Override
    public ChWaypoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject o = json.getAsJsonObject();

        double x = getAsDouble(o, "x", 0);
        double y = getAsDouble(o, "y", 0);
        double z = getAsDouble(o, "z", 0);
        String id = getAsString(o, "id", UUID.randomUUID().toString());
        String name = getAsString(o, "name", "Waypoint");

        Boolean temporary = o.has("temporary") ? o.get("temporary").getAsBoolean() : Boolean.FALSE;
        Integer colorRgb = o.has("colorRgb")
                ? o.get("colorRgb").getAsInt()
                : ColorUtils.getColor(Config.feature.mining.crystalWaypointColor).getRGB();
        Boolean toggled = o.has("toggled") ? o.get("toggled").getAsBoolean() : Boolean.TRUE;

        return new ChWaypoint(x, y, z, id, name, temporary, colorRgb, toggled);
    }

    @Override
    public JsonElement serialize(ChWaypoint src, Type typeOfSrc, JsonSerializationContext ctx) {
        JsonObject o = new JsonObject();
        o.addProperty("x", src.getX());
        o.addProperty("y", src.getY());
        o.addProperty("z", src.getZ());
        o.addProperty("id", src.getId());
        o.addProperty("name", src.getName());
        o.addProperty("temporary", src.isTemporarySafe());
        o.addProperty("colorRgb", src.getColorRgbOrDefault());
        return o;
    }

    private static double getAsDouble(JsonObject o, String k, double def) {
        return o.has(k) ? o.get(k).getAsDouble() : def;
    }
    private static String getAsString(JsonObject o, String k, String def) {
        return o.has(k) ? o.get(k).getAsString() : def;
    }
}