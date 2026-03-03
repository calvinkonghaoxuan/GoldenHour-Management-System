package stockjava.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import stockjava.Login;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        LoginView login = new LoginView(stage);
        stage.setTitle("GoldenHour Management System");
        stage.setScene(login.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        // Initialize everything once for GUI mode
        Login.initForGUI();
        launch(args);
    }
}
