package com.example.chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private Network network;
    @FXML
    private Label welcomeText;
    @FXML
    TextField msgField;
    @FXML
    TextArea mainArea;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new Network();
    }

    public void sendMsgAction(ActionEvent actionEvent) {
        network.sendMessage(msgField.getText());
        msgField.clear();
        msgField.requestFocus();
    }
}