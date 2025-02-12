package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;

public class GameWorldManager extends EditorWorldManager {

    ModelInfo checkModel = null;
    protected Array<String> TILE_MODEL_NAMES_UPPERCASE = new Array<>();
    int rotationSelection = 0;

    public GameWorldManager(String fileName, String newCityName) {
        super();
        setNamesToUppercase();
        islandPrefs = Gdx.app.getPreferences("islandWorlds"); // FOR LOADING NEW FILE ONLY
        LoadedFileInfo loadedFileInfo = loadIsland(fileName);
        init(newCityName, loadedFileInfo.width, loadedFileInfo.depth, loadedFileInfo.tArray, loadedFileInfo.eArray, loadedFileInfo.oArray, loadedFileInfo.hArray, loadedFileInfo.rArray);
        islandPrefs = Gdx.app.getPreferences("CityWorlds");
    }

    public GameWorldManager(String fileName) {
        super();
        setNamesToUppercase();
        islandPrefs = Gdx.app.getPreferences("CityWorlds");
        LoadedFileInfo loadedFileInfo = loadIsland(fileName);
        init(fileName, loadedFileInfo.width, loadedFileInfo.depth, loadedFileInfo.tArray, loadedFileInfo.eArray, loadedFileInfo.oArray, loadedFileInfo.hArray, loadedFileInfo.rArray);
    }

    private void setNamesToUppercase() {
        for (int i = 0; i < TILE_MODEL_NAMES.size; i++) {
            TILE_MODEL_NAMES_UPPERCASE.add(TILE_MODEL_NAMES.get(i).toUpperCase());
        }
    }

    @Override
    public LoadedFileInfo loadIsland(String fileName) {
        LoadedFileInfo currFileInfo = super.loadIsland(fileName);
        Array<tileType> oArray = new Array<>();
        Array<Integer> hArray = new Array<>();
        Array<Integer> rArray = new Array<>();
        int size = currFileInfo.width * currFileInfo.depth;
        for (int i = 0; i < size; i++) {
            oArray.add(tileType.valueOf(islandPrefs.getString(fileName + "O" + Integer.toString(i), tileType.UNOCCUPIED.toString())));
            hArray.add(islandPrefs.getInteger(fileName + "H" + Integer.toString(i), -1));
            rArray.add(islandPrefs.getInteger(fileName + "R" + Integer.toString(i), 0));
        }
        return new LoadedFileInfo(currFileInfo.width, currFileInfo.depth, currFileInfo.tArray, currFileInfo.eArray, oArray, hArray, rArray);
    }

    @Override
    public void saveIsland() {
        super.saveIsland();
        // additional occupation type provided.
        for (int i = 0; i < tileArray.size; i++) {
            TileProperties props = tileArray.get(i).tileProperties;
            islandPrefs.putString(worldName + "O" + Integer.toString(i), props.tileOccupationType.toString());
            if (props.tileOccupationHead != -1) {
                islandPrefs.putInteger(worldName + "H" + Integer.toString(i), props.tileOccupationHead);
            }
            if (props.occupationRotation != 0) {
                islandPrefs.putInteger(worldName + "R" + Integer.toString(i), props.occupationRotation);
            }
        }
        islandPrefs.flush();
    }

    @Override
    protected boolean isAnyChangeValid(TileProperties currTile) {
        return currTile.tileOccupationType == tileType.UNOCCUPIED;
    }

    protected boolean isBuildValid(TileProperties currTile) {
        return currTile.tileType == tileType.LAND || currTile.tileType == tileType.SAND || tileTypeSelection == tileType.BRIDGE;
    }

    protected boolean isBulldozerValid(TileProperties currTile) {
        return currTile.tileOccupationType != tileType.UNOCCUPIED;
    }


    protected Array<Integer> getOneByOneIndexArray(int currIndex, int rotation) {
        return new Array<>(new Integer[]{currIndex});
    }

    protected Array<Integer> getTwoByOneIndexArray(int currIndex, int rotation) {
        switch (rotation) {
            case 0:
                return new Array<>(new Integer[]{currIndex, currIndex + 1});
            case 1:
                return new Array<>(new Integer[]{currIndex, currIndex - worldTileWidth});
            case 2:
                return new Array<>(new Integer[]{currIndex, currIndex - 1});
            default:
                return new Array<>(new Integer[]{currIndex, currIndex + worldTileWidth});
        }
    }

    protected Array<Integer> getTwoByTwoIndexArray(int currIndex, int rotation) {
        switch (rotation) {
            case 0:
                return new Array<>(new Integer[]{currIndex, currIndex + 1, currIndex + worldTileWidth, currIndex + worldTileWidth + 1});
            case 1:
                return new Array<>(new Integer[]{currIndex, currIndex + 1, currIndex - worldTileWidth, currIndex - worldTileWidth + 1});
            case 2:
                return new Array<>(new Integer[]{currIndex, currIndex - 1, currIndex - worldTileWidth, currIndex - worldTileWidth - 1});
            default:
                return new Array<>(new Integer[]{currIndex, currIndex - 1, currIndex + worldTileWidth, currIndex + worldTileWidth - 1});
        }
    }

    protected Array<Integer> getFourByTwoIndexArray(int currIndex, int rotation) {
        switch (rotation) {
            case 0:
                return new Array<>(new Integer[]{currIndex, currIndex + 1, currIndex + 2, currIndex + 3, currIndex + worldTileWidth, currIndex + worldTileWidth + 1, currIndex + worldTileWidth + 2, currIndex + worldTileWidth + 3});
            case 1:
                return new Array<>(new Integer[]{currIndex, currIndex + 1, currIndex - worldTileWidth, currIndex - worldTileWidth + 1, currIndex - 2 * worldTileWidth, currIndex - 2 * worldTileWidth + 1, currIndex - 3 * worldTileWidth, currIndex - 3 * worldTileWidth + 1});
            case 2:
                return new Array<>(new Integer[]{currIndex, currIndex - 1, currIndex - 2, currIndex - 3, currIndex - worldTileWidth, currIndex - worldTileWidth - 1, currIndex - worldTileWidth - 2, currIndex - worldTileWidth - 3});
            default:
                return new Array<>(new Integer[]{currIndex, currIndex - 1, currIndex + worldTileWidth, currIndex + worldTileWidth - 1, currIndex + 2 * worldTileWidth, currIndex + 2 * worldTileWidth - 1, currIndex + 3 * worldTileWidth, currIndex + 3 * worldTileWidth - 1});
        }
    }

    protected Array<Integer> removeInvalidIndices(Array<Integer> array) {
        Array<Integer> newArray = new Array<>();
        for (int item : array) {
            if (item >= 0 && item < worldTileWidth * worldTileDepth) {
                newArray.add(item);
            }
        }
        return newArray;
    }

    @Override
    protected void hoverHighlightEditor(int newHover, tileType tileTypeSelection) {
        TileProperties headTileProperties = tileArray.get(newHover).tileProperties;
        if (tileTypeSelection == tileType.UNOCCUPIED) {
            if (isBulldozerValid(headTileProperties)) {
                highlight(newHover, Color.GREEN);
                isChangeValidFinal = true;
            } else {
                highlight(newHover, Color.ORANGE);
                isChangeValidFinal = false;
            }
        } else if (tileType.tileOccupationType.contains(tileTypeSelection)) { // Building Button Selected
            Array<Integer> indicesToHighlight;
            if (tileType.twoByOneBuildingType.contains(tileTypeSelection)) {
                indicesToHighlight = getTwoByOneIndexArray(newHover, rotationSelection);
            } else if (tileType.twoByTwoBuildingType.contains(tileTypeSelection)) {
                indicesToHighlight = getTwoByTwoIndexArray(newHover, rotationSelection);
            } else if (tileType.fourByTwoBuildingType.contains(tileTypeSelection)) {
                indicesToHighlight = getFourByTwoIndexArray(newHover, rotationSelection);
            } else {
                indicesToHighlight = getOneByOneIndexArray(newHover, rotationSelection);
            }
            indicesToHighlight = removeInvalidIndices(indicesToHighlight);
            int changeIsValidCount = 0;
            for (int indexToHighlight : indicesToHighlight) {
                TileProperties indexedTileProperties = tileArray.get(indexToHighlight).tileProperties;
                // Next 4 lines makes sure whole building is highlighted when highlighting extension tile (would otherwise not appear)
                if (indexedTileProperties.tileOccupationType == tileType.EXTENSION) {
                    if (!indicesToHighlight.contains(indexedTileProperties.tileOccupationHead, false)) {
                        indicesToHighlight.add(indexedTileProperties.tileOccupationHead);
                    }
                }

                if (isAnyChangeValid(indexedTileProperties) && isBuildValid(indexedTileProperties)) {
                    highlight(indexToHighlight, Color.GREEN);
                    changeIsValidCount++;
                } else {
                    highlight(indexToHighlight, Color.ORANGE);
                }
            }
            isChangeValidFinal = changeIsValidCount >= indicesToHighlight.size;
        } else {
            super.hoverHighlightEditor(newHover, tileTypeSelection);
        }
    }


    @Override
    protected TileProperties generateHelperPropertiesOnly(int width, int depth, Array<tileType> tileTypes, Array<Integer> elevations, Array<tileType> tileOccupationTypes, Array<Integer> tileOccupationHeads, Array<Integer> occupationRotations) {
        TileProperties currProperties = super.generateHelperPropertiesOnly(width, depth, tileTypes, elevations, tileOccupationTypes, tileOccupationHeads, occupationRotations);
        int currentIndex = depth * worldTileWidth + width; // equiv to currentIndex++ iteration
        int nIndex = currentIndex - worldTileWidth;
        int sIndex = currentIndex + worldTileWidth;
        int eIndex = currentIndex + 1;
        int wIndex = currentIndex - 1;

        currProperties.tileOccupationType = tileOccupationTypes.get(currentIndex);
        currProperties.tileOccupationHead = tileOccupationHeads.get(currentIndex);
        currProperties.occupationRotation = occupationRotations.get(currentIndex);

        // NSEW occupation type generation
        if (depth == 0) {
            currProperties.nTileOccupationType = null;
            currProperties.sTileOccupationType = tileOccupationTypes.get(sIndex);
        } else if (depth >= worldTileDepth - 1) {
            currProperties.sTileOccupationType = null;
            currProperties.nTileOccupationType = tileOccupationTypes.get(nIndex);
        } else {
            currProperties.nTileOccupationType = tileOccupationTypes.get(nIndex);
            currProperties.sTileOccupationType = tileOccupationTypes.get(sIndex);
        }
        if (width == 0) {
            currProperties.wTileOccupationType = null;
            currProperties.eTileOccupationType = tileOccupationTypes.get(eIndex);
        } else if (width == worldTileWidth - 1) {
            currProperties.eTileOccupationType = null;
            currProperties.wTileOccupationType = tileOccupationTypes.get(wIndex);
        } else {
            currProperties.eTileOccupationType = tileOccupationTypes.get(eIndex);
            currProperties.wTileOccupationType = tileOccupationTypes.get(wIndex);
        }

        // NSEW occupationHeads generation
        if (depth == 0) {
            currProperties.nTileOccupationHead = -1;
            currProperties.sTileOccupationHead = tileOccupationHeads.get(sIndex);
        } else if (depth >= worldTileDepth - 1) {
            currProperties.sTileOccupationHead = -1;
            currProperties.nTileOccupationHead = tileOccupationHeads.get(nIndex);
        } else {
            currProperties.nTileOccupationHead = tileOccupationHeads.get(nIndex);
            currProperties.sTileOccupationHead = tileOccupationHeads.get(sIndex);
        }
        if (width == 0) {
            currProperties.wTileOccupationHead = -1;
            currProperties.eTileOccupationHead = tileOccupationHeads.get(eIndex);
        } else if (width == worldTileWidth - 1) {
            currProperties.eTileOccupationHead = -1;
            currProperties.wTileOccupationHead = tileOccupationHeads.get(wIndex);
        } else {
            currProperties.eTileOccupationHead = tileOccupationHeads.get(eIndex);
            currProperties.wTileOccupationHead = tileOccupationHeads.get(wIndex);
        }

        return currProperties;
    }

    public void updateTileTypeOccupation(int selectedIndex, tileType newTileOccupationType, int occupationHead, int occupationRotation) {
        if (!tileType.tileBuildingType.contains(newTileOccupationType)) { // just because I'd rather non-buildings have rotation = 0;
            occupationRotation = 0;
        }
        TileProperties selectedProperties = tileArray.get(selectedIndex).tileProperties;
        selectedProperties.tileOccupationType = newTileOccupationType;
        selectedProperties.tileOccupationHead = occupationHead;
        selectedProperties.occupationRotation = occupationRotation;
        setTile(selectedIndex, selectedProperties);
        updateEdgeCases(selectedIndex, newTileOccupationType, occupationHead);
    }

    private void updateEdgeCases(int selectedIndex, tileType newTileOccupationType, int occupationHead) {
        TileProperties selectedProperties = tileArray.get(selectedIndex).tileProperties;
        if (selectedProperties.nTileType != null) {
            int northIndex = selectedIndex - worldTileWidth;
            TileProperties northProperties = tileArray.get(northIndex).tileProperties;
            northProperties.sTileOccupationType = newTileOccupationType; // in relation to the north tile, the south tile is changing
            northProperties.sTileOccupationHead = occupationHead;
            setTile(northIndex, northProperties);
        }
        if (selectedProperties.sTileType != null) {
            int southIndex = selectedIndex + worldTileWidth;
            TileProperties southProperties = tileArray.get(southIndex).tileProperties;
            southProperties.nTileOccupationType = newTileOccupationType;
            southProperties.nTileOccupationHead = occupationHead;
            setTile(southIndex, southProperties);
        }
        if (selectedProperties.eTileType != null) {
            int eastIndex = selectedIndex + 1;
            TileProperties eastProperties = tileArray.get(eastIndex).tileProperties;
            eastProperties.wTileOccupationType = newTileOccupationType;
            eastProperties.wTileOccupationHead = occupationHead;
            setTile(eastIndex, eastProperties);
        }
        if (selectedProperties.wTileType != null) {
            int westIndex = selectedIndex - 1;
            TileProperties westProperties = tileArray.get(westIndex).tileProperties;
            westProperties.eTileOccupationType = newTileOccupationType;
            westProperties.eTileOccupationHead = occupationHead;
            setTile(westIndex, westProperties);
        }
    }

    @Override
    protected void changeTileTypeEditor(int newSelection, tileType tileTypeSelection) {
        if (isChangeValidFinal) {
            if (tileTypeSelection == tileType.UNOCCUPIED) {
                Array<Integer> indicesToChange;
                TileProperties headProperties = tileArray.get(newSelection).tileProperties;
                 if (tileType.twoByOneBuildingType.contains(headProperties.tileOccupationType)) {
                    indicesToChange = getTwoByOneIndexArray(newSelection, headProperties.occupationRotation);
                } else if (tileType.twoByTwoBuildingType.contains(headProperties.tileOccupationType)) {
                    indicesToChange = getTwoByTwoIndexArray(newSelection, headProperties.occupationRotation);
                } else if (tileType.fourByTwoBuildingType.contains(headProperties.tileOccupationType)){
                    indicesToChange = getFourByTwoIndexArray(newSelection, headProperties.occupationRotation);
                } else {
                     indicesToChange = getOneByOneIndexArray(newSelection, headProperties.occupationRotation);
                }
                for (int indexToChange : indicesToChange) {
                    updateTileTypeOccupation(indexToChange, tileTypeSelection, -1, 0);
                }

            } else if (tileType.tileOccupationType.contains(tileTypeSelection)) {
                updateTileTypeOccupation(newSelection, tileTypeSelection, newSelection, rotationSelection);
                for (int i = 1; i < indexOfMaterials.size; i++) {
                    updateTileTypeOccupation(indexOfMaterials.get(i), tileType.EXTENSION, newSelection, 0);
                }
            } else {
                super.changeTileTypeEditor(newSelection, tileTypeSelection);
            }
        }
    }

    @Override
    public ModelInfo tileSetAlgorithm(TileProperties properties) {
        if (tileType.tileBuildingType.contains(properties.tileOccupationType)) {
            int modelIndex = TILE_MODEL_NAMES_UPPERCASE.indexOf(properties.tileOccupationType.toString(), false);
            return new ModelInfo(modelIndex, properties.occupationRotation);
        }
        switch (properties.tileOccupationType) {
            case EXTENSION:
                return new ModelInfo(0, 0);
            case TUNNEL: // 37
                checkModel = rotationElevationByPriorityEdgeNSEW(37, 1, 3, 0, 2, properties.elevation, properties.neighbourElevation);
                if (checkModel != null) {return checkModel;}
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
                    if (checkModel != null) { return checkModel; }
                }

                // 19, 23, 30, 31 land bridge
                // ask if elevated (23)
                checkModel = rotationElevationByPriorityEdgeNSEW(23, 1, 3, 0, 2, properties.elevation, properties.neighbourElevation);
                if (checkModel != null) { return checkModel; }

                // ask if ocean beside (12)  // BUG when ocean on 2 sides will prioritize north no matter what
                checkModel = rotationByPriorityEdgeNSEW(12, 3, 1, 2, 0, tileType.OCEAN, properties.nTileType, properties.sTileType, properties.eTileType, properties.wTileType);
                if (checkModel != null) { return checkModel; }

                // ask if road beside (30)
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

                if (properties.nTileOccupationType == tileType.ROAD || properties.nTileOccupationType == tileType.BRIDGE ||  properties.nTileOccupationType == tileType.TUNNEL) {
                    if (properties.sTileOccupationType == tileType.ROAD || properties.sTileOccupationType == tileType.BRIDGE ||  properties.sTileOccupationType == tileType.TUNNEL) {
                        if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE ||  properties.eTileOccupationType == tileType.TUNNEL) {
                            if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                                // NSEW
                                return new ModelInfo(29, 0);
                            }
                            // NSE
                            return new ModelInfo(28, 3);
                        } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                            // NSW
                            return new ModelInfo(28, 1);
                        }
                        // NS
                        return new ModelInfo(27, 1);
                    } else if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE ||  properties.eTileOccupationType == tileType.TUNNEL) {
                        if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                            // NEW
                            return new ModelInfo(28, 0);
                        }
                        // NE
                        return new ModelInfo(26, 3);
                    } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                        // NW
                        return new ModelInfo(26, 0);
                    }
                    // N
                    return new ModelInfo(25, 1);
                } else if (properties.sTileOccupationType == tileType.ROAD || properties.sTileOccupationType == tileType.BRIDGE ||  properties.sTileOccupationType == tileType.TUNNEL) {
                        if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE ||  properties.eTileOccupationType == tileType.TUNNEL) {
                            if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                                // SEW
                                return new ModelInfo(28, 2);
                            }
                            // SE
                            return new ModelInfo(26, 2);
                        } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                            // SW
                            return new ModelInfo(26, 1);
                        }
                    // S
                    return new ModelInfo(25, 3);
                } else if (properties.eTileOccupationType == tileType.ROAD || properties.eTileOccupationType == tileType.BRIDGE ||  properties.eTileOccupationType == tileType.TUNNEL) {
                    if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                        // EW
                        return new ModelInfo(27, 0);
                    }
                    // E
                    return new ModelInfo(25, 0);
                } else if (properties.wTileOccupationType == tileType.ROAD || properties.wTileOccupationType == tileType.BRIDGE ||  properties.wTileOccupationType == tileType.TUNNEL) {
                    // W
                    return new ModelInfo(25, 2);
                }

                // default road direction
                return new ModelInfo(25, 0);
        }
        return super.tileSetAlgorithm(properties);
    }

    // Tried less bulky method of instilling direction in models, hasn't been implemented to all tiles
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

    @Override
    public void increaseRotationSelection() {
        if (rotationSelection >= 3) {
            rotationSelection = 0;
        } else {
            rotationSelection++;
        }

    }

    @Override
    public void resetRotationSelection() {
        rotationSelection = 0;
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
