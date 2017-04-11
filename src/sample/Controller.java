package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Controller {
    static Stage stage;
    static Scene scene;
    DataInputStream _DIS;
    DataOutputStream _DOS;


    @FXML
    public void loginOnClick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                javafx.scene.control.TextField loginTF = (javafx.scene.control.TextField) scene.lookup("#username");
                javafx.scene.control.TextField serverIP_TF = (javafx.scene.control.TextField) scene.lookup("#server_ip");
                javafx.scene.control.TextField passwordTF = (javafx.scene.control.TextField) scene.lookup("#password");
                String serverIP = serverIP_TF.getText();
                String login = loginTF.getText();
                String password = passwordTF.getText();
                Socket socket;
                int port = 2170;
                InetAddress inetAddress;
                try {
                    inetAddress = InetAddress.getByName(serverIP);
                    try {
                        socket = new Socket(inetAddress, port);
                        socket.setSoTimeout(0);
                    } catch (Exception e) {
                        return;
                    }
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String loginData = "l\n";
                    loginData += login + "\n";
                    loginData += password + "\n";
                    dataOutputStream.writeUTF(loginData);
                    String answer = dataInputStream.readUTF();
                    if (answer.equals("l-ok")) {
                        _DIS = dataInputStream;
                        _DOS = dataOutputStream;
                        Parent root = FXMLLoader.load(getClass().getResource("Chat.fxml"));
                        scene = new Scene(root);
                        Platform.runLater(() -> stage.setScene(scene));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void signUpOnClick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket;
                InetAddress inetAddress;
                javafx.scene.control.TextField loginTF = (javafx.scene.control.TextField) scene.lookup("#username");
                javafx.scene.control.TextField serverIP_TF = (javafx.scene.control.TextField) scene.lookup("#server_ip");
                javafx.scene.control.TextField passwordTF = (javafx.scene.control.TextField) scene.lookup("#password");
                javafx.scene.control.TextField confirmPasswordTF = (javafx.scene.control.TextField) scene.lookup("#password");
            }
        }).start();
    }

    public void openLogin() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        scene = new Scene(root);
        Platform.runLater(() -> stage.setScene(scene));
    }

    public void openSignUp() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
        scene = new Scene(root);
        Platform.runLater(() -> stage.setScene(scene));
    }
}
