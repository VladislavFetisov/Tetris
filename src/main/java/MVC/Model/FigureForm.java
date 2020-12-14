package MVC.Model;

import static MVC.Model.Forms.*;

public enum FigureForm {
    O(O_FORM, "#ffff00"),
    I(I_FORM, "#87cefa"),
    S(S_FORM, "#ff0000"),
    Z(Z_FORM, "#90ee90"),
    L(L_FORM, "#ffa500"),
    J(J_FORM, "#ffc0cb"),
    T(T_FORM, "#9932CC");


    private final Forms form;
    private final String color;

    FigureForm(Forms form, String color) {
        this.form = form;
        this.color = color;
    }
    private final static FigureForm[] FIGURE_FORM_ARRAY = new FigureForm[]{ S, Z, L, J, T};

    public static FigureForm getRandomFigureForm() {
        return FIGURE_FORM_ARRAY[(int) Math.floor(Math.random() * FIGURE_FORM_ARRAY.length)];
    }

    public Forms getForm() {
        return form;
    }

    public String getColor() {
        return color;
    }
}

