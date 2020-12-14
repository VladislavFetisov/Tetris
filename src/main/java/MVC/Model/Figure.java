package MVC.Model;

public class Figure {
    private final Coord metaInitialCoord;

    public RotationMode getRotation() {
        return rotation;
    }

    private RotationMode rotation;
    private final FigureForm figureForm;

    public Figure(Coord metaInitialCoord, RotationMode currentRotation, FigureForm figureForm) {
        this.metaInitialCoord = metaInitialCoord;
        this.rotation = currentRotation;
        this.figureForm = figureForm;
    }

    public FigureForm getFigureForm() {
        return figureForm;
    }

    public Coord[] getCoord() {
        return figureForm.getForm().generateFigure(metaInitialCoord, rotation);
    }

    public Coord[] getRotatedCoords() {
        return figureForm.getForm().generateFigure(metaInitialCoord, RotationMode.getNextRotationFrom(rotation));
    }

    public void rotate() {
        rotation = RotationMode.getNextRotationFrom(rotation);
    }

    public Coord[] getShiftedCoord(ShiftDirection direction) {
        Coord newInitialPoint = null;
        switch (direction) {
            case LEFT:
                newInitialPoint = new Coord(metaInitialCoord.x - 1, metaInitialCoord.y);
                break;
            case RIGHT:
                newInitialPoint = new Coord(metaInitialCoord.x + 1, metaInitialCoord.y);
                break;
        }
        return figureForm.getForm().generateFigure(newInitialPoint, rotation);
    }

    public void shift(ShiftDirection direction) {
        switch (direction) {
            case LEFT:
                metaInitialCoord.x--;
                break;
            case RIGHT:
                metaInitialCoord.x++;
                break;
        }
    }

    public Coord[] getFallenCoord() {
        Coord newInitialCoord = new Coord(metaInitialCoord.x, metaInitialCoord.y + 1);
        return figureForm.getForm().generateFigure(newInitialCoord, rotation);
    }

    public Coord[] getElevatedCoord() {
        Coord newInitialCoord = new Coord(metaInitialCoord.x, metaInitialCoord.y - 1);
        return figureForm.getForm().generateFigure(newInitialCoord, rotation);
    }

    public void fall() {
        metaInitialCoord.y++;
    }

    public void elevate() {
        metaInitialCoord.y--;
    }
}
