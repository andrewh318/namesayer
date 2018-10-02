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
        ObservableList<Name> playlist = _model.getPlaylist();
        _allNamesList.setItems(nameList);
        _playlist.setItems(playlist);
    }

    private void bindOnFocusNames(){

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
        Name name = (Name)_allNamesList.getSelectionModel().getSelectedItem();
        return name;
    }



    @FXML
    private void onAddButtonClicked(){
        // get currently selected name
        Name name = handleNameListSelection();
        if (name == null){
            System.out.println("Name is null");
            return;
        } else {
            _model.addNameToPlaylist(name);
        }
    }

}


