package stockjava.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import stockjava.Sale;
import stockjava.Sale.SaleRecord; // Import the inner class from Sale.java

import java.time.LocalDate;
import java.util.ArrayList;

public class ReportView extends Stage {

    // UI Components
    private TableView<SaleRecord> table;
    private ObservableList<SaleRecord> tableData;
    
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<String> sortComboBox;
    
    private Label totalRevenueLabel;
    private Label bestSellerLabel;

    // Data Source
    private ArrayList<SaleRecord> allRecords; // Master list loaded from files
    private ArrayList<SaleRecord> currentRecords; // Filtered list

    public ReportView() {
        setTitle("Sales Report & Analytics");
        
        // 1. Load Data
        loadData();

        // 2. Main Layout (BorderPane)
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // 3. Build Components (Using specific types now, not "Node")
        root.setTop(createTopPanel());
        root.setCenter(createTablePanel());
        root.setBottom(createAnalyticsPanel());

        // 4. Initial Refresh
        refreshTable();
        updateAnalytics();

        // 5. Scene Setup
        Scene scene = new Scene(root, 950, 650);
        setScene(scene);
    }

    // ================== DATA LOADING ==================
    private void loadData() {
        // Call the static method from Sale.java
        allRecords = Sale.loadSalesHistory();
        currentRecords = new ArrayList<>(allRecords);
        
        // Default Sort: Date Newest
        Sale.sortSalesByDate(currentRecords, false);
        
        // Convert to JavaFX ObservableList
        tableData = FXCollections.observableArrayList(currentRecords);
    }

    // ================== TOP PANEL (Filter & Sort) ==================
    // CHANGED: Returns HBox specifically (instead of private Node)
    private HBox createTopPanel() {
        HBox topBox = new HBox(15);
        topBox.setPadding(new Insets(0, 0, 15, 0));
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0; -fx-padding: 10;");

        // --- Date Filter ---
        Label startLbl = new Label("Start Date:");
        startDatePicker = new DatePicker(LocalDate.now().minusDays(30)); // Default 30 days back
        startDatePicker.setPrefWidth(120);

        Label endLbl = new Label("End Date:");
        endDatePicker = new DatePicker(LocalDate.now());
        endDatePicker.setPrefWidth(120);

        Button filterBtn = new Button("Apply Filter");
        filterBtn.setOnAction(e -> applyFilter());

        Button resetBtn = new Button("Show All");
        resetBtn.setOnAction(e -> resetFilter());

        // --- Sorting ---
        Label sortLbl = new Label("|  Sort By:");
        sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll(
            "Date (Newest First)",
            "Date (Oldest First)",
            "Amount (High to Low)",
            "Amount (Low to High)"
        );
        sortComboBox.getSelectionModel().select(0);
        sortComboBox.setOnAction(e -> applySort());

        // Add to HBox
        topBox.getChildren().addAll(
            startLbl, startDatePicker, 
            endLbl, endDatePicker, 
            filterBtn, resetBtn, 
            sortLbl, sortComboBox
        );

        return topBox;
    }

    // ================== CENTER PANEL (Table) ==================
    // CHANGED: Returns TableView specifically (instead of private Node)
    private TableView<SaleRecord> createTablePanel() {
        table = new TableView<>();
        
        // 1. Date Column
        TableColumn<SaleRecord, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(120);

        // 2. Model Column
        TableColumn<SaleRecord, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        modelCol.setPrefWidth(200);

        // 3. Quantity Column
        TableColumn<SaleRecord, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(80);

        // 4. Total Price Column (Custom formatting for "RM")
        TableColumn<SaleRecord, String> priceCol = new TableColumn<>("Total Price");
        priceCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("RM %.2f", cellData.getValue().getTotalPrice()))
        );
        priceCol.setPrefWidth(150);

        table.getColumns().addAll(dateCol, modelCol, qtyCol, priceCol);
        
        // Bind data
        table.setItems(tableData);
        
        return table;
    }

    // ================== BOTTOM PANEL (Analytics) ==================
    // CHANGED: Returns HBox specifically (instead of private Node)
    private HBox createAnalyticsPanel() {
        HBox bottomBox = new HBox(40);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #dddddd; -fx-border-width: 1 0 0 0;");

        // --- Revenue Box ---
        VBox revenueBox = new VBox(5);
        revenueBox.setAlignment(Pos.CENTER);
        Label revTitle = new Label("Total Revenue");
        revTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        totalRevenueLabel = new Label("RM 0.00");
        totalRevenueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        totalRevenueLabel.setTextFill(Color.web("#228B22")); // Forest Green
        revenueBox.getChildren().addAll(revTitle, totalRevenueLabel);

        // --- Best Seller Box ---
        VBox bestSellerBox = new VBox(5);
        bestSellerBox.setAlignment(Pos.CENTER);
        Label bestTitle = new Label("Best Selling Model");
        bestTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        bestSellerLabel = new Label("N/A");
        bestSellerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        bestSellerLabel.setTextFill(Color.web("#00008B")); // Dark Blue
        bestSellerBox.getChildren().addAll(bestTitle, bestSellerLabel);

        bottomBox.getChildren().addAll(revenueBox, bestSellerBox);
        return bottomBox;
    }

    // ================== LOGIC METHODS ==================

    private void refreshTable() {
        // Update the ObservableList which automatically updates the TableView
        if (tableData != null) {
            tableData.setAll(currentRecords);
        }
    }

    private void updateAnalytics() {
        // 1. Calculate Revenue
        double total = Sale.calculateTotalRevenue(currentRecords);
        totalRevenueLabel.setText("RM " + String.format("%.2f", total));

        // 2. Find Best Seller
        String best = Sale.getMostSoldModel(currentRecords);
        bestSellerLabel.setText(best);
    }

    private void applyFilter() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null) {
            showAlert("Date Error", "Please select both start and end dates.");
            return;
        }

        if (start.isAfter(end)) {
            showAlert("Date Error", "Start date cannot be after End date.");
            return;
        }

        // Use logic from Sale.java
        currentRecords = Sale.filterSalesByDate(allRecords, start, end);
        
        // Re-apply sorting preference
        applySort(); 
        
        // UI updates
        refreshTable();
        updateAnalytics();
    }

    private void resetFilter() {
        currentRecords = new ArrayList<>(allRecords);
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());
        
        applySort();
        refreshTable();
        updateAnalytics();
    }

    private void applySort() {
        String selected = sortComboBox.getValue();
        if (selected == null) return;

        switch (selected) {
            case "Date (Newest First)":
                Sale.sortSalesByDate(currentRecords, false);
                break;
            case "Date (Oldest First)":
                Sale.sortSalesByDate(currentRecords, true);
                break;
            case "Amount (High to Low)":
                Sale.sortSalesByAmount(currentRecords, false);
                break;
            case "Amount (Low to High)":
                Sale.sortSalesByAmount(currentRecords, true);
                break;
        }
        refreshTable();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}