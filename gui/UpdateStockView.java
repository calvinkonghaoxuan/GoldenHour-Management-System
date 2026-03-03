package stockjava.gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import stockjava.Stock;
import stockjava.control;

import java.util.ArrayList;

public class UpdateStockView {

    public UpdateStockView(Stage stage,
                           TableView<Stock> table,
                           ArrayList<Stock> stockArray,
                           Runnable refreshCallback) {

        Stock selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a stock item first!").showAndWait();
            return;
        }

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-padding:20");

        Label modelLabel = new Label("Model: " + selected.getModel());
        Label oldPriceLabel = new Label("Current Price: RM " + selected.getPrice());

        TextField newPriceField = new TextField();
        newPriceField.setPromptText("Enter new price");

        Button updateBtn = new Button("Update Price");
        Button cancelBtn = new Button("Cancel");

        grid.addRow(0, modelLabel);
        grid.addRow(1, oldPriceLabel);
        grid.addRow(2, new Label("New Price:"), newPriceField);
        grid.addRow(3, updateBtn, cancelBtn);

        updateBtn.setOnAction(e -> {
            try {
                double newPrice = Double.parseDouble(newPriceField.getText().trim());

                if (newPrice <= 0) {
                    throw new Exception("Price must be greater than 0");
                }

                // Only update price — NEVER quantity
                selected.setPrice(newPrice);

                control.writeFile(stockArray);
                refreshCallback.run();
                stage.close();

            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Enter a valid price").showAndWait();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        stage.setScene(new Scene(grid, 350, 220));
        stage.setTitle("Update Stock Price");
        stage.show();
    }
}
