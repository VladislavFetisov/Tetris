package MVC.Controller;

import MVC.View.View;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class EndOfTheGame {
    public Button button;

    public void restart() {
        end();
        Stage stage = new Stage();
        View view = new View();
        try {
            view.start(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void end() {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
