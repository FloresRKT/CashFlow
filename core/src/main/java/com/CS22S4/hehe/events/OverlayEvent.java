package com.CS22S4.hehe.events;

import com.badlogic.gdx.scenes.scene2d.Event;

public class OverlayEvent extends Event {
    public enum Type {
        TOGGLE_OVERLAY,
        RESUME_GAME,
        QUIT_GAME,
    }

    private final Type type;
    private final Object data;

    public OverlayEvent(Type type, Object data) {
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
