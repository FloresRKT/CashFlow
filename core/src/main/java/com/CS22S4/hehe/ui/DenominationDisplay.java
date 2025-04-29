package com.CS22S4.hehe.ui;

import com.CS22S4.hehe.events.ATMEvent;
import com.CS22S4.hehe.events.EventManager;
import com.CS22S4.hehe.App;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

public class DenominationDisplay extends WidgetGroup {
    private Image bgImage;
    private Rectangle addButton;
    private Rectangle subtractButton;
    private Label denominationLabel;
    private Label amountLabel;
    private final EventManager eventManager;
    private ATMEvent atmEvent;
    private boolean BUTTON_ENABLED = true;

    public DenominationDisplay(Texture texture, String denominationLabel, App game, Stage stage) {
        this.eventManager = EventManager.getInstance();

        bgImage = new Image(texture);
        bgImage.setFillParent(true);

        addButton = new Rectangle();
        subtractButton = new Rectangle();

        this.denominationLabel = new Label(denominationLabel, game.skin, "numericLabelSmall");
        this.denominationLabel.setAlignment(Align.center);

        amountLabel = new Label("0", game.skin, "numericLabelSmall");
        amountLabel.setAlignment(Align.center);

        layoutElements();

        addActor(bgImage);
        addActor(this.denominationLabel);
        addActor(amountLabel);

        // Input listener for add and subtract buttons
        // Fires an event with the appropriate event type upon click
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isAddClicked(x, y) && BUTTON_ENABLED) {
                    atmEvent = new ATMEvent(ATMEvent.Type.ADD_BUTTON_CLICKED, Integer.parseInt(denominationLabel));
                    changeAmount(getAmount() + 1);
                    eventManager.fireEvent(atmEvent, stage);
                    return true;
                }
                if (isSubtractClicked(x, y) && BUTTON_ENABLED) {
                    if (getAmount() <= 0) {
                        return false; // Prevent negative amounts
                    }
                    atmEvent = new ATMEvent(ATMEvent.Type.SUBTRACT_BUTTON_CLICKED, Integer.parseInt(denominationLabel) * -1);
                    changeAmount(getAmount() - 1);
                    eventManager.fireEvent(atmEvent, stage);
                    return true;
                }
                return false;
            }
        });
    }

    public boolean isAddClicked(float x, float y) {
        return addButton.contains(x, y);
    }

    public boolean isSubtractClicked(float x, float y) {
        return subtractButton.contains(x, y);
    }

    public void layoutElements() {
        addButton.setSize(getWidth() * 0.4f, getHeight() * 0.125f);
        addButton.setPosition(getWidth() * 0.3f, getHeight() * 0.6f);
        subtractButton.setSize(getWidth() * 0.4f, getHeight() * 0.1f);
        subtractButton.setPosition(getWidth() * 0.3f, getHeight() * 0.475f);
        denominationLabel.setSize(getWidth() * 0.8f, getHeight() * 0.1f);
        denominationLabel.setPosition(getWidth() * 0.1f, getHeight() * 0.8f);
        amountLabel.setSize(getWidth() * 0.8f, getHeight() * 0.1f);
        amountLabel.setPosition(getWidth() * 0.1f, getHeight() * 0.2f);
    }

    public int getAmount() {
        return Integer.parseInt(amountLabel.getText().toString());
    }

    public void changeAmount(int value) {
        amountLabel.setText(String.valueOf(value));
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);

        // Draw this widget's bounds
        shapes.setColor(1, 0, 0, 1); // Red color for visibility
        shapes.rect(getX(), getY(), getWidth(), getHeight());

        // Draw the clickable bounds
        shapes.setColor(0, 1, 0, 1); // Green color for the clickable area
        shapes.rect(getX() + addButton.x, getY() + addButton.y, addButton.width, addButton.height);

        shapes.setColor(0, 1, 0, 1); // Green color for the clickable area
        shapes.rect(getX() + subtractButton.x, getY() + subtractButton.y, subtractButton.width, subtractButton.height);
    }

    public void setButtonsEnabled(boolean enabled) {
        BUTTON_ENABLED = !enabled;
    }
}
