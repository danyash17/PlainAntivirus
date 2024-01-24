package com.example.desktopantivirus;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


public class MiniByteSequenceController implements Initializable {
    @FXML
    public Text txtByteSequence;
    @FXML
    public ImageView imageviewByte;
    @FXML
    public CheckBox checkBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewByte.setImage(new Image(getClass().getResourceAsStream("pics/parenthesis.png")));
    }

    public void setData(String byteSequence, List<Byte> sequence, Map<List<Byte>, CheckBox> managedBytesMap){
        txtByteSequence.setText(byteSequence);
        managedBytesMap.put(sequence, checkBox);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Text getTxtByteSequence() {
        return txtByteSequence;
    }

    public void setTxtByteSequence(Text txtByteSequence) {
        this.txtByteSequence = txtByteSequence;
    }
}

