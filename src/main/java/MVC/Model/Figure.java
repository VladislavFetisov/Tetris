package MVC.Model;

public class Figure {
    private final Coord metaInitialCoord;
    private RotationMode rotation;
    private final FigureForm figureForm;

    public Figure(Coord metaInitialCoord, RotationMode currentRotation, FigureForm figureForm) {
        this.metaInitialCoord = metaInitialCoord;
        this.rotation = currentRotation;
        this.figureForm = figureForm;
    }

    public Coord[] getCoord() {
        return figureForm.getForm().generateFigure(metaInitialCoord, rotation);
    }
    public Coord[] getRotatedCoords(){
        return figureForm.getForm().generateFigure(metaInitialCoord, RotationMode.getNextRotationFrom(rotation));
    }

    public void rotate(){
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
    public void shift(ShiftDirection direction){
        switch (direction){
            case LEFT:
                metaInitialCoord.x--;
                break;
            case RIGHT:
                metaInitialCoord.x++;
                break;
        }
    }

}
