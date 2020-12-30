package MVC.Model;

import javafx.scene.layout.GridPane;

import java.util.Arrays;

public class Field {

    private final static int width = 10;
    private final static int ceil = 3;
    private final static int height = width * 2 + ceil;

    private Figure figure;//Фигура,которая падает в данный момент

    private static final int[][] field = new int[width][height];

    public static int getCeil() {
        return ceil;
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    public Figure getFigure() {
        return figure;
    }

    public boolean setInitialFigure(RotationMode rotationMode, FigureForm figureForm) {
        figure = new Figure(new Coord(4, ceil), rotationMode, figureForm);
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
            if (coord.y < ceil || coord.y >= height) {
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

    public boolean tryElevateFigure() {
        Coord[] elevatedCoord = figure.getElevatedCoord();
        for (Coord coord : elevatedCoord) {
            if (coord.y < ceil) return false;
        }

        clearFigureOnDesk(figure.getCoord());
        figure.elevate();
        putFigureOnDesk(elevatedCoord);

        return true;
    }

    public boolean putFigureOnDesk(Coord[] coordinates) {
        for (Coord coord : coordinates) {
            if (field[coord.x][coord.y] == 1) return false;
            field[coord.x][coord.y] = 1;
        }
        return true;
    }

    public boolean clearFigureOnDesk(Coord[] coordinates) {
        for (Coord coord : coordinates) {
            if (field[coord.x][coord.y] == 0) return false;
            field[coord.x][coord.y] = 0;
        }
        return true;
    }

    public int getFigureMaxY() {
        int maxY = 0;
        for (Coord coord : figure.getCoord())
            if (Field.getHeight() - coord.y > maxY) maxY = Field.getHeight() - coord.y;
        return maxY;
    }

    public int getCurrentHeight() {
        for (int i = Field.getCeil(); i < Field.getHeight(); i++) {
            for (int j = 0; j < Field.getWidth(); j++)
                if (field[j][i] == 1) return Field.getHeight() - i;
        }
        return 0;
    }

    public Pair getAmountOfHoles() {
        int holesCount = 0, highestHoleY = Field.getHeight();
        for (int i = getFigureMinXY().y; i < Field.getHeight(); i++) {
            for (int j = getFigureMinXY().x; j <= getFigureMaxXY().x; j++) {
                if (field[j][i] == 0 && i != getFigureMinXY().y) {
                    holesCount++;
                    if (i < highestHoleY) highestHoleY = Field.getHeight() - i;
                }
            }
        }
        if (holesCount != 0) return new Pair(holesCount, highestHoleY);
        else return new Pair(0, 0);
    }

    public Coord getFigureMinXY() {
        int minX = Field.getWidth();
        int minY = Field.getHeight();

        for (Coord coord : figure.getCoord()) {
            if (coord.x < minX) minX = coord.x;
            if (coord.y < minY) minY = coord.y;
        }
        return new Coord(minX, minY);
    }

    public Coord getFigureMaxXY() {
        int maxX = -1;
        int maxY = Field.getCeil();

        for (Coord coord : figure.getCoord()) {
            if (coord.x > maxX) maxX = coord.x;
            if (coord.y > maxY) maxY = coord.y;
        }
        return new Coord(maxX, maxY);
    }

    public void clearDesk() {
        for (int i = 0; i < Field.getHeight(); i++) {
            for (int j = 0; j < Field.getWidth(); j++) {
                field[j][i] = 0;
            }
        }
    }

    public void tryDestroyLines(GridPane gridPane) {
        int countInLine = 0;
        int k;
        for (int i = getHeight() - 1; i > ceil; i--) {
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

    public int amountOfSupposingDestLines() {
        int amountOfDestroyingLines = 0;
        int count = 0;
        for (Coord coord : figure.getCoord()) {
            for (int i = 0; i < Field.getWidth(); i++) {
                if (field[i][coord.y] == 1) count++;
            }
            if (count == Field.getWidth()) amountOfDestroyingLines++;
            count = 0;
        }
        return amountOfDestroyingLines;
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
