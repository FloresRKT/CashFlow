package com.CS22S4.hehe.events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventManager {
    private static EventManager instance;
    private final Map<Class<? extends Event>, Set<EventListener>> listeners;

    private EventManager() {
        this.listeners = new HashMap<>();
    }

    public static void initialize() {
        if (instance == null) {
            instance = new EventManager();
        }
    }

    public static EventManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EventManager not initialized");
        }
        return instance;
    }

    public void register(Class<? extends Event> eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new HashSet<>()).add(listener);
    }

    public void fireEvent(Event event, Stage stage) {
        Set<EventListener> eventListeners = listeners.get(event.getClass());
        // System.out.println("Firing event: " + event.getClass().getSimpleName());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                Gdx.app.postRunnable(() -> listener.handle(event));
            }
        }

        // Dispatch the event to the Stage
        if (stage != null) {
            Gdx.app.postRunnable(() -> stage.getRoot().fire(event));
        }
    }

    public void addListener(EventListener listener, Stage stage) {
        stage.addListener(listener);
    }
}
