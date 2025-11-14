package com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.gui;

import com.nef.notenoughfakepixel.features.skyblock.mining.crystalhollows.waypoints.ChWaypoint;

public interface RowActions {
    void onDelete(ChWaypoint wp);
    void onShare(ChWaypoint wp);
    void onEdit(ChWaypoint wp);
}
