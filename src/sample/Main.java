package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    public static void main(String[] args) {
        launch(args);
    }
}
