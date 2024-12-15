package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EditorScreen extends WorldScreen {

    OrthographicCamera camera; // 2d Camera essentially

    protected Stage stage;

    protected Skin skin;
    protected Table table;

    private Label titleLabel;
    private Label islandLabel;

    ImageButton landButton, sandButton, oceanButton, riverButton, elevationUpButton, elevationDownButton;

    TextButton dontSaveButton, saveButton;

    EditorScreen() {}

    EditorScreen(final Main g, String fileName) {
        worldManager = new EditorWorldManager(fileName);
        init(g);
        initEditor(g);
    }


    EditorScreen(final Main g, String name, int width, int depth) { // for new island
        worldManager = new EditorWorldManager(name, width, depth);
        init(g);
        initEditor(g);
    }

    protected void initEditor(final Main g) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        skin = new Skin(Gdx.files.internal("ui/terrainButtonSkin.json"));
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this, cameraController)); // 'this' refers to WorldScreen selection controls

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        setTopBanner(skin);
        initButtons();
        setTable(table);
    }

    protected void initButtons() {
        dontSaveButton = new TextButton("Don't Save", skin);
        saveButton = new TextButton("Save", skin);
        landButton = new ImageButton(skin, "default");
        sandButton = new ImageButton(skin, "sand");
        oceanButton = new ImageButton(skin, "ocean");
        riverButton = new ImageButton(skin, "river");
        elevationUpButton = new ImageButton(skin, "elevationUp");
        elevationDownButton = new ImageButton(skin, "elevationDown");

        landButton.setChecked(true);

        dontSaveButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainScreen(game));
                dispose();
                return false;
            }
        });

        saveButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                save();
                game.setScreen(new MainScreen(game));
                dispose();
                return false;
            }
        });

        landButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.LAND);
            }
        });
        oceanButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.OCEAN);
            }
        });

        sandButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.SAND);
            }
        });

        riverButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.RIVER);
            }
        });

        elevationUpButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.ELEVATION_UP);
            }
        });

        elevationDownButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.ELEVATION_DOWN);
            }
        });
    }

    protected void setTopBanner(Skin skin) {
        titleLabel = new Label("Island Editor", skin, "title", Color.WHITE); // EITHER "title" or "font" for fonts, from skin file
        titleLabel.setColor(Color.WHITE);

        islandLabel = new Label(worldManager.worldName, skin, "font", Color.WHITE);
    }

    protected void setTable(Table table) {
        table.add(titleLabel).top().colspan(8);
        table.row();
        table.add(islandLabel).expand().top().colspan(8);
        table.row();
        commonTableButtons(table);

        // table.debug(); // shows table lines for guidance
    }

    protected void commonTableButtons(Table table) {
        table.add(dontSaveButton).expandY().bottom().right();
        table.add(saveButton).expandX().bottom().left();
        table.add(landButton).bottom().right().width(50).height(50);
        table.add(sandButton).bottom().right().width(50).height(50);
        table.add(oceanButton).bottom().right().width(50).height(50);
        table.add(riverButton).bottom().right().width(50).height(50);
        table.add(elevationUpButton).bottom().right().width(50).height(50);
        table.add(elevationDownButton).bottom().right().width(50).height(50);
    }


    protected void save() {
        System.out.println("-----");
        System.out.println("world name: " + worldManager.worldName);
        Preferences islandPrefs = Gdx.app.getPreferences("IslandList");
        int listSize = islandPrefs.getInteger("size", -1); // will default to -1 if not found
        Array<String> islandList = new Array<>();
        if (listSize != -1) {
            for (int i = 1; i < listSize; i++) {
                islandList.add(islandPrefs.getString(Integer.toString(i)));
            }
        }
        if (!islandList.contains(worldManager.worldName, false)) {
            System.out.println("saving new island");
            islandPrefs.putString(Integer.toString(listSize), worldManager.worldName);
            islandPrefs.putInteger("size", listSize + 1);
            islandPrefs.flush();
            // Add actual island saving for new island here
        } else {
            System.out.println("saving existing island");
            // Add actual island saving for existing island here
        }
        worldManager.saveIsland();
    }

    protected boolean terrainButtonHelper(tileType t) {
        worldManager.setTileTypeSelection(t);
        if (t != tileType.LAND) {
            landButton.setChecked(false);
        }
        if (t != tileType.SAND) {
            sandButton.setChecked(false);
        }
        if (t != tileType.OCEAN) {
            oceanButton.setChecked(false);
        }
        if (t != tileType.RIVER) {
            riverButton.setChecked(false);
        }
        if (t != tileType.ELEVATION_DOWN) {
            elevationDownButton.setChecked(false);
        }
        if (t != tileType.ELEVATION_UP) {
            elevationUpButton.setChecked(false);
        }
        return false;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

}

// CONSTRUCTOR ACTS AS CREATE() FUNCTION