package app;

import javafx.collections.ObservableList;

import java.util.List;

public class Playbar {
    private ObservableList<List<Name>> _currentPlaylist;

    public void setPlaylist(ObservableList<List<Name>> playlist) {
        _currentPlaylist = playlist;
    }

}
