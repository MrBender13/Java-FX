package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import realization.Item;

import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserWindowController implements Initializable{

    @FXML
    private TableView<Item> table;

    @FXML
    private TableColumn<Item, String> name;

    @FXML
    private TableColumn<Item, Integer> cost;

    @FXML
    private TableColumn<Item, Integer> amount;

    @FXML
    private Button shopButton;

    @FXML
    private Button cartButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button buyButton;

    @FXML
    private TextField amountField;

    @FXML
    private Label infoLabel;

    private  ObservableList<Item> shopData;
    private  ObservableList<Item> cartData = FXCollections.observableArrayList();
    private static String username;

    private boolean isAppropriateAmount;
    private boolean isCheckedShopItem;
    private final String amountPattern = "[1-9]{1}[0-9]{0,6}";
    private boolean isCheckedCartItem;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //config showing buttons
        deleteButton.setVisible(false);
        buyButton.setVisible(false);
        buyButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(true);
        infoLabel.setVisible(false);

        //getting data from data base and setting it to table
        getData();
        table.setEditable(true);
        name.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
        amount.setCellValueFactory(new PropertyValueFactory<Item, Integer>("amount"));
        cost.setCellValueFactory(new PropertyValueFactory<Item,Integer>("cost"));
        table.setItems(shopData);


        cartButton.setOnAction(e -> switchToCart());
        shopButton.setOnAction(e -> switchToShop());

        //setting flag about element checked
        table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<>() {
            @Override
            public void onChanged(Change<? extends Item> c) {
                isCheckedShopItem = !shopData.isEmpty();
                isCheckedCartItem = !cartData.isEmpty();
                addButton.setDisable(!isAppropriateAmount);
                deleteButton.setDisable(!isAppropriateAmount);
            }
        });

        //checking amount input and activating add button and del button
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(amountField.getText().matches(amountPattern)) {
                amountField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                isAppropriateAmount = true;
                addButton.setDisable(!isCheckedShopItem);
                deleteButton.setDisable(!isCheckedCartItem);
            } else {
                amountField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                isAppropriateAmount = false;
                addButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        addButton.setOnAction(e -> addToCart());
        deleteButton.setOnAction(e -> delFromCart());

        buyButton.setOnAction(e -> {
            sellItems();
        });
    }

    public static void setUsername(String username) {
        UserWindowController.username = username;
    }

    private void getData() {
        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
            ObservableList<Item> tmp = FXCollections.observableArrayList();

            String query = "SELECT * FROM items";
            PreparedStatement statement = db.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                tmp.add(new Item(
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("amount"),
                        rs.getInt("bought"),
                        rs.getInt("cost")
                ));
            }

            //to show only items, that are available
            List<Item> filtered = tmp.stream()
                    .filter(p -> p.getAmount() > 0)
                    .collect(Collectors.toList());
            shopData = FXCollections.observableArrayList(filtered);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void delFromCart() {
        int toDel = Integer.parseInt(amountField.getText());
        Item crtItem = table.getSelectionModel().getSelectedItem();

        if(toDel <= crtItem.getAmount()) {
            crtItem.setAmount(crtItem.getAmount() - toDel);

            for (Item crt : shopData) {
                if(crt.getName().equals(crtItem.getName())) {
                    crt.setAmount(crt.getAmount() + toDel);
                    crt.setBought(crt.getBought() - toDel);

                    //return to default state
                    amountField.setText("");
                    addButton.setDisable(true);
                    amountField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");
                }
            }

            //delete item from cart if its amount is 0
            if(crtItem.getAmount() == 0) {
                cartData.remove(crtItem);
            }
            buyButton.setDisable(cartData.isEmpty());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Not enough items to return");
            alert.showAndWait();
        }
    }

    private void switchToShop() {
        table.setItems(shopData);
        deleteButton.setVisible(false);
        buyButton.setVisible(false);
        addButton.setVisible(true);
        amountField.setText("");
        //setting default style
        amountField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");
        isCheckedCartItem = false;
    }

    private void switchToCart() {
        table.setItems(cartData);
        deleteButton.setVisible(true);
        buyButton.setVisible(true);
        buyButton.setDisable(cartData.isEmpty());
        addButton.setVisible(false);
        amountField.setText("");
        //setting default style
        amountField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");
        isCheckedShopItem = false;
    }

    private void addToCart() {
        int toAdd = Integer.parseInt(amountField.getText());
        Item crtItem = table.getSelectionModel().getSelectedItem();

        if(toAdd <= crtItem.getAmount()) {
            crtItem.setAmount(crtItem.getAmount() - toAdd);
            crtItem.setBought(crtItem.getBought() + toAdd);

            for (Item crt : cartData) {
                if(crt.getName().equals(crtItem.getName())) {
                    crt.setAmount(crt.getAmount() + toAdd);

                    //return to default state
                    amountField.setText("");
                    addButton.setDisable(true);
                    amountField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");
                    return;
                }
            }

            cartData.add(new Item(
                    crtItem.getName(),
                    crtItem.getType(),
                    toAdd,
                    crtItem.getCost(),
                    crtItem.getCost()
            ));

            //return to default state
            amountField.setText("");
            addButton.setDisable(true);
            amountField.setStyle("-fx-text-box-border: white ; -fx-focus-color: white");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Not enough items at store");
            alert.showAndWait();
        }
    }

    private void sellItems() {
        //counting sum of order
        int sum = 0;
        for (Item crt : cartData) {
            sum += crt.getAmount() * crt.getCost();
        }

        //ask user if he is sure
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation of deal");
        alert.setHeaderText("Sum of your order is " + sum);
        alert.setContentText("Are you sure you want to buy?") ;

        //selling if necessary
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            DateTime buyTime = new DateTime();

            try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
                String queryForItems = "UPDATE items SET amount = ?, bought = ? WHERE name = ?";
                String queryForStatistics = "INSERT INTO statistics VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement statementForItems = db.prepareStatement(queryForItems);
                PreparedStatement statementForStats = db.prepareStatement(queryForStatistics);

                //setting bought items to db
                for (Item crt : shopData) {
                    statementForItems.setInt(1, crt.getAmount());
                    statementForItems.setInt(2, crt.getBought());
                    statementForItems.setString(3, crt.getName());
                    statementForItems.executeUpdate();
                }

                //setting statistics to db
                for (Item crt : cartData) {
                    statementForStats.setString(1,crt.getName());
                    statementForStats.setInt(2,crt.getAmount());
                    statementForStats.setInt(3,crt.getCost());
                    statementForStats.setString(4,buyTime.toString());
                    statementForStats.setString(5,"buy");
                    statementForStats.setString(6,username);
                    statementForStats.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            buyButton.getScene().getWindow().hide();
        }
    }
}

