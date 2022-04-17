package com.example.desktopantivirus;

import com.example.desktopantivirus.mapper.ByteSequenceMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;

import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByteManagerController implements Initializable {
    @FXML
    public Button btnAdd;
    @FXML
    public Button btnPull;
    @FXML
    public Button btnPush;
    @FXML
    public ImageView imageviewDatabase;
    @FXML
    public Text txtDatabase;
    @FXML
    public GridPane grid;
    @FXML
    public TextField byteTextField;
    @FXML
    public Button btnSubmit;
    @FXML
    public Button btnClear;

    private PrimalController primalController;

    private String dbName;
    private String tableName;
    private List<List<Byte>> managedBytes = new LinkedList<>();
    private Map<List<Byte>, CheckBox> managedBytesMap = new HashMap<>();
    private static final int MAX_TXT_LENGTH = 146;
    private Connection connection;

    private static int rowId = 0;

    private void establishDatabaseConnection() {
        Stage window = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("connector.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseConnectionWindowController databaseConnectionWindowController = fxmlLoader.getController();
        databaseConnectionWindowController.setByteManagerController(this);
        window.getIcons().add(new Image(getClass().getResourceAsStream("pics/antivirus.png")));
        window.setTitle("Database connection window");
        window.setScene(new Scene(root, 313, 383));
        window.show();
        window.setResizable(false);
    }

    private List<Byte> parseStringToByteSequence() {
        String[] strBytes = byteTextField.getText().trim().split("\s");
        List<Byte> bytes = new LinkedList<>();
        Pattern pattern = Pattern.compile("[0-9a-fA-F]+");
        for (int i = 0; i < strBytes.length; i++) {
            Matcher matcher = pattern.matcher(strBytes[i]);
            if (matcher.matches()) {
                bytes.add((byte) (Integer.parseInt(strBytes[i], 16) & 0xff));
            }
        }
        return bytes;
    }

    private void addSequence() {
        List<Byte> sequence = parseStringToByteSequence();
        managedBytes.add(sequence);
        showSequence(sequence);
    }

    public void showSequence(List<Byte> sequence) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("sequence.fxml"));
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MiniByteSequenceController miniByteSequenceController = fxmlLoader.getController();
        String str = parseListByteToString(sequence);
        miniByteSequenceController.setData(str, sequence, managedBytesMap);
        if (str.length() / MAX_TXT_LENGTH > 0) {
            anchorPane.setPrefHeight(anchorPane.getPrefHeight() * str.length() / MAX_TXT_LENGTH);
        }
        grid.add(anchorPane, 0, rowId++);
    }

    private String parseListByteToString(List<Byte> list) {
        return Arrays.deepToString(list.toArray());
    }

    public void populateSuccessfulConnection(Stage stage) {
        stage.close();
        imageviewDatabase.setImage(new Image(getClass().getResourceAsStream("pics/database.png")));
        txtDatabase.setText(dbName + "/" + tableName);
    }

    private void push() {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO " + tableName + " (active, sequence) VALUES (?, ?)");
            for (Map.Entry<List<Byte>, CheckBox> entry : managedBytesMap.entrySet()) {
                if (!entry.getValue().isSelected()) {
                    continue;
                }
                preparedStatement.setBoolean(1, true);
                preparedStatement.setString(2, parseListByteToString(entry.getKey()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        grid.getChildren().clear();
    }

    private void pull() {
        ByteSequenceMapper mapper = new ByteSequenceMapper();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT sequence FROM my_db.byte_sequence WHERE active=true");
            while (resultSet.next()) {
                managedBytes.add(mapper.map(resultSet.getString(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        grid.getChildren().clear();
        for (List<Byte> byteList : managedBytes) {
            showSequence(byteList);
        }
    }

    private void clear() {
        managedBytes.clear();
        grid.getChildren().clear();
        byteTextField.setText("");
    }

    private void submit() {
        List<List<Byte>> result = new LinkedList<>();
        for (Map.Entry<List<Byte>, CheckBox> entry : managedBytesMap.entrySet()) {
            if (!entry.getValue().isSelected()) {
                continue;
            }
            result.add(entry.getKey());
        }
        primalController.populateMaliciousBytes(result);
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewDatabase.setImage(new Image(getClass().getResourceAsStream("pics/no-database.png")));
        imageviewDatabase.setPickOnBounds(true);
        imageviewDatabase.setOnMouseClicked(e -> establishDatabaseConnection());
        btnAdd.setOnAction(e -> addSequence());
        btnClear.setOnAction(e -> clear());
        btnPull.setOnAction(e -> pull());
        btnPush.setOnAction(e -> push());
        btnSubmit.setOnAction(e -> submit());
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public List<List<Byte>> getManagedBytes() {
        return managedBytes;
    }

    public void setManagedBytes(List<List<Byte>> managedBytes) {
        this.managedBytes = managedBytes;
    }

    public Map<List<Byte>, CheckBox> getManagedBytesMap() {
        return managedBytesMap;
    }

    public void setManagedBytesMap(Map<List<Byte>, CheckBox> managedBytesMap) {
        this.managedBytesMap = managedBytesMap;
    }

    public PrimalController getPrimalController() {
        return primalController;
    }

    public void setPrimalController(PrimalController primalController) {
        this.primalController = primalController;
    }
}
