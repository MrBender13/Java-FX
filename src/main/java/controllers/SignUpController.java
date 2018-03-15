package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.log4j.Logger;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    private static final Logger log = Logger.getLogger(SignUpController.class);

    //TODO correct name and surname hints
    private String initialsPattern = "[A-Z]{1}([A-Z]{1}|[a-z]{1}){2,9}";
    private String locationPattern = "([A-Z]|[a-z]){1}([A-Z]{1}|[a-z]{1}){2,9}";
    private String usernamePattern = "([A-Z]|[a-z]|_){1}([A-Z]{1}|[a-z]{1}|_){2,14}";
    private String passwordPattern = "([A-Z]|[a-z]|[0-9]){5,14}";

    private boolean isAppropriateName;
    private boolean isAppropriateLocation;
    private boolean isAppropriateSurname;
    private boolean isAppropriatePassword;
    private boolean isAppropriateUsername;

    @FXML
    TextField nameTextField;

    @FXML
    TextField surnameTextField;

    @FXML
    TextField locationTextField;

    @FXML
    TextField usernameTextField;

    @FXML
    TextField passwordTextField;

    @FXML
    Label nameLabel;

    @FXML
    Label surnameLabel;

    @FXML
    Label locationLabel;

    @FXML
    Label usernameLabel;

    @FXML
    Label passwordLabel;

    @FXML
    Label genderLabel;

    @FXML
    Button loginButton;

    @FXML
    Button signUpButton;

    @FXML
    RadioButton maleRadioButton;

    @FXML
    RadioButton femaleRadioButton;

    @FXML
    ToggleGroup gender;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signUpButton.setDisable(true);
        nameLabel.setVisible(false);
        surnameLabel.setVisible(false);
        locationLabel.setVisible(false);
        usernameLabel.setVisible(false);
        passwordLabel.setVisible(false);

        loginButton.setOnAction(e -> loginButton.getScene().getWindow().hide());

        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!nameTextField.getCharacters().toString().matches(initialsPattern)) {
                nameTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                nameLabel.setVisible(true);
                isAppropriateName = false;
            } else {
                nameTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                nameLabel.setVisible(false);
                isAppropriateName = true;
            }
            enableSignUpButton();
        });

        surnameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!surnameTextField.getCharacters().toString().matches(initialsPattern)) {
                surnameTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                surnameLabel.setVisible(true);
                isAppropriateSurname = false;
            } else {
                surnameTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                surnameLabel.setVisible(false);
                isAppropriateSurname = true;
            }
            enableSignUpButton();
        });

        surnameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!surnameTextField.getCharacters().toString().matches(initialsPattern)) {
                surnameTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                surnameLabel.setVisible(true);
                isAppropriateSurname = false;
            } else {
                surnameTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                surnameLabel.setVisible(false);
                isAppropriateSurname = true;
            }
            enableSignUpButton();
        });

        locationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!locationTextField.getCharacters().toString().matches(locationPattern)) {
                locationTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                locationLabel.setVisible(true);
                isAppropriateLocation = false;
            } else {
                locationTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                locationLabel.setVisible(false);
                isAppropriateLocation = true;
            }
            enableSignUpButton();
        });

        //TODO check on uniq login
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!usernameTextField.getCharacters().toString().matches(usernamePattern)) {
                usernameTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                usernameLabel.setVisible(true);
                isAppropriateUsername = false;
            } else {
                usernameTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                usernameLabel.setVisible(false);
                isAppropriateUsername = true;
            }
            enableSignUpButton();
        });

        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!passwordTextField.getCharacters().toString().matches(passwordPattern)) {
                passwordTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                passwordLabel.setVisible(true);
                isAppropriatePassword = false;
            } else {
                passwordTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                passwordLabel.setVisible(false);
                isAppropriatePassword = true;
            }
            enableSignUpButton();
        });


        System.out.println(System.getProperty("user.dir"));
        signUpButton.setOnAction(event -> {
            saveNewUser();
            signUpButton.getScene().getWindow().hide();
        });

    }

    private void enableSignUpButton() {
        signUpButton.setDisable(!(isAppropriateName && isAppropriateSurname && isAppropriateLocation
                                    && isAppropriateUsername && isAppropriatePassword
                                    && (maleRadioButton.isSelected() || femaleRadioButton.isSelected())));
    }

    private void saveNewUser() {
//        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
            String query = "INSERT INTO clients VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = db.prepareStatement(query);
            statement.setString(1,nameTextField.getText());
            statement.setString(2,surnameTextField.getText());
            statement.setString(3,locationTextField.getText());
            statement.setString(4,maleRadioButton.isSelected()
                                ? "man" : "woman");
            statement.setString(5,usernameTextField.getText());
            statement.setString(6,passwordTextField.getText());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
