package MVC.Controller;

import MVC.Model.Coord;
import MVC.Model.FigureForm;
import MVC.Model.Field;
import MVC.Model.ShiftDirection;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import static MVC.Model.RotationMode.NORMAL;
import static MVC.Model.ShiftDirection.*;

public class Main {
    public GridPane gridPane;
    public Button startButton;

    private static Field tetrisPitch;
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

//        while (!gameIsFinished) {
//            tetrisPitch.mainGame();
//        }
    }

    private static void gameInit() {
        tetrisPitch = new Field();
        gameIsFinished = false;
        isRotateRequested = false;
        isBoostRequested = false;
        shiftDirection = NOWHERE;
    }
}
