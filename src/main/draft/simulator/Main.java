package main.draft.simulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static String SERVER_URL = "http://localhost:8080";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../../resources/views/mainScreen.fxml"));
        primaryStage.setTitle("League of Legends Draft Simulator");
        Scene scene = new Scene(root);
        String css = getClass().getResource("/main/resources/styles/mainScreenStyle.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
