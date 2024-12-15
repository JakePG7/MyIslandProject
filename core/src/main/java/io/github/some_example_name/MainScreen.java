package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainScreen implements Screen {

    Texture mainBackground = new Texture("ui/mainBackground.jpg");


    // Persistence variables
    Preferences islandListPrefs;
    Preferences cityListPrefs;

    static final String DEFAULT_LABEL = "default";
    static final String DEFAULT_NEW_ISLAND = "New Island";
    static final String DEFAULT_NEW_PROJECT = "New Project";

    final Main game;
    OrthographicCamera camera; // 2d Camera essentially

    private Stage stage;
    private Skin skin;

    final Label welcomeLabel; // EITHER "title" or "font" for fonts, from skin file
    final Label titleLabel;

    final TextButton takeMeButton;

    final TextButton actMayorButton, editButton;

    final TextButton deleteCityButton, deleteIslandButton;

    final SelectBox<String> citySelectionBox, editIslandSelectionBox, chooseIslandSelectionBox;

    Array<String> citySelectArray;
    Array<String> editSelectArray;
    Array<String> islandSelectArray;

    final Label nameLabel, widthLabel, depthLabel, islandLabel, errorLabel;

    TextField nameField, widthField, depthField;

    private boolean isActMayorSelected;
    private String selection;

    private static final int DIMENSION_MIN = 10;
    private static final int DIMENSION_MAX = 50;

    MainScreen(final Main g) {
        game = g;

        islandListPrefs = Gdx.app.getPreferences("IslandList");
        cityListPrefs = Gdx.app.getPreferences("CityList");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        skin = new Skin(Gdx.files.internal("ui/terrainButtonSkin.json"));
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        errorLabel = new Label("", skin, "font", Color.WHITE);

        welcomeLabel = new Label("Welcome to", skin, "font", Color.TEAL); // EITHER "title" or "font" for fonts, from skin file
        titleLabel = new Label("My Island Project!", skin, "title", Color.TEAL);

        citySelectArray = new Array<String>();
        editSelectArray = new Array<String>();
        islandSelectArray = new Array<String>();

        takeMeButton = new TextButton("Take me there!", skin, "special");
        takeMeButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                String islandName = nameField.getText();
                //selection = DEFAULT_NEW_PROJECT;
                //isActMayorSelected = true;
                if (isActMayorSelected) {
                    if (selection == DEFAULT_NEW_PROJECT) {
                        // new project
                        String chosenIsland = chooseIslandSelectionBox.getSelected();
                        game.setScreen(new GameScreen(g, islandName, chosenIsland));
                        //errorLabel.setText("Doesn't work yet, sorry!");
                    } else {
                        // opening other project
                        System.out.println(chooseIslandSelectionBox.getSelected());
                        game.setScreen(new GameScreen(g, selection));
                        //errorLabel.setText("Doesn't work yet, sorry!");
                    }
                } else {
                    if (selection == DEFAULT_NEW_ISLAND) {
                        // new island
                        String widthText = widthField.getText();
                        String depthText = depthField.getText();

                        try {
                            int worldWidth = Integer.parseInt(widthText);
                            int worldDepth = Integer.parseInt(depthText);
                            // NOT DOONNNEEEEE PEE NEEDS TO BE UPDATED
                            if (islandSelectArray.contains(islandName, false) || citySelectArray.contains(islandName, false)) { // false means it uses .equals() instead of ==, which is necessary for strings
                                throw new Exception("error: name has already been used");
                            } else if (worldWidth < DIMENSION_MIN || worldWidth > DIMENSION_MAX || worldDepth < DIMENSION_MIN || worldDepth > DIMENSION_MAX) {
                                throw new Exception("error: dimensions outside limit of " + Integer.toString(DIMENSION_MIN) + " <= x <= " + Integer.toString(DIMENSION_MAX));
                            }
                            game.setScreen(new EditorScreen(game, islandName, worldWidth, worldDepth));
                            dispose();
                        } catch (NumberFormatException e) {
                            errorLabel.setText("error: dimensions are invalid integers");
                        } catch (Exception e) {
                            String errorMessage = e.getMessage();
                            errorLabel.setText(errorMessage);
                        }

                    } else {
                        // opening other island
                        game.setScreen(new EditorScreen(game, selection));
                        dispose();
                    }
                }
                return false;
            }
        });


        actMayorButton = new TextButton("Act as Mayor", skin, "toggle");
        editButton = new TextButton("Island Editor", skin, "toggle");

        deleteCityButton = new TextButton("Delete", skin, "default");
        deleteIslandButton = new TextButton("Delete", skin, "default");

        citySelectionBox = new SelectBox<String>(skin, "citiesBox");
        citySelectionBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                selection = citySelectionBox.getSelected();
                updateWidgets();
                return;
            }
        });
        editIslandSelectionBox = new SelectBox<String>(skin);
        editIslandSelectionBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                selection = editIslandSelectionBox.getSelected();
                updateWidgets();
                return;
            }
        });

        chooseIslandSelectionBox = new SelectBox<String>(skin); // this one doesn't need an action listener

        nameLabel = new Label("Name: ", skin, "Bordered");
        widthLabel = new Label("Width: ", skin, "Bordered");
        depthLabel = new Label("Depth: ", skin, "Bordered");
        islandLabel = new Label("Island: ", skin, "Bordered");

        nameField = new TextField("MyIsland", skin);
        widthField = new TextField("10", skin);
        depthField = new TextField("15", skin);

        actMayorButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

                citySelectionBox.setSelectedIndex(0);  // sets to new project
                selection = DEFAULT_NEW_PROJECT;
                chooseIslandSelectionBox.setSelectedIndex(0); // sets to default
                isActMayorSelected = true;
                updateWidgets();

                return;
            }
        });

        editButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

                selection = DEFAULT_NEW_ISLAND;
                editIslandSelectionBox.setSelectedIndex(0);  // sets to new island
                isActMayorSelected = false;
                updateWidgets();

                return;
            }
        });

        deleteCityButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

                int deleteIndex = citySelectionBox.getSelectedIndex();
                citySelectArray.removeIndex(deleteIndex);
                saveLists();
                cityListPrefs.remove(Integer.toString(citySelectArray.size + 1)); // have to remove the biggest integer because their indices should all be adjusted down, and the last would have a duplicate
                cityListPrefs.flush();
                citySelectionBox.setItems(citySelectArray);
                return;
            }
        });

        deleteIslandButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {

                // deleting island properties
                String selected = editIslandSelectionBox.getSelected();
                Preferences prefToDelete = Gdx.app.getPreferences("islandWorlds");
                int listSize = prefToDelete.getInteger(selected + "Width") * prefToDelete.getInteger(selected + "Depth");
                for (int i = 0; i < listSize; i++ ){
                    prefToDelete.remove(selected + "T" + Integer.toString(i));
                    prefToDelete.remove(selected + "E" + Integer.toString(i));
                }
                prefToDelete.remove(selected + "Width");
                prefToDelete.remove(selected + "Depth");
                prefToDelete.flush();

                // deleting from island list

                // Something is really wrong here and makes it not delete the name in IslandList

                int deleteIndex = editIslandSelectionBox.getSelectedIndex();
                editSelectArray.removeIndex(deleteIndex);
                islandSelectArray.removeIndex(deleteIndex);
                islandListPrefs.remove(Integer.toString(deleteIndex));
                saveLists();
                islandListPrefs.remove(Integer.toString(islandSelectArray.size)); // have to remove the biggest integer because their indices should all be adjusted down, and the last would have a duplicate
                islandListPrefs.flush();
                editIslandSelectionBox.setItems(editSelectArray);
                chooseIslandSelectionBox.setItems(islandSelectArray);
            }
        });

        int listSize = cityListPrefs.getInteger("size", -1); // will default to -1 if not found
        citySelectArray.add(DEFAULT_NEW_PROJECT);
        if (listSize != -1) {
            for (int i = 1; i < listSize; i++) {
                citySelectArray.add(cityListPrefs.getString(Integer.toString(i)));
            }
        }
        citySelectionBox.setItems(citySelectArray); // ADDING ITEMS AFTER SETITEMS DOES NOT WORK

        listSize = islandListPrefs.getInteger("size", -1); // will default to -1 if not found
        editSelectArray.add(DEFAULT_NEW_ISLAND);
        islandSelectArray.add(DEFAULT_LABEL);
        if (listSize != -1) {
            for (int i = 1; i < listSize; i++) {
                editSelectArray.add(islandListPrefs.getString(Integer.toString(i)));
                islandSelectArray.add(islandListPrefs.getString(Integer.toString(i)));
            }
        }
        editIslandSelectionBox.setItems(editSelectArray);
        chooseIslandSelectionBox.setItems(islandSelectArray);

        actMayorButton.setChecked(true);
        isActMayorSelected = true;
        selection = DEFAULT_NEW_PROJECT;
        updateWidgets();

        table.add(welcomeLabel).expandX().colspan(5);
        table.row();
        table.add(titleLabel).expandX().colspan(5);
        table.row();
        table.add(actMayorButton).right().pad(20).colspan(2);
        table.add(citySelectionBox).fillX().colspan(2);
        table.add(deleteCityButton).left().pad(20);
        table.row();
        table.add(editButton).right().pad(20).colspan(2).padBottom(50);
        table.add(editIslandSelectionBox).padTop(20).fillX().colspan(2).padBottom(50);
        table.add(deleteIslandButton).left().pad(20).padBottom(50);
        table.row();
        table.add(nameLabel).right().pad(20);
        table.add(nameField).fillX().colspan(2).padRight(50);
        table.add(widthLabel).center();
        table.add(widthField).left().width(50).height(50);
        table.row();
        table.add(islandLabel).right().pad(20);
        table.add(chooseIslandSelectionBox).colspan(2).left().fillX();
        table.add(depthLabel).center();
        table.add(depthField).left().width(50).height(50);
        table.row();
        table.add(errorLabel).center().colspan(5).padLeft(20);
        table.row();
        table.add(takeMeButton).expand().top().colspan(5);

        //table.debug();

    }



    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();
        /*game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to my City Game!!! ", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();*/

        game.batch.begin();
        game.batch.draw(mainBackground, 0, 0);
        game.batch.end();

        stage.act(delta);
        stage.draw();

    }

    private void updateWidgets() {
        actMayorButton.setChecked(isActMayorSelected);
        editButton.setChecked(!isActMayorSelected);
        deleteCityButton.setVisible(isActMayorSelected && selection != DEFAULT_NEW_PROJECT);
        deleteIslandButton.setVisible(!isActMayorSelected && selection != DEFAULT_NEW_ISLAND);
        citySelectionBox.setVisible(isActMayorSelected);
        editIslandSelectionBox.setVisible(!isActMayorSelected);
        chooseIslandSelectionBox.setVisible(selection == DEFAULT_NEW_PROJECT);
        islandLabel.setVisible(selection == DEFAULT_NEW_PROJECT);
        nameLabel.setVisible(selection == DEFAULT_NEW_ISLAND || selection == DEFAULT_NEW_PROJECT);
        widthLabel.setVisible(selection == DEFAULT_NEW_ISLAND);
        depthLabel.setVisible(selection == DEFAULT_NEW_ISLAND);
        nameField.setVisible(selection == DEFAULT_NEW_ISLAND || selection == DEFAULT_NEW_PROJECT);
        widthField.setVisible(selection == DEFAULT_NEW_ISLAND);
        depthField.setVisible(selection == DEFAULT_NEW_ISLAND);
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
        saveLists();
        // jsonListWriter = new listsWriter("assets/persistence";
        stage.dispose();
    }

    // this function is currently used by the delete button. If world saving functionality is added in a separate function, it must include the function call in the delete listener
    private void saveLists() {
        islandListPrefs.putInteger("size", islandSelectArray.size);
        for (int i = 1; i < islandSelectArray.size; i++) { // don't need 0, it will load default regardless
            islandListPrefs.putString(Integer.toString(i), islandSelectArray.get(i));
        }
        islandListPrefs.flush();

        cityListPrefs.putInteger("size", citySelectArray.size);
        for (int i = 1; i < citySelectArray.size; i++) {
            cityListPrefs.putString(Integer.toString(i), citySelectArray.get(i));
        }
        cityListPrefs.flush();
    }


}
