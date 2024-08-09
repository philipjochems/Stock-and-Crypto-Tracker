package csc.arizona.pbjava_asset_portfolio.Model;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Author: Peter Bedrick (peterbedrick@email.arizona.edu)
 * Class: CSC436
 *
 * LoginUX Class
 * Purpose: Pop-up window for logging in with an account.
 */
public class LoginUX {

    /**
     * Creates a pop-up window to let user log in to an account in the AccountCollection system.
     * If successfully logs in, window closes, if not, an error message is produced and waits for
     * another login attempt.
     * @param primary : parent stage (Sprint1Gui stage)
     * @return : login Stage created
     */
    public static Stage loginWindow(Stage primary) {
        Stage stage = new Stage();
        BorderPane window = new BorderPane();
        Scene scene = new Scene(window, 200, 200);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Invalid Credentials");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setMaxWidth(100);
        vbox.setAlignment(Pos.CENTER);
        window.setCenter(vbox);

        Label usernameText = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordText = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button login = new Button("Login");
        login.setOnAction(e -> {
            if(AccountCollection.Login(usernameField.getText(), passwordField.getText())) {
                //Sprint1Gui.Login();
                stage.close();
            } else {
                alert.show();
            }
        });

        vbox.getChildren().addAll(usernameText, usernameField, passwordText, passwordField, login);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.initOwner(primary);
        stage.initModality(Modality.WINDOW_MODAL);
        return stage;
    }

}
