package io.github.some_example_name;

import java.util.EnumSet;

public enum tileType {
    DIRT, SAND, OCEAN, RIVER, LAND,
    ELEVATION_UP, ELEVATION_DOWN, // up & down only used for button functions
    UNOCCUPIED, BUILDING, ROAD, BRIDGE, TUNNEL; // eventually will add more building types

    public static final EnumSet<tileType> tileLandType = EnumSet.of(DIRT, SAND, OCEAN, RIVER, LAND);
    public static final EnumSet<tileType> tileOccupationType = EnumSet.of(UNOCCUPIED, BUILDING, ROAD, BRIDGE, TUNNEL);
    }
