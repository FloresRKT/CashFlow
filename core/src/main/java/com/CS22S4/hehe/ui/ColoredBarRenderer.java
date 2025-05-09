package com.CS22S4.hehe.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

public class ColoredBarRenderer {
    private ShapeRenderer shapeRenderer;

    public ColoredBarRenderer() {
        shapeRenderer = new ShapeRenderer();
    }

    public void renderBar(float x, float y, float width, float height, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);    // Draw filled rectangles
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }

    // Method to render multiple bars
    public void renderBars(List<Bar> bars, Viewport viewport) {
        // Apply the viewport to ensure correct coordinate mapping
        viewport.apply();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined); // Set the projection matrix
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Bar bar : bars) {
            shapeRenderer.setColor(bar.getColor());
            shapeRenderer.rect(bar.getX(), bar.getY(), bar.getWidth(), bar.getHeight());
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
