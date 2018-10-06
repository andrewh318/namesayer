package app;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;

public class PracticeModeController {
    @FXML
    private Label _playlistName;
    private Playlist _playlist;
    // keeps track of current name in playlist
    private int _position = 0;
    private Name _currentName;

    @FXML
    private Label _nameLabel;
    @FXML
    private JFXButton _nextButton;
    @FXML
    private JFXButton _previousButton;


    public void setPlaylist(Playlist playlist){
        // Set the playlist
        _playlist = playlist;
        // set the text of the playlist
        _playlistName.setText("Playlist: " + playlist.getName());
        updateScreen();
    }

    @FXML
    private void onNextButtonClicked(){
        _position = Math.floorMod(_position + 1, _playlist.getPlaylist().size());
        updateScreen();
    }

    @FXML
    private void onPreviousButtonClicked(){
        _position = Math.floorMod(_position - 1, _playlist.getPlaylist().size());
        updateScreen();
    }

    // Updates the current name and label showed up screen
    // Should also populate the database
    private void updateScreen(){
        _currentName = _playlist.getPlaylist().get(_position);
        _nameLabel.setText(_playlist.getPlaylistItemAt(_position));
    }



}

