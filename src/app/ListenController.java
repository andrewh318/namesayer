package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class ListenController {
    @FXML
    private JFXListView _allNamesList;
    @FXML
    private JFXListView _playlist;
    @FXML
    private JFXListView _recordings;

    @FXML
    private JFXButton _addButton;
    private NamesModel _model;



    public void initialize(){
        _model = new NamesModel();
        _model.setUp();
        setUpListBindings();
    }

    private void setUpListBindings(){
        ObservableList<Name> nameList = _model.getDatabaseNames();
        ObservableList<Name> playlist = _model.getPlaylist();
        _allNamesList.setItems(nameList);
        _playlist.setItems(playlist);
    }

    private void bindOnFocusNames(){

    }

    private Name handleRecordingListSelection(){
        Name name = (Name) _recordings.getSelectionModel().getSelectedItem();
        return name;

    }


    private Name handleNameListSelection(){
        // need to cast because JFXlistviews return Objects
        Name name = (Name)_allNamesList.getSelectionModel().getSelectedItem();
        return name;
    }


    @FXML
    private void onDatabaseNameClicked(){
        // get currently selected name
        Name name = handleNameListSelection();
        // show error if null
        if (name == null){
            System.out.println("Name is null");
            return;
        } else {
            // bind recordings to list view
            _recordings.setItems(name.getDatabaseRecordings());
        }
    }

    @FXML
    private void onAddToPlaylistClicked(){
        // get currently selected name
        Name name = handleRecordingListSelection();
        if (name == null){
            System.out.println("Name is null");
            return;
        } else {
            // add name to playlist
            _model.addNameToPlaylist(name);
        }
    }

}
