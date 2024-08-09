package csc.arizona.pbjava_asset_portfolio.View;

import csc.arizona.pbjava_asset_portfolio.AssetPortfolio;
import csc.arizona.pbjava_asset_portfolio.Controller.AssetPortfolioController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

//import com.jpro.JProApplication;

public class Sprint2GUI extends Application {
    private AssetPortfolioController controller= new AssetPortfolioController();
    public final static int WIDTH = 750;
    public final static int HEIGHT = 750;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Sprint 2: Asset Portfolio");
        FXMLLoader homeViewLoader = new FXMLLoader(AssetPortfolio.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(homeViewLoader.load(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }





}