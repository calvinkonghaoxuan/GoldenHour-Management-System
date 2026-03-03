package stockjava.gui;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stockjava.Login;
import stockjava.control;

public class ClockInView {

    public ClockInView(Stage stage) {

        // Make sure Login system is ready for GUI
        Login.initForGUI();

        VBox root = new VBox(12);
        root.setStyle("-fx-padding: 20;");

        Label title = new Label("Clock In");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label employee = new Label("Employee: " + control.getCurrentEmployeeID());
        Label outlet = new Label("Outlet: " + Login.yourOutletCode());

        Label status = new Label("Not clocked in yet");

        Button clockInBtn = new Button("Clock In");
        clockInBtn.setStyle("-fx-font-size: 14px;");

        // ===== CLOCK IN BUTTON =====
        clockInBtn.setOnAction(e -> {

            String empID = control.getCurrentEmployeeID();

            if (empID == null) {
                showAlert("Error", "No employee logged in.");
                return;
            }

            String time = Login.clockInGUI();

            if (time.startsWith("❌")) {
                showAlert("Error", time);
                status.setText(time);
            } else {
                status.setText("Clocked in at: " + time);
                showAlert("Success", "Clocked in at " + time);
            }
        });

        root.getChildren().addAll(
                title,
                employee,
                outlet,
                clockInBtn,
                status
        );

        Scene scene = new Scene(root, 320, 220);
        stage.setTitle("Clock In");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
