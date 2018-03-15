package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private String usernamePattern = "([A-Z]|[a-z]|_){1}([A-Z]{1}|[a-z]{1}|_){2,14}";
    private String passwordPattern = "([A-Z]|[a-z]|[0-9]){5,14}";
    private boolean isAppropriatePassword;
    private boolean isAppropriateUsername;

    private final boolean ADMIN = true;
    private final boolean USER = false;
    private final String adminTable = "administrators";
    private final String userTable = "clients";

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //disabling buttons and labels at start
        loginButton.setDisable(true);
        usernameLabel.setVisible(false);
        passwordLabel.setVisible(false);

        //controlling input of password
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!passwordTextField.getCharacters().toString().matches(passwordPattern)) {
                passwordTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                passwordLabel.setVisible(true);
                isAppropriatePassword = false;
            } else {
                passwordTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                passwordLabel.setVisible(false);
                isAppropriatePassword = true;
            }
            enableLogInButton();
        });

        //controlling input of username
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!usernameTextField.getCharacters().toString().matches(usernamePattern)) {
                usernameTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                usernameLabel.setVisible(true);
                isAppropriateUsername = false;
            } else {
                usernameTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                usernameLabel.setVisible(false);
                isAppropriateUsername = true;
            }
            enableLogInButton();
        });

        //trying to login
        loginButton.setOnAction(e -> {
            if(!logIn(ADMIN)) {
                if(!logIn(USER)) {
                   invalidLoginOrPassword();
                }
            }
        });

        signUpButton.setOnAction(e -> signUp());
    }

    private void enableLogInButton() {
        loginButton.setDisable(!(isAppropriatePassword && isAppropriateUsername));
    }

    private void invalidLoginOrPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Invalid login or password!");
        alert.showAndWait();
    }

    private void signUp() {
        //making login window clear
        usernameTextField.setText("");
        usernameLabel.setVisible(false);
        usernameTextField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");
        passwordTextField.setText("");
        passwordLabel.setVisible(false);
        passwordTextField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");

        Stage signUpStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/signUp.fxml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        signUpStage.setTitle("New user registration");
        signUpStage.setScene(new Scene(root, 600, 400));
        signUpStage.initModality(Modality.APPLICATION_MODAL);
        signUpStage.showAndWait();
    }

    private boolean logIn(boolean isAdmin) {
        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
            String query;
            if(isAdmin) {
                query = "SELECT password FROM " + adminTable + " WHERE login = ?";
            } else {
                query = "SELECT password FROM " + userTable + " WHERE username = ?";
            }
            PreparedStatement statement = db.prepareStatement(query);

            statement.setString(1, usernameTextField.getText());
            ResultSet rs = statement.executeQuery();

            //if there is no such user or password is incorrect
            if (!rs.next()) {
                return false;
            }
            if (!(rs.getString("password").equals(passwordTextField.getText()))) {
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //login
        Stage newStage = new Stage();
        Parent root = null;
        try {
            if(isAdmin) {
                RedactorController.setAdminLogin(usernameTextField.getText());
                root = FXMLLoader.load(getClass().getResource("/fxml/adminMenu.fxml"));
                newStage.setTitle("Administrator menu");
                newStage.setScene(new Scene(root, 300, 200));
            } else {
                UserWindowController.setUsername(usernameTextField.getText());
                root = FXMLLoader.load(getClass().getResource("/fxml/userWindow.fxml"));
                newStage.setTitle("Shop window");
                newStage.setScene(new Scene(root, 600, 450));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
        return true;
    }
}
