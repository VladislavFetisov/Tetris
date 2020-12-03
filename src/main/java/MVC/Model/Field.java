package MVC.Model;

import javafx.scene.layout.GridPane;

import java.util.Arrays;

import static MVC.Model.FigureForm.I;
import static MVC.Model.RotationMode.NORMAL;

public class Field {

    private final static int width = 10;
    private final static int ceil = 3;
    private final static int height = width * 2 + ceil;

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    private Figure figure;//Фигура,которая падает в данный момент

    private static int[][] field = new int[width][height];


    public boolean setInitialFigure() {
        figure = new Figure(new Coord(4, 3), NORMAL, I);
        return putFigureOnDesk(figure.getCoord());
    }

    public boolean tryShiftFigure(ShiftDirection direction) {
        Coord[] shiftedCoord = figure.getShiftedCoord(direction);
        for (Coord coord : shiftedCoord) {
            if (coord.x < 0 || coord.x >= width)
                return false;
            if (field[coord.x][coord.y] == 1 &&
                    !Arrays.asList(figure.getCoord()).contains(coord)) {
                return false;
            }
        }
        clearFigureOnDesk(figure.getCoord());
        figure.shift(direction);
        putFigureOnDesk(shiftedCoord);
        return true;
    }

    public boolean tryRotateFigure() {
        Coord[] rotatedCoord = figure.getRotatedCoords();
        for (Coord coord : rotatedCoord) {
            if ((coord.x < 0 || coord.x >= width) || (coord.y < 0 || coord.y >= height))
                return false;
            if (field[coord.x][coord.y] == 1 && !Arrays.asList(figure.getCoord()).contains(coord))
                return false;
        }
        clearFigureOnDesk(figure.getCoord());
        figure.rotate();
        putFigureOnDesk(rotatedCoord);
        return true;
    }

    public boolean tryFallFigure() {
        Coord[] fallenCoord = figure.getFallenCoord();
        for (Coord coord : fallenCoord) {
            if (coord.y < 0 || coord.y >= height) {
                return false;
            }
            if (field[coord.x][coord.y] == 1 &&
                    !Arrays.asList(figure.getCoord()).contains(coord)) {
                return false;
            }
        }
        clearFigureOnDesk(figure.getCoord());
        figure.fall();
        putFigureOnDesk(fallenCoord);

        return true;
    }

    private static boolean putFigureOnDesk(Coord[] coordinates) {
        for (Coord coord : coordinates) {
            if (field[coord.x][coord.y] == 1) return false;
            field[coord.x][coord.y] = 1;
        }
        return true;
    }

    private static boolean clearFigureOnDesk(Coord[] coordinates) {
        for (Coord coord : coordinates) {
            if (field[coord.x][coord.y] == 0) return false;
            field[coord.x][coord.y] = 0;
        }
        return true;
    }


    public void tryDestroyLines(GridPane gridPane) {
        int countInLine = 0;
        int k;
        for (int i = getHeight() - 1; i > 3; i--) {
            for (int j = 0; j < getWidth(); j++) if (field[j][i] == 1) countInLine++;

            k = i - 1;//разница i-k= кол-во линий,которое необходимо сломать
            if (countInLine == 10) {
                while (true) {
                    countInLine = 0;
                    for (int j = 0; j < getWidth(); j++) if (field[j][k] == 1) countInLine++;
                    if (countInLine != 10) break;
                    k--;
                }
                int c = 0;
                for (int j = i - k; j > 0; j--) {
                    for (int l = 0; l < getWidth(); l++) {
                        field[l][k + j] = field[l][k - c];
                        gridPane.getChildren().get(Field.getHeight() * l + (k + j))
                                .setStyle(gridPane.getChildren().get(Field.getHeight() * l + (k - c)).getStyle());
                    }
                    c++;
                }
                for (int j = k; j > 4; j--) {
                    for (int l = 0; l < width; l++) {
                        field[l][j] = field[l][j - (i - k)];
                        gridPane.getChildren().get(Field.getHeight() * l + j)
                                .setStyle(gridPane.getChildren().get(Field.getHeight() * l + j - (i - k)).getStyle());
                    }
                }
                break;
            }
            countInLine = 0;
        }
    }

    public boolean isOverfilled() {
        for (int x = 0; x < width; x++) if (field[x][ceil - 1] == 1) return true;
        return false;
    }

    public Figure getFigure() {
        return figure;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Field.getHeight(); i++) {
            for (int j = 0; j < Field.getWidth(); j++) {
                if (field[j][i] == 1) builder.append(1);
                else builder.append(0);
            }
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
