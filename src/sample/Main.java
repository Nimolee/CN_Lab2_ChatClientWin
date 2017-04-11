package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Main extends Application {

    Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        primaryStage.setTitle("ChatClientWin");
        primaryStage.setScene(new Scene(root));
        stage = primaryStage;
        stage.show();
        Controller.stage = stage;
        Controller.scene = stage.getScene();
    }

    public void OnLoginClick(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Chat.fxml"));
        stage.setScene(new Scene(root));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
