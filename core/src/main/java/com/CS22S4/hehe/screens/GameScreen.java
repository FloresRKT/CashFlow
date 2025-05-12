package com.CS22S4.hehe.screens;

import com.CS22S4.hehe.entity.Customer;
import com.CS22S4.hehe.events.ATMEvent;
import com.CS22S4.hehe.events.EventManager;
import com.CS22S4.hehe.App;
import com.CS22S4.hehe.events.OverlayEvent;
import com.CS22S4.hehe.overlays.PauseOverlay;
import com.CS22S4.hehe.overlays.PostGameOverlay;
import com.CS22S4.hehe.ui.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScreen implements Screen {
    private final App game;
    public Stage stage;
    private EventManager eventManager;
    private Customer currentCustomer;
    private ArrayList<Customer> servedCustomers;
    private ArrayList<Customer> customerQueue;
    private int difficulty;
    private int totalAmountDispensed;

    private Table labelTable;

    private boolean isPauseOverlayVisible;

    private Image atmBgImage;
    private Image atmBorderImage;
    private Image atmCardImage;
    private Image bgImage;
    private Label timer;
    private Label dispenseLabel;
    public Image npcImage;

    private ColoredBarRenderer barRenderer;
    private List<Bar> bars;
    private List<Integer> bills;
    private List<Color> gradientColors;
    private List<Bar> barDisplay;

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
    private boolean isSorting = false;

    private int rating;

    private Label contextLabel;
    private Label contextLabel1;
    private Label contextLabel2;
    private Label contextLabel3;
    private Label contextLabel4;
    private Label timerLabel;

    private float elapsedTime = 0f; // Accumulated time
    private final float stepInterval = 0.2f; // Interval in seconds
    private List<int[]> sortingSteps; // Stores the steps of the sorting process
    private int currentStep = 0; // Tracks the current step

    public GameScreen(App game, int difficulty) {
        this.game = game;
        this.difficulty = difficulty;

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

        barRenderer = new ColoredBarRenderer();
        bars = new ArrayList<>();
        barDisplay = new ArrayList<>();

        bills = new ArrayList<>();

        List<Color> colors = List.of(Color.GOLD, Color.PINK);
        int size = game.denominations.size();
        gradientColors = GradientColorGenerator.generateGradientColors(size, colors);

        totalAmountDispensed = 0;
        rating = 0;

        createUI();
        setupEventListeners();
    }

    @Override
    public void render(float delta) {
        game.viewport.apply();

        if (isSorting) {
            // Accumulate delta time
            elapsedTime += delta;

            // Execute the next step if the interval has passed
            if (elapsedTime >= stepInterval && currentStep < sortingSteps.size()) {
                elapsedTime -= stepInterval; // Reset elapsed time
                executeSortingStep(sortingSteps.get(currentStep)); // Execute the current step

                bills.clear(); // Clear the existing bills
                bars.clear();
                for (int value : sortingSteps.get(currentStep)) {
                    bills.add(value); // Add each value from the current step to the bills list
                    bars.add(new Bar(gradientColors.get(game.denominations.indexOf(value))));
                }
                updateBarPositions();

                // Move to next step, otherwise finish animation and move to next customer
                if (currentStep < sortingSteps.size() - 1) {
                    currentStep++; // Move to the next step
                } else {
                    isSorting = false;
                    currentStep = 0;
                    contextLabel.setText("Done!");
                    resetUI();

                    // Fire customer leave event to handle animation
                    eventManager.fireEvent(new ATMEvent(ATMEvent.Type.CUSTOMER_LEAVE, null), stage);
                }

            }
        }

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

        // Render the bars
        barRenderer.renderBars(bars, game.viewport);
        barRenderer.renderBars(barDisplay, game.viewport);

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
                            changeAmount(atmEvent);
                            addBar(atmEvent);
                            return true;
                        case SUBTRACT_BUTTON_CLICKED:
                            changeAmount(atmEvent);
                            removeBar(atmEvent);
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

        //eventManager.fireEvent(new ATMEvent(ATMEvent.Type.GAME_OVER, null), stage);
        eventManager.fireEvent(new ATMEvent(ATMEvent.Type.CUSTOMER_REQUEST, null), stage);
    }

    private void gameOver() {
        List<Integer> highScores = game.sqliteService.getHighScores();

        isTimerRunning = false;
        game.sqliteService.storeGameRecord(highScoreDisplay.getAmount(), servedCustomers.size(), totalAmountDispensed);

        if (highScores.get(difficulty) < ratingDisplay.getAmount()) {
            switch (difficulty) {
                case 0:
                    game.sqliteService.setHighScores(ratingDisplay.getAmount(), highScores.get(1), highScores.get(2));
                    break;
                case 1:
                    game.sqliteService.setHighScores(highScores.get(0), ratingDisplay.getAmount(), highScores.get(2));
                    break;
                case 2:
                    game.sqliteService.setHighScores(highScores.get(0), highScores.get(1), ratingDisplay.getAmount());
                    break;
            }
        }
        game.screenManager.pushOverlay(new PostGameOverlay(new ScreenViewport(), game, stage, game.skin,
            ratingDisplay.getAmount(), totalAmountDispensed, servedCustomers.size()));
    }

    private void customerRequest() {
        npcImage.setVisible(true);

        currentCustomer = new Customer(generateRandomRequestAmount());
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

                float easyTimeMultiplier = (float) Math.round(Math.pow(0.7, (float) (servedCustomers.size() / 4)) * 100) / 100;
                float normalTimeMultiplier = (float) Math.round(Math.pow(0.65, (float) (servedCustomers.size() / 3)) * 100) / 100;
                float hardTimeMultiplier = (float) Math.round(Math.pow(0.6, (float) (servedCustomers.size() / 2)) * 100) / 100;

                float startTime = 30f;

                switch (difficulty) {
                    case 0:
                        timeLeft = (float) Math.ceil(startTime * easyTimeMultiplier);
                        break;
                    case 1:
                        timeLeft = (float) Math.ceil(startTime * normalTimeMultiplier);
                        break;
                    case 2:
                        timeLeft = (float) Math.ceil(startTime * hardTimeMultiplier);
                        break;
                }

                isTimerRunning = true;
                contextLabel1.setText("");
                contextLabel2.setText("");
                contextLabel3.setText("");
                contextLabel4.setText("");
                contextLabel.setText("Transaction in progress...");
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

                // New customer
                eventManager.fireEvent(new ATMEvent(ATMEvent.Type.CUSTOMER_REQUEST, null), stage);
            })
        ));
    }

    private void dispense() {
        float serviceTime = 300 - timeLeft;
        isTimerRunning = false;
        currentCustomer.completeRequest(serviceTime);
        totalAmountDispensed += totalAmountDisplay.getAmount();

        int correctAmountOfBills = solveAmount(game.denominations, currentCustomer.getRequestedAmount()).size();
        if (bills.size() - correctAmountOfBills > 0) {
            rating += 100 - (bills.size() - correctAmountOfBills) * 10;
        }

        prepareSortingSteps(new ArrayList<>(bills));

        if (checkArrayOrder(bills)) {
            rating += 100;
        } else {
            rating += 100 - sortingSteps.size();
        }

        if (sortingSteps.isEmpty()) {
            isSorting = false;
            contextLabel.setText("Done!");

            // Initiate customer leaving animation early
            eventManager.fireEvent(new ATMEvent(ATMEvent.Type.CUSTOMER_LEAVE, null), stage);
        } else {
            isSorting = true;
            contextLabel.setText("Sorting...");
            int amountRating = 100 - (bills.size() - correctAmountOfBills) * 10;
            int sortedRating = 100 - sortingSteps.size();

            contextLabel2.setText("Completed Transaction: 100");
            contextLabel3.setText("Bills Rating : " + amountRating);
            contextLabel4.setText("Sorting Rating: " + sortedRating);
        }
    }

    private void resetUI() {
        // Reset UI state
        setCorrectButtonEnabled(false);
        setWrongButtonEnabled(false);
        setDispenseButtonEnabled(false);
        totalAmountDisplay.changeAmount(0);
        requiredAmountDisplay.changeAmount(0);
        rating = 100 + (int) (timeLeft * 10);
        ratingDisplay.changeAmount(ratingDisplay.getAmount() + rating);
        // Reset rating
        rating = 0;
        if (ratingDisplay.getAmount() > highScoreDisplay.getAmount()) {
            highScoreDisplay.changeAmount(ratingDisplay.getAmount());
        }
        bars.clear();
        bills.clear();

        for (DenominationDisplay panel: denominationDisplays) {
            panel.changeAmount(0);
        }
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

    private void addBar(ATMEvent event) {
        int index = game.denominations.indexOf((Integer)event.getData());
        int denomination = game.denominations.get(index);
        Color color = gradientColors.get(index);
        bills.add(denomination);
        bars.add(new Bar(color));
        updateBarPositions();
    }

    private void removeBar(ATMEvent event) {
        int lastIndex = findLastIndexOf(bills, Math.abs((Integer)event.getData()));
        if (lastIndex > -1) {
            bills.remove(lastIndex);
            bars.remove(lastIndex);
            updateBarPositions();
        }
    }

    private void updateBarPositions() {
        float barWidth = game.viewport.getWorldWidth() * 0.005f;
        float spacing = game.viewport.getWorldWidth() * 0.01f;

        for (int i=0; i<bars.size(); i++) {
            bars.get(i).setRect(
                game.viewport.getWorldWidth() * 0.075f + i * (barWidth + spacing),
                game.viewport.getWorldHeight() * 0.425f,
                barWidth,
                game.viewport.getWorldHeight() * 0.05f
            );
        }
    }

    private int findLastIndexOf(List<Integer> list, int element) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) == element) {
                return i; // Return the highest index of the matching element
            }
        }
        return -1; // Return -1 if the element is not found
    }

    // Method to create a valid amount for the customer request
    private int generateRandomRequestAmount() {
        int amount = 0;
        do {
            // Generate random amount of bills per denomination
            for (int denom : game.denominations) {
                amount += denom * (int)(Math.floor(Math.random() * 4));
            }
        } while (amount == 0);  // Prevent a request of 0
        return amount;
    }

    public void updateTimerDisplay() {
        int minutes = (int) (timeLeft / 60);
        int seconds = (int) (timeLeft % 60);
        timer.setText(String.format("%d:%02d", minutes, seconds));
    }

    private void executeSortingStep(int[] step) {
        // Logic to visualize the current sorting step
        System.out.println("Executing step: " + java.util.Arrays.toString(step));
    }

    private void prepareSortingSteps(List<Integer> array) {
        sortingSteps = new ArrayList<>();
        // Simulate sorting (e.g., bubble sort) and store each step
        for (int i = 0; i < array.size() - 1; i++) {
            for (int j = 0; j < array.size() - i - 1; j++) {
                if (array.get(j) > array.get(j + 1)) { // Change comparison for decreasing order
                    // Swap elements
                    int temp = array.get(j);
                    array.set(j, array.get(j + 1));
                    array.set(j + 1, temp);

                    // Store the current state of the array
                    int[] step = array.stream().mapToInt(Integer::intValue).toArray();
                    sortingSteps.add(step);
                }
            }
        }
    }

    public static boolean checkArrayOrder(List<Integer> array) {
        boolean isAscending = true;
        boolean isDescending = true;

        for (int i = 0; i < array.size() - 1; i++) {
            if (array.get(i) > array.get(i + 1)) {
                isAscending = false;
            }
            if (array.get(i) < array.get(i + 1)) {
                isDescending = false;
            }
        }

        if (isAscending) {
            return true;
        } else if (isDescending) {
            return true;
        } else {
            return false;
        }
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
        timer = new Label("00:00", game.skin, "numericLabelSmall");
        timeLeft = game.LEVEL_LENGTH_IN_SECONDS;
        isTimerRunning = false;
        timer.setPosition(game.viewport.getWorldWidth() * 0.5f, game.viewport.getWorldHeight() * 0.9f);

        contextLabel = new Label("", game.skin, "gameScreenLabel");
        timerLabel = new Label("Timer: ", game.skin, "gameScreenLabel");

        dispenseLabel = new Label("DISPENSE", game.skin, "gameScreenLabel");
        dispenseLabel.setPosition(game.viewport.getWorldWidth() * 0.435f, game.viewport.getWorldHeight() * 0.4f);

        bgImage = new Image(game.textureManager.gameBgTexture);
        bgImage.setSize(game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        bgImage.setPosition(0, 0);

        npcImage = new Image(game.textureManager.npcTextures.get(0));
        npcImage.setVisible(false);
        npcImage.setSize(game.viewport.getWorldWidth() * 0.225f, game.viewport.getWorldWidth() * 0.225f);
        npcImage.setPosition(game.viewport.getWorldWidth() * 0.11f, game.viewport.getWorldHeight() * 0.52f);

        atmBgImage = new Image(game.textureManager.atmBgTexture);
        atmBgImage.setSize(game.viewport.getWorldWidth() * 0.275f, game.viewport.getWorldWidth() * 0.25f);
        atmBgImage.setPosition(game.viewport.getWorldWidth() * 0.06f, game.viewport.getWorldHeight() * 0.45f);

        atmBorderImage = new Image(game.textureManager.atmBorderTexture);
        atmBorderImage.setSize(game.viewport.getWorldWidth() * 0.365f, game.viewport.getWorldWidth() * 0.23f);
        atmBorderImage.setPosition(game.viewport.getWorldWidth() * 0.045f, game.viewport.getWorldHeight() * 0.52f);

        atmCardImage = new Image(game.textureManager.atmCardTexture);
        atmCardImage.setSize(game.viewport.getWorldWidth() * 0.225f, game.viewport.getWorldWidth() * 0.15f);
        atmCardImage.setPosition(game.viewport.getWorldWidth() * 0.375f, game.viewport.getWorldHeight() * 0.575f);

        requiredAmountDisplay = new TransactionPanel(game, game.textureManager.amountLabelTexture, "REQUIRED");
        requiredAmountDisplay.setSize(game.viewport.getWorldWidth() * 0.305f, game.viewport.getWorldHeight() * 0.15f);
        requiredAmountDisplay.setPosition(game.viewport.getWorldWidth() * 0.075f, game.viewport.getWorldHeight() * 0.215f);
        requiredAmountDisplay.layoutElements();

        totalAmountDisplay = new TransactionPanel(game, game.textureManager.amountLabelTexture, "TOTAL");
        totalAmountDisplay.setSize(game.viewport.getWorldWidth() * 0.305f, game.viewport.getWorldHeight() * 0.15f);
        totalAmountDisplay.setPosition(game.viewport.getWorldWidth() * 0.075f, game.viewport.getWorldHeight() * 0.025f);
        totalAmountDisplay.layoutElements();

        highScoreDisplay  = new ScorePanel(game, game.textureManager.scoreLabelTexture, "HIGH SCORE");
        highScoreDisplay.setSize(game.viewport.getWorldWidth() * 0.305f, game.viewport.getWorldHeight() * 0.15f);
        highScoreDisplay.setPosition(game.viewport.getWorldWidth() * 0.685f, game.viewport.getWorldHeight() * 0.78f);
        highScoreDisplay.layoutElements();

        highScoreDisplay.changeAmount(game.sqliteService.getHighScores().get(difficulty));

        ratingDisplay = new ScorePanel(game, game.textureManager.scoreLabelTexture, "RATING");
        ratingDisplay.setSize(game.viewport.getWorldWidth() * 0.305f, game.viewport.getWorldHeight() * 0.15f);
        ratingDisplay.setPosition(game.viewport.getWorldWidth() * 0.685f, game.viewport.getWorldHeight() * 0.585f);
        ratingDisplay.layoutElements();

        float denominationPanelSectionWidth = game.viewport.getWorldWidth() * 0.525f;
        float panelSpacing = denominationPanelSectionWidth / (game.denominations.size() + 1);

        for (int i = 0; i < game.denominations.size(); i++) {
            DenominationDisplay panel = getDenominationDisplay(i, panelSpacing);
            panel.layoutElements();
            denominationDisplays.add(panel);
        }

        ImageButton.ImageButtonStyle pauseStyle = new ImageButton.ImageButtonStyle();
        pauseStyle.up = new Image(game.textureManager.pauseButtonTexture).getDrawable();
        pauseButton = new ImageButton(pauseStyle);
        pauseButton.setSize(game.viewport.getWorldWidth() * 0.03f, game.viewport.getWorldWidth() * 0.03f);
        pauseButton.setPosition(game.viewport.getWorldWidth() / 2 - pauseButton.getWidth() / 2, game.viewport.getWorldHeight() * 0.94f);

        ImageButton.ImageButtonStyle correctStyle = new ImageButton.ImageButtonStyle();
        correctStyle.up = new Image(game.textureManager.correctButtonDisabledTexture).getDrawable();
        correctStyle.checked = new Image(game.textureManager.correctButtonEnabledTexture).getDrawable();
        correctButton = new ImageButton(correctStyle);
        correctButton.setTouchable(Touchable.disabled);
        correctButton.setSize(game.viewport.getWorldWidth() * 0.06f, game.viewport.getWorldWidth() * 0.06f);
        correctButton.setPosition(game.viewport.getWorldWidth() * 0.435f, game.viewport.getWorldHeight() * 0.235f);

        ImageButton.ImageButtonStyle wrongStyle = new ImageButton.ImageButtonStyle();
        wrongStyle.up = new Image(game.textureManager.wrongButtonDisabledTexture).getDrawable();
        wrongStyle.checked = new Image(game.textureManager.wrongButtonEnabledTexture).getDrawable();
        wrongButton = new ImageButton(wrongStyle);
        wrongButton.setTouchable(Touchable.disabled);
        wrongButton.setSize(game.viewport.getWorldWidth() * 0.06f, game.viewport.getWorldWidth() * 0.06f);
        wrongButton.setPosition(game.viewport.getWorldWidth() * 0.435f, game.viewport.getWorldHeight() * 0.06f);

        ImageButton.ImageButtonStyle dispenseStyle = new ImageButton.ImageButtonStyle();
        dispenseStyle.up = new Image(game.textureManager.dispenseButtonUpTexture).getDrawable();
        dispenseStyle.down = new Image(game.textureManager.dispenseButtonDownTexture).getDrawable();
        dispenseStyle.disabled = new Image(game.textureManager.dispenseButtonDownTexture).getDrawable();
        dispenseButton = new ImageButton(dispenseStyle);
        dispenseButton.setSize(game.viewport.getWorldWidth() * 0.075f, game.viewport.getWorldWidth() * 0.075f);
        dispenseButton.setPosition(game.viewport.getWorldWidth() * 0.43f, game.viewport.getWorldHeight() * 0.445f);
        dispenseButton.setDisabled(true);
        dispenseButton.setDisabled(true);

        contextLabel1 = new Label("", game.skin, "gameScreenLabel");
        contextLabel2 = new Label("", game.skin, "gameScreenLabel");
        contextLabel3 = new Label("", game.skin, "gameScreenLabel");
        contextLabel4 = new Label("", game.skin, "gameScreenLabel");

        labelTable = new Table();

        // Add labels to the table
        labelTable.add(timerLabel).right();
        labelTable.add(timer).left();
        labelTable.top().row();
        labelTable.add(contextLabel).colspan(2).center().padTop(game.viewport.getWorldHeight() * 0.025f).row(); // Add context label
        labelTable.add(contextLabel1).colspan(2).left().row();
        labelTable.add(contextLabel2).colspan(2).left().row();
        labelTable.add(contextLabel3).colspan(2).left().row();
        labelTable.add(contextLabel4).colspan(2).left().row();

        labelTable.setSize(game.viewport.getWorldWidth() * 0.23f, game.viewport.getWorldHeight() * 0.3f);
        labelTable.setPosition(game.viewport.getWorldWidth() * 0.4125f, game.viewport.getWorldHeight() * 0.62f);

        Label legendLabel = new Label("LEGEND:", game.skin, "gameScreenLabel");
        legendLabel.setPosition(game.viewport.getWorldWidth() * 0.545f, game.viewport.getWorldHeight() * 0.525f);

        stage.addActor(bgImage);
        //stage.addActor(atmBgImage);
        stage.addActor(npcImage);
        stage.addActor(atmBorderImage);
        //stage.addActor(atmCardImage);
        stage.addActor(requiredAmountDisplay);
        stage.addActor(totalAmountDisplay);
        stage.addActor(highScoreDisplay);
        stage.addActor(ratingDisplay);
        stage.addActor(correctButton);
        stage.addActor(wrongButton);
        stage.addActor(dispenseButton);
        stage.addActor(pauseButton);
        stage.addActor(dispenseLabel);
        //stage.addActor(contextLabel);
        stage.addActor(labelTable);
        stage.addActor(legendLabel);

        for (int i = 0; i < game.denominations.size(); i++) {
            stage.addActor(denominationDisplays.get(i));
        }

        for (int i = 0; i < game.denominations.size(); i++) {
            Bar b = new Bar(gradientColors.get(i));
            float width = denominationDisplays.get(i).getWidth() / 2;
            b.setRect(
                denominationDisplays.get(i).getX() + width / 2,
                denominationDisplays.get(i).getY() + (float) (width * 9.75),
                width,
                width
            );
            barDisplay.add(b);
        }
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
            game.viewport.getWorldWidth() * 0.5f + (i + 1) * panelSpacing - panel.getWidth() / 2,
            game.viewport.getWorldHeight() * 0.025f
        );

        return panel;
    }

    public static List<Integer> solveAmount(List<Integer> denominations, int amount) {
        // Sort denominations in descending order
        List<Integer> sortedDenominations = new ArrayList<>(denominations);
        Collections.sort(sortedDenominations, Collections.reverseOrder());

        List<Integer> result = new ArrayList<>();
        int remainingAmount = amount;

        for (int denomination : sortedDenominations) {
            while (remainingAmount >= denomination) {
                result.add(denomination);
                remainingAmount -= denomination;
            }
        }

        // If the remaining amount is not zero, the amount cannot be fulfilled
        if (remainingAmount != 0) {
            return new ArrayList<>(); // Return an empty list if the amount cannot be fulfilled
        }

        return result;
    }

    public int getGameMode() {
        return difficulty;
    }
}
