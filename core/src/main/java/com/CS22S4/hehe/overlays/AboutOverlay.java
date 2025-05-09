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

public class AboutOverlay extends BaseOverlay {
    private final Skin skin;
    private App game;
    private Label title;
    private Label body;
    private TextButton confirmButton;

    public AboutOverlay(Viewport viewport, App game, Stage stage, Skin skin) {
        super(viewport);
        this.skin = skin;
        this.game = game;

        createOverlayContent(viewport);

        EventManager.getInstance().register(OverlayEvent.class, event -> {
            OverlayEvent overlayEvent = (OverlayEvent) event;
            if (overlayEvent.getType() == OverlayEvent.Type.TOGGLE_OVERLAY) {
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
        title = new Label("About", skin.get("overlayTitleLabel", Label.LabelStyle.class));
        confirmButton = new TextButton("OK", skin.get("transparentButton", TextButton.TextButtonStyle.class));

        rootTable.add(title).padBottom(20).row();
        rootTable.add(new Label("CashFlow: An ATM Game", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("CS 201A - Data Structures and Algorithms Analysis", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("CS22S4", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("------------------------------", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("Group Hehe", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("Trazo, Ysabella Moeka", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("Flores, Renz Ken", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("Rodriguez, Dave Matthew", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(new Label("Tunay, Aron Mc Vincent", skin, "overlayBodyLabel")).padBottom(10).row();
        rootTable.add(confirmButton);

        // Inside your PauseOverlay constructor or initialization method
        Image overlayBg = new Image(game.textureManager.overlayTexture);
        overlayBg.setSize(viewport.getWorldWidth() * 0.7f, viewport.getWorldHeight() * 0.8f);
        overlayBg.setPosition(viewport.getWorldWidth()/2 - overlayBg.getWidth()/2, viewport.getWorldHeight()/2 - overlayBg.getHeight()/2);

        overlayStage.clear();
        overlayStage.addActor(overlayBg);
        overlayStage.addActor(rootTable);
    }
}
