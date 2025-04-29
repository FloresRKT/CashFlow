package com.CS22S4.hehe.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class FontManager {
    private BitmapFont defaultFont;
    private BitmapFont calculatorFont;
    private BitmapFont calculatorFontSmall;
    private BitmapFont segoeUI10;
    private BitmapFont segoeUI20;
    private BitmapFont segoeUI100;
    private BitmapFont segoeUIBlack36;
    private Label.LabelStyle defaultLabelStyle;
    private Label.LabelStyle gameScreenLabelStyle;
    private Label.LabelStyle numericlabelStyle;
    private Label.LabelStyle numericlabelStyleSmall;
    private Label.LabelStyle titleLabelStyle;
    private Label.LabelStyle overlayTitleLabelStyle;
    private Label.LabelStyle overlayBodyLabelStyle;
    private TextButton.TextButtonStyle buttonStyle;

    public FontManager(Skin skin) {
        initializeFonts(skin);
    }

    private void initializeFonts(Skin skin) {
        defaultFont = new BitmapFont();

        segoeUI10 = generateFont("C:/Windows/Fonts/segoeui.ttf", 10, Color.WHITE);
        segoeUI20 = generateFont("C:/Windows/Fonts/segoeui.ttf", 20, Color.BLACK);
        segoeUI100 = generateFont("C:/Windows/Fonts/segoeui.ttf", 100, Color.valueOf("494744"));
        segoeUIBlack36 = generateFont("C:/Windows/Fonts/segoeui.ttf", 36, Color.BLACK);
        calculatorFont = generateFont("fonts/Calculator.ttf", 24, Color.WHITE);
        calculatorFontSmall = generateFont("fonts/Calculator.ttf", 14, Color.WHITE);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        createStyles();
        addToSkin(skin);
    }

    private BitmapFont generateFont(String fontPath, int size, Color color) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

    private void createStyles() {
        defaultLabelStyle = new Label.LabelStyle(defaultFont, null);
        gameScreenLabelStyle = new Label.LabelStyle(segoeUI10, null);
        numericlabelStyle = new Label.LabelStyle(calculatorFont, null);
        numericlabelStyleSmall = new Label.LabelStyle(calculatorFontSmall, null);
        titleLabelStyle = new Label.LabelStyle(segoeUI100, null);
        overlayTitleLabelStyle = new Label.LabelStyle(segoeUIBlack36, null);
        overlayBodyLabelStyle = new Label.LabelStyle(segoeUI20, Color.BLACK);

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = defaultFont;
        buttonStyle.fontColor = Color.BLACK;
    }

    public void addToSkin(Skin skin) {
        skin.add("default", defaultFont);
        skin.add("segoeUI11", segoeUI10);
        skin.add("segoeUI100", segoeUI100);
        skin.add("segoeUIBlack36", segoeUIBlack36);
        skin.add("calculator", calculatorFont);
        skin.add("default", defaultLabelStyle);
        skin.add("gameScreenLabel", gameScreenLabelStyle);
        skin.add("numericLabel", numericlabelStyle);
        skin.add("numericLabelSmall", numericlabelStyleSmall);
        skin.add("titleLabel", titleLabelStyle);
        skin.add("overlayTitleLabel", overlayTitleLabelStyle);
        skin.add("overlayBodyLabel", overlayBodyLabelStyle);
        skin.add("default", buttonStyle);
    }

    public void dispose() {
        defaultFont.dispose();
        calculatorFont.dispose();
        segoeUI10.dispose();
        segoeUI100.dispose();
    }
}
