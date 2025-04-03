package com.example.excelanalyzer;

import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class ChartAnalyzer {
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series;
    private Text revenueText;
    private NumberAxis xAxis;

    public ChartAnalyzer() {
        xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Date of sales");
        yAxis.setLabel("Sum of sales (₽)");

        xAxis.setAutoRanging(false); // Отключаем авто-масштабирование
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return LocalDate.ofEpochDay(object.longValue()).toString();
            }
            @Override
            public Number fromString(String string) {
                return LocalDate.parse(string).toEpochDay();
            }
        });

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Dynamic Sales Chart");
        lineChart.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #d1d1d1; -fx-padding: 10px;");

        series = new XYChart.Series<>();
        series.setName("Sales Data");

        revenueText = new Text("Total Revenue: 0 ₽");
        revenueText.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-fill: #333;");

        lineChart.getData().add(series);
        lineChart.setCreateSymbols(true);
        lineChart.setLegendVisible(true);
    }

    public void updateChart(ObservableList<SalesData> salesData, boolean groupByMonth) {
        series.getData().clear();
        double totalRevenue = 0.0;

        if (salesData.isEmpty()) return;

        salesData.sort((a, b) -> a.getSaleDate().compareTo(b.getSaleDate()));

        Map<Long, Double> salesMap = new TreeMap<>();

        for (SalesData data : salesData) {
            LocalDate saleDate = data.getSaleDate();
            long dateKey = saleDate.toEpochDay();
            salesMap.put(dateKey, salesMap.getOrDefault(dateKey, 0.0) + data.getTotalSale());
        }

        long minDate = salesMap.keySet().stream().min(Long::compareTo).orElse(0L);
        long maxDate = salesMap.keySet().stream().max(Long::compareTo).orElse(0L);
        xAxis.setLowerBound(minDate);
        xAxis.setUpperBound(maxDate);
        xAxis.setTickUnit((maxDate - minDate) / 10.0); // Разбиваем ось X на 10 частей

        for (Map.Entry<Long, Double> entry : salesMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            totalRevenue += entry.getValue();
        }

        revenueText.setText(String.format("Total Revenue: %.2f ₽", totalRevenue));
    }

    public StackPane getChart() {
        StackPane stackPane = new StackPane(lineChart);
        stackPane.setStyle("-fx-padding: 20px;");
        StackPane.setAlignment(revenueText, javafx.geometry.Pos.BOTTOM_LEFT);
        stackPane.getChildren().add(revenueText);
        return stackPane;
    }
}
