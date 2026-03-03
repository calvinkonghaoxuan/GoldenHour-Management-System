package stockjava.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stockjava.Stock;
import stockjava.control;

import java.util.ArrayList;

public class RecordSaleView {

    private static class CartItem {
        String model;
        int qty;
        double price;

        CartItem(String m, int q, double p) {
            model = m;
            qty = q;
            price = p;
        }
    }

    public RecordSaleView(Stage stage, ArrayList<Stock> stockList, Runnable refresh) {

        TextField modelField = new TextField();
        TextField qtyField = new TextField();

        Button addBtn = new Button("Add Item");
        Button removeBtn = new Button("Remove Selected");
        Button finalizeBtn = new Button("Finalize Sale");
        Button cancelBtn = new Button("Cancel");

        TableView<CartItem> table = new TableView<>();
        ObservableList<CartItem> cart = FXCollections.observableArrayList();
        table.setItems(cart);

        TableColumn<CartItem, String> mCol = new TableColumn<>("Model");
        mCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().model));

        TableColumn<CartItem, Integer> qCol = new TableColumn<>("Qty");
        qCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().qty).asObject());

        TableColumn<CartItem, Double> pCol = new TableColumn<>("Price");
        pCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().price).asObject());

        table.getColumns().addAll(mCol, qCol, pCol);

        Label subtotalLabel = new Label("Subtotal: RM0.00");
        cart.addListener((javafx.collections.ListChangeListener<CartItem>) c -> {
            double total = cart.stream().mapToDouble(i -> i.qty * i.price).sum();
            subtotalLabel.setText(String.format("Subtotal: RM%.2f", total));
        });

        GridPane input = new GridPane();
        input.setHgap(10);
        input.setVgap(10);
        input.addRow(0, new Label("Model:"), modelField);
        input.addRow(1, new Label("Quantity:"), qtyField);
        input.addRow(2, addBtn, removeBtn);

        HBox bottom = new HBox(10, finalizeBtn, cancelBtn);
        VBox root = new VBox(10, input, table, subtotalLabel, bottom);
        root.setStyle("-fx-padding:20");

        stage.setScene(new Scene(root, 500, 450));
        stage.setTitle("Record Sale");
        stage.show();

        // ===== Add item to cart =====
        addBtn.setOnAction(e -> {
            try {
                String model = modelField.getText().trim();
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (model.isEmpty()) throw new Exception("Model cannot be empty");
                if (qty <= 0) throw new Exception("Quantity must be > 0");

                String outlet = control.getSelectedOutletCode();
                if (outlet == null) throw new Exception("Outlet not set");

                Stock s = stockList.stream()
                        .filter(x -> x.getModel().equalsIgnoreCase(model))
                        .findFirst()
                        .orElseThrow(() -> new Exception("Model not found"));

                if (s.getQuantityByOutlet(outlet) < qty)
                    throw new Exception("Not enough stock for this model");

                boolean found = false;
                for (CartItem item : cart) {
                    if (item.model.equalsIgnoreCase(model)) {
                        item.qty += qty;
                        table.refresh();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cart.add(new CartItem(model, qty, s.getPrice()));
                }

                modelField.clear();
                qtyField.clear();

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        // ===== Remove item =====
        removeBtn.setOnAction(e -> {
            CartItem selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) cart.remove(selected);
        });

        // ===== Finalize sale =====
        finalizeBtn.setOnAction(e -> {
            try {
                if (cart.isEmpty()) throw new Exception("Cart is empty");

                String outlet = control.getSelectedOutletCode();
                if (outlet == null) throw new Exception("Outlet not set");

                // Buyer name
                TextInputDialog buyerDialog = new TextInputDialog();
                buyerDialog.setHeaderText("Enter Buyer Name:");
                buyerDialog.setContentText("Name:");
                String buyerName = buyerDialog.showAndWait().orElse("").trim();
                if (buyerName.isEmpty()) throw new Exception("Buyer name cannot be empty");

                // Payment method
                TextInputDialog paymentDialog = new TextInputDialog();
                paymentDialog.setHeaderText("Enter Payment Method:");
                paymentDialog.setContentText("Method:");
                String paymentMethod = paymentDialog.showAndWait().orElse("").trim();
                if (paymentMethod.isEmpty()) throw new Exception("Payment method cannot be empty");

                // Convert GUI cart items to SaleItem
                ArrayList<control.SaleItem> saleCart = new ArrayList<>();
                for (CartItem item : cart) {
                    saleCart.add(new control.SaleItem(item.model, item.qty));
                }

                // Get current employee info (fixed method names)
                String empID = control.getCurrentEmployeeID();
                String empName = control.getCurrentEmployeeName();

                // Record sale using the correct method
                control.completeSale(stockList, saleCart, outlet, buyerName, paymentMethod, empID, empName);

                refresh.run();
                stage.close();

            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> stage.close());
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
