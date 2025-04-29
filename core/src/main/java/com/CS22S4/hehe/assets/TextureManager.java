package com.CS22S4.hehe.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class TextureManager {
    public Texture correctButtonEnabledTexture;
    public Texture correctButtonDisabledTexture;
    public Texture wrongButtonDisabledTexture;
    public Texture wrongButtonEnabledTexture;
    public Texture dispenseButtonUpTexture;
    public Texture dispenseButtonDownTexture;
    public Texture atmCardTexture;
    public Texture atmBgTexture;
    public Texture atmBorderTexture;
    public Texture buttonContainerTexture;
    public Texture buttonTexture;
    public Texture scoreLabelTexture;
    public Texture amountLabelTexture;
    public Texture denominationTexture;
    public Texture gameBgTexture;
    public Texture mainMenuBgTexture;
    public Texture pauseButtonTexture;
    public Texture menuLeftButtonUpTexture;
    public Texture menuLeftButtonDownTexture;
    public Texture menuRightButtonUpTexture;
    public Texture menuRightButtonDownTexture;
    public Texture menuScreenTexture1;
    public Texture menuScreenTexture2;

    public Array<Texture> npcTextures;

    // Method wrapper for loading all sprite textures
    public void loadTextures() {
        npcTextures = new Array<>();
        npcTextures.add(new Texture(Gdx.files.internal("npc_1.png")));
        npcTextures.add(new Texture(Gdx.files.internal("npc_2.png")));
        npcTextures.add(new Texture(Gdx.files.internal("npc_3.png")));
        npcTextures.add(new Texture(Gdx.files.internal("npc_4.png")));
        npcTextures.add(new Texture(Gdx.files.internal("npc_5.png")));

        menuScreenTexture1 = new Texture(Gdx.files.internal("menu_screen_1.png"));
        menuScreenTexture2 = new Texture(Gdx.files.internal("menu_screen_2.png"));

        atmBgTexture = new Texture(Gdx.files.internal("atm_screen_bg.png"));
        atmBorderTexture = new Texture(Gdx.files.internal("atm_screen.png"));
        buttonContainerTexture = new Texture(Gdx.files.internal("button_container_bg.png"));
        correctButtonEnabledTexture = new Texture(Gdx.files.internal("correct_button_enabled.png"));
        correctButtonDisabledTexture = new Texture(Gdx.files.internal("correct_button_disabled.png"));
        wrongButtonEnabledTexture = new Texture(Gdx.files.internal("wrong_button_enabled.png"));
        wrongButtonDisabledTexture = new Texture(Gdx.files.internal("wrong_button_disabled.png"));
        atmCardTexture = new Texture(Gdx.files.internal("atm_card.png"));
        dispenseButtonUpTexture = new Texture(Gdx.files.internal("dispense_button_up.png"));
        dispenseButtonDownTexture = new Texture(Gdx.files.internal("dispense_button_down.png"));
        buttonTexture = new Texture(Gdx.files.internal("button_generic.png"));
        scoreLabelTexture = new Texture(Gdx.files.internal("score_label.png"));
        amountLabelTexture = new Texture(Gdx.files.internal("amount_label.png"));
        denominationTexture = new Texture(Gdx.files.internal("denomination.png"));
        gameBgTexture = new Texture(Gdx.files.internal("game_bg.png"));
        mainMenuBgTexture = new Texture(Gdx.files.internal("mainmenu_bg.png"));
        pauseButtonTexture = new Texture(Gdx.files.internal("pause_button.png"));
        menuLeftButtonUpTexture = new Texture(Gdx.files.internal("menu_button_left_up.png"));
        menuLeftButtonDownTexture = new Texture(Gdx.files.internal("menu_button_left_down.png"));
        menuRightButtonUpTexture = new Texture(Gdx.files.internal("menu_button_right_up.png"));
        menuRightButtonDownTexture = new Texture(Gdx.files.internal("menu_button_right_down.png"));
    }
}
