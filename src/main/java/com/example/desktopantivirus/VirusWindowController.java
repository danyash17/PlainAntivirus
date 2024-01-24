package com.example.desktopantivirus;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class VirusWindowController implements Initializable {
    public ImageView imageviewVirus;
    public Text txtFile;
    public GridPane gridVirus;
    private Map<File, List<List<Byte>>> virusScanResults = new HashMap<>();
    private File file;
    private static final int MAX_TXT_LENGTH = 100;

    private static int rowId = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewVirus.setImage(new Image(getClass().getResourceAsStream("pics/virus.png")));
    }

    public void populateViruses(){
        for (Map.Entry<File, List<List<Byte>>> entry : virusScanResults.entrySet()) {
            if(entry.getKey().equals(file)) {
                showAllSequences(entry.getValue());
            }
        }
    }

    public void showAllSequences(List<List<Byte>> sequences) {
        for (List<Byte> byteList: sequences){
            showSequence(byteList);
        }
    }

    private void showSequence(List<Byte> sequence){
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
        miniByteSequenceController.getTxtByteSequence().setText(str);
        miniByteSequenceController.setCheckBox(null);
        if (str.length() / MAX_TXT_LENGTH > 0) {
            anchorPane.setPrefHeight(anchorPane.getPrefHeight() * str.length() / MAX_TXT_LENGTH);
        }
        gridVirus.add(anchorPane, 0, rowId++);
    }

    private String parseListByteToString(List<Byte> list) {
        return Arrays.deepToString(list.toArray());
    }

    public Text getTxtFile() {
        return txtFile;
    }

    public void setTxtFile(Text txtFile) {
        this.txtFile = txtFile;
    }

    public GridPane getGridVirus() {
        return gridVirus;
    }

    public void setGridVirus(GridPane gridVirus) {
        this.gridVirus = gridVirus;
    }

    public Map<File, List<List<Byte>>> getVirusScanResults() {
        return virusScanResults;
    }

    public void setVirusScanResults(Map<File, List<List<Byte>>> virusScanResults) {
        this.virusScanResults = virusScanResults;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
