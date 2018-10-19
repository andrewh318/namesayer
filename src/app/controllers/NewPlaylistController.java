package app.controllers;

import app.models.NamesModel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class NewPlaylistController {
    private ListenController _controller;

    @FXML private JFXButton _submitButton;
    @FXML private JFXTextField _textField;
    // need to inject listen controller into this class

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
    public void setController(ListenController controller){
        _controller = controller;
    }

    @FXML
    private void onSubmitButtonClicked(){
        String name;
        if (_textField.getText().length() == 0){
            name = NamesModel.DEFAULT_PLAYLIST_NAME;
        } else {
            name = _textField.getText();
        }
        // create the playlist in the listen screen
        _controller.createNewPlaylist(name);
        // close stage
       Stage stage = (Stage) _submitButton.getScene().getWindow();
       stage.close();
    }
}
