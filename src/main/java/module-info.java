module MVC {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    exports MVC.Controller;
    exports MVC.View;
    opens MVC.View;
    opens MVC.Controller;
}