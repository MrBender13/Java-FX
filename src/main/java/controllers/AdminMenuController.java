package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable{
    @FXML
    private Button statsButton;

    @FXML
    private Button redactorButton;

    @FXML
    private Button exitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //opening redactor window
        redactorButton.setOnAction(e -> {
            Stage newStage = new Stage();
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/redactor.fxml"));
                newStage.setTitle("Store managing");
                newStage.setScene(new Scene(root, 600, 500));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.showAndWait();
        });

        //opening statistics window
        statsButton.setOnAction(e -> {
            Stage newStage = new Stage();
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/stats.fxml"));
                newStage.setTitle("Statistics");
                newStage.setScene(new Scene(root, 530, 460));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.showAndWait();
        });

        exitButton.setOnAction(e -> exitButton.getScene().getWindow().hide());
    }
}
