package MVC.Controller;

import MVC.Model.*;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.HashSet;

import static MVC.Model.RotationMode.NORMAL;
import static MVC.Model.ShiftDirection.LEFT;
import static MVC.Model.ShiftDirection.RIGHT;

public class Main {
    public GridPane gridPane;
    public Button startButton;
    public Button AIButton;

    private static Field tetrisBoard;
    private static boolean isFigureStatic;


    public void initialize() {
        startButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> startGame());

        for (int i = 0; i < Field.getWidth(); i++) {
            for (int j = 0; j < Field.getHeight(); j++) {
                Button cell = new Button();
                cell.setPrefSize(25, 25);
                cell.setStyle("-fx-border-color: Black");
                cell.setStyle("-fx-alternative-column-fill-visible: true");

                gridPane.add(cell, i, j);
                if (j < Field.getCeil()) cell.setVisible(false);
            }
        }

    }

    public void startGame() {
        Visualiser visualiser = new Visualiser();
        startButton.setDisable(true);
        AIButton.setDisable(true);
        gameInit();
        startButton.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case RIGHT:
                    visualiser.wipeOffFigure(tetrisBoard.getFigure());
                    tetrisBoard.tryShiftFigure(RIGHT);
                    visualiser.drawFigure(tetrisBoard.getFigure());
                    break;
                case LEFT:
                    visualiser.wipeOffFigure(tetrisBoard.getFigure());
                    tetrisBoard.tryShiftFigure(LEFT);
                    visualiser.drawFigure(tetrisBoard.getFigure());
                    break;
                case DOWN:
                    visualiser.wipeOffFigure(tetrisBoard.getFigure());
                    tetrisBoard.tryFallFigure();
                    visualiser.drawFigure(tetrisBoard.getFigure());
                    break;
                case UP:
                    visualiser.wipeOffFigure(tetrisBoard.getFigure());
                    tetrisBoard.tryRotateFigure();
                    visualiser.drawFigure(tetrisBoard.getFigure());
                    break;
            }
        });
        new Thread(() -> {
            while (tetrisBoard.setInitialFigure(NORMAL, FigureForm.getRandomFigureForm())) {
                visualiser.drawFigure(tetrisBoard.getFigure());
                while (!isFigureStatic) {
                    try {
                        Thread.sleep(1000);
                        visualiser.wipeOffFigure(tetrisBoard.getFigure());
                        tetrisBoard.tryFallFigure();
                        if (!tetrisBoard.tryFallFigure()) {
                            isFigureStatic = true;
                            visualiser.drawFigure(tetrisBoard.getFigure());
                            tetrisBoard.tryDestroyLines(gridPane);
                        } else visualiser.drawFigure(tetrisBoard.getFigure());
                        System.out.println(tetrisBoard.toString());

                    } catch (InterruptedException ignored) {
                    }
                }
                isFigureStatic = false;
                if (tetrisBoard.isOverfilled()) break;
            }
        }).start();

        //endOfthegame()
    }

    public void aITurnOn() {
        AIButton.setDisable(true);
        startButton.setDisable(true);
        Visualiser visualiser = new Visualiser();
        Solver solver = new Solver();
        gameInit();
        new Thread(() -> {
            int height = tetrisBoard.getCurrentHeight();
            HashSet<Integer> bestXs = new HashSet<>(4);
            boolean ready;
            while (tetrisBoard.setInitialFigure(NORMAL, FigureForm.getRandomFigureForm())) {
                FigureForm currentFigureForm = tetrisBoard.getFigure().getFigureForm();

                solver.solve(tetrisBoard, height);

                RotationMode bestRotation = solver.getBestRotation();
                Coord[] bestCoord = solver.getBestCoord();

                System.out.println(bestRotation);
                System.out.println(Arrays.toString(bestCoord));
                System.out.println(currentFigureForm);

                tetrisBoard.clearFigureOnDesk(tetrisBoard.getFigure().getCoord());
                tetrisBoard.setInitialFigure(bestRotation, currentFigureForm);
                System.out.println(tetrisBoard.toString());
                while (tetrisBoard.tryShiftFigure(RIGHT)) {
                }

                for (Coord coord : bestCoord) bestXs.add(coord.x);
                System.out.println(bestXs.toString());
                ready = false;
                while (!ready) {
                    ready = true;
                    for (Coord coord : tetrisBoard.getFigure().getCoord()) {
                        if (!bestXs.contains(coord.x)) {
                            ready = false;
                            tetrisBoard.tryShiftFigure(LEFT);
                            break;
                        }
                    }
                }


                visualiser.drawFigure(tetrisBoard.getFigure());

                while (!isFigureStatic) {
                    try {
                        Thread.sleep(250);
                        visualiser.wipeOffFigure(tetrisBoard.getFigure());
                        if (!tetrisBoard.tryFallFigure()) {
                            isFigureStatic = true;
                            visualiser.drawFigure(tetrisBoard.getFigure());
                            tetrisBoard.tryDestroyLines(gridPane);
                        } else visualiser.drawFigure(tetrisBoard.getFigure());

                        height = tetrisBoard.getCurrentHeight();
                        bestXs.clear();
                    } catch (InterruptedException ignored) {
                    }
                }
                isFigureStatic = false;
                if (tetrisBoard.isOverfilled()) break;
            }
        }).start();
    }

    private static void gameInit() {
        isFigureStatic = false;
        tetrisBoard = new Field();
    }

    private class Visualiser {

        public void drawFigure(Figure figure) {
            for (Coord coordinate : figure.getCoord()) {
                gridPane.getChildren().get(Field.getHeight() * coordinate.x + coordinate.y)
                        .setStyle("-fx-background-color: " + figure.getFigureForm().getColor());
            }

        }

        public void wipeOffFigure(Figure figure) {
            for (Coord coordinate : figure.getCoord()) {
                gridPane.getChildren().get(Field.getHeight() * coordinate.x + coordinate.y)
                        .setStyle("-fx-alternative-column-fill-visible: true");
            }
        }
    }
}
