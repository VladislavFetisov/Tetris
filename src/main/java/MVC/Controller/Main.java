package MVC.Controller;

import MVC.Model.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;

import static MVC.Model.RotationMode.NORMAL;
import static MVC.Model.ShiftDirection.LEFT;
import static MVC.Model.ShiftDirection.RIGHT;

public class Main {
    public GridPane gridPane;
    public Button startButton;
    public Button AIButton;

    private Field tetrisBoard;
    private boolean isFigureStatic;

    public void initialize() {
        startButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                startGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

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

    public void startGame() throws IOException {
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
                        if (!tetrisBoard.tryFallFigure()) {
                            isFigureStatic = true;
                            visualiser.drawFigure(tetrisBoard.getFigure());
                            tetrisBoard.tryDestroyLines(gridPane);
                        } else visualiser.drawFigure(tetrisBoard.getFigure());

                    } catch (InterruptedException ignored) {
                    }
                }
                isFigureStatic = false;
                if (tetrisBoard.isOverfilled()) {
                    break;
                }
            }
            tetrisBoard.clearDesk();
            Platform.runLater(() -> {
                try {
                    end();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    public void aITurnOn() {
        AIButton.setDisable(true);
        startButton.setDisable(true);
        Visualiser visualiser = new Visualiser();
        Solver solver = new Solver();
        gameInit();
        solver.createInitialGeneration(100);

        new Thread(() -> {
            int height = tetrisBoard.getCurrentHeight();
            HashSet<Integer> bestXs = new HashSet<>(4);
            boolean ready;
            while (tetrisBoard.setInitialFigure(NORMAL, FigureForm.getRandomFigureForm())) {
                FigureForm currentFigureForm = tetrisBoard.getFigure().getFigureForm();

                solver.solve(tetrisBoard, height);

                RotationMode bestRotation = solver.getBestRotation();
                Coord[] bestCoord = solver.getBestCoord();


                tetrisBoard.clearFigureOnDesk(tetrisBoard.getFigure().getCoord());
                tetrisBoard.setInitialFigure(bestRotation, currentFigureForm);

                while (tetrisBoard.tryShiftFigure(RIGHT)) {
                }

                for (Coord coord : bestCoord) bestXs.add(coord.x);

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
                        Thread.sleep(100);
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
            tetrisBoard.clearDesk();
            Platform.runLater(() -> {
                try {
                    end();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }).start();
    }

    private void gameInit() {
        isFigureStatic = false;
        tetrisBoard = new Field();
    }

    private void end() throws IOException {
        try {
            Group root = new Group();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EndOfTheGame.fxml"));
            root.getChildren().add(loader.load());

            Scene secondScene = new Scene(root);

            Stage newWindow = new Stage();
            newWindow.setTitle("Конец игры");
            newWindow.setScene(secondScene);

            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.close();

            newWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
