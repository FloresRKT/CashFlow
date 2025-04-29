package com.CS22S4.hehe.screens;

import com.CS22S4.hehe.App;
import com.CS22S4.hehe.overlays.AboutOverlay;
import com.CS22S4.hehe.overlays.StatsOverlay;
import com.CS22S4.hehe.ui.LabeledButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final App game;
    public Stage stage;
    public LabeledButton menuButton1;
    public LabeledButton menuButton2;
    public LabeledButton menuButton3;
    public LabeledButton menuButton4;
    public LabeledButton menuButton5;
    public LabeledButton menuButton6;
    private Image backgroundImage;
    private Image menuScreen1;
    private Image menuScreen2;

    private Stage overlayStage;
    private boolean isOverlayVisible;

    public MainMenuScreen(App game) {
        this.game = game;

        stage = new Stage(new ScreenViewport(game.camera));
        overlayStage = new Stage(new ScreenViewport(game.camera));
        isOverlayVisible = false;

        // Use InputMultiplexer to handle input for both stages
        Gdx.input.setInputProcessor(new InputMultiplexer(overlayStage, stage));

        createUI();
        setupEventHandlers();
    }

    private void createUI() {
        Table leftTable = new Table();
        Table rightTable = new Table();

        leftTable.setPosition(game.viewport.getWorldWidth() * 0.14f, game.viewport.getWorldHeight() * 0.5f);
        rightTable.setPosition(game.viewport.getWorldWidth() * 0.86f, game.viewport.getWorldHeight() * 0.5f);

        menuScreen1 = new Image(game.textureManager.menuScreenTexture1);
        menuScreen1.setSize(game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        menuScreen1.setPosition(0, 0);

        menuScreen2 = new Image(game.textureManager.menuScreenTexture2);
        menuScreen2.setSize(game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        menuScreen2.setPosition(0, 0);

        backgroundImage = new Image(game.textureManager.mainMenuBgTexture);
        backgroundImage.setSize(game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        backgroundImage.setPosition(0, 0);
        backgroundImage.setZIndex(0);

        ImageButton.ImageButtonStyle menuLeftButtonStyle = new ImageButton.ImageButtonStyle();
        menuLeftButtonStyle.up = new Image(game.textureManager.menuLeftButtonUpTexture).getDrawable();
        menuLeftButtonStyle.checked = new Image(game.textureManager.menuLeftButtonDownTexture).getDrawable();

        ImageButton.ImageButtonStyle menuRightButtonStyle = new ImageButton.ImageButtonStyle();
        menuRightButtonStyle.up = new Image(game.textureManager.menuRightButtonUpTexture).getDrawable();
        menuRightButtonStyle.checked = new Image(game.textureManager.menuRightButtonDownTexture).getDrawable();

        menuButton1 = new LabeledButton(new Label("", game.skin, "default"), new ImageButton(menuLeftButtonStyle));
        menuButton2 = new LabeledButton(new Label("", game.skin, "default"), new ImageButton(menuLeftButtonStyle));
        menuButton3 = new LabeledButton(new Label("", game.skin, "default"), new ImageButton(menuLeftButtonStyle));

        menuButton4 = new LabeledButton(new Label("", game.skin, "default"), new ImageButton(menuRightButtonStyle));
        menuButton5 = new LabeledButton(new Label("", game.skin, "default"), new ImageButton(menuRightButtonStyle));
        menuButton6 = new LabeledButton(new Label("", game.skin, "default"), new ImageButton(menuRightButtonStyle));

        float buttonWidth = game.viewport.getWorldWidth() * 0.1f;
        float buttonHeight = game.viewport.getWorldHeight() * 0.12f;

        // Add the buttons to the table
        leftTable.add(menuButton1).width(buttonWidth).height(buttonHeight);
        leftTable.row().pad(game.viewport.getWorldHeight() * 0.07f, 0, 0, 0);
        leftTable.add(menuButton2).width(buttonWidth).height(buttonHeight);
        leftTable.row().pad(game.viewport.getWorldHeight() * 0.07f, 0, 0, 0);
        leftTable.add(menuButton3).width(buttonWidth).height(buttonHeight);

        rightTable.add(menuButton4).width(buttonWidth).height(buttonHeight);
        rightTable.row().pad(game.viewport.getWorldHeight() * 0.07f, 0, 0, 0);
        rightTable.add(menuButton5).width(buttonWidth).height(buttonHeight);
        rightTable.row().pad(game.viewport.getWorldHeight() * 0.08f, 0, 0, 0);
        rightTable.add(menuButton6).width(buttonWidth).height(buttonHeight);

        stage.addActor(backgroundImage);
        stage.addActor(menuScreen1);
        stage.addActor(menuScreen2);
        stage.addActor(leftTable);
        stage.addActor(rightTable);

        menuScreen2.setVisible(false);
    }

    private void setupEventHandlers() {
        menuButton1.setOnClick(() -> {
            if (menuScreen1.isVisible()) {
                menuScreen1.setVisible(false);
                menuScreen2.setVisible(true);
            } else {
                game.screenManager.popScreen();
                game.screenManager.pushScreen(new GameScreen(game));
            }
        });

        menuButton2.setOnClick(() -> {
            if (menuScreen1.isVisible()) {
                game.screenManager.pushOverlay(new AboutOverlay(new ScreenViewport(), game, stage, game.skin));
            } else {
                game.screenManager.popScreen();
                game.screenManager.pushScreen(new GameScreen(game));
            }
        });

        menuButton3.setOnClick(() -> {
            if (menuScreen1.isVisible()) {
                Gdx.app.exit();
            } else {
                game.screenManager.popScreen();
                game.screenManager.pushScreen(new GameScreen(game));
            }
        });

        menuButton4.setOnClick(() -> {
            if (menuScreen1.isVisible()) {
                game.screenManager.pushOverlay(new StatsOverlay(new ScreenViewport(), game, stage, game.skin));
            } else {
                menuScreen1.setVisible(true);
                menuScreen2.setVisible(false);
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Update and draw the main stage
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        // Update and draw the overlay stage if visible
        if (isOverlayVisible) {
            overlayStage.act(delta);
            overlayStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        stage.clear();
    }

    @Override
    public void dispose() {
    }
}
