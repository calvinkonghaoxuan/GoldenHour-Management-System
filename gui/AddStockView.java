package stockjava.gui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import stockjava.Stock;
import stockjava.control;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class AddStockView {

    public AddStockView(Stage stage, ArrayList<Stock> stockArray, Runnable refreshCallback) {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-padding:20");

        TextField modelField = new TextField();
        TextField priceField = new TextField();

        // Outlets
        TextField klccField = new TextField("0");
        TextField mvField = new TextField("0");
        TextField svField = new TextField("0");
        TextField icmField = new TextField("0");
        TextField llField = new TextField("0");
        TextField klemField = new TextField("0");
        TextField nsField = new TextField("0");
        TextField pklField = new TextField("0");
        TextField u1Field = new TextField("0");
        TextField mtField = new TextField("0");
        TextField hqField = new TextField("0");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");

        grid.addRow(0, new Label("Model:"), modelField);
        grid.addRow(1, new Label("Price:"), priceField);
        grid.addRow(2, new Label("KLCC:"), klccField);
        grid.addRow(3, new Label("MIDVALLEY:"), mvField);
        grid.addRow(4, new Label("SUNWAYVELOCITY:"), svField);
        grid.addRow(5, new Label("IOICM:"), icmField);
        grid.addRow(6, new Label("LALAPORT:"), llField);
        grid.addRow(7, new Label("KLEASTM:"), klemField);
        grid.addRow(8, new Label("NSENTRAL:"), nsField);
        grid.addRow(9, new Label("PAVILLIONKL:"), pklField);
        grid.addRow(10, new Label("U1:"), u1Field);
        grid.addRow(11, new Label("MYTOWN:"), mtField);
        grid.addRow(12, new Label("HQ:"), hqField);
        grid.addRow(13, addBtn, cancelBtn);

        addBtn.setOnAction(e -> {
            try {
                String model = modelField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());

                Stock newStock = new Stock(
                        LocalDate.now(), LocalTime.now(), model, price,
                        Integer.parseInt(klccField.getText()),
                        Integer.parseInt(mvField.getText()),
                        Integer.parseInt(svField.getText()),
                        Integer.parseInt(icmField.getText()),
                        Integer.parseInt(llField.getText()),
                        Integer.parseInt(klemField.getText()),
                        Integer.parseInt(nsField.getText()),
                        Integer.parseInt(pklField.getText()),
                        Integer.parseInt(u1Field.getText()),
                        Integer.parseInt(mtField.getText()),
                        Integer.parseInt(hqField.getText())
                );

                stockArray.add(newStock);
                control.writeFile(stockArray);
                refreshCallback.run();
                stage.close();

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numbers!");
                alert.showAndWait();
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        stage.setScene(new Scene(grid, 400, 650));
        stage.setTitle("Add Stock");
        stage.show();
    }
}
