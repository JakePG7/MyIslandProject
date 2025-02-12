package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class GameScreen extends EditorScreen {

    Label islandLabel;

    // TODO: implement roadButton & bridgeButton in setTable
    ImageButton roadButton, bridgeButton, tunnelButton, bulldozerButton, buildingButton;
    ScrollPane buildingScrollPane;
    Table scrollPaneTable;
    VerticalGroup buildingTypeButtonGroup;
    HorizontalGroup residentialGroup, commercialGroup, industrialGroup, civicGroup;
    ImageButton residentialButton, commercialButton, industrialButton, civicButton;
    ImageTextButton shackButton, farmhouseButton, sHomeButton, trailerParkButton, bHomeButton, homeWBackButton, townhouseButton, hotelButton, apartmentButton, villaButton, mansionButton, firstResButton;
    ImageTextButton convStoreButton, fastFoodButton, clothingButton, groceryButton, furnitureButton, salonButton, cafeButton, toyButton, restaurantButton, jewelryButton, stripmallButton, technologyButton;
    ImageTextButton cropsButton, wellButton, livestockButton, energyButton, factoryButton, quarryButton, wasteButton, emergButton, officesButton, greenhousesButton, jailButton, hospitalButton;
    ImageTextButton churchButton, parkButton, commCentButton, libraryButton, tSquareButton, cinemaButton, schoolButton, natureTButton, sportsCButton, cityHallButton, festivalGButton, stadiumButton;


    String cityName;

    GameScreen(final Main g ,String cityName) {
        worldManager = new GameWorldManager(cityName);
        this.cityName = cityName;
        init(g);
        initEditor(g);
    }

    GameScreen(final Main g ,String newCityName, String islandName) {
        worldManager = new GameWorldManager(islandName, newCityName);
        this.cityName = newCityName;
        init(g);
        initEditor(g);

    }

    @Override
    // TODO:  Function after save button is pressed. See EditorScreen
    protected void save() {
        System.out.println("-----");
        System.out.println("world name: " + worldManager.worldName);
        Preferences cityListPrefs = Gdx.app.getPreferences("CityList");
        savePreferencesHelper(cityListPrefs);
    }

    @Override
    protected void setTopBanner(Skin skin) {
        islandLabel = new Label(cityName + "        NOTE: Normal Textures: Shack(1), Farmhouse(2), Hotel(4), Apartment(8)", skin, "font", Color.WHITE);
    }

    private void setScrollPaneTable(Table table) {
        buildingTypeButtonGroup.addActor(residentialButton);
        buildingTypeButtonGroup.addActor(commercialButton);
        buildingTypeButtonGroup.addActor(industrialButton);
        buildingTypeButtonGroup.addActor(civicButton);

        residentialGroup.addActor(shackButton);
        residentialGroup.addActor(farmhouseButton);
        residentialGroup.addActor(sHomeButton);
        residentialGroup.addActor(trailerParkButton);
        residentialGroup.addActor(bHomeButton);
        residentialGroup.addActor(homeWBackButton);
        residentialGroup.addActor(townhouseButton);
        residentialGroup.addActor(hotelButton);
        residentialGroup.addActor(apartmentButton);
        residentialGroup.addActor(villaButton);
        residentialGroup.addActor(mansionButton);
        residentialGroup.addActor(firstResButton);

        commercialGroup.addActor(convStoreButton);
        commercialGroup.addActor(fastFoodButton);
        commercialGroup.addActor(clothingButton);
        commercialGroup.addActor(groceryButton);
        commercialGroup.addActor(furnitureButton);
        commercialGroup.addActor(salonButton);
        commercialGroup.addActor(cafeButton);
        commercialGroup.addActor(toyButton);
        commercialGroup.addActor(restaurantButton);
        commercialGroup.addActor(jewelryButton);
        commercialGroup.addActor(stripmallButton);
        commercialGroup.addActor(technologyButton);

        industrialGroup.addActor(cropsButton);
        industrialGroup.addActor(wellButton);
        industrialGroup.addActor(livestockButton);
        industrialGroup.addActor(energyButton);
        industrialGroup.addActor(factoryButton);
        industrialGroup.addActor(quarryButton);
        industrialGroup.addActor(wasteButton);
        industrialGroup.addActor(emergButton);
        industrialGroup.addActor(officesButton);
        industrialGroup.addActor(greenhousesButton);
        industrialGroup.addActor(jailButton);
        industrialGroup.addActor(hospitalButton);

        civicGroup.addActor(churchButton);
        civicGroup.addActor(parkButton);
        civicGroup.addActor(commCentButton);
        civicGroup.addActor(libraryButton);
        civicGroup.addActor(tSquareButton);
        civicGroup.addActor(cinemaButton);
        civicGroup.addActor(schoolButton);
        civicGroup.addActor(natureTButton);
        civicGroup.addActor(sportsCButton);
        civicGroup.addActor(cityHallButton);
        civicGroup.addActor(festivalGButton);
        civicGroup.addActor(stadiumButton);

        table.add(buildingTypeButtonGroup).pad(20);
        table.add(residentialGroup);
        residentialButton.setChecked(true);

        //table.debug();
    }

    @Override
    protected void setTable(Table table) {
        setScrollPaneTable(scrollPaneTable);

        table.add(islandLabel).top().colspan(13).padBottom(475);
        table.row();
        table.add(buildingScrollPane).bottom().colspan(13).width(1200);
        table.row();
        // could probably add road buttons here, would put save buttons in the middle
        table.add(bulldozerButton).bottom().right().width(50).height(50);
        table.add(buildingButton).bottom().right().width(50).height(50);
        table.add(roadButton).bottom().right().width(50).height(50);
        table.add(bridgeButton).bottom().right().width(50).height(50);
        table.add(tunnelButton).bottom().right().width(50).height(50);

        commonTableButtons(table);
        //table.debug();
    }

    private void tileChangerButtonHelper(Button button, tileType t) {
        button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                worldManager.resetRotationSelection();
                return terrainButtonHelper(t);
            }
        });
    }

    @Override
    protected void initButtons() {
        super.initButtons();
        scrollPaneTable = new Table();
        buildingTypeButtonGroup = new VerticalGroup();
        residentialGroup = new HorizontalGroup();
        commercialGroup = new HorizontalGroup();
        industrialGroup = new HorizontalGroup();
        civicGroup = new HorizontalGroup();
        buildingScrollPane = new ScrollPane(scrollPaneTable, skin);
        buildingScrollPane.setScrollingDisabled(false, true);
        buildingScrollPane.setVisible(false);
        residentialButton = new ImageButton(skin, "toggle");
        commercialButton = new ImageButton(skin, "toggle");
        industrialButton = new ImageButton(skin, "toggle");
        civicButton = new ImageButton(skin, "toggle");

        shackButton = new ImageTextButton("Shack", skin);
        farmhouseButton = new ImageTextButton("Farmhouse", skin);
        sHomeButton = new ImageTextButton("Small Home", skin);
        trailerParkButton = new ImageTextButton("Trailer Park", skin);
        bHomeButton = new ImageTextButton("Big Home", skin);
        homeWBackButton = new ImageTextButton("Home w/ Backyard", skin);
        townhouseButton = new ImageTextButton("Townhouse", skin);
        hotelButton = new ImageTextButton("Hotel", skin);
        apartmentButton = new ImageTextButton("Apartment", skin);
        villaButton = new ImageTextButton("Villa", skin);
        mansionButton = new ImageTextButton("Mansion", skin);
        firstResButton = new ImageTextButton("First Residence", skin);

        convStoreButton = new ImageTextButton("Convenience Store", skin);
        fastFoodButton = new ImageTextButton("Fast Food", skin);
        clothingButton = new ImageTextButton("Clothing Boutique", skin);
        groceryButton = new ImageTextButton("Grocery Store", skin);
        furnitureButton = new ImageTextButton("Furniture Store", skin);
        salonButton = new ImageTextButton("Salon", skin);
        cafeButton = new ImageTextButton("Cafe", skin);
        toyButton = new ImageTextButton("Toy Store", skin);
        restaurantButton = new ImageTextButton("Restaurant", skin);
        jewelryButton = new ImageTextButton("Jewelry Store", skin);
        stripmallButton = new ImageTextButton("Stripmall", skin);
        technologyButton = new ImageTextButton("Tech Shop", skin);

        cropsButton = new ImageTextButton("Crops", skin);
        wellButton = new ImageTextButton("Well", skin);
        livestockButton = new ImageTextButton("Livestock", skin);
        energyButton = new ImageTextButton("Energy", skin);
        factoryButton = new ImageTextButton("Factory", skin);
        quarryButton = new ImageTextButton("Quarry", skin);
        wasteButton = new ImageTextButton("Waste", skin);
        emergButton = new ImageTextButton("Emergency Services", skin);
        officesButton = new ImageTextButton("Offices", skin);
        greenhousesButton = new ImageTextButton("Greenhouses", skin);
        jailButton = new ImageTextButton("Jail", skin);
        hospitalButton = new ImageTextButton("Hospital", skin);

        churchButton = new ImageTextButton("Church", skin);
        parkButton = new ImageTextButton("Park", skin);
        commCentButton = new ImageTextButton("Community Centre", skin);
        libraryButton = new ImageTextButton("Library", skin);
        tSquareButton = new ImageTextButton("Town Square", skin);
        cinemaButton = new ImageTextButton("Cinema", skin);
        schoolButton = new ImageTextButton("School", skin);
        natureTButton = new ImageTextButton("Nature Trail", skin);
        sportsCButton = new ImageTextButton("Sports Centre", skin);
        cityHallButton = new ImageTextButton("City Hall", skin);
        festivalGButton = new ImageTextButton("Festival Grounds", skin);
        stadiumButton = new ImageTextButton("Stadium", skin);

        roadButton = new ImageButton(skin, "road");
        bridgeButton = new ImageButton(skin, "bridge");
        tunnelButton = new ImageButton(skin, "tunnel");

        bulldozerButton = new ImageButton(skin, "bulldoze");
        buildingButton = new ImageButton(skin, "building");

        residentialButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return switchGroups(residentialGroup);
            }
        });

        commercialButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return switchGroups(commercialGroup);
            }
        });

        industrialButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return switchGroups(industrialGroup);
            }
        });

        civicButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return switchGroups(civicGroup);
            }
        });

        tileChangerButtonHelper(roadButton, tileType.ROAD);
        tileChangerButtonHelper(bridgeButton, tileType.BRIDGE);
        tileChangerButtonHelper(tunnelButton, tileType.TUNNEL);
        tileChangerButtonHelper(bulldozerButton, tileType.UNOCCUPIED);

        tileChangerButtonHelper(shackButton, tileType.SHACK);
        tileChangerButtonHelper(farmhouseButton, tileType.FARMHOUSE);
        tileChangerButtonHelper(sHomeButton, tileType.SHOME);
        tileChangerButtonHelper(trailerParkButton, tileType.TRAILERPARK);
        tileChangerButtonHelper(bHomeButton, tileType.BHOME);
        tileChangerButtonHelper(homeWBackButton, tileType.HOMEWBACK);
        tileChangerButtonHelper(townhouseButton, tileType.TOWNHOUSE);
        tileChangerButtonHelper(hotelButton, tileType.HOTEL);
        tileChangerButtonHelper(apartmentButton, tileType.APARTMENT);
        tileChangerButtonHelper(villaButton, tileType.VILLA);
        tileChangerButtonHelper(mansionButton, tileType.MANSION);
        tileChangerButtonHelper(firstResButton, tileType.FIRSTRES);

        tileChangerButtonHelper(convStoreButton, tileType.CONVSTORE);
        tileChangerButtonHelper(fastFoodButton, tileType.FASTFOOD);
        tileChangerButtonHelper(clothingButton, tileType.CLOTHING);
        tileChangerButtonHelper(groceryButton, tileType.GROCERY);
        tileChangerButtonHelper(furnitureButton, tileType.FURNITURE);
        tileChangerButtonHelper(salonButton, tileType.SALON);
        tileChangerButtonHelper(cafeButton, tileType.CAFE);
        tileChangerButtonHelper(toyButton, tileType.TOY);
        tileChangerButtonHelper(restaurantButton, tileType.RESTAURANT);
        tileChangerButtonHelper(jewelryButton, tileType.JEWELRY);
        tileChangerButtonHelper(stripmallButton, tileType.STRIPMALL);
        tileChangerButtonHelper(technologyButton, tileType.TECHNOLOGY);

        tileChangerButtonHelper(cropsButton, tileType.CROPS);
        tileChangerButtonHelper(wellButton, tileType.WELL);
        tileChangerButtonHelper(livestockButton, tileType.LIVESTOCK);
        tileChangerButtonHelper(energyButton, tileType.ENERGY);
        tileChangerButtonHelper(factoryButton, tileType.FACTORY);
        tileChangerButtonHelper(quarryButton, tileType.QUARRY);
        tileChangerButtonHelper(wasteButton, tileType.WASTE);
        tileChangerButtonHelper(emergButton, tileType.EMERG);
        tileChangerButtonHelper(officesButton, tileType.OFFICES);
        tileChangerButtonHelper(greenhousesButton, tileType.GREENHOUSES);
        tileChangerButtonHelper(jailButton, tileType.JAIL);
        tileChangerButtonHelper(hospitalButton, tileType.HOSPITAL);

        tileChangerButtonHelper(churchButton, tileType.CHURCH);
        tileChangerButtonHelper(parkButton, tileType.PARK);
        tileChangerButtonHelper(commCentButton, tileType.COMMCENT);
        tileChangerButtonHelper(libraryButton, tileType.LIBRARY);
        tileChangerButtonHelper(tSquareButton, tileType.TSQUARE);
        tileChangerButtonHelper(cinemaButton, tileType.CINEMA);
        tileChangerButtonHelper(schoolButton, tileType.SCHOOL);
        tileChangerButtonHelper(natureTButton, tileType.NATURET);
        tileChangerButtonHelper(sportsCButton, tileType.SPORTSC);
        tileChangerButtonHelper(cityHallButton, tileType.CITYHALL);
        tileChangerButtonHelper(festivalGButton, tileType.FESTIVALG);
        tileChangerButtonHelper(stadiumButton, tileType.STADIUM);

        buildingButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (buildingScrollPane.isVisible()) {
                    buildingScrollPane.setVisible(false);
                    return false;
                } else {
                    buildingScrollPane.setVisible(true);
                    return terrainButtonHelper(tileType.SHACK);
                }
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
        if (!tileType.tileBuildingType.contains(t)) {
            buildingButton.setChecked(false);
            buildingScrollPane.setVisible(false);
        }
        return super.terrainButtonHelper(t);
    }

    private boolean switchGroups(HorizontalGroup group) {
        if (!group.equals(residentialGroup)) {
            residentialButton.setChecked(false);
        }
        if (!group.equals(commercialGroup)) {
            commercialButton.setChecked(false);
        }
        if (!group.equals(industrialGroup)) {
            industrialButton.setChecked(false);
        }
        if (!group.equals(civicGroup)) {
            civicButton.setChecked(false);
        }
        scrollPaneTable.removeActorAt(1, false);
        scrollPaneTable.add(group);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.R) {
            worldManager.increaseRotationSelection();
        }
        return false;
    }
}
