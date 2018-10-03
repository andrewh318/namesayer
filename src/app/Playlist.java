package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
    private ObservableList<Name> _playlist = FXCollections.observableArrayList();
    private String _playlistName;
    private int _position;

    public Playlist(String name){
        _playlistName = name;
    }
    public ObservableList<Name> getPlaylist(){
        return _playlist;
    }

    public void shufflePlaylist(){
        FXCollections.shuffle(_playlist);
    }

    @Override
    public String toString(){
        return _playlistName;
    }

    public void addName(Name name){
        if (name != null){
            _playlist.add(name);
        }
    }
}
