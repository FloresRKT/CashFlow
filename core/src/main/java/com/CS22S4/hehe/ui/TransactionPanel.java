package com.CS22S4.hehe.ui;

import com.CS22S4.hehe.App;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

public class TransactionPanel extends WidgetGroup {
    private final Image bgImage;
    private final Label titleLabel;
    private final Label valueLabel;

    public TransactionPanel(App game, Texture texture, String labelText) {
        bgImage = new Image(texture);
        bgImage.setFillParent(true);

        titleLabel = new Label(labelText, game.skin, "gameScreenLabel");
        titleLabel.setAlignment(Align.center);

        valueLabel = new Label("0", game.skin, "numericLabel");
        //valueLabel.setSize(getWidth() * 0.85f, getHeight() * 0.3f);
        valueLabel.setAlignment(Align.right);

        // Layout the elements
        layoutElements();

        addActor(bgImage);
        addActor(titleLabel);
        addActor(valueLabel);
    }

    public void layoutElements() {
        titleLabel.setSize(getWidth() * 0.4f, getHeight() * 0.3f);
        titleLabel.setPosition(getWidth() * 0.075f, getHeight() * 0.66f);
        valueLabel.setPosition(getWidth() * 0.875f, getHeight() * 0.12f);
    }

    public void changeAmount(int value) {
        valueLabel.setText(value);
        layoutElements();
    }

    public int getAmount() {
        return Integer.parseInt(valueLabel.getText().toString());
    }
}
