package sample;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

public class Controller {
    static Stage stage;
    static Scene scene;
    private DataInputStream _DIS;
    static DataOutputStream _DOS;

    @FXML
    public void loginOnClick() {
        new Thread(new Runnable() {

            HashMap<String, Boolean> send_to = new HashMap<>();

            @Override
            public void run() {
                TextField loginTF = (TextField) scene.lookup("#login_username");
                TextField serverIP_TF = (TextField) scene.lookup("#login_server_ip");
                PasswordField passwordTF = (PasswordField) scene.lookup("#login_password");
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
                        new Thread(() -> {
                            try {
                                ObservableList<String> users;
                                ListView listView = (ListView) scene.lookup("#ChatLV");
                                ListView listViewUser = (ListView) scene.lookup("#selectUserLV");
                                listViewUser.setCellFactory(CheckBoxListCell.forListView((Callback<String, ObservableValue<Boolean>>) item -> {
                                    BooleanProperty observable = new SimpleBooleanProperty();
                                    observable.addListener((obs, wasSelected, isNowSelected) ->
                                            {
                                                send_to.replace(item, !send_to.get(item));
                                                String out = "t";
                                                for (int i = 0; i < listViewUser.getItems().size(); i++) {
                                                    if (send_to.get(listViewUser.getItems().get(i)))
                                                        out += "\n" + listViewUser.getItems().get(i).toString().split("")[0];
                                                }
                                                try {
                                                    _DOS.writeUTF(out);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected);
                                            }
                                    );
                                    return observable;
                                }));
                                ObservableList<String> items = FXCollections.observableArrayList();

                                while (true) {
                                    final String msg = _DIS.readUTF();
                                    switch (msg.split("\n")[0]) {
                                        case "m":
                                            items.add(msg.split("\n")[2] + "[" + msg.split("\n")[1] + "]:\n" + msg.split("\n")[3]);
                                            listView.setItems(items);
                                            break;
                                        case "u":
                                            users = FXCollections.observableArrayList();
                                            send_to = new HashMap<>();
                                            for (int i = 1; i < msg.split("\n").length; i++) {
                                                users.add(msg.split("\n")[i]);
                                                send_to.put(msg.split("\n")[i], false);
                                            }
                                            listViewUser.setItems(users);
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void sendMessageOnClick() {
        new Thread(() -> {
            try {
                String msg = "m\n" + ((TextField) scene.lookup("#textTF")).getText();
                _DOS.writeUTF(msg);
                ((TextField) scene.lookup("#textTF")).setText("");
            } catch (IOException e) {
                e.printStackTrace();
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
