package com.example.desktopantivirus;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

public class PrimalController implements Initializable {
    @FXML
    public GridPane grid;
    @FXML
    public Text txtDirectory;
    @FXML
    public ImageView imageviewFolder;
    @FXML
    public Button btnFindPe;
    @FXML
    public Button btnManage;
    @FXML
    public Button btnVirusScan;
    @FXML
    public GridPane gridByte;
    @FXML
    public Button btnClear;

    private static final int MAX_TXT_LENGTH = 68;
    private static final long PE_OFFSET = 0x3c;
    private static int rowId = 0;

    private List<List<Byte>> maliciousBytes = new LinkedList<>();
    private Map<List<Byte>, CheckBox> maliciousBytesMap = new HashMap<>();
    private Map<File, List<List<Byte>>> virusScanResults = new HashMap<>();
    private File directory;
    private List<File> files;
    private Map<File, CheckBox> filesMap = new HashMap<>();

    public void manage() {
        Stage window = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("manager.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteManagerController byteManagerController = fxmlLoader.getController();
        byteManagerController.setPrimalController(this);
        window.getIcons().add(new Image(getClass().getResourceAsStream("pics/antivirus.png")));
        window.setTitle("Byte manager");
        window.setScene(new Scene(root, 824, 554));
        window.show();
        window.setResizable(false);
    }

    private void chooseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Set directory");
        directory = chooser.showDialog(new Stage());
        if (directory != null) {
            imageviewFolder.setImage(new Image(getClass().getResourceAsStream("pics/opened-folder.png")));
            if (directory.getPath().length() > MAX_TXT_LENGTH) {
                txtDirectory.setText(directory.getPath().substring(0, MAX_TXT_LENGTH) + "..");
            } else {
                txtDirectory.setText(directory.getPath());
            }
        }
    }

    private void scanViruses() {
        for (Map.Entry<File, CheckBox> entry : filesMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                standaloneVirusScan(entry.getKey());
            }
        }
        List<Integer> indexes = new LinkedList<>();
        for (Map.Entry<File, List<List<Byte>>> entry : virusScanResults.entrySet()) {
            indexes.add(files.indexOf(entry.getKey()));
        }
        showDetectedViruses(indexes);
    }

    private void showDetectedViruses(List<Integer> indexes) {
        grid.getChildren().clear();
        for (int i = 0; i < files.size(); i++) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("template.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MiniFileController miniFileController = fxmlLoader.getController();
            miniFileController.setData(files.get(i), filesMap);
            if (indexes.contains(i)) {
                miniFileController.getPane().setStyle("-fx-background-color: RED;");
                miniFileController.getImageviewFile().setImage(new Image(getClass().getResourceAsStream("pics/virus.png")));
                miniFileController.getTxtFile().setOnMouseClicked(this::openVirusWindow);
            }
            grid.add(anchorPane, 0, i);
        }
    }

    private void openVirusWindow(Event event) {
        String text = ((Text)event.getSource()).getText();
        Stage window = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("virus.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        VirusWindowController virusWindowController = fxmlLoader.getController();
        virusWindowController.getTxtFile().setText(text);
        virusWindowController.setManagedBytesMap(maliciousBytesMap);
        virusWindowController.populateViruses();
        window.getIcons().add(new Image(getClass().getResourceAsStream("pics/antivirus.png")));
        window.setTitle("Virus Window");
        window.setScene(new Scene(root, 312, 464));
        window.show();
        window.setResizable(false);
    }

    private void standaloneVirusScan(File file) {
        FileInputStream fl = null;
        List<List<Byte>> detectedViruses = new LinkedList<>();
        try {
            fl = new FileInputStream(file);
            byte[] arr = new byte[(int) file.length()];
            fl.read(arr);
            Byte[] byteObject = ArrayUtils.toObject(arr);
            List<Byte> fileBytes = new ArrayList<>(List.of(byteObject));
            for (Map.Entry<List<Byte>, CheckBox> entry : maliciousBytesMap.entrySet()) {
                if (hasByteSequence(fileBytes, entry.getKey())) {
                    detectedViruses.add(entry.getKey());
                }
            }
            fl.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!detectedViruses.isEmpty()) {
            virusScanResults.put(file, detectedViruses);
        }
    }

    private boolean hasByteSequence(List<Byte> fileBytes, List<Byte> maliciousBytes) {
        if (!fileBytes.containsAll(maliciousBytes)) {
            return false;
        }
        int[] indexes = IntStream.range(0, fileBytes.size())
                .filter(i -> fileBytes.get(i).equals(maliciousBytes.get(0)))
                .toArray();
        for (int index : indexes) {
            boolean noMatch = false;
            int k = index;
            for (int j = 0; j < maliciousBytes.size(); j++, k++) {
                if (!fileBytes.get(k).equals(maliciousBytes.get(j))) {
                    noMatch = true;
                    break;
                }
            }
            if (!noMatch) {
                return true;
            }
        }
        return false;
    }

    private void scanPe(File dir) {
        files = new LinkedList<>();
        recursiveFileScan(dir);
        showFiles(files);
    }

    private void recursiveFileScan(File dir) {
        for (File file : dir.listFiles()) {
            if (!file.isDirectory() && isPe(file)) {
                files.add(file);
            }
            if (file.isDirectory()) {
                recursiveFileScan(file);
            }
        }
    }

    private void showFiles(List<File> files) {
        grid.getChildren().clear();
        for (int i = 0; i < files.size(); i++) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("template.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MiniFileController miniFileController = fxmlLoader.getController();
            miniFileController.setData(files.get(i), filesMap);
            grid.add(anchorPane, 0, i);
        }
    }

    private boolean isPe(File file) {
        byte[] bytes = new byte[2];
        boolean isPe = false;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.read(bytes);
            if (bytes[0] == 0x4d && bytes[1] == 0x5a) {
                //pe header address
                randomAccessFile.seek(PE_OFFSET);
                randomAccessFile.read(bytes);
                //pe
                randomAccessFile.seek(0x100 + bytes[0]);
                randomAccessFile.read(bytes);
                isPe = bytes[0] == 0x50 && bytes[1] == 0x45;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isPe;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewFolder.setImage(new Image(getClass().getResourceAsStream("pics/closed-folder.png")));
        imageviewFolder.setPickOnBounds(true);
        imageviewFolder.setOnMouseClicked(e -> chooseDirectory());
        btnFindPe.setOnAction(e -> scanPe(directory));
        btnManage.setOnAction(e -> manage());
        btnVirusScan.setOnAction(e -> scanViruses());
        btnClear.setOnAction(e -> clear());
    }

    private void clear() {
        grid.getChildren().clear();
        gridByte.getChildren().clear();
        txtDirectory.setText("Directory not set");
        files = null;
        directory = null;
        maliciousBytes = new LinkedList<>();
        maliciousBytesMap = new HashMap<>();
        virusScanResults = new HashMap<>();
    }

    public void populateMaliciousBytes(List<List<Byte>> maliciousBytes) {
        for (List<Byte> currentMaliciousBytes : maliciousBytes) {
            showSequence(currentMaliciousBytes);
        }
    }

    private void showSequence(List<Byte> maliciousBytes) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("sequence.fxml"));
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MiniByteSequenceController miniByteSequenceController = fxmlLoader.getController();
        String str = Arrays.deepToString(maliciousBytes.toArray());
        miniByteSequenceController.setData(str, maliciousBytes, maliciousBytesMap);
        if (str.length() / MAX_TXT_LENGTH > 0) {
            anchorPane.setPrefHeight(anchorPane.getPrefHeight() * str.length() / MAX_TXT_LENGTH);
        }
        gridByte.add(anchorPane, 0, rowId++);
    }
}
