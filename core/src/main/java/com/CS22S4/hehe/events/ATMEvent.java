package com.CS22S4.hehe.events;

import com.badlogic.gdx.scenes.scene2d.Event;

public class ATMEvent extends Event {
    public enum Type {
        START_GAME,
        SCREEN_CHANGE,
        ADD_BUTTON_CLICKED,
        SUBTRACT_BUTTON_CLICKED,
        AMOUNT_CHANGED,
        GAME_PAUSED,
        GAME_OVER,
        CUSTOMER_REQUEST,  // Add new types
        CUSTOMER_LEAVE,
        DISPENSE
    }

    private final Type type;
    private final Object data;

    public ATMEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
