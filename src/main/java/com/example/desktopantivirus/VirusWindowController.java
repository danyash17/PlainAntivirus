package com.example.desktopantivirus;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class VirusWindowController implements Initializable {
    public ImageView imageviewVirus;
    public Text txtFile;
    public GridPane gridVirus;
    private Map<List<Byte>, CheckBox> managedBytesMap = new HashMap<>();
    private static final int MAX_TXT_LENGTH = 100;

    private static int rowId = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewVirus.setImage(new Image(getClass().getResourceAsStream("pics/virus.png")));
    }

    public void populateViruses(){
        for (Map.Entry<List<Byte>, CheckBox> entry : managedBytesMap.entrySet()) {
            showSequence(entry.getKey());
        }
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

    public Map<List<Byte>, CheckBox> getManagedBytesMap() {
        return managedBytesMap;
    }

    public void setManagedBytesMap(Map<List<Byte>, CheckBox> managedBytesMap) {
        this.managedBytesMap = managedBytesMap;
    }
}
