package com.example.excelanalyzer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainApp extends Application {
    private TableView<SalesData> table = new TableView<>();
    private ObservableList<SalesData> data = FXCollections.observableArrayList();
    private boolean isDarkMode = false;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Button openButton = new Button("Open file");
        openButton.setOnAction(e -> openFile(stage));

        Button analyzeButton = new Button("Analyze");
        analyzeButton.setOnAction(e -> openAnalysisWindow(stage));

        Button themeToggleButton = new Button("Switch Theme");
        themeToggleButton.setOnAction(e -> toggleTheme(stage));

        setupTable();

        HBox topPanel = new HBox(10, openButton, analyzeButton, themeToggleButton);
        root.setTop(topPanel);
        root.setCenter(table);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Matical Excel Reader");
        stage.setScene(scene);

        applyTheme(scene);

        stage.show();
    }

    private void setupTable() {
        TableColumn<SalesData, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asString());

        TableColumn<SalesData, String> productColumn = new TableColumn<>("Name");
        productColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProductName()));

        TableColumn<SalesData, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getPrice())));

        TableColumn<SalesData, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));

        TableColumn<SalesData, String> totalSaleColumn = new TableColumn<>("Total Sales");
        totalSaleColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getTotalSale())));

        TableColumn<SalesData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSaleDate().toString()));

        table.getColumns().addAll(idColumn, productColumn, priceColumn, quantityColumn, totalSaleColumn, dateColumn);
    }

    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                ExcelReader excelReader = new ExcelReader();
                List<SalesData> salesDataList = excelReader.readExcelFile(file.getAbsolutePath());
                data.setAll(salesDataList);
                table.setItems(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openAnalysisWindow(Stage parentStage) {
        new AnalysisWindow(parentStage, data);
    }

    private void toggleTheme(Stage stage) {
        isDarkMode = !isDarkMode;
        Scene scene = stage.getScene();
        applyTheme(scene);
    }

    private void applyTheme(Scene scene) {
        if (isDarkMode) {
            scene.setFill(Color.rgb(40, 40, 40));

            scene.getRoot().setStyle(
                    "-fx-base: #333; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-color: #222; "
            );

            // дарк
            updateButtonStyle(Color.WHITE, Color.rgb(50, 50, 50)); // фон тут
            updateTableStyle(Color.rgb(50, 50, 50), Color.WHITE);
        } else {
            // Light mode styles
            scene.setFill(Color.rgb(255, 255, 255));

            scene.getRoot().setStyle(
                    "-fx-base: #f0f0f0; " +
                            "-fx-text-fill: black; " +
                            "-fx-background-color: #ffffff;"
            );

            // Update buttons and table for light mode
            updateButtonStyle(Color.BLACK, Color.rgb(240, 240, 240));
            updateTableStyle(Color.rgb(255, 255, 255), Color.BLACK);
        }
    }

    private void updateButtonStyle(Color textColor, Color backgroundColor) {
        for (javafx.scene.Node node : table.getScene().getRoot().lookupAll(".button")) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setTextFill(textColor);
                button.setStyle("-fx-background-color: " + toHexString(backgroundColor) + ";");
            }
        }
    }

    private void updateTableStyle(Color bgColor, Color textColor) {
        table.setStyle("-fx-background-color: " + toHexString(bgColor) + ";");
        table.setStyle("-fx-text-fill: " + toHexString(textColor) + ";");
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
