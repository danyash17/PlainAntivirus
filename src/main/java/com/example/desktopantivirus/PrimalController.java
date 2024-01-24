package com.example.desktopantivirus;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

public class PrimalController implements Initializable {
    @FXML
    public GridPane gridFile;
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
    @FXML
    public RadioButton radioCustom;
    @FXML
    public RadioButton radioAllFiles;
    @FXML
    public RadioButton radioAllBytes;
    @FXML
    public ScrollPane gridByteScrollpane;
    @FXML
    public ScrollPane gridFileScrollpane;
    @FXML
    public Button btnErase;

    private static final int MAX_TXT_LENGTH = 68;
    private static final long PE_OFFSET = 0x3c;
    private static int rowId = 0;

    private List<List<Byte>> maliciousBytes = new LinkedList<>();
    private Map<List<Byte>, CheckBox> maliciousBytesViewMap = new HashMap<>();
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
        gridFile.getChildren().clear();
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
                miniFileController.getPane().setOnMouseClicked(null);
            }
            gridFile.add(anchorPane, 0, i);
        }
    }

    private void openVirusWindow(Event event) {
        String text = ((Text) event.getSource()).getText();
        File file = null;
        for (File fileIter : files) {
            if (fileIter.getName().equals(text)) {
                file = fileIter;
                break;
            }
        }
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
        virusWindowController.setVirusScanResults(virusScanResults);
        virusWindowController.setFile(file);
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
            for (Map.Entry<List<Byte>, CheckBox> entry : maliciousBytesViewMap.entrySet()) {
                if (hasByteSequence(arr, entry.getKey())) {
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

    private boolean hasByteSequence(byte[] fileBytes, List<Byte> maliciousBytes) {
        int[] indexes = IntStream.range(0, fileBytes.length)
                .filter(i -> fileBytes[i] == maliciousBytes.get(0))
                .toArray();
        for (int index : indexes) {
            boolean noMatch = false;
            int k = index;
            for (int j = 0; j < maliciousBytes.size(); j++, k++) {
                if (!(fileBytes[k] == maliciousBytes.get(j))) {
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
        gridFile.getChildren().clear();
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
            gridFile.add(anchorPane, 0, i);
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
        btnErase.setOnAction(e -> eraseScan());
        ToggleGroup toggleGroup = new ToggleGroup();
        radioCustom.setToggleGroup(toggleGroup);
        radioAllFiles.setToggleGroup(toggleGroup);
        radioAllBytes.setToggleGroup(toggleGroup);
        radioCustom.setSelected(true);
        radioAllFiles.setOnAction(e -> selectAllFiles());
        radioAllBytes.setOnAction(e -> selectAllBytes());
        increaseScrollPaneSpeed(gridByteScrollpane);
        increaseScrollPaneSpeed(gridFileScrollpane);
    }

    private void eraseScan() {
        gridFile.getChildren().clear();
        showFiles(files);
    }

    private void selectAllFiles() {
        for (Map.Entry<File, CheckBox> entry : filesMap.entrySet()) {
            entry.getValue().setSelected(true);
        }
    }

    private void selectAllBytes() {
        for (Map.Entry<List<Byte>, CheckBox> entry : maliciousBytesViewMap.entrySet()) {
            entry.getValue().setSelected(true);
        }
    }

    private void clear() {
        gridFile.getChildren().clear();
        gridByte.getChildren().clear();
        txtDirectory.setText("Directory not set");
        imageviewFolder.setImage(new Image(getClass().getResourceAsStream("pics/closed-folder.png")));
        files = null;
        directory = null;
        maliciousBytes = new LinkedList<>();
        maliciousBytesViewMap = new HashMap<>();
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
        miniByteSequenceController.setData(str, maliciousBytes, maliciousBytesViewMap);
        if (str.length() / MAX_TXT_LENGTH > 0) {
            anchorPane.setPrefHeight(anchorPane.getPrefHeight() * str.length() / MAX_TXT_LENGTH);
        }
        gridByte.add(anchorPane, 0, rowId++);
    }

    private void increaseScrollPaneSpeed(final ScrollPane customScrollPane) {

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double vpHeight = bounds.getHeight();
        double contentHeight = customScrollPane.getContent().getBoundsInLocal().getHeight();

        double ratio = (vpHeight / contentHeight);

        System.out.println(ratio);

        final double[] MAX_VERTICAL = new double[1];
        if (ratio > 0.9) {
            MAX_VERTICAL[0] = 1;
        } else if (ratio > 0.7) {
            MAX_VERTICAL[0] = 2;
        } else {
            MAX_VERTICAL[0] = 10;
        }

        final double SCROLL_SPEED = ratio;
        customScrollPane.setVmax(MAX_VERTICAL[0]);
        final double[] i = {0};
        customScrollPane.addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() != 0) {
                    boolean isScrollDown = event.getDeltaY() < 0;
                    event.consume();
                    double newPos = i[0];
                    if (isScrollDown) {
                        newPos += SCROLL_SPEED;
                    } else {
                        newPos -= SCROLL_SPEED;
                    }
                    newPos = newPos < 0 ? 0 : newPos;
                    newPos = Math.min(newPos, MAX_VERTICAL[0]);
                    i[0] = newPos;
                    customScrollPane.setVvalue(newPos);

                }
            }
        });
    }
}
