package com.CS22S4.hehe.screens;

import com.CS22S4.hehe.entity.Customer;
import com.CS22S4.hehe.events.ATMEvent;
import com.CS22S4.hehe.events.EventManager;
import com.CS22S4.hehe.App;
import com.CS22S4.hehe.events.OverlayEvent;
import com.CS22S4.hehe.overlays.PauseOverlay;
import com.CS22S4.hehe.overlays.PostGameOverlay;
import com.CS22S4.hehe.ui.DenominationDisplay;
import com.CS22S4.hehe.ui.ScorePanel;
import com.CS22S4.hehe.ui.TransactionPanel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final App game;
    public Stage stage;
    private EventManager eventManager;
    private Customer currentCustomer;
    private ArrayList<Customer> servedCustomers;
    private ArrayList<Customer> customerQueue;

    private boolean isPauseOverlayVisible;

    private Image atmBgImage;
    private Image atmBorderImage;
    private Image atmCardImage;
    private Image bgImage;
    private Label timer;
    private Label dispenseLabel;
    public Image npcImage;

    private ImageButton correctButton;
    public ImageButton pauseButton;
    private ImageButton wrongButton;
    public ImageButton dispenseButton;
    public float timeLeft;
    public boolean isTimerRunning;

    public TransactionPanel requiredAmountDisplay;
    public TransactionPanel totalAmountDisplay;
    public ScorePanel highScoreDisplay;
    public ScorePanel ratingDisplay;
    public ArrayList<DenominationDisplay> denominationDisplays;

    private Texture lastUsedTexture;
    private boolean isAnimating = false;
    private boolean isGameOver = false;

    public GameScreen(App game) {
        this.game = game;

        stage = new Stage(new ScreenViewport(game.camera));
        Gdx.input.setInputProcessor(stage);

        // Pause screen overlay
        isPauseOverlayVisible = false;

        // Event manager
        eventManager = EventManager.getInstance();

        // Customer queue and served customers for tracking
        servedCustomers = new ArrayList<>();
        customerQueue = new ArrayList<>();

        // Set of denominations to be used
        denominationDisplays = new ArrayList<>();

        createUI();
        setupEventListeners();
    }

    @Override
    public void render(float delta) {
        game.viewport.apply();

        if (isTimerRunning && timeLeft > 0) {
            timeLeft -= delta;
            updateTimerDisplay();
        } else if (timeLeft <= 0 && !isGameOver) {
            // Fire a game over event
            isGameOver = true;
            eventManager.fireEvent(new ATMEvent(ATMEvent.Type.GAME_OVER, null), stage);
        }

        if (!isPauseOverlayVisible) {
            stage.act(delta);
        }

        stage.draw();

        // Set this to true to enable debug lines on all actors
        stage.setDebugAll(false);
    }

    // Entry point of the game loop. Automatically starts the game whenever the screen is shown
    @Override
    public void show() {
        EventManager.getInstance().fireEvent(new ATMEvent(ATMEvent.Type.START_GAME, null), stage);
    }

    @Override
    public void pause() {
        isTimerRunning = false;
        isPauseOverlayVisible = true;
        Gdx.input.setInputProcessor(null);
        game.screenManager.pushOverlay(new PauseOverlay(new ScreenViewport(), game, stage, game.skin));
    }

    @Override
    public void resume() {
        isTimerRunning = true;
        isPauseOverlayVisible = false;
        Gdx.input.setInputProcessor(stage);
    }

    // Main game loop event handling
    private void setupEventListeners() {
        eventManager.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof ATMEvent) {
                    ATMEvent atmEvent = (ATMEvent) event;
                    switch (atmEvent.getType()) {
                        case START_GAME:
                            startGame();
                            return true;
                        case GAME_OVER:
                            gameOver();
                            return true;
                        case ADD_BUTTON_CLICKED:
                        case SUBTRACT_BUTTON_CLICKED:
                            changeAmount(atmEvent);
                            return true;
                        case GAME_PAUSED:
                            pause();
                            return true;
                        case CUSTOMER_REQUEST:
                            customerRequest();
                            return true;
                        case CUSTOMER_LEAVE:
                            customerLeave();
                            return true;
                        case DISPENSE:
                            dispense();
                            return true;
                    }
                } else if (event instanceof OverlayEvent) {
                    OverlayEvent overlayEvent = (OverlayEvent) event;
                    if (overlayEvent.getType() == OverlayEvent.Type.QUIT_GAME) {
                        game.screenManager.popOverlay();
                        game.screenManager.popScreen();
                        game.screenManager.pushScreen(new MainMenuScreen(game));
                        return true;
                    }
                }
                return false;
            }
        }, stage);

        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                eventManager.fireEvent(new ATMEvent(ATMEvent.Type.GAME_PAUSED, null), stage);
                return true;
            }
        });

        dispenseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!dispenseButton.isDisabled()) {
                    eventManager.fireEvent(new ATMEvent(ATMEvent.Type.DISPENSE, null), stage);
                    return true;
                }
                return false;
            }
        });
    }

    private void startGame() {
        customerQueue.clear();
        servedCustomers.clear();

        // Generate amount of customers between the minimum and maximum
        int customerAmount = game.MINIMUM_CUSTOMER_AMOUNT +
            (int)Math.floor((Math.random() * (game.MAXIMUM_CUSTOMER_AMOUNT - game.MINIMUM_CUSTOMER_AMOUNT)));

        for (int i=0; i<customerAmount; i++) {
            customerQueue.add(new Customer(generateRandomRequestAmount()));
        }

        //eventManager.fireEvent(new ATMEvent(ATMEvent.Type.GAME_OVER, null), stage);
        eventManager.fireEvent(new ATMEvent(ATMEvent.Type.CUSTOMER_REQUEST, null), stage);
    }

    private void gameOver() {
        isTimerRunning = false;
        game.screenManager.pushOverlay(new PostGameOverlay(new ScreenViewport(), game, stage, game.skin));
    }

    private void customerRequest() {
        npcImage.setVisible(true);

        currentCustomer = customerQueue.remove(0);
        setRandomNPCTexture();
        // Set initial alpha to 0 before fading in
        npcImage.getColor().a = 0f;

        npcImage.addAction(sequence(
            run(() -> setAnimating(true)),
            fadeIn(1),
            run(() -> {
                setAnimating(false);
                requiredAmountDisplay.changeAmount(currentCustomer.getRequestedAmount());
                setWrongButtonEnabled(true);
            })
        ));
    }

    private void customerLeave() {
        servedCustomers.add(currentCustomer);

        npcImage.addAction(sequence(
            run(() -> setAnimating(true)),
            fadeOut(1),
            run(() -> {
                setAnimating(false);
                npcImage.setVisible(false);
                if (!customerQueue.isEmpty()) {
                    // Fire customer request event to set the next customer
                    eventManager.fireEvent(new ATMEvent(ATMEvent.Type.CUSTOMER_REQUEST, null), stage);
                }
            })
        ));
    }

    private void dispense() {
        float serviceTime = 300 - timeLeft;
        currentCustomer.completeRequest(serviceTime);

        // Reset UI state
        setCorrectButtonEnabled(false);
        setWrongButtonEnabled(false);
        setDispenseButtonEnabled(false);
        totalAmountDisplay.changeAmount(0);
        requiredAmountDisplay.changeAmount(0);
        ratingDisplay.changeAmount(ratingDisplay.getAmount() + 100);

        for (DenominationDisplay panel: denominationDisplays) {
            panel.changeAmount(0);
        }

        // Fire customer leave event to handle animation
        eventManager.fireEvent(new ATMEvent(ATMEvent.Type.CUSTOMER_LEAVE, null), stage);
    }

    private void changeAmount(ATMEvent event) {
        totalAmountDisplay.changeAmount(
            totalAmountDisplay.getAmount() +
                (Integer) event.getData());

        // Enable correct/wrong buttons only if amounts match/don't match
        boolean amountsMatch = totalAmountDisplay.getAmount() ==
            requiredAmountDisplay.getAmount();
        setCorrectButtonEnabled(amountsMatch);
        setWrongButtonEnabled(!amountsMatch);
        setDispenseButtonEnabled(amountsMatch);
    }

    // Method to create a valid amount for the customer request
    private int generateRandomRequestAmount() {
        int amount = 0;
        do {
            // Generate random amount of bills per denomination
            for (int denom : game.denominations) {
                amount += denom * (int)(Math.floor(Math.random() * 5));
            }
        } while (amount == 0);  // Prevent a request of 0
        return amount;
    }

    public void updateTimerDisplay() {
        int minutes = (int) (timeLeft / 60);
        int seconds = (int) (timeLeft % 60);
        timer.setText(String.format("%d:%02d", minutes, seconds));
    }

    // Set random texture for NPCs. Called whenever a new NPC spawns in
    public void setRandomNPCTexture() {
        Texture newTexture;

        // Prevent the same NPC texture from appearing twice in a row
        do {
            newTexture = game.textureManager.npcTextures.random();
        } while (newTexture == lastUsedTexture && game.textureManager.npcTextures.size > 1);   // Failsafe condition when there's only one texture

        lastUsedTexture = newTexture;
        npcImage.setDrawable(new TextureRegionDrawable(new TextureRegion(newTexture)));
    }

    // Handles input locking on animations
    public void setAnimating(boolean animating) {
        this.isAnimating = animating;

        // Disable/enable buttons based on animation state
        for (DenominationDisplay panel : denominationDisplays) {
            panel.setButtonsEnabled(isAnimating);
        }
    }

    public void setCorrectButtonEnabled(boolean enabled) {
        correctButton.setDisabled(!enabled);
        correctButton.setChecked(enabled);
    }

    public void setWrongButtonEnabled(boolean enabled) {
        wrongButton.setDisabled(!enabled);
        wrongButton.setChecked(enabled);
    }

    public void setDispenseButtonEnabled(boolean enabled) {
        dispenseButton.setDisabled(!enabled);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.clear();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    // Initialization of all UI elements and parameters
    private void createUI() {
        timer = new Label("00:00", game.skin, "numericLabel");
        timeLeft = game.LEVEL_LENGTH_IN_SECONDS;
        isTimerRunning = true;
        timer.setPosition(game.viewport.getWorldWidth() * 0.5f, game.viewport.getWorldHeight() * 0.9f);

        dispenseLabel = new Label("DISPENSE", game.skin, "gameScreenLabel");
        dispenseLabel.setPosition(game.viewport.getWorldWidth() * 0.3725f, game.viewport.getWorldHeight() * 0.366f);

        bgImage = new Image(game.textureManager.gameBgTexture);
        bgImage.setSize(game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        bgImage.setPosition(0, 0);

        npcImage = new Image(game.textureManager.npcTextures.get(0));
        npcImage.setVisible(false);
        npcImage.setSize(game.viewport.getWorldWidth() * 0.225f, game.viewport.getWorldWidth() * 0.225f);
        npcImage.setPosition(game.viewport.getWorldWidth() * 0.085f, game.viewport.getWorldHeight() * 0.45f);

        atmBgImage = new Image(game.textureManager.atmBgTexture);
        atmBgImage.setSize(game.viewport.getWorldWidth() * 0.275f, game.viewport.getWorldWidth() * 0.25f);
        atmBgImage.setPosition(game.viewport.getWorldWidth() * 0.06f, game.viewport.getWorldHeight() * 0.45f);

        atmBorderImage = new Image(game.textureManager.atmBorderTexture);
        atmBorderImage.setSize(game.viewport.getWorldWidth() * 0.3f, game.viewport.getWorldWidth() * 0.3f);
        atmBorderImage.setPosition(game.viewport.getWorldWidth() * 0.05f, game.viewport.getWorldHeight() * 0.4f);

        atmCardImage = new Image(game.textureManager.atmCardTexture);
        atmCardImage.setSize(game.viewport.getWorldWidth() * 0.225f, game.viewport.getWorldWidth() * 0.15f);
        atmCardImage.setPosition(game.viewport.getWorldWidth() * 0.375f, game.viewport.getWorldHeight() * 0.575f);

        requiredAmountDisplay = new TransactionPanel(game, game.textureManager.amountLabelTexture, "REQUIRED");
        requiredAmountDisplay.setSize(game.viewport.getWorldWidth() * 0.275f, game.viewport.getWorldHeight() * 0.15f);
        requiredAmountDisplay.setPosition(game.viewport.getWorldWidth() * 0.05f, game.viewport.getWorldHeight() * 0.225f);
        requiredAmountDisplay.layoutElements();

        totalAmountDisplay = new TransactionPanel(game, game.textureManager.amountLabelTexture, "TOTAL");
        totalAmountDisplay.setSize(game.viewport.getWorldWidth() * 0.275f, game.viewport.getWorldHeight() * 0.15f);
        totalAmountDisplay.setPosition(game.viewport.getWorldWidth() * 0.05f, game.viewport.getWorldHeight() * 0.05f);
        totalAmountDisplay.layoutElements();

        highScoreDisplay  = new ScorePanel(game, game.textureManager.scoreLabelTexture, "HIGH SCORE");
        highScoreDisplay.setSize(game.viewport.getWorldWidth() * 0.275f, game.viewport.getWorldHeight() * 0.15f);
        highScoreDisplay.setPosition(game.viewport.getWorldWidth() * 0.625f, game.viewport.getWorldHeight() * 0.7f);
        highScoreDisplay.layoutElements();

        ratingDisplay = new ScorePanel(game, game.textureManager.scoreLabelTexture, "RATING");
        ratingDisplay.setSize(game.viewport.getWorldWidth() * 0.275f, game.viewport.getWorldHeight() * 0.15f);
        ratingDisplay.setPosition(game.viewport.getWorldWidth() * 0.625f, game.viewport.getWorldHeight() * 0.525f);
        ratingDisplay.layoutElements();

        float denominationPanelSectionWidth = game.viewport.getWorldWidth() * 0.55f;
        float panelSpacing = denominationPanelSectionWidth / (game.denominations.size() + 1);

        for (int i = 0; i < game.denominations.size(); i++) {
            DenominationDisplay panel = getDenominationDisplay(i, panelSpacing);
            panel.layoutElements();
            denominationDisplays.add(panel);
        }

        ImageButton.ImageButtonStyle pauseStyle = new ImageButton.ImageButtonStyle();
        pauseStyle.up = new Image(game.textureManager.pauseButtonTexture).getDrawable();
        pauseButton = new ImageButton(pauseStyle);
        pauseButton.setSize(game.viewport.getWorldWidth() * 0.04f, game.viewport.getWorldWidth() * 0.04f);
        pauseButton.setPosition(game.viewport.getWorldWidth() * 0.92f, game.viewport.getWorldHeight() * 0.875f);

        ImageButton.ImageButtonStyle correctStyle = new ImageButton.ImageButtonStyle();
        correctStyle.up = new Image(game.textureManager.correctButtonDisabledTexture).getDrawable();
        correctStyle.checked = new Image(game.textureManager.correctButtonEnabledTexture).getDrawable();
        correctButton = new ImageButton(correctStyle);
        correctButton.setTouchable(Touchable.disabled);
        correctButton.setSize(game.viewport.getWorldWidth() * 0.06f, game.viewport.getWorldWidth() * 0.06f);
        correctButton.setPosition(game.viewport.getWorldWidth() * 0.35f, game.viewport.getWorldHeight() * 0.225f);

        ImageButton.ImageButtonStyle wrongStyle = new ImageButton.ImageButtonStyle();
        wrongStyle.up = new Image(game.textureManager.wrongButtonDisabledTexture).getDrawable();
        wrongStyle.checked = new Image(game.textureManager.wrongButtonEnabledTexture).getDrawable();
        wrongButton = new ImageButton(wrongStyle);
        wrongButton.setTouchable(Touchable.disabled);
        wrongButton.setSize(game.viewport.getWorldWidth() * 0.06f, game.viewport.getWorldWidth() * 0.06f);
        wrongButton.setPosition(game.viewport.getWorldWidth() * 0.35f, game.viewport.getWorldHeight() * 0.06f);

        ImageButton.ImageButtonStyle dispenseStyle = new ImageButton.ImageButtonStyle();
        dispenseStyle.up = new Image(game.textureManager.dispenseButtonUpTexture).getDrawable();
        dispenseStyle.down = new Image(game.textureManager.dispenseButtonDownTexture).getDrawable();
        dispenseStyle.disabled = new Image(game.textureManager.dispenseButtonDownTexture).getDrawable();
        dispenseButton = new ImageButton(dispenseStyle);
        dispenseButton.setSize(game.viewport.getWorldWidth() * 0.075f, game.viewport.getWorldWidth() * 0.075f);
        dispenseButton.setPosition(game.viewport.getWorldWidth() * 0.366f, game.viewport.getWorldHeight() * 0.4f);
        dispenseButton.setDisabled(true);
        dispenseButton.setDisabled(true);

        stage.addActor(bgImage);
        stage.addActor(atmBgImage);
        stage.addActor(npcImage);
        stage.addActor(atmBorderImage);
        stage.addActor(atmCardImage);
        stage.addActor(requiredAmountDisplay);
        stage.addActor(totalAmountDisplay);
        stage.addActor(highScoreDisplay);
        stage.addActor(ratingDisplay);
        stage.addActor(correctButton);
        stage.addActor(wrongButton);
        stage.addActor(dispenseButton);
        stage.addActor(pauseButton);
        stage.addActor(dispenseLabel);

        for (int i = 0; i < game.denominations.size(); i++) {
            stage.addActor(denominationDisplays.get(i));
        }

        stage.addActor(timer);
    }

    private DenominationDisplay getDenominationDisplay(int i, float panelSpacing) {
        DenominationDisplay panel = new DenominationDisplay(
            game.textureManager.denominationTexture,
            game.denominations.get(i).toString(),
            game,
            stage
        );
        panel.setSize(game.viewport.getWorldWidth() * 0.05f, game.viewport.getWorldHeight() * 0.4f);

        panel.setPosition(
            game.viewport.getWorldWidth() * 0.425f + (i + 1) * panelSpacing - panel.getWidth() / 2,
            game.viewport.getWorldHeight() * 0.1f
        );

        return panel;
    }
}
