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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.beans.Visibility;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
                javafx.scene.control.TextField loginTF = (javafx.scene.control.TextField) scene.lookup("#login_username");
                javafx.scene.control.TextField serverIP_TF = (javafx.scene.control.TextField) scene.lookup("#login_server_ip");
                javafx.scene.control.TextField passwordTF = (javafx.scene.control.TextField) scene.lookup("#login_password");
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
        new Thread(() -> {
            int port = 2170;
            Socket socket;
            InetAddress inetAddress;
            TextField loginTF = (TextField) scene.lookup("#sign_up_username");
            TextField serverIP_TF = (TextField) scene.lookup("#sign_up_server_ip");
            TextField passwordTF = (TextField) scene.lookup("#sign_up_password");
            TextField confirmPasswordTF = (TextField) scene.lookup("#sign_up_cpassword");
            Text error = (Text) scene.lookup("#sign_up_error");
            try {
                inetAddress = InetAddress.getByName(serverIP_TF.getText());
                socket = new Socket(inetAddress, port);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String signUpData = "r\n";
                signUpData += loginTF.getText() + "\n";
                if (!signUpData.contains(" ")) {
                    String signUpPassword = passwordTF.getText();
                    if (signUpPassword.length() > 3 && signUpPassword.length() < 33) {
                        if (!signUpPassword.contains(" ")) {
                            if (signUpPassword.equals(confirmPasswordTF.getText())) {
                                signUpData += signUpPassword + "\n";
                                dataOutputStream.writeUTF(signUpData);
                                String answer = dataInputStream.readUTF();
                                if (answer.equals("s-ok")) {
                                    error.setText("Реєстрація пройшла успішно.");
                                } else {
                                    error.setText("Даний логін вже зайнято.");
                                }
                            } else {
                                error.setText("Не співпадають паролі.");
                            }
                        } else {
                            error.setText("Пароль не повинен містити пробіли.");
                        }
                    } else {
                        error.setText("Неприпустима довжина паролю.");
                    }
                } else {
                    error.setText("Логін не повинен містити пробіли.");
                }
            } catch (Exception e) {
                error.setText("Сервер недоступний.");
                e.printStackTrace();
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
