package io.github.some_example_name;

import com.badlogic.gdx.math.Vector4;

public class TileProperties {

    // REQUIRES tileType.tileLandType.contains(tileType);
    public tileType tileType;
    // REQUIRES tileType.tileOccupationType.contains(tileOccupationType);
    public tileType tileOccupationType;
    public int elevation;
    public tileType nTileType;
    public tileType sTileType;
    public tileType eTileType;
    public tileType wTileType;
    public tileType nTileOccupationType;
    public tileType sTileOccupationType;
    public tileType eTileOccupationType;
    public tileType wTileOccupationType;
    public Vector4 isCornerNeighbourOcean; // treat like boolean vector
    public Vector4 neighbourElevation;
    public Vector4 cornerNeighbourElevation;
    // public int occupationPrincipalTile = -1;

    /*
    private int longevity;
    private int appeal;
    private int population;
    private int profit;
    private int commercialBoost;
    */

    public TileProperties(tileType t, tileType o, int e, tileType nT, tileType sT, tileType eT, tileType wT, Vector4 cornerNeighbourO, Vector4 neighbourE, Vector4 cornerNeighbourE) {
        tileType = t;
        tileOccupationType = o;
        elevation = e;
        nTileType = nT;
        sTileType = sT;
        eTileType = eT;
        wTileType = wT;
        // TODO: add tileOccupationTypes for coordinate directions
        // (I may not need to because may just set directly
        // nTileOccupationType = nTOT
        isCornerNeighbourOcean = cornerNeighbourO;
        neighbourElevation = neighbourE;
        cornerNeighbourElevation = cornerNeighbourE;

    }

}
