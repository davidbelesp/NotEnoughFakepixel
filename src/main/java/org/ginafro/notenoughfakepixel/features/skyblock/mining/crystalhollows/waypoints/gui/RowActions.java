package org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.gui;

import org.ginafro.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.ChWaypoint;

public interface RowActions {
    void onDelete(ChWaypoint wp);
    void onShare(ChWaypoint wp);
    void onEdit(ChWaypoint wp);
}
