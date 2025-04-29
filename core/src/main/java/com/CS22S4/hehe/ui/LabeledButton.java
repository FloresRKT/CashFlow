package com.CS22S4.hehe.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;

public class LabeledButton extends Stack {
    private Label label;
    private ImageButton button;
    private Runnable onClick;

    public LabeledButton(Label label, ImageButton button) {
        this.label = label;
        this.button = button;

        label.setAlignment(Align.center);

        addActor(button);
        addActor(label);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                LabeledButton.this.button.setChecked(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                LabeledButton.this.button.setChecked(false);
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}


