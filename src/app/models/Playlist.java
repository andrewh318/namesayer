package app.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class represents a single playlist instance that holds a collection of names or combined names.
 * @author: Andrew Hu and Vincent Tunnell
 */
public class Playlist {
    private ObservableList<Name> _playlist = FXCollections.observableArrayList();
    private String _playlistName;

    public Playlist(String name){ _playlistName = name; }

    public ObservableList<Name> getPlaylist(){ return _playlist; }

    public void shufflePlaylist(){ FXCollections.shuffle(_playlist); }

    @Override
    public String toString(){ return _playlistName; }

    public void addName(Name name){
        if (name != null){
            _playlist.add(name);
        }
    }

    public void deleteName(Name name){
        if (name != null){
            _playlist.remove(name);
        }
    }

    public int getNumberOfItems(){ return _playlist.size(); }

    public void setPlaylistName(String name){ _playlistName = name; }

    public String getName(){ return _playlistName; }
}
