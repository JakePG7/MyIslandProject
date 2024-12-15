package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameScreen extends EditorScreen {

    Label islandLabel;

    // TODO: implement roadButton & bridgeButton in setTable
    ImageButton roadButton, bridgeButton, tunnelButton, bulldozerButton, buildingButton;

    String cityName;

    GameScreen(final Main g ,String cityName) {
        worldManager = new GameWorldManager(cityName);
        this.cityName = cityName;
        init(g);
        initEditor(g);
    }

    GameScreen(final Main g ,String newCityName, String islandName) {
        worldManager = new GameWorldManager(islandName);
        this.cityName = newCityName;
        init(g);
        initEditor(g);

    }

    @Override
    // TODO:  Function after save button is pressed. See EditorScreen
    protected void save() {}

    @Override
    // TODO: Sets the titles at the top. See EditorScreen
    protected void setTopBanner(Skin skin) {
        islandLabel = new Label(cityName, skin, "font", Color.WHITE);
    }

    @Override
    // TODO: Set table functions, commonTableButtons is shared with EditorScreen
    protected void setTable(Table table) {
        table.add(islandLabel).top().colspan(13);
        table.row();
        // could probably add road buttons here, would put save buttons in the middle
        table.add(bulldozerButton).bottom().right().width(50).height(50);
        table.add(buildingButton).bottom().right().width(50).height(50);
        table.add(roadButton).bottom().right().width(50).height(50);
        table.add(bridgeButton).bottom().right().width(50).height(50);
        table.add(tunnelButton).bottom().right().width(50).height(50);

        commonTableButtons(table);
        // table.debug();
    }

    @Override
    protected void initButtons() {
        super.initButtons();
        roadButton = new ImageButton(skin, "road");
        bridgeButton = new ImageButton(skin, "bridge");
        tunnelButton = new ImageButton(skin, "tunnel");

        bulldozerButton = new ImageButton(skin, "bulldoze");
        buildingButton = new ImageButton(skin, "building");

        roadButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.ROAD);
            }
        });
        bridgeButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.BRIDGE);
            }
        });
        tunnelButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.TUNNEL);
            }
        });
        bulldozerButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.UNOCCUPIED);
            }
        });
        buildingButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return terrainButtonHelper(tileType.BUILDING);
            }
        });

    }

    @Override
    // TODO: Add
    protected boolean terrainButtonHelper(tileType t) {
        if (t != tileType.ROAD) {
            roadButton.setChecked(false);
        }
        if (t != tileType.BRIDGE) {
            bridgeButton.setChecked(false);
        }
        if (t != tileType.TUNNEL) {
            tunnelButton.setChecked(false);
        }
        if (t != tileType.UNOCCUPIED) {
            bulldozerButton.setChecked(false);
        }
        if (t != tileType.BUILDING) {
            buildingButton.setChecked(false);
        }
        return super.terrainButtonHelper(t);
    }
}