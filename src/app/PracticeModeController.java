package app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PracticeModeController {
    @FXML
    private Label _playlistName;
    private Playlist _playlist;


    public void setPlaylist(Playlist playlist){
        _playlist = playlist;
        // set the text of the playlist
        _playlistName.setText("Playlist: " + playlist.getName());
    }
}
