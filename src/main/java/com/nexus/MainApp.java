package com.nexus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application{

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/com/nexus/main_view.fxml"));
        Scene scene = new Scene(root, 1000, 700);

        scene.getStylesheets().add(getClass().getResource("/com/nexus/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Nexus File Explorer - Alpha");
        stage.show();
    }
    public static void main(String args[]){
        launch(args);
    }
}
