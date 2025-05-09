package com.CS22S4.hehe;

import com.CS22S4.hehe.assets.FontManager;
import com.CS22S4.hehe.assets.TextureManager;
import com.CS22S4.hehe.events.EventManager;
import com.CS22S4.hehe.screens.MainMenuScreen;
import com.CS22S4.hehe.screens.ScreenManager;
import com.CS22S4.hehe.services.SQLiteService;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class App extends Game {
    public FitViewport viewport;
    public OrthographicCamera camera;
    public Skin skin;
    public ScreenManager screenManager;
    public TextureManager textureManager;
    public SQLiteService sqliteService;
    private FontManager fontManager;

    public List<Integer> denominations;
    public float worldWidth;
    public float worldHeight;

    // Constants for game logic. Serves as difficulty modifiers
    public int LEVEL_LENGTH_IN_SECONDS = 10;
    public int MINIMUM_CUSTOMER_AMOUNT = 5;
    public int MAXIMUM_CUSTOMER_AMOUNT = 10;

    @Override
    public void create() {
        // World dimensions, used for rendering
        worldWidth = 640;
        worldHeight = 360;

        // Data for denominations in-game. Modify this
        denominations = new ArrayList<>(List.of(3, 4, 10, 15, 25, 50, 100));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth, worldHeight);
        viewport = new FitViewport(worldWidth, worldHeight, camera);

        Gdx.graphics.setWindowedMode(1280, 720);

        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        // Load fonts
        fontManager = new FontManager(skin);

        // Load database
        sqliteService = new SQLiteService();

        // Load all the textures
        textureManager = new TextureManager();
        textureManager.loadTextures();

        // Start event handler
        EventManager.initialize();

        // Load screen manager and all game screens
        screenManager = new ScreenManager(this);

        // Start with main menu after everything is initialized
        screenManager.pushScreen(new MainMenuScreen(this));
    }

    /**
     * Parent method for rendering game scenes
     *
     */
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen with a black color

        // Update viewport if window is resized
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        camera.update();

        screenManager.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        fontManager.dispose();
    }
}
