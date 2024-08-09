package csc.arizona.pbjava_asset_portfolio.Controller;

import csc.arizona.pbjava_asset_portfolio.AssetPortfolio;
import csc.arizona.pbjava_asset_portfolio.Model.*;
import csc.arizona.pbjava_asset_portfolio.View.FinalGUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Main file to organize methods and functionality to buttons and other visuals in the program.
 */

public class AssetPortfolioController {
    private AssetPortfolioModel model;
    @FXML private LineChart<Integer, Double> graph;
    @FXML private LineChart<Number, Number> userChart;
    @FXML private TableView<Asset> stockTableView, cryptoTableView;
    @FXML private TableColumn<Asset, String>
            stockTickerColumn, stockPriceColumn, stockPriceChangeColumn, stockPercentChangeColumn,
            cryptoTickerColumn, cryptoPriceColumn, cryptoMarketCapColumn, cryptoPercentChangeColumn;
    @FXML private PieChart pieChart;
    @FXML private VBox homeLeftBox, addAllocationVBox;
    @FXML private TextField usernameField,searchBar;
    @FXML private PasswordField passwordField;
    @FXML private ListView homeList, userList, followList;
    @FXML private HBox homeCenterBox, homeTopBox, userCenterBox, userTopBox;
    @FXML private Button loginButton, logoutButton, refreshApi, refreshUserAsset, updateChartButton;
    @FXML private ImageView largeCloudImage, smallCloudImage;
    @FXML private Label chartLabel, portChangeLabel,portValueLabel,portInitialLabel,portAssetCountLabel;
    @FXML private NumberAxis xAxis, yAxis;

    private boolean newAsset = false;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    Asset shown;

    public AssetPortfolioController(){
        model = new AssetPortfolioModel();
    }

    // BUTTON ACTIONS
    @FXML public void onRefreshApiButtonClick() throws IOException{
        try{
            stockTableView.getItems().setAll( model.fetchAllStockFromAPI());
        }catch (Exception e){
            System.out.println("Could not fetch stocks.");
            e.printStackTrace();
        }

        try {
            cryptoTableView.getItems().setAll(model.fetchAllCryptoFromAPI());
        } catch (Exception e){
            System.out.println("Could not fetch cryptos.");
            e.printStackTrace();
        }
    }

    // Checks account information based on what's in the text fields and changes windows if it's correct
    @FXML public void onLoginButtonClick() throws IOException {
        AccountCollection.Logout();
        if(AccountCollection.Login(usernameField.getText(), passwordField.getText())) {
            FXMLLoader fxmlLoader = new FXMLLoader(AssetPortfolio.class.getResource("user-view.fxml"));
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load(), FinalGUI.WIDTH, FinalGUI.HEIGHT));
        } else {
            alert.setHeaderText("Invalid Credentials");
            alert.show();
        }
    }

    // Logs the currently logged in account out
    @FXML public void onLogoutButtonClick() throws IOException {
        AccountCollection.Logout();
        FXMLLoader fxmlLoader = new FXMLLoader(AssetPortfolio.class.getResource("hello-view.fxml"));
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load(), 750, 750));
    }

    // Creates a new account with the info in the text fields and logs in
    @FXML public void onCreateButtonClick(ActionEvent actionEvent) throws IOException {
        if(usernameField.getText() == "" || passwordField.getText() == "") {
            alert.setHeaderText("Invalid Credentials");
            alert.show();
            return;
        }
        if(AccountCollection.GetAccount(usernameField.getText()) != null) {
            alert.setHeaderText("That username already exists");
            alert.show();
            return;
        }
        AccountCollection.AddAccount(usernameField.getText(), passwordField.getText(), false);
        onLoginButtonClick();
    }

    // Updates line chart with updated history of the current stock
    @FXML public void onUpdateChartButtonClick(){
        shown.updatePriceHistory();
        makeChart(shown.getPriceHistory(), graph);
    }

    // Shows current view
    @FXML protected void initialize() throws IOException {
        if(AccountCollection.loggedIn != null){
            initializeUserView();
        }else{
            initializeHomeView();
        }
    }

    // Shows home view with the account's followed stocks
    private void initializeHomeView(){
        EventHandler<KeyEvent> searchBarEvent =
                keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        for (int i = 0; i < homeList.getItems().size(); i++) {
                            if (((Label) homeList.getItems().get(i)).getText().toLowerCase().contains(searchBar.getText().toLowerCase())) {
                                keyEvent.consume();
                                return;
                            }
                        }
                        System.out.println("Searching");
                        String stockName = searchBar.getText().toUpperCase();

                        for (Asset a : AssetCollection.getCryptos()) {
                            if (a.ticker.equals(stockName)) {
                                //Add to list?
                                updateHomeList(a, false);
                                keyEvent.consume();
                                return;
                            }
                        }

                        for (Asset a : AssetCollection.getStocks()) {
                            if (a.ticker.equals(stockName)) {
                                //Add to list?
                                updateHomeList(a, true);
                                keyEvent.consume();
                                return;
                            }
                        }
                        keyEvent.consume();
                    }
                };
        searchBar.setOnKeyPressed(searchBarEvent);

        //TABLE SETUP
        stockTickerColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("ticker"));
        stockPriceColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("price"));
        stockPercentChangeColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("percentChange"));
        stockPriceChangeColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("marketCap"));

        try{
            stockTableView.getItems().setAll(model.getAllStock());
        }catch (Exception e) {
            System.out.println("Could not fetch stocks.");
            e.printStackTrace();
        }

        stockTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        stockTableView.setOnMouseClicked(e -> {
            // call api for stock history
            // change line chart to show history
            if (stockTableView.getSelectionModel().getSelectedItem() != null) {
                shown = stockTableView.getSelectionModel().getSelectedItem();
                System.out.println("You clicked on " + shown.getTicker());
                updateHomeList(shown, true);
            }
        });

        cryptoTickerColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("ticker"));
        cryptoPriceColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("price"));
        cryptoPercentChangeColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("percentChange"));
        cryptoMarketCapColumn.setCellValueFactory(new PropertyValueFactory<Asset, String>("marketCap"));

        try {
            cryptoTableView.getItems().setAll(model.getAllCrypto());
        } catch (Exception e){
            System.out.println("Could not fetch cryptos.");
            e.printStackTrace();
        }

        cryptoTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        cryptoTableView.setOnMouseClicked(e -> {
            // call api for crypto history
            // change line chart to show history
            if (cryptoTableView.getSelectionModel().getSelectedItem() != null) {
                shown = cryptoTableView.getSelectionModel().getSelectedItem();
                System.out.println("You clicked on " + shown.getTicker());
                updateHomeList(shown, false);
            }
        });
    }

    // Buttons to change view of line chart to different times
    @FXML public void sixMonths() {
        makeChart(shown.getPriceHistory(180), graph);
    }
    @FXML public void threeMonths() {
        makeChart(shown.getPriceHistory(91), graph);
    }
    @FXML public void oneMonths() {
        makeChart(shown.getPriceHistory(31), graph);
    }
    @FXML public void oneWeek() {
        makeChart( shown.getPriceHistory(8), graph);
    }


    public void userChartSetup(){
        //LINE CHART SETUP
        ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections.observableArrayList();

        LineChart.Series<Double, Double> series1 = new LineChart.Series<Double, Double>();
        series1.setName("Series 1");
        series1.getData().add(new XYChart.Data<Double, Double>(0.0, 1.0));
        series1.getData().add(new XYChart.Data<Double, Double>(1.2, 1.4));
        series1.getData().add(new XYChart.Data<Double, Double>(2.2, 1.9));
        series1.getData().add(new XYChart.Data<Double, Double>(2.7, 2.3));
        series1.getData().add(new XYChart.Data<Double, Double>(2.9, 0.5));

        lineChartData.add(series1);

        LineChart.Series<Double, Double> series2 = new LineChart.Series<Double, Double>();
        series2.setName("Series 2");
        series2.getData().add(new XYChart.Data<Double, Double>(0.0, 1.6));
        series2.getData().add(new XYChart.Data<Double, Double>(0.8, 0.4));
        series2.getData().add(new XYChart.Data<Double, Double>(1.4, 2.9));
        series2.getData().add(new XYChart.Data<Double, Double>(2.1, 1.3));
        series2.getData().add(new XYChart.Data<Double, Double>(2.6, 0.9));

        lineChartData.add(series2);

        //userChart
        NumberAxis xAxis = new NumberAxis("Values for X-Axis", 0, 3, 1);
        NumberAxis yAxis = new NumberAxis("Values for Y-Axis", 0, 3, 1);

        userChart = new LineChart(xAxis, yAxis, lineChartData);

        userChart.createSymbolsProperty();
        userChart.setMaxWidth(200);
    }
    // Updates home view
    public void updateHomeList(Asset a, boolean stock){
        shown = a;
        List<DataPoint> shownHistory = a.getPriceHistory();
        if(shownHistory == null){
            return;
        }
        if(stock){
            chartLabel.setText(shown.getTicker()+" Weekly Price Chart");
            xAxis.setLabel("Days");
            yAxis.setLabel("$ Price");
        }else{
            chartLabel.setText(shown.getTicker()+" Daily Price Chart");
            xAxis.setLabel("Days");
            yAxis.setLabel("$ Price");
        }
        makeChart(shownHistory, graph);
        graph.setMaxWidth(400);
        homeList.getItems().clear();
        homeList.getItems().add(new Label(a.getTicker()+" Price: "+a.getPrice()));
        homeList.getItems().add(new Label(a.getTicker()+" Market Cap: "+a.getMarketCap()));
        if(a.crypto) {
            homeList.getItems().add(new Label(a.getTicker() + " Percent Change: " + a.getPercentChange()+"%"));
        }else{
            homeList.getItems().add(new Label(a.getTicker() + " Volume: " + a.getPercentChange()));
        }
//        homeList.getItems().add(new Label(a.getTicker()+" Info: "+a.info));
        //Add Asset Representation
    }
    // Gets price of a specific ticker
    public void getStockPrice(String ticker, Label priceLabel) throws IOException {
        Double price = model.getStockPrice(ticker);
        priceLabel.setText(String.valueOf(price));
    }
    // Gets history of the stock ticker
    public void getStockHistory(String ticker){
        model.getStockHistory(ticker);
    }
    // Gets history of the crypto ticker
    public void getCryptoPrice(String ticker, Label priceLabel) throws IOException {
        Double price = model.getCryptoPrice(ticker);
        priceLabel.setText(String.valueOf(price));
    }
    // Makes the line chart
    private void makeChart(List<DataPoint> list, LineChart lc){

        if(list == null) {
            return;
        }
        ObservableList<XYChart.Series<Integer, Double>> lineChartData = FXCollections.observableArrayList();

        LineChart.Series<Integer, Double> series1 = new LineChart.Series<Integer, Double>();
        series1.setName("Series 1");
        int j=0;
        for(int i=0;i<list.size();i++) {
            series1.getData().add(new XYChart.Data<Integer, Double>(j, list.get(i).price));
            j++;
        }

        lineChartData.add(series1);
        lc.setData(lineChartData);
        lc.createSymbolsProperty();
        lc.setLegendVisible(false);
        lc.setCreateSymbols(false);

    }
    private void makeBTCTechnicalChart(List<DataPoint> list){
        if(list == null) {
            return;
        }
        System.out.println("here");
        ObservableList<XYChart.Series<Integer, Double>> lineChartData = FXCollections.observableArrayList();

        LineChart.Series<Integer, Double> series1 = new LineChart.Series<Integer, Double>();
        series1.setName("Series 1");

        for(int i=0; i< list.size();i++){
            series1.getData().add(new XYChart.Data<Integer, Double>(i, list.get(i).price));
        }
        System.out.println("here2");
        lineChartData.add(series1);
        graph.setData(lineChartData);
        graph.createSymbolsProperty();
        graph.setMaxWidth(400);

    }

    // Show user view
    private void initializeUserView() {
        cloudSetup();
        portfolioStatsSetup();
        portfolioPieChartSetup();
        portfolioListSetup();
        addAllocationSetup();
    }
    public void portfolioStatsSetup(){
        Account user = AccountCollection.loggedIn;
        portValueLabel.setText("$"+Math.floor(user.portfolio.currPortfolioValue*100)/100);
        portInitialLabel.setText("$"+Math.floor(user.portfolio.initialPortfolioValue*100)/100);
        portChangeLabel.setText(""+Math.floor(user.portfolio.portfolioTotalChange*100)+"%");
        portAssetCountLabel.setText(""+user.portfolio.holdingCount);
        if(user.portfolio.following.isEmpty()){
            followList.setVisible(false);
        }else{
            followList.setVisible(true);
            followList.getItems().clear();
            followList.getItems().add("Following:");
            for(Asset f: user.portfolio.following){
                followList.getItems().add(f.ticker+":\n$"+f.price+"\n");
            }
        }
    }
    private void cloudSetup(){
        Label largeCloudLabel = new Label("Welcome Back\n"+AccountCollection.loggedIn.getUsername());

        largeCloudLabel.setStyle("-fx-font-size: 40;");
        largeCloudLabel.setTextAlignment(TextAlignment.CENTER);
        largeCloudLabel.setPadding(new Insets(0,0,0,100));

        Image i = null;
        try {
            i = new Image(new FileInputStream("src/main/resources/csc/arizona/pbjava_asset_portfolio/images/largeCloud.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        largeCloudImage = new ImageView(i);
        largeCloudImage.setFitHeight(150);
        largeCloudImage.setFitWidth(600);
//        largeCloudImage.fitWidthProperty().bind(userTopBox.widthProperty());

        Account user = AccountCollection.loggedIn;
        i = null;
        try {
            if(user.portfolio.portfolioDailyChange >= 1 || user.portfolio.portfolioDailyChange==0.0){
                i = new Image(new FileInputStream("src/main/resources/csc/arizona/pbjava_asset_portfolio/images/smallLightCloud.png"));
            }else{
                i = new Image(new FileInputStream("src/main/resources/csc/arizona/pbjava_asset_portfolio/images/smallDarkCloud.png"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        smallCloudImage = new ImageView(i);
        smallCloudImage.setFitHeight(125);
        smallCloudImage.setFitWidth(225);

        String sentiment;
        double portPercent = user.portfolio.portfolioDailyChange;
        String portChange;
        if(portPercent==0.0){
            sentiment = "Flat";
            portChange = "0.0%";
        }else if(portPercent<1.0 && portPercent>0.0){
            sentiment = "Down";
            portChange = "-"+(portPercent);
        }else{
            if(portPercent<0.0){
                portPercent*=-1;
            }
            sentiment = "Up";
            portChange = "+"+(portPercent-1.0)+"%";
        }

        Label smallCloudLabel = new Label("Your Portfolio is "+ sentiment +"\n"+portChange);
        smallCloudLabel.setTextAlignment(TextAlignment.CENTER);
        smallCloudLabel.setAlignment(Pos.CENTER);
        smallCloudLabel.setStyle("-fx-font-size: 15;");
        smallCloudLabel.setPadding(new Insets(50,30,0,0));

        StackPane smallCloudStack = new StackPane();
        smallCloudStack.getChildren().addAll(smallCloudImage, smallCloudLabel, logoutButton);
        smallCloudStack.setAlignment(Pos.TOP_RIGHT);
        StackPane largeCloudStack = new StackPane();
        largeCloudStack.getChildren().addAll(largeCloudImage, largeCloudLabel);
        largeCloudStack.setAlignment(Pos.CENTER_LEFT);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(largeCloudStack, smallCloudStack);

        StackPane.setAlignment(logoutButton,Pos.BOTTOM_RIGHT);
        stackPane.setPrefWidth(FinalGUI.WIDTH);

        this.userTopBox.getChildren().addAll(stackPane);
    }
    private void addAllocationSetup(){
        addAllocationVBox.getChildren().clear();
        HBox buttonBox = new HBox();

        Button addAssetButton = new Button("Add Asset Holding +");
        Button addFollowButton = new Button("Add Asset Following +");

        buttonBox.getChildren().addAll(addAssetButton,addFollowButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5,10,5,10));

        addAllocationVBox.getChildren().add(buttonBox);
        addAllocationVBox.setAlignment(Pos.CENTER);

        // ADD HOLDING
        addAssetButton.setOnAction(actionEvent -> {
            //TODO: Add Option for unlisted assets
            addAllocationVBox.getChildren().clear();
            HBox addAllocationHBox = new HBox();

            HBox titleHBox = new HBox();

            Label allocationTitle = new Label("Add Asset Allocation");
            allocationTitle.setStyle("-fx-font-size: 20;");

            Button createAssetButton = new Button("New Asset?");
            createAssetButton.setOnAction(actionEvent1 -> {newAsset = !newAsset; addAssetButton.fire();});
            Button backButton = new Button("Back");
            backButton.setOnAction(actionEvent1 -> {addAllocationSetup();});

            titleHBox.getChildren().addAll(allocationTitle,createAssetButton,backButton);
            titleHBox.setPadding(new Insets(0,0,0,100));
            createAssetButton.setAlignment(Pos.TOP_RIGHT);

            Label tickL  = new Label("    Ticker: ");
            Label quantL = new Label("  Quantity: ");
            Label costL  = new Label("Total Cost: ");
            TextField tickF = new TextField();
            TextField quantF = new TextField();
            TextField costF = new TextField();

//            Button enterB = new Button("Enter");
//            enterB.setOnAction(submitHoldingAction(buttonBox, tickF, quantF, costF));

            // SUBMIT ADD HOLDING
            if(newAsset){
                Label nameL   = new Label("      Name: ");
                Label priceL  = new Label("     Price: ");
                Label mktCapL = new Label("Market Cap: ");

                TextField mktCapF = new TextField();
                TextField nameF   = new TextField();
                TextField priceF  = new TextField();

                nameF.setOnKeyPressed(submitHoldingAction(buttonBox, tickF, quantF, costF, nameF, priceF, mktCapF));
                tickF.setOnKeyPressed(submitHoldingAction(buttonBox, tickF, quantF, costF, nameF, priceF, mktCapF));
                costF.setOnKeyPressed(submitHoldingAction(buttonBox, tickF, quantF, costF, nameF, priceF, mktCapF));
                quantF.setOnKeyPressed(submitHoldingAction(buttonBox, tickF, quantF, costF, nameF, priceF, mktCapF));

                HBox hb1 = new HBox(nameL,nameF,tickL,tickF,priceL,priceF);
                HBox hb2 = new HBox(mktCapL,mktCapF,quantL,quantF,costL,costF);
                VBox vb = new VBox(hb1,hb2);

                addAllocationHBox.getChildren().addAll(vb);
            }else{
                tickF.setOnKeyPressed(submitHoldingAction(buttonBox, tickF, quantF, costF, null, null, null));
                costF.setOnKeyPressed(submitHoldingAction(buttonBox, tickF, quantF, costF,  null, null, null));
                quantF.setOnKeyPressed(submitHoldingAction(buttonBox, tickF, quantF, costF,  null, null, null));
                addAllocationHBox.getChildren().addAll(tickL,tickF,quantL,quantF,costL,costF);
            }

            addAllocationHBox.setAlignment(Pos.CENTER);
            addAllocationHBox.setPadding(new Insets(5,10,5,10));

            addAllocationVBox.getChildren().addAll(titleHBox, addAllocationHBox);
            addAllocationVBox.setAlignment(Pos.CENTER);
        });

        // ADD FOLLOWING
        addFollowButton.setOnAction(actionEvent -> {
            //TODO: finish this part

            addAllocationVBox.getChildren().remove(buttonBox);
            HBox titleHBox = new HBox();
            Label followingTitle = new Label("Add Asset to Track: ");
            followingTitle.setStyle("-fx-font-size: 20;");
            Button backButton = new Button("Back");
            backButton.setOnAction(actionEvent1 -> {addAllocationSetup();});
            titleHBox.getChildren().addAll(followingTitle,backButton);
            titleHBox.setPadding(new Insets(0,0,0,100));
            HBox addFollowingHBox = new HBox();

            Label tickL = new Label("Ticker: ");
            TextField tickF = new TextField();
            tickF.setOnKeyPressed(submitFollowingAction(buttonBox, tickF));

            addFollowingHBox.getChildren().addAll(tickL,tickF);
            addFollowingHBox.setAlignment(Pos.CENTER);
            addFollowingHBox.setPadding(new Insets(5,10,5,10));

            addAllocationVBox.getChildren().addAll(titleHBox,addFollowingHBox);
            addAllocationVBox.setAlignment(Pos.CENTER);
        });
    }
    private EventHandler<KeyEvent> submitFollowingAction(HBox buttonBox, TextField tickF){
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    String tickFText = tickF.getText().toUpperCase();
                    Account user = AccountCollection.loggedIn;
                    Asset a = AssetCollection.getAsset(tickFText);

                    if (a == null) {
                        alert.setHeaderText("Asset Not Found");
                        alert.show();
                    } else {
                        user.addFollowing(a);
                        System.out.println(user.getFollowing());
                        addAllocationVBox.getChildren().clear();
                        addAllocationVBox.getChildren().add(buttonBox);
                    }
                    portfolioStatsSetup();
                }
            }
        };
    }
    private EventHandler<KeyEvent> submitHoldingAction(HBox buttonBox, TextField tickF, TextField quantF, TextField costF,TextField nameF, TextField priceF, TextField mktCapF) {
        return new EventHandler<KeyEvent>() {@Override public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                boolean allGood = true;

                String tickFText = tickF.getText().toUpperCase();
                String quantFText = quantF.getText();
                String costFText = costF.getText();
                String nameFText = "";
                String priceFText = "0.0";
                String mktCapFText = "0.0";
                Account user = AccountCollection.loggedIn;
                Asset a = AssetCollection.getAsset(tickFText);

                // IF CUSTOM ASSET
                if(nameF != null){
                    nameFText = nameF.getText();
                    priceFText = priceF.getText();
                    mktCapFText = mktCapF.getText();
                    if (nameFText.isBlank() || priceFText.isBlank() || mktCapFText.isBlank()
                            || tickFText.isBlank() || quantFText.isBlank() || costFText.isBlank()) {
                        alert.setHeaderText("Please Fill All Fields");
                        alert.show();
                        allGood = false;
                    }
                }else{
                    if (a == null) {
                        alert.setHeaderText("Asset Not Found");
                        alert.show();
                        allGood = false;
                    } else if (quantFText.isBlank()) {
                        alert.setHeaderText("Quantity Is Blank");
                        alert.show();
                        allGood = false;
                    } else if (costFText.isBlank()) {
                        alert.setHeaderText("Cost Is Blank");
                        alert.show();
                        allGood = false;
                    }
                }

                if(allGood){
                    // IF CUSTOM ASSET
                    if(nameF != null){

                        try {
                            Double.valueOf(priceFText);
                        } catch (NumberFormatException e) {
                            alert.setHeaderText("Price Is Not a Number");
                            alert.show();
                            return;
                        }

                        try {
                            BigDecimal.valueOf(Double.valueOf(mktCapFText));
                        } catch (NumberFormatException e) {
                            alert.setHeaderText("Market Cap Is Not a Number");
                            alert.show();
                            return;
                        }

                        nameF.clear();
                        priceF.clear();
                        mktCapF.clear();
                        a = new Asset(tickFText,
                                Double.valueOf(priceFText),
                                BigDecimal.valueOf(Double.valueOf(mktCapFText)),
                                0.0,
                                false,
                                true);
                    }

                    try {
                        Double.valueOf(quantFText);
                    } catch (NumberFormatException e) {
                        alert.setHeaderText("Quantity Is Not a Number");
                        alert.show();
                        return;
                    }
                    try {
                        Double.valueOf(costFText);
                    } catch (NumberFormatException e) {
                        alert.setHeaderText("Cost Is Not a Number");
                        alert.show();
                        return;
                    }

                    double quant = Double.valueOf(quantFText);
                    double cost = Double.valueOf(costFText);
                    user.addHolding(a, quant, cost);
                    //TODO: Add Asset Representation

                    tickF.clear();
                    quantF.clear();
                    costF.clear();
                    portfolioListSetup();
                    updatePieChart();
                    portfolioStatsSetup();
                    addAllocationVBox.getChildren().clear();
                    addAllocationVBox.getChildren().add(buttonBox);
                }
            }
        }
        };
    }
    private VBox getAssetBox(Asset a, Label info){
        VBox assetBox = new VBox();
        HBox titleBox = new HBox();
        HBox infoBox = new HBox();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart lc = new LineChart(xAxis, yAxis);

        if(!a.custom){
//            a.updatePriceHistory();
            if(!a.crypto) {
                Collections.reverse(a.getPriceHistory());
            }
            makeChart(a.getPriceHistory(), lc);

            lc.setMaxWidth(userList.getWidth()-50);
            lc.setMaxHeight(200);
        }

        assetBox.setBackground(new Background(new BackgroundFill(
                Color.color(Math.random(), Math.random(), Math.random()), CornerRadii.EMPTY, Insets.EMPTY)));
        titleBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        infoBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        String t = " "+a.getTicker();
        String v = "    $"+a.getPrice()+"      "
                +"$B " +a.getMarketCap() +"                     ";
        if(a.crypto){
            v = "    " +
                    "$"+a.getPrice()+"   "
                    +a.getPercentChange()+"%    $B "
                    +a.getMarketCap() +"              ";
        }else if(a.custom){
            v = "    $"+a.getPrice()+"      "
                    +"$" +a.getMarketCap() +"          ";
        }
        Label title = new Label(t);
        title.setStyle("-fx-font-weight: bold");
        Label values = new Label(v);

        Button rb = new Button("X");
        rb.setOnAction(removeHoldingAction(a));
        rb.setAlignment(Pos.BASELINE_RIGHT);
        rb.setStyle("-fx-background-radius:30px; -fx-font-size: 10pt; -fx-text-fill: blue; -fx-color: white;");

        Label l = new Label(
                "quantity:                         \n"+
                        "unit cost:                          \n"+
                        "total value:                        \n"+
                        "return:                             \n"+
                        "return %:                            \n");
        l.setAlignment(Pos.CENTER_LEFT);

        titleBox.getChildren().addAll(title,values, rb);
        infoBox.getChildren().addAll(l,info);
        if(a.custom){
            assetBox.getChildren().addAll(titleBox, infoBox);
        }else{
            assetBox.getChildren().addAll(titleBox, lc, infoBox);
        }

        titleBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setAlignment(Pos.BASELINE_LEFT);

        assetBox.setSpacing(15);
        assetBox.setPadding(new Insets(5, 20, 5, 20));

        return assetBox;
    }
    private void portfolioListSetup() {
        userList.getItems().clear();
        Account user = AccountCollection.loggedIn;
        for(AccountPortfolio.Holding h : user.getHoldings()){

            Label l = new Label(
                    "  "+Math.floor(h.quantity*100)/100+"\n"+
                            "$"+Math.floor(h.unitCost*100)/100+"\n"+
                            "$"+Math.floor(h.holdingValue*100)/100+"\n"+
                            "$"+Math.floor(h.profit*100)/100+"\n"+
                            "  "+Math.floor(h.profitPercent*100)+"%\n");
            l.setAlignment(Pos.CENTER_RIGHT);

            userList.getItems().add(getAssetBox(h.asset, l));
        }
    }
    private EventHandler<ActionEvent> removeHoldingAction(Asset a){
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Account user = AccountCollection.loggedIn;
                user.removeHolding(a);
                portfolioListSetup();
                updatePieChart();
                portfolioStatsSetup();
            }
        };
    }

    private void portfolioPieChartSetup(){
        //        PIE CHART SETUP
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for(AccountPortfolio.Holding h : AccountCollection.loggedIn.getHoldings()){
            pieData.add(new PieChart.Data(h.asset.ticker, h.percentOfPortfolio));
        }
        if(pieData.isEmpty()){
            pieData.add(new PieChart.Data("None", 1));
        }
        pieChart.setData(pieData);
        pieChart.setLegendVisible(false);
    }
    private void updatePieChart(){
        pieChart.getData().clear();

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for(AccountPortfolio.Holding h : AccountCollection.loggedIn.getHoldings()){
            pieData.add(new PieChart.Data(h.asset.ticker, h.percentOfPortfolio));
        }
        if(pieData.isEmpty()){
            pieData.add(new PieChart.Data("None", 1));
        }
        pieChart.setData(pieData);
//        pieChart.setLegendVisible(false);
    }
}
