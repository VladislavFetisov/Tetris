package MVC.Model;

import java.util.Arrays;

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

    public void mainGame() {
        setInitialFigure();

    }

    public void setInitialFigure() {
        this.figure = new Figure(new Coord(4, 3), NORMAL, FigureForm.getRandomFigureForm());
        putFigureOnDesk(figure.getCoord());
    }

    public boolean tryShiftFigure(ShiftDirection direction) {
        Coord[] shiftedCoord = figure.getShiftedCoord(direction);
        for (Coord coord : shiftedCoord) {
            if (coord.x < 0 || coord.x > width || (field[coord.x][coord.y] == 1 &&
                    !Arrays.asList(figure.getCoord()).contains(coord))) {
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
            if ((coord.x < 0 || coord.x > width) || (coord.y < 0 || coord.y > height)||
                    (field[coord.x][coord.y] == 1 && !Arrays.asList(figure.getCoord()).contains(coord))) {
                return false;
            }
        }
        clearFigureOnDesk(figure.getCoord());
        figure.rotate();
        putFigureOnDesk(rotatedCoord);
        return true;
    }

    public boolean tryFallFigure() {
        Coord[] fallenCoord = figure.getFallenCoord();
        for (Coord coord : fallenCoord) {
            if (coord.y < 0 || coord.y > height || (field[coord.x][coord.y] == 1 &&
                    !Arrays.asList(figure.getCoord()).contains(coord))) {
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
            else field[coord.x][coord.y] = 1;
        }
        return true;
    }

    private static boolean clearFigureOnDesk(Coord[] coordinates) {
        for (Coord coord : coordinates) {
            if (field[coord.x][coord.y] == 0) return false;
            else field[coord.x][coord.y] = 0;
        }
        return true;
    }

}
