package de.bht.fpa.mail.s769164.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Katrin Jeremies
 */

public class MailMain extends Application {    
 
    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/de/bht/fpa/mail/s769164/view/MailFXML.fxml"));

        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("FPA Mailer");
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}