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
    private Label title;
    private Label body;
    private TextButton confirmButton;

    public AboutOverlay(Viewport viewport, App game, Stage stage, Skin skin) {
        super(viewport);
        this.skin = skin;

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
        body = new Label("", skin.get("default", Label.LabelStyle.class));
        confirmButton = new TextButton("OK", skin.get("transparentButton", TextButton.TextButtonStyle.class));

        rootTable.add(title).padBottom(20).row();
        rootTable.add(body).padBottom(10).row();
        rootTable.add(confirmButton);

        // Inside your PauseOverlay constructor or initialization method
        Texture whiteTexture = new Texture("white_bg.png"); // A plain white 1x1 texture
        Image background = new Image(new TextureRegionDrawable(whiteTexture));
        background.setColor(1, 1, 1, 0.9f); // Set white color with 50% transparency
        background.setSize(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2);
        background.setPosition(viewport.getWorldWidth()/2 - background.getWidth()/2, viewport.getWorldHeight()/2 - background.getHeight()/2);

        overlayStage.clear();
        overlayStage.addActor(background);
        overlayStage.addActor(rootTable);
    }
}
