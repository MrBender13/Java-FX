package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import realization.Item;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

//TODO add pictures on buttons
public class AddItemController implements Initializable{

    private String stringPattern = "([A-Z]|[a-z]){1}([A-Z]{1}|[a-z]{1}){2,9}";
    private String numberPattern = "[0-9]{1,7}";

    private static Item answer;

    private boolean isAppropriateName;
    private boolean isAppropriateCost;
    private boolean isAppropriateAmount;
    private boolean isAppropriateType;

    @FXML
    private Button createButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField costTextField;

    @FXML
    private TextField amountTextField;

    @FXML
    private TextField typeTextField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label costLabel;

    @FXML
    private Label amountLabel;

    @FXML
    private Label typeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        answer = null;
        createButton.setDisable(true);
        nameLabel.setVisible(false);
        costLabel.setVisible(false);
        amountLabel.setVisible(false);
        typeLabel.setVisible(false);

        exitButton.setOnAction(e -> exitButton.getScene().getWindow());

        //TODO fix css calls
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!nameTextField.getCharacters().toString().matches(stringPattern)) {
                nameTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                nameLabel.setVisible(true);
                isAppropriateName = false;
            } else {
                nameTextField.setStyle("-fx-text-box-border: #D3D3D3 ; -fx-focus-color: #D3D3D3");
                nameLabel.setVisible(false);
                isAppropriateName = true;
            }
            enableCreateButton();
        });

        typeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!typeTextField.getCharacters().toString().matches(stringPattern)) {
                typeTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                typeLabel.setVisible(true);
                isAppropriateType = false;
            } else {
                typeTextField.setStyle("-fx-text-box-border: #D3D3D3 ; -fx-focus-color: #D3D3D3");
                typeLabel.setVisible(false);
                isAppropriateType = true;
            }
            enableCreateButton();
        });

        costTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!costTextField.getCharacters().toString().matches(numberPattern)) {
                costTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                costLabel.setVisible(true);
                isAppropriateCost = false;
            } else {
                costTextField.setStyle("-fx-text-box-border: #D3D3D3 ; -fx-focus-color: #D3D3D3");
                costLabel.setVisible(false);
                isAppropriateCost = true;
            }
            enableCreateButton();
        });

        amountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!amountTextField.getCharacters().toString().matches(numberPattern)) {
                amountTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                amountLabel.setVisible(true);
                isAppropriateAmount = false;
            } else {
                amountTextField.setStyle("-fx-text-box-border: #D3D3D3 ; -fx-focus-color: #D3D3D3");
                amountLabel.setVisible(false);
                isAppropriateAmount = true;
            }
            enableCreateButton();
        });

        createButton.setOnAction(e -> create());

        exitButton.setOnAction(e -> exitButton.getScene().getWindow().hide());
    }



    private void enableCreateButton() {
        createButton.setDisable(!(isAppropriateAmount & isAppropriateCost & isAppropriateName & isAppropriateType));
    }

    private void create() {
        answer = new Item(
                nameTextField.getText(),
                typeTextField.getText(),
                Integer.parseInt(amountTextField.getText()),
                0,
                Integer.parseInt(costTextField.getText())
        );

        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
            String query = "INSERT INTO items VALUES(?, ?, ?, ?, ?)";
            PreparedStatement statement = db.prepareStatement(query);
            statement.setString(1,nameTextField.getText());
            statement.setInt(2,Integer.parseInt(amountTextField.getText()));
            statement.setInt(3,Integer.parseInt(costTextField.getText()));
            statement.setString(4,typeTextField.getText());
            statement.setInt(5,0);//at start bought is 0
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        exitButton.getScene().getWindow().hide();
    }

    public static Item getAnswer() {
        return answer;
    }
}
