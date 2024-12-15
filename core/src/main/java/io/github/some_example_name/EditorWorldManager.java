package io.github.some_example_name;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.UBJsonReader;

import static io.github.some_example_name.tileType.OCEAN;
import static io.github.some_example_name.tileType.RIVER;
import static io.github.some_example_name.tileType.ELEVATION_DOWN;
import static io.github.some_example_name.tileType.ELEVATION_UP;


public class EditorWorldManager {

    Preferences islandPrefs; // islandSpecific

    protected int worldTileWidth;
    protected int worldTileDepth;
    public String worldName;

    final private float TILE_WIDTH = 8f; // Exact width
    final static private float ELEVATION_HEIGHT = 2f;
    final static int ELEVATION_MAX = 4;

    private Array<ModelInstance> borderTileArray; // model instance because it has no true tile properties
    protected Array<TileInstance> tileArray;
    // TILE_MODEL_NAMES.size = 39 at the moment
    protected static final Array<String> TILE_MODEL_NAMES = new Array<>(new String[]{"dirt", "sand", "sandShortEdgeOceanBend", "sandLongEdgeOceanBend", "sandOceanStraight", "sandOceanBridge", "ocean", "oceanRiver", "oceanShortEdgeLandBend", "oceanLongEdgeLandBend", "oceanLandStraight", "oceanBridge", "oceanBridgeEdge", "riverEnd", "riverBend", "riverStraight", "riverElevation", "riverBridge", "land", "landBridge", "elevationShortEdgeBend", "elevationLongEdgeBend", "elevationStraight", "elevationRoadStraightDownhill", "elevationRoadStraightUphill", "roadEnd", "roadBend", "roadStraight", "roadTheta", "roadCross", "roadBridge", "roadBridgePitched", "roadParkingLot", "parkingLot", "parkingLotDecor", "parkingLotBend", "parkingLotStraight", "tunnel", "building"});
    protected Array<Model> tileModels;

    // Selection Function Variables
    private Material selectionMaterial;
    private Material originalMaterial;
    private boolean isPrevHoverElevation = false;

    protected tileType tileTypeSelection = tileType.LAND; // Starts with land

    public EditorWorldManager(String n, int w, int d) { // new world
        init(n, w, d, new Array<tileType>(), new Array<Integer>());
        islandPrefs = Gdx.app.getPreferences("islandWorlds");
    }

    public EditorWorldManager(String fileName) { // retrieved world, essentially loadIsland function
        islandPrefs = Gdx.app.getPreferences("islandWorlds");
        int width = islandPrefs.getInteger(fileName + "Width");
        int depth = islandPrefs.getInteger(fileName + "Depth");
        int size = width * depth;
        Array<tileType> tArray = new Array<tileType>();
        Array<Integer> eArray = new Array<Integer>();
        for (int i = 0; i < size; i++) {
            tArray.add(tileType.valueOf(islandPrefs.getString(fileName+ "T" + Integer.toString(i))));
            eArray.add(islandPrefs.getInteger(fileName + "E" + Integer.toString(i)));
        }
        init(fileName, width, depth, tArray, eArray);

    }

    public void saveIsland() {
        islandPrefs.putInteger(worldName + "Width", worldTileWidth);
        islandPrefs.putInteger(worldName + "Depth", worldTileDepth);
        for (int i = 0; i < tileArray.size; i++) {
            TileProperties props = tileArray.get(i).tileProperties;
            islandPrefs.putString(worldName + "T" + Integer.toString(i), props.tileType.toString());
            islandPrefs.putInteger(worldName + "E" + Integer.toString(i), props.elevation);
        }
        islandPrefs.flush();
    }

    private void init(String n, int w, int d, Array<tileType> typeArray, Array<Integer> elevArray)  {
        worldName = n;
        worldTileWidth = w;
        worldTileDepth = d;

        if (typeArray.isEmpty()) { // new island case
            for (int depth = 0; depth < worldTileDepth; depth++) {
                for (int width = 0; width < worldTileWidth; width++) {
                    typeArray.add(tileType.LAND);
                    elevArray.add(0);
                }
            }
        }

        // Retrieval of all tile models

        tileModels = new Array<>();
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        for (String fileHandle : TILE_MODEL_NAMES) {
            tileModels.add(modelLoader.loadModel(Gdx.files.getFileHandle("model/" + fileHandle + ".g3db", Files.FileType.Internal)));
            Material modelMaterial = tileModels.peek().materials.get(0);
            modelMaterial.set(new TextureAttribute(TextureAttribute.Diffuse, new Texture("model/basetexture.jpg")));
            // WORKED FIRST FUCKING TRY LETS GO wow that was very accomplishing but took like days to figure out
            // btw I just randomly started guessing how to set textures I am very impressed with my instincts
            modelMaterial.remove(ColorAttribute.Emissive); // The models were glowing white, so I added this line

        }

        // Selection Material Set

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.GOLD));
        originalMaterial = new Material();

        // Tile Array Initiation (World generation)

        generateAllTileProperties(typeArray, elevArray);

        // Border Array Initiation

        borderTileArray = new Array<ModelInstance>();
        for (int depth = -1; depth <= worldTileDepth; depth++) {
            makeBorderTile(-1, depth);
            makeBorderTile(worldTileWidth, depth);
        }
        for (int width = 0; width < worldTileWidth; width++) {
            makeBorderTile(width, -1);
            makeBorderTile(width, worldTileDepth);
        }


    }

    public void selectFunction(int newSelection) {

        if (newSelection >= 0) {
            changeTileTypeEditor(newSelection, tileTypeSelection); // should make a EditorManager & GameManager and put this function in there
        }
    }

    public void hoverFunction(int prevHover, int newHover) {
        if (newHover >= 0) {
            hoverHighlightEditor(prevHover, newHover, tileTypeSelection);
        }
    }

    public void hoverHighlightEditor(int prevHover, int newHover, tileType tileTypeSelection) {
        TileProperties currTile = tileArray.get(newHover).tileProperties;
        if (tileTypeSelection == ELEVATION_UP) {
            hoverElevationUpHelper(prevHover, newHover, currTile);
        } else if (tileTypeSelection == ELEVATION_DOWN) {
            hoverElevationDownHelper(prevHover, newHover, currTile);
        } else if (tileType.tileOccupationType.contains(tileTypeSelection)) { // Occupation button
            if (currTile.tileOccupationType != tileTypeSelection) {
                highlight(prevHover, newHover, Color.GREEN);
            } else {
                highlight(prevHover, newHover, Color.ORANGE);
            }
        } else {
            if (currTile.tileType != tileTypeSelection) {
                highlight(prevHover, newHover, Color.GREEN);
            } else {
                highlight(prevHover, newHover, Color.ORANGE);
            }
        }
    }

    private void hoverElevationUpHelper(int prevHover, int newHover, TileProperties currTile) {
        if (currTile.elevation < ELEVATION_MAX) {
            highlight(prevHover, newHover, Color.GREEN);
        } else {
            highlight(prevHover, newHover, Color.ORANGE);
        }
        /*if (currTile.elevation > currTile.neighbourElevation.x) {
            Material mat = tileArray.get(newHover - worldTileWidth).materials.get(0); // north is gotten
            mat.clear();
            mat.set(ColorAttribute.createDiffuse(Color.GREEN));
        }*/ // chaning the colour of the edges makes me nervous because I'm not sure if it will set the "originalMaterial" to the colour diffused material if new hover becomes one of these edges

    }
    private void hoverElevationDownHelper(int prevHover, int newHover, TileProperties currTile) {
        if (currTile.elevation > 0) {
            highlight(prevHover, newHover, Color.GREEN);
        } else {
            highlight(prevHover, newHover, Color.ORANGE);
        }
    }

    protected void highlight(int prevHover, int newHover, Color color) {
        if (prevHover >= 0) {
            Material mat = tileArray.get(prevHover).materials.get(0);
            mat.clear();
            mat.set(originalMaterial);
            if (isPrevHoverElevation) {
                // ADD IMPLMEMENTATION
            }
        }
        selectionMaterial.set(ColorAttribute.createDiffuse(color));
        Material mat = tileArray.get(newHover).materials.get(0);
        originalMaterial.clear();
        originalMaterial.set(mat);
        mat.clear();
        mat.set(selectionMaterial);
    }

    /*protected void changeTileTypeOldFunctions(int prevSelection, int newSelection, tileType tileTypeSelection) {

        // This is for exchanging model types by cycling through all models
        if (newSelection >= 0) {
            tileArray.set(newSelection, new TileInstance(tileModels.get(testIndex), new ModelInfo(testIndex, 0), new TileProperties(tileType.LAND, 0))); // tileType.LAND IS NOT RIGHT FOR THE MOMENT
            testIndex++;
            TileInstance switchedTile = tileArray.get(newSelection);
            setTileLocation(switchedTile, newSelection % worldTileWidth, (newSelection - (newSelection % worldTileWidth)) / worldTileWidth);
        }
    } */

    protected void changeTileTypeEditor(int newSelection, tileType tileTypeSelection) {  // this should go in EditorManager Class
        // This is for changing the tileType
        if (tileTypeSelection == ELEVATION_UP) {
            updateElevation(newSelection, 1);
            System.out.println("index: " + newSelection + ", it's elevation: " + tileArray.get(newSelection).tileProperties.elevation);
        } else if (tileTypeSelection == ELEVATION_DOWN) {
            updateElevation(newSelection, -1);
        } else if (tileTypeSelection == OCEAN) {
            updateTileTypeOcean(newSelection);
        } else {
            updateTileTypeNonOcean(newSelection, tileTypeSelection);
        }
    }

    public void dispose() {
        for (Model model : tileModels) {
            model.dispose();
        }
    }

    private void setTileLocation(ModelInstance tile, int width, int depth, int elevation) {
        tile.transform.setToTranslation(width * TILE_WIDTH, elevation * ELEVATION_HEIGHT, depth * TILE_WIDTH);
    }

    private void makeBorderTile(int width, int depth) {

        ModelInstance currentModelInstance = new ModelInstance(tileModels.get(6));
        borderTileArray.add(currentModelInstance);
        setTileLocation(currentModelInstance, width, depth, 0);
    }

    public Array<TileInstance> getTileArray() {
        return tileArray;
    }
    public Array<ModelInstance> getBorderTileArray() { return borderTileArray; }

    public void setTileTypeSelection(tileType t) {
        tileTypeSelection = t;
    }

    // _______________ WORLD UPDATING FUNCTIONS

    // TODO: this will need tile occupation types array soon
    public void generateAllTileProperties(Array<tileType> tileTypes, Array<Integer> elevations) { // produces entirely new tile Array based on saved information

        tileArray = new Array<TileInstance>();

        // Start by setting all properties
        for (int depth = 0; depth < worldTileDepth; depth++) {  // FOR REFERENCE (0 = north west, worldTileWidth = north east)
            for (int width = 0; width < worldTileWidth; width++) {

                // current index properties
                int currentIndex = depth * worldTileWidth + width; // equiv to currentIndex++ iteration
                tileType thisTileType = tileTypes.get(currentIndex);
                int thisElevation = elevations.get(currentIndex);

                // adjacent neighbour properties (sets to null if next to edge tile)
                int nIndex = currentIndex - worldTileWidth;
                int sIndex = currentIndex + worldTileWidth;
                int eIndex = currentIndex + 1;
                int wIndex = currentIndex - 1;
                tileType thisNT, thisST, thisET, thisWT;
                Vector4 thisNeighbourElevation = new Vector4(0, 0, 0, 0);
                if (depth == 0) {
                    thisNT = null;
                    thisST = tileTypes.get(sIndex);
                    thisNeighbourElevation.y = elevations.get(sIndex);
                } else if (depth >= worldTileDepth - 1) {
                    thisST = null;
                    thisNT = tileTypes.get(nIndex);
                    thisNeighbourElevation.x = elevations.get(nIndex);
                } else {
                    thisNT = tileTypes.get(nIndex);
                    thisST = tileTypes.get(sIndex);
                    thisNeighbourElevation.x = elevations.get(nIndex);
                    thisNeighbourElevation.y = elevations.get(sIndex);
                }
                if (width == 0) {
                    thisWT = null;
                    thisET = tileTypes.get(eIndex);
                    thisNeighbourElevation.z = elevations.get(eIndex);
                } else if (width == worldTileWidth - 1) {
                    thisET = null;
                    thisWT = tileTypes.get(wIndex);
                    thisNeighbourElevation.w = elevations.get(wIndex);
                } else {
                    thisET = tileTypes.get(eIndex);
                    thisWT = tileTypes.get(wIndex);
                    thisNeighbourElevation.z = elevations.get(eIndex);
                    thisNeighbourElevation.w = elevations.get(wIndex);
                }

                // corner neighbour properties (sets to null if next to edge tile)
                Vector4 thisCornerNeighbourOcean = new Vector4(0, 0, 0, 0); // 0 = false, 1 = true
                Vector4 thisNeighbourCornerElevation = new Vector4(0, 0, 0, 0);
                if (thisNT != null && thisET != null) {
                    if (tileTypes.get(nIndex + 1) == OCEAN) { thisCornerNeighbourOcean.x = 1;}
                    thisNeighbourCornerElevation.x = elevations.get(nIndex + 1);
                } else {
                    thisCornerNeighbourOcean.x = 1; // N-W is null, therefore should be considered OCEAN
                }
                if (thisNT != null && thisWT != null) {
                    if (tileTypes.get(nIndex - 1) == OCEAN) { thisCornerNeighbourOcean.y = 1;}
                    thisNeighbourCornerElevation.y = elevations.get(nIndex - 1);
                }  else {
                    thisCornerNeighbourOcean.y = 1;
                }
                if (thisST != null && thisET != null) {
                    if (tileTypes.get(sIndex + 1) == OCEAN) { thisCornerNeighbourOcean.z = 1;}
                    thisNeighbourCornerElevation.z = elevations.get(sIndex + 1);
                } else {
                    thisCornerNeighbourOcean.z = 1;
                }
                if (thisST != null && thisWT != null) {
                    if (tileTypes.get(sIndex - 1) == OCEAN) { thisCornerNeighbourOcean.w = 1;}
                    thisNeighbourCornerElevation.w = elevations.get(sIndex - 1);
                }  else {
                    thisCornerNeighbourOcean.w = 1;
                }

                TileProperties currentProperties = new TileProperties(thisTileType, tileType.UNOCCUPIED, thisElevation, thisNT, thisST, thisET, thisWT, thisCornerNeighbourOcean, thisNeighbourElevation, thisNeighbourCornerElevation);
//------ // LATER ON TRY RUNNING IT THROUGH UPDATETILEHELPER
                // Using properties, send through tileSetAlgorithm and return modelIndex & rotation
                ModelInfo currentModelInfo = tileSetAlgorithm(currentProperties);
                Model currentModel = tileModels.get(currentModelInfo.modelIndex);
                TileInstance currentInstance = new TileInstance(currentModel, currentModelInfo, currentProperties);
                currentInstance.transform.setToRotation(Vector3.Y, 90 * currentModelInfo.modelRotation);
                tileArray.add(currentInstance);
                setTileLocation(currentInstance, width, depth, currentProperties.elevation);
                currentInstance.transform.rotate(Vector3.Y, 90 * currentModelInfo.modelRotation);
            }
        }

        // compass block (orange = NW)              // REMOVE THIS ______________________________________________
        Material mat = tileArray.get(0).materials.get(0);
        originalMaterial.clear();
        originalMaterial.set(mat);
        mat.clear();
        mat.set(selectionMaterial);
    }

    public void updateTileTypeNonOcean(int selectedIndex, tileType t) { // updates tile type & direct neighbours
        TileProperties selectedProperties = tileArray.get(selectedIndex).tileProperties;
        if (selectedProperties.tileType == OCEAN) {
            updateOceanCorners(selectedIndex, 0);
        }
        selectedProperties.tileType = t;
        updateTileHelper(selectedIndex, selectedProperties);
        // MUST ALSO UPDATE NEIGHBOUR TILES, done
        if (selectedProperties.nTileType != null) {
            int northIndex = selectedIndex - worldTileWidth;
            TileProperties northProperties = tileArray.get(northIndex).tileProperties;
            northProperties.sTileType = t; // in relation to the north tile, the south tile is changing
            updateTileHelper(northIndex, northProperties);
        }
        if (selectedProperties.sTileType != null) {
            int southIndex = selectedIndex + worldTileWidth;
            TileProperties southProperties = tileArray.get(southIndex).tileProperties;
            southProperties.nTileType = t;
            updateTileHelper(southIndex, southProperties);
        }
        if (selectedProperties.eTileType != null) {
            int eastIndex = selectedIndex + 1;
            TileProperties eastProperties = tileArray.get(eastIndex).tileProperties;
            eastProperties.wTileType = t;
            updateTileHelper(eastIndex, eastProperties);
        }
        if (selectedProperties.wTileType != null) {
            int westIndex = selectedIndex - 1;
            TileProperties westProperties = tileArray.get(westIndex).tileProperties;
            westProperties.eTileType = t;
            updateTileHelper(westIndex, westProperties);
        }
        // should work idk tho
    }

    private void updateOceanCorners(int selectedIndex, int isOceanNow) {
        TileProperties selectedProperties = tileArray.get(selectedIndex).tileProperties;
        if (selectedProperties.nTileType != null && selectedProperties.eTileType != null) {
            int northEastIndex = (selectedIndex - worldTileWidth) + 1;
            TileProperties northEastProperties = tileArray.get(northEastIndex).tileProperties;
            northEastProperties.isCornerNeighbourOcean.w = isOceanNow;  // in relation to the north-east tile, the south-west tile is changing
            updateTileHelper(northEastIndex, northEastProperties);
        }
        if (selectedProperties.nTileType != null && selectedProperties.wTileType != null) {
            int northWestIndex = (selectedIndex - worldTileWidth) - 1;
            TileProperties northWestProperties = tileArray.get(northWestIndex).tileProperties;
            northWestProperties.isCornerNeighbourOcean.z = isOceanNow;
            updateTileHelper(northWestIndex, northWestProperties);
        }
        if (selectedProperties.sTileType != null && selectedProperties.eTileType != null) {
            int southEastIndex = (selectedIndex + worldTileWidth) + 1;
            TileProperties southEastProperties = tileArray.get(southEastIndex).tileProperties;
            southEastProperties.isCornerNeighbourOcean.y = isOceanNow;
            updateTileHelper(southEastIndex, southEastProperties);
        }
        if (selectedProperties.sTileType != null && selectedProperties.wTileType != null) {
            int southWestIndex = (selectedIndex + worldTileWidth) - 1;
            TileProperties southWestProperties = tileArray.get(southWestIndex).tileProperties;
            southWestProperties.isCornerNeighbourOcean.x = isOceanNow;
            updateTileHelper(southWestIndex, southWestProperties);
        }
    }

    public void updateTileTypeOcean(int selectedIndex) { // updates tile type & all 8 neighbours (THIS WILL ALSO NEED TO BE CALLED IF IT CHANGES FROM OCEAN TO SOMETHING ELSE)
        // use updateTileTypeNonOcean(); for reference
        updateTileTypeNonOcean(selectedIndex, OCEAN);

        // cornerNeighbourUpdate
        updateOceanCorners(selectedIndex, 1);
    }

    public void updateElevation(int selectedIndex, int elevationIncrement) { // updates elevation & all 8 neighbours
        TileProperties selectedProperties = tileArray.get(selectedIndex).tileProperties;
        if (elevationIncrement == -1 && selectedProperties.elevation <= 0) {
            return; // add throw exception when error dialogues are added
        }
        if (elevationIncrement == 1 && selectedProperties.elevation >= ELEVATION_MAX) {
            return; // add throw exception when error dialogues are added
        }
        if (selectedProperties.nTileType != null && selectedProperties.sTileType != null && selectedProperties.eTileType != null && selectedProperties.wTileType != null) {
            // add throw exception in else case aswell when error dialogues are added
            selectedProperties.elevation += elevationIncrement;
            updateTileHelper(selectedIndex, selectedProperties);

            // Direct Neighbours update
            int northIndex = selectedIndex - worldTileWidth;
            TileProperties northProperties = tileArray.get(northIndex).tileProperties;
            northProperties.neighbourElevation.y += elevationIncrement; // in relation to the north tile, the south tile is changing
            updateTileHelper(northIndex, northProperties);

            int southIndex = selectedIndex + worldTileWidth;
            TileProperties southProperties = tileArray.get(southIndex).tileProperties;
            southProperties.neighbourElevation.x += elevationIncrement;
            updateTileHelper(southIndex, southProperties);

            int eastIndex = selectedIndex + 1;
            TileProperties eastProperties = tileArray.get(eastIndex).tileProperties;
            eastProperties.neighbourElevation.w += elevationIncrement;
            updateTileHelper(eastIndex, eastProperties);

            int westIndex = selectedIndex - 1;
            TileProperties westProperties = tileArray.get(westIndex).tileProperties;
            westProperties.neighbourElevation.z += elevationIncrement;
            updateTileHelper(westIndex, westProperties);

            // cornerNeighbour update
            int northEastIndex = northIndex + 1;
            TileProperties northEastProperties = tileArray.get(northEastIndex).tileProperties;
            northEastProperties.cornerNeighbourElevation.w += elevationIncrement;  // in relation to the north-east tile, the south-west tile is changing
            updateTileHelper(northEastIndex, northEastProperties);

            int northWestIndex = northIndex - 1;
            TileProperties northWestProperties = tileArray.get(northWestIndex).tileProperties;
            northWestProperties.cornerNeighbourElevation.z += elevationIncrement;
            updateTileHelper(northWestIndex, northWestProperties);

            int southEastIndex = southIndex + 1;
            TileProperties southEastProperties = tileArray.get(southEastIndex).tileProperties;
            southEastProperties.cornerNeighbourElevation.y += elevationIncrement;  // in relation to the north-east tile, the south-west tile is changing
            updateTileHelper(southEastIndex, southEastProperties);

            int southWestIndex = southIndex - 1;
            TileProperties southWestProperties = tileArray.get(southWestIndex).tileProperties;
            southWestProperties.cornerNeighbourElevation.x += elevationIncrement;
            updateTileHelper(southWestIndex, southWestProperties);

        }
    }

    public void updateTileHelper(int selectedIndex, TileProperties selectedProperties) {
        ModelInfo currentModelInfo = tileSetAlgorithm(selectedProperties);
        //System.out.println("Index: " + selectedIndex);
        //System.out.println("Model info: " + currentModelInfo.modelIndex + ", " + currentModelInfo.modelRotation);
        Model currentModel = tileModels.get(currentModelInfo.modelIndex);
        TileInstance currentInstance = new TileInstance(currentModel, currentModelInfo, selectedProperties);
        tileArray.set(selectedIndex, currentInstance);
        setTileLocation(currentInstance, selectedIndex % worldTileWidth, (selectedIndex - (selectedIndex % worldTileWidth)) / worldTileWidth, selectedProperties.elevation);  // width = selectedIndex % worldTileWidth, depth = selectedIndex - (selectedIndex % worldTileWidth) / worldTileWidth
        currentInstance.transform.rotate(Vector3.Y, 90 * currentModelInfo.modelRotation);
    }

    // _________________ TILE SET ALGORITHM

    // TODO
    public ModelInfo tileSetAlgorithm(TileProperties properties) {
        // return new ModelInfo(18, 0); // stub, always returns land

        switch (properties.tileType) { // IGNORING ROAD & BRIDGE TILES
            case DIRT:
                // 0
                return new ModelInfo(0, 0);
            case SAND:
                // 1 - 4 (5)     1, 2 x 4rot, 3 x 4rot, 4 x 4rot (5 x 4rot)
                if (properties.nTileType == null || properties.nTileType == OCEAN) {
                    /*if (properties.sTileType == OCEAN) {
                        if (properties.eTileType == OCEAN) {
                            if (properties.eTileType == OCEAN) {
                                // NSEW
                            }
                            // NSE
                        }
                        if (properties.wTileType == OCEAN) {
                            // NSW
                        }
                        // NS   */

                    if (properties.eTileType == null || properties.eTileType == OCEAN) {
                        /*if (properties.wTileType == OCEAN) {
                            // NEW
                        }*/
                        // NE
                        return new ModelInfo(2, 0);
                    }
                    if (properties.wTileType == null || properties.wTileType == OCEAN) {
                        // NW
                        return new ModelInfo(2, 1);
                    }
                    // N
                    return new ModelInfo(4, 1);
                }

                if (properties.sTileType == null || properties.sTileType == OCEAN) {
                    if (properties.eTileType == null || properties.eTileType == OCEAN) {
                        /*if (properties.wTileType == OCEAN) {
                            // SEW
                        }*/
                        // SE
                        return new ModelInfo(2, 3);
                    }
                    if (properties.wTileType == null || properties.wTileType == OCEAN) {
                        // SW
                        return new ModelInfo(2, 2);
                    }
                    // S
                    return new ModelInfo(4, 3);
                }

                if (properties.eTileType == null || properties.eTileType == OCEAN) {
                    /*if (properties.wTileType == OCEAN) {
                        // EW
                    } */
                    // E
                    return new ModelInfo(4, 0);
                }

                if (properties.wTileType == null || properties.wTileType == OCEAN) {
                    // W
                    return new ModelInfo(4, 2);
                }

                // N/A OCEAN in directNeighbours

                if (properties.isCornerNeighbourOcean.x == 1) {
                    // N-E
                    return new ModelInfo(3, 0);
                }
                if (properties.isCornerNeighbourOcean.y == 1) {
                    // N-W
                    return new ModelInfo(3, 1);
                }
                if (properties.isCornerNeighbourOcean.z == 1) {
                    // S-E
                    return new ModelInfo(3, 3);
                }
                if (properties.isCornerNeighbourOcean.w == 1) {
                    // S-W
                    return new ModelInfo(3, 2);
                }
                // N/A OCEAN in directNeighbours & N/A cornerNeighbourOcean
                return new ModelInfo(1, 0);

            case OCEAN:
                // 6

                return new ModelInfo(6, 0); // ocean stub

            case RIVER:
                // 13 - 17 (+ 7)
                // river elevation (elevation always takes priority over ocean transition)
                if (properties.neighbourElevation.x > properties.elevation) {
                    // elev-N
                    return new ModelInfo(16, 1);
                } else if (properties.neighbourElevation.y > properties.elevation) {
                    // elev-S
                    return new ModelInfo(16, 3);
                }
                if (properties.neighbourElevation.z > properties.elevation) {
                    // elev-E
                    return new ModelInfo(16, 0);
                } else if (properties.neighbourElevation.w > properties.elevation) {
                    // elev-W
                    return new ModelInfo(16, 2);
                }

                if (properties.nTileType == null || properties.nTileType == OCEAN) {
                    return new ModelInfo(7, 2);
                } else if (properties.sTileType == null || properties.sTileType == OCEAN) {
                    return new ModelInfo(7, 0);
                } else if (properties.eTileType == null || properties.eTileType == OCEAN) {
                    return new ModelInfo(7, 1);
                } else if (properties.wTileType == null || properties.wTileType == OCEAN) {
                    return new ModelInfo(7, 3);
                }

                if (properties.nTileType == RIVER) {
                    // Has N and doesn't matter its other tile types
                    // elevation

                    if (properties.sTileType == RIVER) {
                        /*if (properties.eTileType == RIVER) {
                            if (properties.eTileType == RIVER) {
                                // NSEW
                            }
                            // NSE
                        }
                        if (properties.wTileType == RIVER) {
                            // NSW
                        } */

                        // NS
                        return new ModelInfo(15, 1);
                    }
                if (properties.eTileType == RIVER) {
                    /*if (properties.wTileType == RIVER) {
                        // NEW
                    }*/
                    // NE
                    return new ModelInfo(14, 1);
                }
                if (properties.wTileType == RIVER) {
                    // NW
                    return new ModelInfo(14, 2);
                }
                // N
                return new ModelInfo(13, 3);
            }

                if (properties.sTileType == RIVER) {
                    if (properties.eTileType == RIVER) {
                        /*if (properties.wTileType == RIVER) {
                            // SEW
                        }*/
                        // SE
                        return new ModelInfo(14, 0);
                    }
                    if (properties.wTileType == RIVER) {
                        // SW
                        return new ModelInfo(14, 3);
                    }
                    // S
                    return new ModelInfo(13, 1);
                }

                if (properties.eTileType == RIVER) {
                    if (properties.wTileType == RIVER) {
                        // EW
                        return new ModelInfo(15, 0);
                    }
                    // E
                    return new ModelInfo(13, 2);
                }
                if (properties.wTileType == RIVER) {
                    // W
                    return new ModelInfo(13, 0);
                }
                // N/A RIVER in directNeighbours
                break; // It will have dirt

            case LAND:
                // 18 - 22, 8 - 10 (11 - 12)

                // elevation transitions
                if (properties.neighbourElevation.x > properties.elevation) {
                    if (properties.neighbourElevation.z > properties.elevation) {
                        //elev-NE
                        return new ModelInfo(21, 2);
                    } else if (properties.neighbourElevation.w > properties.elevation) {
                        // elev-NW
                        return new ModelInfo(21, 3);
                    }
                    // elev-N
                    return new ModelInfo(22, 3);
                } else if (properties.neighbourElevation.y > properties.elevation) {
                    if (properties.neighbourElevation.z > properties.elevation) {
                        // elev-SE
                        return new ModelInfo(21, 1);
                    } else if (properties.neighbourElevation.x > properties.elevation) {
                        // elev-SW
                        return new ModelInfo(21, 0);
                    }
                    // elev-S
                    return new ModelInfo(22, 1);
                }
                if (properties.neighbourElevation.z > properties.elevation) {
                    // elev-E
                    return new ModelInfo(22, 2);
                } else if (properties.neighbourElevation.w > properties.elevation) {
                    // elev-W
                    return new ModelInfo(22, 0);
                }

                // corner-elevation transitions
                if (properties.cornerNeighbourElevation.x > properties.elevation) {
                    // elev-N-E
                    return new ModelInfo(20, 2);
                } else if (properties.cornerNeighbourElevation.y > properties.elevation) {
                    // elev-N-W
                    return new ModelInfo(20, 3);
                } else if (properties.cornerNeighbourElevation.z > properties.elevation) {
                    // elev-S-E
                    return new ModelInfo(20, 1);
                } else if (properties.cornerNeighbourElevation.w > properties.elevation) {
                    // elev-S-W
                    return new ModelInfo(20, 0);
                }

                // land-ocean transitions
                if (properties.nTileType == null || properties.nTileType == OCEAN) {

                    if (properties.eTileType == null || properties.eTileType == OCEAN) {

                        // NE
                        return new ModelInfo(9, 2);
                    }
                    if (properties.wTileType == null || properties.wTileType == OCEAN) {
                        // NW
                        return new ModelInfo(9, 3);
                    }
                    // N
                    return new ModelInfo(10, 2);
                }

                if (properties.sTileType == null || properties.sTileType == OCEAN) {
                    if (properties.eTileType == null || properties.eTileType == OCEAN) {

                        // SE
                        return new ModelInfo(9, 1);
                    }
                    if (properties.wTileType == null || properties.wTileType == OCEAN) {
                        // SW
                        return new ModelInfo(9, 0);
                    }
                    // S
                    return new ModelInfo(10, 0);
                }

                if (properties.eTileType == null || properties.eTileType == OCEAN) {
                    /*if (properties.wTileType == OCEAN) {
                        // EW
                    } */
                    // E
                    return new ModelInfo(10, 1);
                }

                if (properties.wTileType == null || properties.wTileType == OCEAN) {
                    // W
                    return new ModelInfo(10, 3);
                }

                // N/A OCEAN in directNeighbours

                if (properties.isCornerNeighbourOcean.x == 1) {
                    // N-E
                    return new ModelInfo(8, 2);
                }
                if (properties.isCornerNeighbourOcean.y == 1) {
                    // N-W
                    return new ModelInfo(8, 3);
                }
                if (properties.isCornerNeighbourOcean.z == 1) {
                    // S-E
                    return new ModelInfo(8, 1);
                }
                if (properties.isCornerNeighbourOcean.w == 1) {
                    // S-W
                    return new ModelInfo(8, 0);
                }
                // N/A OCEAN in directNeighbours & N/A cornerNeighbourOcean

                return new ModelInfo(18, 0);
            // will eventually need: case ROAD, case BRIDGE, case TUNNEL (can't be in the same variable
        }
        return new ModelInfo(0, 0); // stub, always returns dirt
    } // should return modelInfo, which represents both modelIndex & rotation

    public float getTileTerrainWidth(){
        return worldTileWidth * TILE_WIDTH;
    }
    public float getTileTerrainDepth(){
        return worldTileDepth * TILE_WIDTH;
    }



}
