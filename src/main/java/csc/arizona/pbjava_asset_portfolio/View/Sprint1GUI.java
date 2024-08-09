package csc.arizona.pbjava_asset_portfolio.View;

import csc.arizona.pbjava_asset_portfolio.AssetPortfolio;
import csc.arizona.pbjava_asset_portfolio.Controller.AssetPortfolioController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Sprint1GUI extends Application {
    private AssetPortfolioController controller = new AssetPortfolioController();
    private final static int WIDTH = 750;
    private final static int HEIGHT = 750;
    private BorderPane homePane;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Sprint 1: Asset Portfolio");
        FXMLLoader fxmlLoader = new FXMLLoader(AssetPortfolio.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
//        homePane = startupUI();
//        Scene scene = new Scene(homePane, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    public BorderPane startupUI(){
        BorderPane bp = new BorderPane();

        Label welcomeLabel = new Label();
        welcomeLabel.setId("welcomeText");

        Button welcomeButton = new Button("Hello!");

        welcomeButton.setOnAction(actionEvent -> welcomeLabel.setText("Welcome to our Asset Portfolio Manager"));

        VBox welcomeBox = new VBox(welcomeLabel, welcomeButton);
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(20,20,20,20));

        String[] stocks = {"TSLA", "SQ", "FB"};
        String[] crypto = {"BTC", "ADA"};
        for(String ticker: stocks){
            HBox stockBox = getAssetBox(ticker, false);
            welcomeBox.getChildren().add(stockBox);
        }
        for(String ticker: crypto){
            HBox stockBox = getAssetBox(ticker, true);
            welcomeBox.getChildren().add(stockBox);
        }

        //Adds the linechart and pieChart
        BorderPane center = new BorderPane();
        center.setTop(createPieChart(stocks));
        center.setCenter(makeLineChart(stocks));
        bp.setBottom(welcomeBox);
        bp.setCenter(center);

        // Creates search bar and search button at the top
        BorderPane top = new BorderPane();
        TextField searchBar = new TextField();
        top.setTop(searchBar);
        Button searchButton = new Button("Search");

        top.setLeft(searchButton);
        bp.setTop(top);

        //Returns border pane
        return bp;
    }

    private HBox getAssetBox(String ticker, boolean crypto){
        Label priceLabel = new Label();
        Button assetButton = new Button(ticker);
        if(crypto){
            assetButton.setOnAction(actionEvent -> {
                try {
                    controller.getCryptoPrice(ticker, priceLabel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }else{
            assetButton.setOnAction(actionEvent -> {
                try {
                    controller.getStockPrice(ticker, priceLabel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        HBox stockBox = new HBox(assetButton, priceLabel);
        stockBox.setAlignment(Pos.CENTER);
        return stockBox;
    }


    /**
     * Creates and returns a piechart made from data that has been selected
     * @param values
     * @return chart -- Piechart made from data
     */
    public PieChart createPieChart(String[] values){

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for(String dataPoint : values){
            int dataValue =1;       // Replace with data
            pieData.add(new PieChart.Data(dataPoint, dataValue));
        }
        PieChart myChart = new PieChart(pieData);
        myChart.setTitle("Stocks data");
        myChart.setPrefSize(250,250);
        return myChart;
    }


    /**
     * Creates a Line Chart based on the values in a potfolio
     * @param values : the portfolio
     * @return lineChart : linechart made from values in portfolio
     */
    public LineChart makeLineChart(String[] values){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        String stockName="Insert stock name";
        LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setTitle(stockName);
        XYChart.Series mySeries = new XYChart.Series();
        mySeries.setName("Portfolio for: "+stockName);
        int increament=10;
        for(String dataPoint : values) {
            mySeries.getData().add(new XYChart.Data(dataPoint, 10+increament));
            increament+=10;
        }
        lineChart.getData().add(mySeries);
        lineChart.setPrefSize(250,250);
        return lineChart;
    }





}