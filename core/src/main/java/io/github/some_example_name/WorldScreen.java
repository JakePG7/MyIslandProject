package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public abstract class WorldScreen implements Screen, InputProcessor {

    static Main game;

    protected PerspectiveCamera camera; // perspective (rather than orthagonal) is for 3d
    private ModelBatch modelBatch; // Culmination of all vectors for efficiency
    // private ModelBuilder modelBuilder; // incase you need simple geometric 3d shapes
    // private ModelInstance modelInstance; // represents instace specific data if there are multiple
    private Environment environment;
    // private AnimationController controller;
    protected MyCameraController cameraController;

    // CameraPositionVariables
    protected float cameraY;
    protected float cameraZ;
    protected float cameraX;
    protected float lookAtY;
    protected float lookAtZ;
    protected float lookAtX;

    // protected MyShader customShader; // 124, 148,

    protected EditorWorldManager worldManager; // info abt tiles goes here
    private float tileWidth = 8f; // Exact width
    private int testIndex = 0;

    // Selection variables
    private int selecting = -1;
    private int hovered = -1, hovering = -1;
    private Vector3 position = new Vector3();

    public WorldScreen() {}

    // CONSTRUCTOR ACTS AS CREATE() FUNCTION
    public WorldScreen(final Main g, String n, int w, int d) {
        init(g);
    }

    public WorldScreen(final Main g, String fn) {
        init(g);
    }

    protected void init(final Main g) {
        game = g;
        cameraX = worldManager.getTileTerrainWidth() / 2f;
        cameraY = 50f;
        cameraZ = -10f;
        lookAtX = worldManager.getTileTerrainWidth() / 2f;
        lookAtY = 0f;
        lookAtZ = 30f;

        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 300.0f;

        cameraController = new MyCameraController(camera, worldManager, cameraX, cameraY, cameraZ, lookAtX, lookAtY, lookAtZ);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, cameraController)); // multiplexer takes priority over selection, then camera controls
        // child classes will likely override this setInput

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        environment.add(new DirectionalLight().set(0.0001f, 0.0001f, 0.0001f, -1f, -0.8f, -0.2f)); // added from stack overflow asker

        // controller = new AnimationController(modelInstance);
        // controller.setAnimation("ANIMATION NAME FROM JSON FILE", -1);

        //customShader = new MyShader();
        //customShader.init();
    }

    // IMPORTED SCREEN CLASSES

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // can probably remove this line

       // Gdx.gl.glClearColor(111f/255f, 192f/255f, 211f/255f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cameraController.update(); // eliminates need for camera.update();
        // controller.update(Gdx.graphics.getDeltaTime());

        modelBatch.begin(camera);
        //modelBatch.render(modelInstance, environment);
        modelBatch.render(worldManager.getTileArray(), environment); // add 3rd param customShader on both lines
        modelBatch.render(worldManager.getBorderTileArray(), environment);
        modelBatch.end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        worldManager.dispose();
        //customShader.dispose();
    }

    // IMPORTED INPUT PROCESSOR CLASSES
    // For tile selection
    // Remember, if false returns, the input is controlled by the camera controller

    @Override
    public boolean keyDown(int keycode) {
        return false;

    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        selecting = getObject(screenX, screenY);
        return selecting >= 0;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY))
                setSelected(selecting);
            selecting = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return selecting >= 0;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        hovering = getObject(screenX, screenY);
        if (hovering >= 0) {
            setHovered(hovering);
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }


    // ____________________ Selection Functions
    // Accessed in Input Functions

    public void setSelected (int newSelect) {
        worldManager.selectFunction(newSelect);
    }

    public void setHovered(int newHover) {
        if (hovered == newHover) return; // ensures no repetition (not needed in selecting unless we need the previous material type like in hovering
        worldManager.hoverFunction(hovered, newHover);
        hovered = newHover;
    }

    public int getObject (int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;
        Array<TileInstance> tileArray = worldManager.getTileArray(); // Only accessing it because it doesn't change it and has to do with Screen selection
        for (int i = 0; i < tileArray.size; ++i) {
            final TileInstance instance = tileArray.get(i);
            instance.transform.getTranslation(position);
            position.add(instance.center);
            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance) continue;
            if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }


}
