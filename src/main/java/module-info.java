module csc.arizona.pbjava_asset_portfolio {
    requires javafx.controls;
    requires javafx.fxml;
    requires jfx.asynctask;
    requires jetty.util;
    requires org.json;
    requires javafx.graphics;

    opens csc.arizona.pbjava_asset_portfolio to javafx.fxml;
    exports csc.arizona.pbjava_asset_portfolio.View;
    opens csc.arizona.pbjava_asset_portfolio.View to javafx.fxml;
    exports csc.arizona.pbjava_asset_portfolio.Controller;
    opens csc.arizona.pbjava_asset_portfolio.Controller to javafx.fxml;
    exports csc.arizona.pbjava_asset_portfolio.Model;
    opens csc.arizona.pbjava_asset_portfolio.Model to javafx.fxml;
}