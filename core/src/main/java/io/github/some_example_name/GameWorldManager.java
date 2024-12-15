package io.github.some_example_name;

import com.badlogic.gdx.math.Vector4;

public class GameWorldManager extends EditorWorldManager {

    int testAllTilesIndex = 0;
    ModelInfo checkModel = null;

    public GameWorldManager(String fileName) {
        super(fileName);
    }

    public void updateTileTypeRoad(int selectedIndex, tileType newTileOccupationType) { // updates tile type & direct neighbours
        TileProperties selectedProperties = tileArray.get(selectedIndex).tileProperties;

        selectedProperties.tileOccupationType = newTileOccupationType;

        updateTileHelper(selectedIndex, selectedProperties);

        if (selectedProperties.nTileType != null) {
            int northIndex = selectedIndex - worldTileWidth;
            TileProperties northProperties = tileArray.get(northIndex).tileProperties;
            northProperties.sTileOccupationType = newTileOccupationType; // in relation to the north tile, the south tile is changing
            updateTileHelper(northIndex, northProperties);
        }
        if (selectedProperties.sTileType != null) {
            int southIndex = selectedIndex + worldTileWidth;
            TileProperties southProperties = tileArray.get(southIndex).tileProperties;
            southProperties.nTileOccupationType = newTileOccupationType;
            updateTileHelper(southIndex, southProperties);
        }
        if (selectedProperties.eTileType != null) {
            int eastIndex = selectedIndex + 1;
            TileProperties eastProperties = tileArray.get(eastIndex).tileProperties;
            eastProperties.wTileOccupationType = newTileOccupationType;
            updateTileHelper(eastIndex, eastProperties);
        }
        if (selectedProperties.wTileType != null) {
            int westIndex = selectedIndex - 1;
            TileProperties westProperties = tileArray.get(westIndex).tileProperties;
            westProperties.eTileOccupationType = newTileOccupationType;
            updateTileHelper(westIndex, westProperties);
        }
    }

    @Override
    protected void changeTileTypeEditor(int newSelection, tileType tileTypeSelection) {
        if (tileType.tileOccupationType.contains(tileTypeSelection)) {
            updateTileTypeRoad(newSelection, tileTypeSelection);
        } else {
            super.changeTileTypeEditor(newSelection, tileTypeSelection);
        }
    }

    @Override
    public ModelInfo tileSetAlgorithm(TileProperties properties) {
        switch (properties.tileOccupationType) {
            case BUILDING: // 38
                return new ModelInfo(38, 0);
            case TUNNEL: // 37
                checkModel = rotationElevationByPriorityEdgeNSEW(37, 1, 3, 0, 2, properties.elevation, properties.neighbourElevation);
                if (checkModel != null) {
                    return checkModel;
                }
                return new ModelInfo(37, 0);

            case BRIDGE: // 5, 11, 12, 17, 19, 23, 30, 31,
                // 5 ask if sand ONLY NEEDED IF SEPERATE TILE (otherwise will be caught by ocean)

                // 11 ask if ocean
                if (properties.tileType == tileType.OCEAN) {
                    if (properties.nTileOccupationType == tileType.BRIDGE || properties.sTileOccupationType == tileType.BRIDGE) {
                        // NS
                        return new ModelInfo(11, 1);
                    } else {
                        // EW
                        return new ModelInfo(11, 0);
                    }
                }

                // 17 ask if river
                if (properties.tileType == tileType.RIVER) {
                    checkModel = rotationByPriorityEdgeNSEW(17, 0, 0, 1, 1, tileType.RIVER, properties.nTileType, properties.sTileType, properties.eTileType, properties.wTileType);
                    if (checkModel != null) {
                        return checkModel;
                    }
                }

                // 19, 23, 30, 31 land bridge
                // ask if elevated 23
                checkModel = rotationElevationByPriorityEdgeNSEW(23, 1, 3, 0, 2, properties.elevation, properties.neighbourElevation);
                if (checkModel != null) {
                    return checkModel;
                }

                // ask if ocean beside 12
                checkModel = rotationByPriorityEdgeNSEW(12, 3, 1, 2, 0, tileType.OCEAN, properties.nTileType, properties.sTileType, properties.eTileType, properties.wTileType);
                if (checkModel != null) {
                    return checkModel;
                }
                // ask if road beside 30
                if (properties.nTileOccupationType == tileType.BRIDGE && properties.sTileOccupationType == tileType.BRIDGE && (properties.eTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.ROAD)) {
                    // NS
                    return new ModelInfo(30, 1);
                } else if (properties.eTileOccupationType == tileType.BRIDGE && properties.wTileOccupationType == tileType.BRIDGE && (properties.nTileOccupationType == tileType.ROAD || properties.sTileOccupationType == tileType.ROAD)) {
                    // NS
                    return new ModelInfo(30, 0);
                }

                // 19 bridge on both side
                if (properties.nTileOccupationType == tileType.BRIDGE && properties.sTileOccupationType == tileType.BRIDGE) {
                    // NS
                    return new ModelInfo(19, 1);
                } else if (properties.eTileOccupationType == tileType.BRIDGE && properties.wTileOccupationType == tileType.BRIDGE) {
                    // NS
                    return new ModelInfo(19, 0);
                }

                // Put this tileIndex = 31 3 times because:
                // 1. connects directionally to road only
                // 2. connects directionally to bridge only
                // 3. else case (no notihin)
                checkModel = rotationByPriorityEdgeNSEW(31, 3, 1, 2, 0, tileType.ROAD, properties.nTileOccupationType, properties.sTileOccupationType, properties.eTileOccupationType, properties.wTileOccupationType);
                if (checkModel != null) {
                    return checkModel;
                }
                checkModel = rotationByPriorityEdgeNSEW(31, 1, 3, 0, 2, tileType.BRIDGE, properties.nTileOccupationType, properties.sTileOccupationType, properties.eTileOccupationType, properties.wTileOccupationType);
                if (checkModel != null) {
                    return checkModel;
                }
                return new ModelInfo(31, 0);

            case ROAD: // 24, 25 - 29

                checkModel = rotationElevationByPriorityEdgeNSEW(24, 3, 1, 2, 0, properties.elevation, properties.neighbourElevation);
                if (checkModel != null) {
                    return checkModel;
                }

                if (properties.nTileOccupationType == tileType.ROAD || properties.nTileOccupationType == tileType.BRIDGE) {
                    if (properties.sTileOccupationType == tileType.ROAD || properties.sTileOccupationType == tileType.BRIDGE) {
                        if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE) {
                            if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                                // NSEW
                                return new ModelInfo(29, 0);
                            }
                            // NSE
                            return new ModelInfo(28, 3);
                        } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                            // NSW
                            return new ModelInfo(28, 1);
                        }
                        // NS
                        return new ModelInfo(27, 1);
                    } else if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE) {
                        if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                            // NEW
                            return new ModelInfo(28, 0);
                        }
                        // NE
                        return new ModelInfo(26, 3);
                    } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                        // NW
                        return new ModelInfo(26, 0);
                    }
                    // N
                    return new ModelInfo(25, 1);
                } else if (properties.sTileOccupationType == tileType.ROAD || properties.sTileOccupationType == tileType.BRIDGE) {
                        if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE) {
                            if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                                // SEW
                                return new ModelInfo(28, 2);
                            }
                            // SE
                            return new ModelInfo(26, 2);
                        } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                            // SW
                            return new ModelInfo(26, 1);
                        }
                    // S
                    return new ModelInfo(25, 3);
                } else if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE) {
                    if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                        // EW
                        return new ModelInfo(27, 0);
                    }
                    // E
                    return new ModelInfo(25, 0);
                } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE) {
                    // W
                    return new ModelInfo(25, 2);
                }

                // default road direction
                return new ModelInfo(25, 0);
        }
        return super.tileSetAlgorithm(properties);
    }

    private ModelInfo rotationByPriorityEdgeNSEW(int tileIndex, int nrot, int srot, int erot, int wrot, tileType type, tileType nType, tileType sType, tileType eType, tileType wType) {
        if (nType == type){
            // N
            return new ModelInfo(tileIndex, nrot);
        } else if (sType == type) {
            // S
            return new ModelInfo(tileIndex, srot);
        } else if (eType == type) {
            // E
            return new ModelInfo(tileIndex, erot);
        } else if (wType == type) {
            // W
            return new ModelInfo(tileIndex, wrot);
        }
        return null;
    }

    private ModelInfo rotationElevationByPriorityEdgeNSEW(int tileIndex, int nrot, int srot, int erot, int wrot, int elevation, Vector4 neighbourElevation) {
        if (neighbourElevation.x > elevation){
            // N
            return new ModelInfo(tileIndex, nrot);
        } else if (neighbourElevation.y > elevation) {
            // S
            return new ModelInfo(tileIndex, srot);
        } else if (neighbourElevation.z > elevation) {
            // E
            return new ModelInfo(tileIndex, erot);
        } else if (neighbourElevation.w > elevation) {
            // W
            return new ModelInfo(tileIndex, wrot);
        } else {
            return null;
        }

    }

}






/* ----------------------------------
// SUPPLEMENTARY SHIT

// GO THROUGH ALL TILES TEST ---------- VV
                //testAllTilesIndex++;
                //return new ModelInfo((testAllTilesIndex % 39), 0);
                // ----- ^^

This is supplementary NSEW if statements

if (properties.nTileType == tileType.ROAD) {
    if (properties.sTileType == tileType.ROAD) {
        if (properties.eTileType == tileType.ROAD) {
            if (properties.wTileType == tileType.ROAD) {
                // NSEW
            }
            // NSE
        } else if (properties.wTileType == tileType.ROAD) {
            // NSW
        }
        // NS
    } else if (properties.eTileType == tileType.ROAD) {
        if (properties.wTileType == tileType.ROAD) {
            // NEW
        }
        // NE
    } else if (properties.wTileType == tileType.ROAD) {
        // NW
    }
    // N
} else if (properties.sTileType == tileType.ROAD) {
        if (properties.eTileType == tileType.ROAD) {
            if (properties.wTileType == tileType.ROAD) {
                // SEW
            }
            // SE
        } else if (properties.wTileType == tileType.ROAD) {
            // SW
        }
    // S
} else if (properties.eTileType == tileType.ROAD) {
    if (properties.wTileType == tileType.ROAD) {
        // EW
    }
    // E
} else if (properties.wTileType == tileType.ROAD) {
    // W
}
 */
