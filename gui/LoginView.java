package stockjava.gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stockjava.control;
import stockjava.Login;
import stockjava.Stock;

import java.util.ArrayList;

public class LoginView {

    private Scene scene;

    public LoginView(Stage stage) {
        Login.initForGUI();

        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding:20");

        Label lblInfo = new Label("Login to Stock Management System");

        TextField txtID = new TextField();
        txtID.setPromptText("Employee ID");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");

        // Outlet selection
        ComboBox<String> outletCombo = new ComboBox<>();
        outletCombo.getItems().addAll(Login.getAllOutletCodes());
        outletCombo.setPromptText("Select Outlet");

        Button loginBtn = new Button("Login");
        Label lblStatus = new Label();

        loginBtn.setOnAction(e -> {
            String empID = txtID.getText().trim();
            String pass = txtPassword.getText().trim();
            String selectedOutlet = outletCombo.getValue();

            if (empID.isEmpty() || pass.isEmpty() || selectedOutlet == null) {
                lblStatus.setText("Please enter ID, password, and select outlet!");
                return;
            }

            int empIndex = Login.authenticate(empID, pass);
            if (empIndex == -1) {
                lblStatus.setText("❌ Invalid ID or Password");
                return;
            }

            // Set current user
            control.setCurrentUser(
                    Login.getEmployeeID(empIndex),
                    Login.getEmployeeName(empIndex),
                    Login.getRole(empIndex)
            );

            // Set outlet in both systems
            control.setSelectedOutletCode(selectedOutlet);
            Login.setSelectedOutletByCode(selectedOutlet);

            // Show welcome popup
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login Successful");
            alert.setHeaderText("Welcome " + Login.getEmployeeName(empIndex));
            alert.setContentText(
                    "Employee ID: " + Login.getEmployeeID(empIndex) + "\n" +
                    "Role: " + Login.getRole(empIndex) + "\n" +
                    "Outlet: " + Login.yourOutletCode() + " - " + Login.yourOutletName()
            );
            alert.showAndWait();

            // Load stock
            ArrayList<Stock> stockArray = new ArrayList<>();
            control.readFile(stockArray);

            // Open dashboard
            DashboardView dash = new DashboardView(stage, stockArray);
            stage.setScene(dash.getScene());
        });

        vbox.getChildren().addAll(
                lblInfo,
                txtID,
                txtPassword,
                outletCombo,
                loginBtn,
                lblStatus
        );

        scene = new Scene(vbox, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}
