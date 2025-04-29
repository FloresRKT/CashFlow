package com.CS22S4.hehe.overlays;

import com.CS22S4.hehe.App;
import com.CS22S4.hehe.events.EventManager;
import com.CS22S4.hehe.events.OverlayEvent;
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
    private Label title;
    private Label totalCustomersServed;
    private Label totalAmountDispensed;
    private Label totalScore;
    private TextButton restartButton;
    private TextButton quitButton;

    public PostGameOverlay(Viewport viewport, App game, Stage stage, Skin skin) {
        super(viewport);
        this.skin = skin;

        createOverlayContent(viewport);

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
        restartButton = new TextButton("OK", skin.get("transparentButton", TextButton.TextButtonStyle.class));
        quitButton = new TextButton("Quit to Menu", skin.get("transparentButton", TextButton.TextButtonStyle.class));

        rootTable.add(title).padBottom(20).center().row();
        rootTable.add(totalCustomersServed).padBottom(10).left().row();
        rootTable.add(totalAmountDispensed).padBottom(10).left().row();
        rootTable.add(restartButton).row();
        rootTable.add(quitButton).row();

        // Inside your PauseOverlay constructor or initialization method
        Texture whiteTexture = new Texture("white_bg.png"); // A plain white 1x1 texture
        Image background = new Image(new TextureRegionDrawable(whiteTexture));
        background.setColor(1, 1, 1, 0.5f); // Set white color with 50% transparency
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        background.setPosition(0, 0);

        overlayStage.clear();
        overlayStage.addActor(background);
        overlayStage.addActor(rootTable);
    }
}
