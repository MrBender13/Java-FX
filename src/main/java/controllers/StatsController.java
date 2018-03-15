package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.joda.time.DateTime;
import realization.Item;
import realization.Stats;

import java.net.URL;
import java.sql.*;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class StatsController implements Initializable {
    @FXML
    private TableView<Stats> table;

    @FXML
    private TableColumn<Stats, String> name;

    @FXML
    private TableColumn<Stats, Integer> amount;

    @FXML
    private TableColumn<Stats, Integer> cost;

    @FXML
    private TableColumn<Stats, String> owner;

    @FXML
    private TableColumn<Stats, String> time;

    @FXML
    private TableColumn<Stats, String> type;

    @FXML
    private TextField filterTextField;

    @FXML
    private Button filterButton;


    @FXML
    private Button countGainButton;

    @FXML
    private Button exitButton;

    private volatile ObservableList<Stats> data;
    private final String dataPattern = "[0-9]{2}[.]{1}[0-9]{2}[.][0-9]{4}";
    private boolean isAppropriateAmount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setting data to table
        getData();
        table.setItems(data);
        filterButton.setDisable(true);
        countGainButton.setDisable(true);

        //assuming cell factories to columns
        table.setEditable(true);
        name.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        owner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));

        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(filterTextField.getText().matches(dataPattern)) {
                filterTextField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                isAppropriateAmount = true;
                filterButton.setDisable(filterTextField.getText().equals(""));
                countGainButton.setDisable(filterTextField.getText().equals(""));
            } else {
                filterTextField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                isAppropriateAmount = false;
                filterButton.setDisable(true);
                countGainButton.setDisable(true);
            }
            if(filterTextField.getText().equals("")) {
                filterTextField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");

            }
        });

        //adding set on action
        filterButton.setOnAction(e -> filter());
        countGainButton.setOnAction(e -> countGain());
        exitButton.setOnAction(e -> exitButton.getScene().getWindow().hide());
    }

    private void getData() {
        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
            ObservableList<Stats> tmp = FXCollections.observableArrayList();

            String query = "SELECT * FROM statistics";
            PreparedStatement statement = db.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                tmp.add(new Stats(
                        rs.getString("itemName"),
                        rs.getInt("amount"),
                        rs.getInt("cost"),
                        new DateTime(rs.getString("time")),
                        rs.getString("owner"),
                        rs.getString("type")
                ));
            }
            data = tmp;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void countGain() {
        int gain = 0;
        for (Stats crt : data) {
            if(crt.getDate().equals(filterTextField.getText())) {
                gain += crt.getAmount() * crt.getCost();
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Gain by " + filterTextField.getText() + " is " + Integer.toString(gain));
        alert.showAndWait();
    }

    private void filter() {
        List<Stats> filtered = data.stream()
                .filter(e -> e.getDate().equals(filterTextField.getText()))
                .collect(Collectors.toList());
        table.setItems(FXCollections.observableArrayList(filtered));
    }
}
