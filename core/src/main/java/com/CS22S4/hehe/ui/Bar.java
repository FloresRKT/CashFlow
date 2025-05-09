package com.CS22S4.hehe.ui;

import com.badlogic.gdx.graphics.Color;

public class Bar {
    private float x, y, width, height;
    private Color color;

    public Bar(Color color) {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.color = color;
    }

    public void setRect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Color getColor() { return color; }
}
