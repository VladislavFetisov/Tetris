package MVC.Controller;

import MVC.Model.Coord;
import MVC.Model.Field;
import MVC.Model.Figure;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import static MVC.Model.ShiftDirection.LEFT;
import static MVC.Model.ShiftDirection.RIGHT;

public class Main {
    public GridPane gridPane;
    public Button startButton;

    private static Field tetrisBoard;
    private static boolean isFigureStatic;

    public void initialize() {
        startButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            startGame();
        });
        for (int i = 0; i < Field.getWidth(); i++) {
            for (int j = 0; j < Field.getHeight(); j++) {
                Button cell = new Button();
                cell.setPrefSize(25, 25);
                cell.setStyle("-fx-border-color: Black");
                cell.setStyle("-fx-alternative-column-fill-visible: true");

                gridPane.add(cell, i, j);

            }
        }

    }

    private class Visualiser {
        public void drawFigure(Figure figure) {//x-горизонталь y-вертикаль
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

    public void startGame() {
        Visualiser visualiser = new Visualiser();
        startButton.setDisable(true);
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
            while (tetrisBoard.setInitialFigure()) {
                visualiser.drawFigure(tetrisBoard.getFigure());
                while (!isFigureStatic) {
                    try {
                        Thread.sleep(1000);
                        visualiser.wipeOffFigure(tetrisBoard.getFigure());
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

    private static void gameInit() {
        isFigureStatic = false;
        tetrisBoard = new Field();
    }
}
