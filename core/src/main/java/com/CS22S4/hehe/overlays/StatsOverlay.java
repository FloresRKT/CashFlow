package com.CS22S4.hehe.overlays;

import com.CS22S4.hehe.App;
import com.CS22S4.hehe.events.EventManager;
import com.CS22S4.hehe.events.OverlayEvent;
import com.badlogic.gdx.Gdx;
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

public class StatsOverlay extends BaseOverlay {
    private final Skin skin;
    private App game;
    private Label title;
    private Label totalCustomersServed;
    private Label totalAmountDispensed;
    private TextButton confirmButton;
    private TextButton resetButton;

    public StatsOverlay(Viewport viewport, App game, Stage stage, Skin skin) {
        super(viewport);
        this.skin = skin;
        this.game = game;

        createOverlayContent(viewport);

        // Register to listen for events
        EventManager.getInstance().register(OverlayEvent.class, event -> {
            OverlayEvent overlayEvent = (OverlayEvent) event;
            if (overlayEvent.getType() == OverlayEvent.Type.TOGGLE_OVERLAY) {
                // Hide the overlay and resume the game
                Gdx.input.setInputProcessor(stage);
                game.screenManager.popOverlay();
            }
            return true;
        });

        confirmButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                EventManager.getInstance().fireEvent(new OverlayEvent(OverlayEvent.Type.TOGGLE_OVERLAY, null), overlayStage);
                return true;
            }
        });
    }

    @Override
    protected void createOverlayContent(Viewport viewport) {
        title = new Label("Statistics", skin.get("overlayTitleLabel", Label.LabelStyle.class));
        totalCustomersServed = new Label("Total Customers Served: ", skin.get("overlayBodyLabel", Label.LabelStyle.class));
        totalAmountDispensed = new Label("Total Amount Dispensed: ", skin.get("overlayBodyLabel", Label.LabelStyle.class));
        confirmButton = new TextButton("OK", skin.get("transparentButton", TextButton.TextButtonStyle.class));
        resetButton = new TextButton("Reset my progress", skin.get("transparentButton", TextButton.TextButtonStyle.class));

        rootTable.add(title).padBottom(20).center().row();
        rootTable.add(totalCustomersServed).padBottom(10).left().row();
        rootTable.add(totalAmountDispensed).padBottom(10).left().row();
        rootTable.add(confirmButton).row();
        rootTable.add(resetButton).row();

        Image overlayBg = new Image(game.textureManager.overlayTexture);
        overlayBg.setSize(viewport.getWorldWidth() * 0.5f, viewport.getWorldHeight() * 0.6f);
        overlayBg.setPosition(viewport.getWorldWidth()/2 - overlayBg.getWidth()/2, viewport.getWorldHeight()/2 - overlayBg.getHeight()/2);

        overlayStage.clear();
        overlayStage.addActor(overlayBg);
        overlayStage.addActor(rootTable);
    }
}
