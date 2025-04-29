package com.CS22S4.hehe.screens;

import com.CS22S4.hehe.App;
import com.CS22S4.hehe.overlays.BaseOverlay;
import com.badlogic.gdx.Screen;

import java.util.Stack;

public class ScreenManager {
    private final App game;
    private final Stack<Screen> screenStack;
    private final Stack<BaseOverlay> overlayStack;

    public ScreenManager(App game) {
        this.game = game;
        this.screenStack = new Stack<>();
        this.overlayStack = new Stack<>();
    }

    private void debugStack() {
        System.out.println("Screen stack size: " + screenStack.size());
        System.out.println("Overlay stack size: " + overlayStack.size());
    }

    public void pushScreen(Screen screen) {
        if (!screenStack.isEmpty()) {
            screenStack.peek().hide();
        }
        screenStack.push(screen);
        game.setScreen(screen);
        screen.show();
        debugStack();
    }

    public void pushOverlay(BaseOverlay overlay) {
        if (!overlayStack.isEmpty()) {
            overlayStack.peek().hideOverlay(overlay.getStage());
        }
        overlayStack.push(overlay);
        overlay.showOverlay();
        debugStack();
    }

    public void popScreen() {
        if (!screenStack.isEmpty()) {
            screenStack.pop().dispose();
            if (!screenStack.isEmpty()) {
                game.setScreen(screenStack.peek());
                screenStack.peek().show();
            }
        }
        debugStack();
    }

    public void popOverlay() {
        if (!overlayStack.isEmpty()) {
            overlayStack.pop().dispose();
            if (!overlayStack.isEmpty()) {
                overlayStack.peek().showOverlay();
            }
        }
        debugStack();
    }

    public Screen peekScreen() {
        if (!screenStack.isEmpty()) {
            return screenStack.peek();
        }
        return null;
    }

    public void update(float delta) {
        for (Screen item : screenStack) {
            item.render(delta);
        }

        for (BaseOverlay item : overlayStack) {
            item.render(delta);
        }

    }

    public void dispose() {
        while (!screenStack.isEmpty()) {
            screenStack.pop().dispose();
        }
        while (!overlayStack.isEmpty()) {
            overlayStack.pop().dispose();
        }
    }
}
