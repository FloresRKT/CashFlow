package com.CS22S4.hehe.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class BaseOverlay {
    protected Stage overlayStage;
    protected Table rootTable;

    public BaseOverlay(Viewport viewport) {
        overlayStage = new Stage(viewport);
        rootTable = new Table();
        rootTable.setFillParent(true);
        overlayStage.addActor(rootTable);
    }

    // Method to be implemented by subclasses to define overlay content
    protected abstract void createOverlayContent(Viewport viewport);

    public void showOverlay() {
        overlayStage.getRoot().setVisible(true);
        Gdx.input.setInputProcessor(overlayStage);
    }

    public void hideOverlay(Stage stage) {
        overlayStage.getRoot().setVisible(false);
        Gdx.input.setInputProcessor(stage);
    }

    public void render(float delta) {
        overlayStage.act(delta);
        overlayStage.draw();
    }

    public void resize(int width, int height) {
        overlayStage.getViewport().update(width, height, true);
    }

    public void dispose() {
        overlayStage.dispose();
    }

    public Stage getStage() {
        return overlayStage;
    }
}
