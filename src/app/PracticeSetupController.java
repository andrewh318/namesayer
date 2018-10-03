package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;

public class PracticeSetupController {
    @FXML
    private JFXButton _continueButton;
    @FXML
    private JFXComboBox<Playlist> _comboBox;

    private NamesModel _model;


    public void setModel(NamesModel model){
        _model = model;
        // populate combobox with playlist data from model
        _comboBox.setItems(_model.getPlaylists());

    }



}
