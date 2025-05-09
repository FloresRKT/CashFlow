package com.CS22S4.hehe.overlays;

import com.CS22S4.hehe.App;
import com.CS22S4.hehe.events.EventManager;
import com.CS22S4.hehe.events.OverlayEvent;
import com.CS22S4.hehe.screens.GameScreen;
import com.CS22S4.hehe.screens.MainMenuScreen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PostGameOverlay extends BaseOverlay {
    private final Skin skin;
    private App game;
    private Label title;
    private Label totalCustomersServed;
    private Label totalAmountDispensed;
    private Label totalScore;
    private TextButton restartButton;
    private TextButton quitButton;

    public PostGameOverlay(Viewport viewport, App game, Stage stage, Skin skin) {
        super(viewport);
        this.skin = skin;
        this.game = game;

        createOverlayContent(viewport);

        restartButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                EventManager.getInstance().fireEvent(new OverlayEvent(OverlayEvent.Type.TOGGLE_OVERLAY, null), overlayStage);
                int difficulty = ((GameScreen) game.getScreen()).getGameMode();
                game.screenManager.popOverlay();
                game.screenManager.popScreen();
                game.screenManager.pushScreen(new GameScreen(game, difficulty));
                return true;
            }
        });

        quitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                EventManager.getInstance().fireEvent(new OverlayEvent(OverlayEvent.Type.TOGGLE_OVERLAY, null), overlayStage);
                game.screenManager.popOverlay();
                game.screenManager.popScreen();
                game.screenManager.pushScreen(new MainMenuScreen(game));
                return true;
            }
        });
    }

    @Override
    protected void createOverlayContent(Viewport viewport) {
        title = new Label("Game Over", skin.get("overlayTitleLabel", Label.LabelStyle.class));
        totalCustomersServed = new Label("Total Customers Served: ", skin.get("overlayBodyLabel", Label.LabelStyle.class));
        totalAmountDispensed = new Label("Total Amount Dispensed: ", skin.get("overlayBodyLabel", Label.LabelStyle.class));
        totalScore = new Label("Total Score: ", skin.get("overlayBodyLabel", Label.LabelStyle.class));
        restartButton = new TextButton("Restart", skin.get("transparentButton", TextButton.TextButtonStyle.class));
        quitButton = new TextButton("Quit to Menu", skin.get("transparentButton", TextButton.TextButtonStyle.class));

        rootTable.add(title).padBottom(20).center().row();
        rootTable.add(totalCustomersServed).padBottom(10).left().row();
        rootTable.add(totalAmountDispensed).padBottom(10).left().row();
        rootTable.add(restartButton).row();
        rootTable.add(quitButton).row();

        Image overlayBg = new Image(game.textureManager.overlayTexture);
        overlayBg.setSize(viewport.getWorldWidth() * 0.5f, viewport.getWorldHeight() * 0.6f);
        overlayBg.setPosition(viewport.getWorldWidth()/2 - overlayBg.getWidth()/2, viewport.getWorldHeight()/2 - overlayBg.getHeight()/2);

        overlayStage.clear();
        overlayStage.addActor(overlayBg);
        overlayStage.addActor(rootTable);
    }
}
