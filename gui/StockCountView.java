package stockjava.gui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stockjava.Stock;
import stockjava.control;

import java.util.ArrayList;

public class StockCountView {

    // ================== 2-PARAMETER CONSTRUCTOR ==================
    public StockCountView(Stage stage, ArrayList<Stock> stockArray) {
        VBox vbox = new VBox(10.0);
        vbox.setStyle("-fx-padding:20");

        // Input field for outlet code
        TextField outletField = new TextField();
        outletField.setPromptText("Outlet code (C60-C69/HQ)");

        // Buttons
        Button startBtn = new Button("Start Stock Count");
        Button cancelBtn = new Button("Cancel");

        // Result area
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);

        // Start counting action
        startBtn.setOnAction(e -> {
            String outletCode = outletField.getText().toUpperCase();
            control.setSelectedOutletCode(outletCode);

            for (Stock s : stockArray) {
                TextInputDialog dialog = new TextInputDialog("0");
                dialog.setHeaderText("Enter counted quantity for " + s.getModel() + " at " + outletCode);
                int counted = Integer.parseInt(dialog.showAndWait().orElse("0"));
                s.setCountedQuantity(counted, outletCode);
            }

            resultArea.clear();
            for (Stock s : stockArray) {
                resultArea.appendText(s.getModel() + ": Record=" + s.getQuantityByOutlet(outletCode) +
                        " Counted=" + s.getCountedQuantity() + "\n");
            }

            control.performStockCount("Morning", stockArray);
        });

        // Cancel action
        cancelBtn.setOnAction(e -> stage.close());

        vbox.getChildren().addAll(new Label("Outlet:"), outletField, startBtn, cancelBtn, resultArea);

        stage.setScene(new Scene(vbox, 400, 600));
        stage.setTitle("Stock Count");
        stage.show();
    }

    // ================== 3-PARAMETER CONSTRUCTOR ==================
    public StockCountView(Stage stage, ArrayList<Stock> stockArray, Runnable refreshCallback) {
        // Call the existing 2-parameter constructor
        this(stage, stockArray);

        // After counting, run the refresh callback (if provided)
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}
