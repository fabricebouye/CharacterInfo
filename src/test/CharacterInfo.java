/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Application CharacterInfo.
 * @author Fabrice Bouyé
 */
public class CharacterInfo extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
         // chargement de l'interface graphique.
        final ResourceBundle bundle = ResourceBundle.getBundle("test.CharacterInfo"); // NOI18N.
        final URL fxmlURL = getClass().getResource("CharacterInfo.fxml"); // NOI18N.
        final FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL, bundle);
        final Parent root = fxmlLoader.load();
        // Préparation de la scène.
        final Scene scene = new Scene(root);
        final URL cssURL = getClass().getResource("CharacterInfo.css"); // NOI18N.
        scene.getStylesheets().add(cssURL.toExternalForm());
        primaryStage.setTitle(bundle.getString("app.title")); // NOI18N.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}