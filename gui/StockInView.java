package stockjava.gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import stockjava.Stock;
import stockjava.control;

import java.util.ArrayList;

public class StockInView {

    public StockInView(Stage stage, ArrayList<Stock> stockArray, Runnable refreshCallback) {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-padding:20");

        TextField modelField = new TextField();
        ComboBox<String> fromOutletBox = new ComboBox<>();
        fromOutletBox.getItems().addAll("C60","C61","C62","C63","C64","C65","C66","C67","C68","C69","HQ");
        fromOutletBox.getSelectionModel().selectFirst();

        TextField qtyField = new TextField();

        Button transferBtn = new Button("Stock In");
        Button cancelBtn = new Button("Cancel");

        grid.addRow(0, new Label("Model:"), modelField);
        grid.addRow(1, new Label("Receiving from Outlet:"), fromOutletBox);
        grid.addRow(2, new Label("Quantity:"), qtyField);
        grid.addRow(3, transferBtn, cancelBtn);

        transferBtn.setOnAction(e -> {
            try {
                String model = modelField.getText().trim();
                String fromOutlet = fromOutletBox.getValue();
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (model.isEmpty()) throw new Exception("Model cannot be empty");
                if (qty <= 0) throw new Exception("Quantity must be > 0");

                String receivingOutlet = control.getSelectedOutletCode(); // employee's own outlet

                control.stockIn(stockArray, receivingOutlet, model, fromOutlet, qty);
                control.writeFile(stockArray);
                refreshCallback.run();
                stage.close();

            } catch (NumberFormatException ex) {
                showAlert("Please enter a valid number!");
            } catch (Exception ex) {
                showAlert(ex.getMessage());
            }
        });

        cancelBtn.setOnAction(ev -> stage.close());

        stage.setScene(new Scene(grid, 400, 250));
        stage.setTitle("Stock In");
        stage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
