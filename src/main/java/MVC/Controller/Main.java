package MVC.Controller;

import MVC.Model.Coord;
import MVC.Model.Field;
import MVC.Model.FigureForm;
import MVC.Model.ShiftDirection;
import MVC.View.View;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import static MVC.Model.RotationMode.NORMAL;
import static MVC.Model.ShiftDirection.NOWHERE;

public class Main {
    public GridPane gridPane;
    public Button startButton;

    private static final int FPS = 60;
    private static final int BRICKS_PER_SECOND = 2;
    private static final int FRAMES_PER_MOVE = FPS / BRICKS_PER_SECOND;
    private static final int BOOST = 5;

    private static int gameIteration;


    private static Field tetrisBoard;
    private static boolean gameIsFinished;
    private static boolean isRotateRequested;
    private static boolean isBoostRequested;
    private static ShiftDirection shiftDirection;


    public void initialize() {
        startButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> startGame());
        for (int i = 0; i < Field.getWidth(); i++) {
            for (int j = 0; j < Field.getHeight(); j++) {
                Button cell = new Button();
                cell.setPrefSize(25, 25);
                cell.setStyle("-fx-border-color: Black");
                gridPane.add(cell, i, j);

            }
        }
        gridPane.setStyle("-fx-border-color: black");
    }

    private class Controller {
        public void setFigure(FigureForm figureForm, Coord initialCoord) {//x-горизонталь y-вертикаль

            for (Coord coordinate : figureForm.getForm().generateFigure(initialCoord, NORMAL)) {
                gridPane.getChildren().get(Field.getHeight() * coordinate.x + coordinate.y)
                        .setStyle("-fx-background-color: " + figureForm.getColor());
            }

        }

    }

    public void startGame() {
        gameInit();

        while (!gameIsFinished) {
            tetrisBoard.mainGame();
            handleKeyboard();
            logic();
        }
        //endOfthegame()
    }

    private static void handleKeyboard() {
        isRotateRequested = View.getIsRotateRequested();
        isBoostRequested = View.getIsBoostRequested();
        shiftDirection = View.getShiftDirection();
    }

    private static void logic() {
        if (shiftDirection != NOWHERE) {

            tetrisBoard.tryShiftFigure(shiftDirection);

            shiftDirection = NOWHERE;
        }

        if (isRotateRequested) {

            tetrisBoard.tryRotateFigure();

            isRotateRequested = false;
        }
        if (gameIteration % (FRAMES_PER_MOVE/(isBoostRequested?BOOST:1)) == 0) tetrisBoard.tryFallFigure();

        gameIteration = (gameIteration + 1) % FRAMES_PER_MOVE;
    }


    private static void gameInit() {
        gameIsFinished = false;
        isRotateRequested = false;
        isBoostRequested = false;
        shiftDirection = NOWHERE;
        tetrisBoard = new Field();
    }
}
