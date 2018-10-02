package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;

public class ListenController {
    @FXML
    private JFXListView<Name> _allNamesList;
    @FXML
    private JFXListView<Name> _currentPlaylist;
    @FXML
    private JFXListView<Playlist> _allPlaylists;

    @FXML
    private JFXButton _addButton;
    private NamesModel _model;





    public void initialize(){
        _model = new NamesModel();
        _model.setUp();
        setUpListBindings();
        setUpDoubleClickListeners();
    }

    private void setUpListBindings(){
        ObservableList<Name> nameList = _model.getDatabaseNames();
        ObservableList<Playlist> allPlaylists = _model.getPlaylists();
        _allNamesList.setItems(nameList);
        _allPlaylists.setItems(allPlaylists);
    }



    private void setUpDoubleClickListeners(){
        _allNamesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2){
                    onAddButtonClicked();
                }
            }
        });
    }



    private Name handleNameListSelection(){
        // need to cast because JFXlistviews return Objects
        Name name = _allNamesList.getSelectionModel().getSelectedItem();
        return name;
    }

    @FXML
    private void handleAllPlaylistsSelect(){
        Playlist playlist =  _allPlaylists.getSelectionModel().getSelectedItem();
        if (playlist == null){
            System.out.println("Playlist is null");
            return;
        } else {
            // bind selected playlist to current playlist
            _currentPlaylist.setItems(playlist.getPlaylist());
        }
    }



    @FXML
    private void onAddButtonClicked(){
        // get currently selected name
        Name name = handleNameListSelection();
        if (name == null){
            System.out.println("Name is null");
            return;
        } else {
//            _model.addNameToPlaylist(name);
        }
    }


    private void setUpEditableCells(){


    }
}


