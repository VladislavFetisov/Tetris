package MVC.Model;

public enum RotationMode {
    NORMAL(0),

    FLIP_CCW(1),

    INVERT(2),

    FLIP_CW(3);

    //Поворот осуществляется только против часовой стрелки

    private final int number;

    RotationMode(int number) {
        this.number = number;
    }

    private static final RotationMode[] rotationByNumber = {NORMAL, FLIP_CCW, INVERT, FLIP_CW};

    public static RotationMode getNextRotationFrom(RotationMode previousRotation) {
        int newRotationIndex = (previousRotation.number + 1) % rotationByNumber.length;
        return rotationByNumber[newRotationIndex];
    }
}