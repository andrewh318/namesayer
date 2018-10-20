package app.controllers;

import app.models.NamesModel;
import app.models.Playlist;
import app.models.ShopModel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PracticeSetupController {
    @FXML private JFXButton _continueButton;
    @FXML private JFXComboBox<Playlist> _comboBox;
    private BorderPane _borderPane;

    private NamesModel _model;
    private ShopModel _shopModel;
    private FrameController _controller;


    public void setModels(NamesModel model, ShopModel shopModel){
        _model = model;
        _shopModel = shopModel;
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
            showAlert("Error: Playlist cannot be empty", "Please go back to the manage screen to \nadd a name");

        }else if (playlist != null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PracticeMode.fxml"));
                Parent root = (Parent) loader.load();
                PracticeModeController controller = loader.getController();
                controller.setPlaylist(playlist);
                controller.setModels(_model, _shopModel);
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

    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }


}
