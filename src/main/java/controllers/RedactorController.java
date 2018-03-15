package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import realization.ItemData;
import realization.Item;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;



//TODO filter of items
//TODO db!!!!!!!
public class RedactorController implements Initializable {

    private static final Logger log = Logger.getLogger(RedactorController.class);

    @FXML
    private TableColumn<Item, String> name;

    @FXML
    private TableColumn<Item,Integer> cost;

    @FXML
    private TableColumn<Item, Integer> amount;

    @FXML
    private TableColumn<Item, String> type;

    @FXML
    private TableColumn<Item, Integer> bought;

    @FXML
    private TableView<Item> table;

    @FXML
    private Button addButton;

    @FXML
    private Button exitButton;

    private volatile ObservableList<Item> data;

    @FXML
    private Button reportButton;

    @FXML
    private Button increaseButton;

    @FXML
    private Button decreaseButton;

    @FXML
    private TextField amountField;

    @FXML
    private Button deleteButton;

    private static String adminLogin;
    private boolean isCheckedItem;
    private boolean isAppropriateAmount;
    private final String amountPattern = "[1-9]{1}[0-9]{0,6}";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("redactor window was opened");
        //disabling some buttons at start
        decreaseButton.setDisable(true);
        increaseButton.setDisable(true);
        deleteButton.setDisable(true);

        //additing data to table
        getData();
        table.setItems(data);
        table.setEditable(true);

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        bought.setCellValueFactory(new PropertyValueFactory<>("bought"));
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        //setting flag about element checked
        table.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Item>() {
            @Override
            public void onChanged(Change<? extends Item> c) {
                isCheckedItem = !data.isEmpty();
                deleteButton.setDisable(data.isEmpty());
                increaseButton.setDisable(!isAppropriateAmount);
                decreaseButton.setDisable(!isAppropriateAmount);
            }
        });

        //checking amount input and activating add button and del button
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(amountField.getText().matches(amountPattern)) {
                amountField.setStyle("-fx-text-box-border: green ; -fx-focus-color: green");
                isAppropriateAmount = true;
                increaseButton.setDisable(!isCheckedItem);
                decreaseButton.setDisable(!isCheckedItem);
            } else {
                amountField.setStyle("-fx-text-box-border: red ; -fx-focus-color: red");
                isAppropriateAmount = false;
                increaseButton.setDisable(true);
                decreaseButton.setDisable(true);
            }
        });

        //set on actions
        addButton.setOnAction(e -> registerNewItem());
        increaseButton.setOnAction(e -> addToStore());
        decreaseButton.setOnAction(e -> getFomStore());
        deleteButton.setOnAction(e -> delItem());
        exitButton.setOnAction(e -> exitButton.getScene().getWindow().hide());

        //creating report on store state
        reportButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("/home/mrbender/IdeaProjects/Pharmacy/src/main/java/reports");
            dialog.setTitle("Path choosing");
            dialog.setHeaderText("");
            dialog.setContentText("Please enter file path:");

            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        createReport(result.get());
                    }
                }).start();
            }
        });
        //laba();
    }

    private void delItem() {
        Item crtItem = table.getSelectionModel().getSelectedItem();

        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
              //updating store
            String queryToDel = "DELETE FROM items WHERE name = ?";
            PreparedStatement delStatement = db.prepareStatement(queryToDel);
            delStatement.setString(1, crtItem.getName());
            delStatement.executeUpdate();

            //delete from crt observable list
            data.remove(crtItem);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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

            data = tmp;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addToStore() {
        Item crtItem = table.getSelectionModel().getSelectedItem();

        try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
            //updating store
            String queryForAdding = "UPDATE items SET amount = ? WHERE name = ?";
            PreparedStatement addStatement = db.prepareStatement(queryForAdding);

            crtItem.setAmount(crtItem.getAmount() + Integer.parseInt(amountField.getText()));
            addStatement.setInt(1, crtItem.getAmount());
            addStatement.setString(2,crtItem.getName());
            addStatement.executeUpdate();

            //adding new entry to stats
            String queryForStatistics = "INSERT INTO statistics VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement statementForStats = db.prepareStatement(queryForStatistics);

            statementForStats.setString(1,crtItem.getName());
            statementForStats.setInt(2,crtItem.getBought());
            statementForStats.setInt(3,crtItem.getCost());
            statementForStats.setString(4,new DateTime().toString());
            statementForStats.setString(5,"add");
            statementForStats.setString(6,adminLogin);
            statementForStats.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        amountField.setText("");
        amountField.setStyle("-fx-text-box-border: white; -fx-focus-color: white");
    }

    private void getFomStore() {
        int toDel = Integer.parseInt(amountField.getText());
        Item crtItem = table.getSelectionModel().getSelectedItem();

        if(toDel <= crtItem.getAmount()) {
            try (Connection db = DriverManager.getConnection("jdbc:sqlite:/home/mrbender/IdeaProjects/Pharmacy/src/main/java/dataBase/pharmacyBase")) {
                //updating store
                String queryForAdding = "UPDATE items SET amount = ? WHERE name = ?";
                PreparedStatement addStatement = db.prepareStatement(queryForAdding);

                crtItem.setAmount(crtItem.getAmount() - Integer.parseInt(amountField.getText()));
                addStatement.setInt(1, crtItem.getAmount());
                addStatement.setString(2, crtItem.getName());
                addStatement.executeUpdate();

                //adding new entry to stats
                String queryForStatistics = "INSERT INTO statistics VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement statementForStats = db.prepareStatement(queryForStatistics);

                statementForStats.setString(1, crtItem.getName());
                statementForStats.setInt(2, crtItem.getBought());
                statementForStats.setInt(3, crtItem.getCost());
                statementForStats.setString(4, new DateTime().toString());
                statementForStats.setString(5, "get");
                statementForStats.setString(6, adminLogin);
                statementForStats.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Not enough items to get");
            alert.showAndWait();
        }
        amountField.setText("");
        amountField.setStyle("-fx-text-box-border: white; -fx-focus-color: white");
    }

    private void registerNewItem() {
        Stage addItemStage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/addItem.fxml"));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        addItemStage.setTitle("New item registration");
        addItemStage.setScene(new Scene(root, 600, 400));
        addItemStage.initModality(Modality.APPLICATION_MODAL);
        addItemStage.showAndWait();

        Item newItem = AddItemController.getAnswer();
        if(newItem != null) {
            data.add(newItem);
        }
    }

    public static void setAdminLogin(String adminLogin) {
        RedactorController.adminLogin = adminLogin;
    }

    private void createReport(String fileName) {
//        synchronized (mutex) {
            log.info("createReport method entered");
            ArrayList<ItemData> tmp = new ArrayList<>();
            for (Item crt : data) {
                tmp.add(new ItemData(crt));
            }

            try {
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(tmp);
                JasperReport report = JasperCompileManager.compileReport("/home/mrbender/IdeaProjects/Pharmacy/src/main/java/reports/myReport.jrxml");
                JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), dataSource);
                JasperExportManager.exportReportToPdfFile(print, fileName + "/pharmacyReport.pdf");
                JasperExportManager.exportReportToXmlFile(print, fileName + "/pharmacyReport.xml", false);
                JasperExportManager.exportReportToHtmlFile(print, fileName + "/pharmacyReport.hml");

            } catch (JRException e) {
                log.info("Error " + e.toString());
                e.printStackTrace();
            }
//        }
    }

//    private Object mutex = new Object();
//    private ObservableList<Item> data1;

//    private void laba() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getItemsFromFile("/home/mrbender/IdeaProjects/Pharmacy/src/main/java/files/redactorInput.txt");
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                createReport("/home/mrbender/IdeaProjects/Pharmacy/src/main/java/reports");
//            }
//        }).start();
//    }

//    private void getItemsFromFile(String fileName) {
//        synchronized (mutex) {
//            try (Scanner in = new Scanner(new FileInputStream(fileName))) {
//                ObservableList<Item> tmp = FXCollections.observableArrayList();
//
//                while (in.hasNextLine()) {
//                    String name = in.next();
//                    Integer amount = in.nextInt();
//                    String type = in.next();
//                    Integer cost = in.nextInt();
//                    Integer bought = in.nextInt();
//
//                    tmp.add(new Item(name, type, amount, bought, cost));
//                }
//                data1 = tmp;
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
}


