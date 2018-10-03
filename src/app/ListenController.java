package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

public class ListenController {
    @FXML
    private JFXListView<Name> _allNamesList;
    @FXML
    private JFXListView<Name> _currentPlaylistList;
    @FXML
    private JFXListView<Playlist> _allPlaylists;

    @FXML
    private Label _currentPlaylistName;

    @FXML
    private JFXButton _addButton;
    private NamesModel _model;
    private Playlist _currentPlaylist;

    @FXML
    private JFXButton _newPlaylistButon;

    @FXML
    private JFXButton _deleteButton;

    @FXML
    private TextField _searchBar;


    public void initialize(){
        _model = new NamesModel();
        _model.setUp();
        setUpListBindings();
        setUpDoubleClickListeners();
        setUpEditableCells();
        setUpSearchBar();
    }

    private void setUpListBindings(){
        ObservableList<Name> nameList = _model.getDatabaseNames();
        ObservableList<Playlist> allPlaylists = _model.getPlaylists();
        _allNamesList.setItems(nameList);
        _allPlaylists.setItems(allPlaylists);
        // by default set current playlist to the first playlist in all playlists
        _currentPlaylist = allPlaylists.get(0);
        _currentPlaylistList.setItems(_currentPlaylist.getPlaylist());


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
            _currentPlaylistList.setItems(null);
            return;
        } else {
            // set the current playlist LIST
            _currentPlaylist = playlist;
            // bind selected playlist to current playlist
            _currentPlaylistList.setItems(_currentPlaylist.getPlaylist());
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
            Playlist playlist = _allPlaylists.getSelectionModel().getSelectedItem();
            if (playlist == null){
                System.out.println("playlist is null");
                return;
            } else {
                playlist.addName(name);
            }
        }
    }


    private void setUpEditableCells(){
        _allPlaylists.setEditable(true);

        _allPlaylists.setCellFactory(listView ->{
            TextFieldListCell<Playlist> cell = new TextFieldListCell<>();
            cell.setConverter(new StringConverter<Playlist>() {
                @Override
                public String toString(Playlist object) {
                    return object.toString();
                }

                @Override
                public Playlist fromString(String string) {
                    Playlist playlist = cell.getItem();
                    // if user delets the entire playlist name, it will default back to 'New Playlist'
                    if (string.length() == 0){
                        string = NamesModel.DEFAULT_PLAYLIST_NAME;
                        // change this later to a pop up
                        System.out.println("Playlist cannot be empty");
                    }
                    playlist.setPlaylistName(string);
                    return playlist;
                }
            });
            return cell;
        });


        _allPlaylists.setOnEditCommit(t ->{
            _allPlaylists.getItems().set(t.getIndex(), t.getNewValue());
        });

    }

    @FXML
    private void onNewPlaylistClicked(){
        Playlist playlist = new Playlist("");
        _model.addPlaylist(playlist);
        int index = _model.getPlaylists().indexOf(playlist);
        _allPlaylists.getSelectionModel().select(index);
        _allPlaylists.edit(index);

    }

    @FXML
    private void onDeleteButtonClicked(){
        Name name = _currentPlaylistList.getSelectionModel().getSelectedItem();
        Playlist playlist = _allPlaylists.getSelectionModel().getSelectedItem();
        if (name != null){
            playlist.deleteName(name);
        } else {
            System.out.println("cant delete: no name selected");
            return;
        }

    }

    /*
    @FXML
    private void writePlaylistToFile() {
        try {
            PrintWriter writer = new PrintWriter(_currentPlaylist.getName() + ".txt");

            for (Name name : _currentPlaylist.getPlaylist()) {
                writer.println(name);
            }

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */


    private void setUpSearchBar() {

        _searchBar.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                String text = _searchBar.getText();
                if (text.contains(" ")) {
                    text = text.substring(text.lastIndexOf(" ") + 1);
                }

                ObservableList<Name> updatedNames = FXCollections.observableArrayList();

                if (text.equals("")) {
                    _allNamesList.setItems(_model.getDatabaseNames());
                } else {
                    _allNamesList.setItems(updatedNames);
                }

                for (Name name : _model.getDatabaseNames()) {
                    if (name.getName().startsWith(text)) {
                        updatedNames.add(name);
                    }
                }
            }

        });
    }
}


