package app.controllers;

import app.models.NamesModel;
import app.models.Playlist;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.List;

public class NewPlaylistController {
    private ListenController _controller;

    @FXML private JFXButton _submitButton;
    @FXML private JFXTextField _textField;

    private NamesModel _model;

    public void initialize(){
        bindEnterKey();
    }

    private void bindEnterKey(){
        _textField.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    onSubmitButtonClicked();
                }
            }
        });
    }
    // need to inject listen controller into this class, as well as the list of playlists
    public void setController(ListenController controller, NamesModel model){
        _controller = controller;
        _model = model;
    }

    @FXML
    private void onSubmitButtonClicked(){
        String name;
        if (_textField.getText().length() == 0){
            name = NamesModel.DEFAULT_PLAYLIST_NAME;
        } else {
            name = _textField.getText();
        }

        if (checkIfPlaylistNameExists(name)){
            // show an error saying playlist already exists
            showAlert("Error: Playlist aleady exists", "Please choose a new playlist name");
            return;
        } else {
            // create the playlist in the listen screen
            _controller.createNewPlaylist(name);
            // close stage
            Stage stage = (Stage) _submitButton.getScene().getWindow();
            stage.close();
        }



    }

    private boolean checkIfPlaylistNameExists(String playlistName){
        List<Playlist> playlists = _model.getPlaylists();

        boolean flag = false;
        for (Playlist playlist : playlists){
            if (playlist.getName().equals(playlistName)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
}
