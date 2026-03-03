package stockjava.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stockjava.Stock;
import stockjava.control;
import stockjava.Login;

import java.util.ArrayList;

public class DashboardView {

    private Scene scene;
    private ObservableList<Stock> stockData;
    private TableView<Stock> table;
    private ArrayList<Stock> stockArray;

    public DashboardView(Stage stage, ArrayList<Stock> stockArray) {
        this.stockArray = stockArray;

        // === Observable List ===
        stockData = FXCollections.observableArrayList(stockArray);

        // === Table Setup ===
        table = new TableView<>();
        table.setItems(stockData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columns
        table.getColumns().addAll(
                createCol("Model", "model", 120),
                createCol("Price", "price", 80),
                createCol("KLCC", "KLCC", 60),
                createCol("MIDVALLEY", "MV", 80),
                createCol("SUNWAYVELOCITY", "SV", 100),
                createCol("IOICM", "ICM", 60),
                createCol("LALAPORT", "LL", 60),
                createCol("KLEASTM", "KLEM", 80),
                createCol("NSENTRAL", "NS", 80),
                createCol("PAVILLIONKL", "PKL", 100),
                createCol("U1", "U1", 60),
                createCol("MYTOWN", "MT", 80),
                createCol("HQ", "HQ", 60)
        );

        // === Logged-in User Info ===
        Label userInfo = new Label(
                "👤 " + Login.getCurrentUserName() +
                " (" + Login.getCurrentUserRole() + ")    |    " +
                "🏢 " + Login.yourOutletCode() + " - " + Login.yourOutletName()
        );
        userInfo.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8;" +
                "-fx-background-color: #e8f0ff;" +
                "-fx-border-color: #a0b4ff;"
        );

        // === Clock In / Clock Out Buttons ===
        Button clockInBtn = new Button("Clock In");
        Button clockOutBtn = new Button("Clock Out");
        Label clockStatus = new Label(""); // show clock in/out time

        clockInBtn.setOnAction(e -> {
            String time = Login.clockInGUI();
            if (time != null) clockStatus.setText("🟢 Clocked In at: " + time);
            else clockStatus.setText("❌ Employee not found!");
        });

        clockOutBtn.setOnAction(e -> {
            String time = Login.clockOutGUI();
            if (time != null) clockStatus.setText("🔴 Clocked Out at: " + time);
            else clockStatus.setText("❌ Employee not found!");
        });

        HBox clockBox = new HBox(10, clockInBtn, clockOutBtn, clockStatus);
        VBox topBox = new VBox(8, userInfo, clockBox);
        topBox.setStyle("-fx-padding:10");

        // === Bottom HBox: Stock Management Buttons ===
        Button addStockBtn = new Button("Add Stock");
        Button updateStockBtn = new Button("Update Stock");
        Button stockInBtn = new Button("Stock In");
        Button stockOutBtn = new Button("Stock Out");
        Button stockCountBtn = new Button("Stock Count");
        Button salesBtn = new Button("Record Sale");
        
        // ⬇️⬇️⬇️ New Addition: Report Button ⬇️⬇️⬇️
        Button reportBtn = new Button("Sales Report"); 
        // ⬆️⬆️⬆️ End of New Addition ⬆️⬆️⬆️
        
        Button logoutBtn = new Button("Logout");

        // ⬇️⬇️⬇️ Update: Added reportBtn to HBox ⬇️⬇️⬇️
        HBox bottomBox = new HBox(10, addStockBtn, updateStockBtn, stockInBtn, stockOutBtn,
                stockCountBtn, salesBtn, reportBtn, logoutBtn);
        // ⬆️⬆️⬆️ End of Update ⬆️⬆️⬆️
        
        bottomBox.setStyle("-fx-padding:10");

        // === Layout ===
        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(table);
        root.setBottom(bottomBox);

        scene = new Scene(root, 1200, 600);

        // === Button Actions with auto-save ===
        addStockBtn.setOnAction(e -> new AddStockView(new Stage(), stockArray, () -> refreshTable()));
        updateStockBtn.setOnAction(e -> new UpdateStockView(new Stage(), table, stockArray, () -> refreshTable()));
        stockInBtn.setOnAction(e -> new StockInView(new Stage(), stockArray, () -> refreshTable()));
        stockOutBtn.setOnAction(e -> new StockOutView(new Stage(), stockArray, () -> refreshTable()));
        stockCountBtn.setOnAction(e -> new StockCountView(new Stage(), stockArray, () -> refreshTable()));
        salesBtn.setOnAction(e -> new RecordSaleView(new Stage(), stockArray, () -> refreshTable()));
        
        // ⬇️⬇️⬇️ New Addition: Action for Report Button ⬇️⬇️⬇️
        reportBtn.setOnAction(e -> new ReportView().show());
        // ⬆️⬆️⬆️ End of New Addition ⬆️⬆️⬆️

        logoutBtn.setOnAction(e -> {
            LoginView login = new LoginView(stage);
            stage.setScene(login.getScene());
        });
    }

    // === Refresh table from current stockArray ===
    private void refreshTable() {
        stockData.setAll(stockArray);
    }

    private TableColumn<Stock, ?> createCol(String title, String property, double width) {
        TableColumn<Stock, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    public Scene getScene() {
        return scene;
    }
}