package MVC.View;

import MVC.Model.ShiftDirection;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

import static MVC.Model.ShiftDirection.LEFT;
import static MVC.Model.ShiftDirection.RIGHT;

public class View extends Application {
    private static boolean isRotateRequested;
    private static boolean isBoostRequested;
    private static ShiftDirection shiftDirection;

    public static void main(String[] args) {
        launch(args);

    }

    public static boolean isIsRotateRequested() {
        return isRotateRequested;
    }

    public static boolean isIsBoostRequested() {
        return isBoostRequested;
    }

    public static ShiftDirection getShiftDirection() {
        return shiftDirection;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Group root = new Group();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        root.getChildren().add(loader.load());

        Scene scene = new Scene(root);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case KP_RIGHT:
                    shiftDirection = RIGHT;
                    System.out.println("вправо");
                    break;
                case A:
                    shiftDirection = LEFT;
                    System.out.println("влево");
                    break;
                case S:
                    isBoostRequested = true;
                    break;
                case W:
                    isRotateRequested = true;
                    break;
            }
        });
        primaryStage.setTitle("Tetris");
        //primaryStage.getIcons().add(new Image("/fillword.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
