package app.controllers;

import app.models.NamesModel;
import app.models.Playlist;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PracticeSetupController {
    @FXML
    private JFXButton _continueButton;
    @FXML
    private JFXComboBox<Playlist> _comboBox;
    private BorderPane _borderPane;

    private NamesModel _model;
    private FrameController _controller;


    public void setModel(NamesModel model){
        _model = model;
    }

    public void setUpComboBox(){
        // populate combobox with playlist data from model
        _comboBox.setItems(_model.getPlaylists());
        // focus on the first item of the combo list
        _comboBox.getSelectionModel().select(0);
    }

    public void setPane(BorderPane pane){
      _borderPane = pane;
    }

    public void setFrameController(FrameController controller){
        _controller = controller;
    }

    @FXML
    public void loadPracticeMode(){
        // only load practice mode if a playlist is selected
        Playlist playlist = _comboBox.getValue();
        // if playlist is empty show warning
        if (playlist.getNumberOfItems() == 0){
            System.out.println("Playlist cannot be emty");
        }else if (playlist != null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/PracticeMode.fxml"));
                Parent root = (Parent) loader.load();
                PracticeModeController controller = loader.getController();
                controller.setPlaylist(playlist);
                controller.setModel(_model);
                controller.setFrameController(_controller);
                controller.setPane(_borderPane);
                _borderPane.setCenter(root);
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.out.println("no playlist selected");
        }

    }


}
