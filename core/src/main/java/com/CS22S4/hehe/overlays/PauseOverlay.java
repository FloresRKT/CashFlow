package com.CS22S4.hehe.overlays;

import com.CS22S4.hehe.App;
import com.CS22S4.hehe.events.EventManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.CS22S4.hehe.events.OverlayEvent;

public class PauseOverlay extends BaseOverlay {
    private final Skin skin;
    private App game;
    private TextButton resumeButton;
    private TextButton quitButton;

    public PauseOverlay(Viewport viewport, App game, Stage stage, Skin skin) {
        super(viewport);
        this.skin = skin;
        this.game = game;

        createOverlayContent(viewport);

        // Register to listen for events
        EventManager.getInstance().register(OverlayEvent.class, event -> {
            OverlayEvent overlayEvent = (OverlayEvent) event;
            if (overlayEvent.getType() == OverlayEvent.Type.RESUME_GAME) {
                game.screenManager.popOverlay();
                game.screenManager.peekScreen().resume();
            }
            return true;
        });

        resumeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                EventManager.getInstance().fireEvent(new OverlayEvent(OverlayEvent.Type.RESUME_GAME, null), overlayStage);
                return true;
            }
        });

        quitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                EventManager.getInstance().fireEvent(new OverlayEvent(OverlayEvent.Type.QUIT_GAME, null), stage);
                return true;
            }
        });
    }

    protected void createOverlayContent(Viewport viewport) {
        Label pauseLabel = new Label("Paused", skin.get("overlayTitleLabel", Label.LabelStyle.class));
        resumeButton = new TextButton("Resume", skin.get("transparentButton", TextButton.TextButtonStyle.class));
        quitButton = new TextButton("Quit", skin.get("transparentButton", TextButton.TextButtonStyle.class));

        Image overlayBg = new Image(game.textureManager.overlayTexture);
        overlayBg.setSize(viewport.getWorldWidth() * 0.5f, viewport.getWorldHeight() * 0.4f);
        overlayBg.setPosition(viewport.getWorldWidth()/2 - overlayBg.getWidth()/2, viewport.getWorldHeight()/2 - overlayBg.getHeight()/2);

        rootTable.add(pauseLabel).padBottom(20).row();
        rootTable.add(resumeButton).padBottom(10).row();
        rootTable.add(quitButton);

        overlayStage.clear();
        overlayStage.addActor(overlayBg);
        overlayStage.addActor(rootTable);
    }
}
