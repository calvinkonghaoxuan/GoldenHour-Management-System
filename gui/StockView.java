package stockjava.gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import stockjava.Stock;
import stockjava.control;

import java.util.ArrayList;

public class StockView {

    private Scene scene;
    private static ArrayList<Stock> stockList = new ArrayList<>();

    public StockView(Stage stage) {

        control.readFile(stockList);

        TextField model = new TextField();
        TextField price = new TextField();
        TextField qty = new TextField();

        ComboBox<String> outlet = new ComboBox<>();
        outlet.getItems().addAll("C60","C61","C62","C63","C64","C65","C66","C67","C68","C69","HQ");
        outlet.setPromptText("Select Outlet");

        Button update = new Button("Update");
        Button back = new Button("Back");

        update.setOnAction(e -> {
            try {
                String modelText = model.getText().trim();
                double priceChange = Double.parseDouble(price.getText().trim());
                int qtyChange = Integer.parseInt(qty.getText().trim());
                String outletCode = outlet.getValue();

                if (modelText.isEmpty() || outletCode == null) {
                    new Alert(Alert.AlertType.WARNING, "Please fill all fields").showAndWait();
                    return;
                }

                control.updateStock(stockList, modelText, priceChange, qtyChange, outletCode);
                control.writeFile(stockList);
                new Alert(Alert.AlertType.INFORMATION, "✅ Stock updated successfully!").showAndWait();

            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Price and Qty must be numbers").showAndWait();
            }
        });

        back.setOnAction(e -> {
           ArrayList<Stock> stockArray = new ArrayList<>();
control.readFile(stockArray);

DashboardView dash = new DashboardView(stage, stockArray);
stage.setScene(dash.getScene());

            stage.setScene(dash.getScene());
        });

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-padding:20");

        grid.addRow(0, new Label("Model"), model);
        grid.addRow(1, new Label("Price Change"), price);
        grid.addRow(2, new Label("Qty Change"), qty);
        grid.addRow(3, new Label("Outlet"), outlet);
        grid.addRow(4, update, back);

        scene = new Scene(grid, 450, 300);
    }

    public Scene getScene() {
        return scene;
    }
}
