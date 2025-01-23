package io.github.some_example_name;

import com.badlogic.gdx.utils.Array;

// Lazy class to make sure method returns a bunch of information (I forgot I can't use pointers)
public class LoadedFileInfo {
    int width, depth;
    Array<tileType> tArray, oArray;
    Array<Integer> eArray;

    public LoadedFileInfo(int width, int height, Array<tileType> tArray, Array<Integer> eArray, Array<tileType> oArray) {
        this.width = width;
        this.depth = height;
        this.tArray = tArray;
        this.eArray = eArray;
        this.oArray = oArray;
    }
}

