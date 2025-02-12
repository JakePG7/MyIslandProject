package io.github.some_example_name;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;



public class TileInstance extends ModelInstance {

    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public float radius; // supposed to be final but whatever


    public TileProperties tileProperties;
    public ModelInfo modelInfo; // may not end up needing this field, just in case

    private final static BoundingBox bounds = new BoundingBox();

    private void baseConstructor() {
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2f;
    }

    // this constructor would be for default start or read from save file (all tile properties will be recreated)
    public TileInstance(Model model, ModelInfo modelI, TileProperties properties) {
        super(model);
        baseConstructor();
        modelInfo = modelI;
        tileProperties = properties;
    }

}
