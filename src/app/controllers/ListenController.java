package app.controllers;

import app.Main;
import app.models.Name;
import app.models.NamesModel;
import app.models.Playlist;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Optional;

public class ListenController {
    @FXML private JFXListView<Name> _allNamesList;
    @FXML private JFXListView<Name> _currentPlaylistList;
    @FXML private JFXListView<Playlist> _allPlaylists;

    @FXML private Label _currentPlaylistName;

    private FilteredList<Name> _filteredNamesList;
    @FXML private JFXButton _addButton;
    @FXML private JFXButton _newPlaylistButon;
    @FXML private JFXButton _deleteButton;

    private static final int MAX_NAME_LENGTH = 50;

    @FXML private TextField _searchBar;

    private Playlist _currentPlaylist;

    private FrameController _frameController;

    private NamesModel _model;

    // injects the model into listen controller from frame
    // passes a reference of 'this' controller into the controller
    // sets up the required bindings
    public void setModel(NamesModel model, FrameController controller){
        _model = model;
        _frameController = controller;
        setUpListBindings();
        setUpDoubleClickListeners();
        setUpEditableCells();
        setUpSearchBar();
        setUpCurrentPlaylistCellFactory();
        bindSearchKeys();
    }

    private void setUpListBindings(){
        ObservableList<Name> nameList = _model.getDatabaseNames();
        ObservableList<Playlist> allPlaylists = _model.getPlaylists();
        _allPlaylists.setItems(allPlaylists);
        // by default set current playlist to the first playlist in all playlists
        _currentPlaylist = allPlaylists.get(0);
        updateCurrentPlaylist();
        // select first playlist
        _allPlaylists.getSelectionModel().select(0);
        _filteredNamesList = new FilteredList<>(nameList, e -> true);
        _allNamesList.setItems(_filteredNamesList);

    }



    private void setUpDoubleClickListeners(){
        // set up double click to add names into search box
        _allNamesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2){
                    String searchText = _searchBar.getText();

                    String lastNameofSearchText;
                    String beginningOfSearchText;

                    if (searchText.contains(" ")) {
                        lastNameofSearchText = searchText.substring(searchText.lastIndexOf(" ") + 1);
                        beginningOfSearchText = searchText.substring(0, searchText.lastIndexOf(" ") + 1);
                    } else {
                        lastNameofSearchText = searchText;
                        beginningOfSearchText = "";
                    }

                    String nameString = _allNamesList.getSelectionModel().getSelectedItem().getName();

                    if (nameString.toLowerCase().startsWith(lastNameofSearchText.toLowerCase())) {
                        _searchBar.setText(beginningOfSearchText + nameString + " ");
                    }
                    _searchBar.requestFocus();
                    _searchBar.end();
                }
            }
        });

        // set up double click to play a name from the playlist
        _currentPlaylistList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    // play audio recording
                    onPlayCLicked();
                }
            }
        });
    }

    @FXML
    private void handleAllPlaylistsSelect(){
        _currentPlaylist =  _allPlaylists.getSelectionModel().getSelectedItem();
        updateCurrentPlaylist();
    }

    private void updateCurrentPlaylist(){
        if (_currentPlaylist == null){
            _currentPlaylistList.setItems(null);
            _currentPlaylistName.setText("No Playlist Selected");
        } else {
            _currentPlaylistList.setItems(_currentPlaylist.getPlaylist());
            _currentPlaylistName.setText(_currentPlaylist.getName());
        }
    }

    @FXML
    private void onAddButtonClicked(){
        // get the number of characters in the search bar
        if (_searchBar.getText().length() >= MAX_NAME_LENGTH){
            showAlert("Error: Name too long", "Name must be 50 characters or below");
            return;
        // empty names are not allowed
        } else if (_searchBar.getText().length() == 0){
            showAlert("Error: Empty name", "Name can not be empty");
            return;
        }

        String searchText = _model.formatNamesString(_searchBar.getText());

        // get the name object from the searchbar text
        Name name = _model.findName(searchText);

        if (name == null){
            // if search bar is empty then display empty error message
            if (_searchBar.getText().isEmpty()){
                showAlert("Error: Empty Name", "Must enter a non empty valid name");
            } else {
                showAlert("Error: Invalid Name", _searchBar.getText() + " is not a valid name");
            }
            return;
        } else {
            Playlist playlist = _allPlaylists.getSelectionModel().getSelectedItem();
            if (playlist == null){
                showAlert("Error: No playlist select", "Please select a playlist you would like to add to");
                System.out.println("playlist is null");
                return;
            } else {
                playlist.addName(name);
                _searchBar.clear();
                _searchBar.requestFocus();
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
                    playlist.setPlaylistName(string);
                    return playlist;
                }
            });
            return cell;
        });


        _allPlaylists.setOnEditCommit(t ->{
            Playlist playlist = t.getNewValue();
            // if the newly edit playlist name is empty, set the name to 'Default Playlist'
            if (playlist.toString().length() == 0){
                playlist.setPlaylistName(NamesModel.DEFAULT_PLAYLIST_NAME);
            }
            _allPlaylists.getItems().set(t.getIndex(), playlist);
            // after edit finishes, update the current playlist name
            _currentPlaylistName.setText(playlist.toString());
        });
    }

    private void setUpCurrentPlaylistCellFactory(){

        _currentPlaylistList.setCellFactory(param -> new ListCell<Name>(){
            @Override
            protected void updateItem(Name names, boolean empty){
                super.updateItem(names, empty);
                // need this code so the list view knows the correct behaviour when the cell is empty
                // throws null pointer otherwise
                if (empty || names == null){
                    setText(null);
                } else {
                    setText(names.toString());
                }
            }
        });

    }

    @FXML
    private void onNewPlaylistClicked(){
        // load new playlist FXML
        Stage stage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/NewPlaylist.fxml"));
            root = (Parent) loader.load();

            // need to set CSS for this node as its a new stage
            Main.setTheme(Main.currentTheme, root);

            NewPlaylistController controller = loader.getController();
            controller.setController(this);
            stage.setTitle("New Playlist");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onDeletePlaylistButtonClicked(){
        // get the current playlist selected
        Playlist playlist = _allPlaylists.getSelectionModel().getSelectedItem();
        // check if number of items in playlist is 1, if yes, then do not allow delete (there must at least be one
        // playlist at all times.
        int numOfPlaylists = _model.getPlaylists().size();
        if (numOfPlaylists == 1){
            showAlert("Error: Cannot delete playlist", "You must have at least one playlist");
        } else if (playlist == null) {
            showAlert("Error: No playlist selected", "Please select a playlist to delete");
        } else {
            // show delete confirmation
            if (isDeleteConfirmed()){
                // delete playlist
                _model.deletePlaylist(playlist);
                // get new playlist selected and display as current playlist
                 _currentPlaylist = _allPlaylists.getSelectionModel().getSelectedItem();
                updateCurrentPlaylist();
            }

        }

    }

    @FXML
    public void onPlayCLicked() {
        Name name = _currentPlaylistList.getSelectionModel().getSelectedItem();
        // check if name is null
        if (name != null){
            _frameController.startProgressBar(name.getRecordingLength());
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    name.playRecording();
                    return null;
                }
            };
            new Thread(task).start();
        } else {
            showAlert("Error: No name selected", "Please select a name to play");
        }

    }

    // this method is called from the new playlist controller
    public void createNewPlaylist(String name){
        Playlist playlist = new Playlist(name);
        _model.addPlaylist(playlist);
        int index = _model.getPlaylists().indexOf(playlist);
        _allPlaylists.getSelectionModel().select(index);
        _currentPlaylist = _allPlaylists.getSelectionModel().getSelectedItem();

        // bind the current playlist list view to the newly created playlist
        updateCurrentPlaylist();
    }


    @FXML
    private void onDeleteButtonClicked(){
        Name name = _currentPlaylistList.getSelectionModel().getSelectedItem();
        Playlist playlist = _allPlaylists.getSelectionModel().getSelectedItem();
        if (name != null){
            if (isDeleteConfirmed()){
                playlist.deleteName(name);
            }
        } else {
            showAlert("Error: Can't delete", "Please select a name from the playlist");
            return;
        }
    }


    // show delete confirmation box, returns whether or not user clicks confirm
    private boolean isDeleteConfirmed(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete?");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }


    private void bindSearchKeys(){
        _searchBar.setOnKeyPressed(e -> {
            // bind autocomplete on enter
            if (e.getCode().equals(KeyCode.SPACE)){
                // check if list is empty
                int numOfListItems = _allNamesList.getItems().size();
                if (numOfListItems != 0){
                    String autoCompleteName = _allNamesList.getItems().get(0).getName();
                    // get the last index of space in the search bar
                    String searchBarText = _searchBar.getText();
                    int lastIndexOfSpace = searchBarText.lastIndexOf(' ');
                    int lastIndexOfHyphen = searchBarText.lastIndexOf('-');

                    // cut off index is the last occurrence of either a space or a hyphen
                    // everything after the cut off index will be removed and replaced for the auto completed name
                    int cutOffIndex;
                    // true if cut off symbol is a space, false if it is a hypehn
                    boolean isSpace;
                    if (lastIndexOfSpace > lastIndexOfHyphen){
                        cutOffIndex = lastIndexOfSpace;
                        isSpace = true;
                    } else if (lastIndexOfHyphen > lastIndexOfSpace){
                        cutOffIndex = lastIndexOfHyphen;
                        isSpace = false;
                    } else {
                        // otherwise there isn't a space or a hyphen in the current query
                        cutOffIndex = -1;
                        // when there is neither a space or hyphen, we want a space separator
                        isSpace = true;
                    }
                    // if theres no space, then lastIndexOf will return -1, in this case we want to just delete the
                    // whole search bar entry
                    if (cutOffIndex == -1){
                        _searchBar.setText(autoCompleteName);
                    } else {
                        // get the text in search bar up to the last space/hypehn ( we dont want the text after them as we are
                        // replacing it with the first name in the list)

                        searchBarText = searchBarText.substring(0, cutOffIndex);

                        // concatenate the auto completed name onto search bar text
                        if (isSpace){
                            searchBarText = searchBarText + " " + autoCompleteName;
                        } else {
                            searchBarText = searchBarText + "-" + autoCompleteName;

                        }
                        // update the search bar text
                        _searchBar.setText(searchBarText);
                    }
                    _searchBar.requestFocus();
                    _searchBar.end();
                }
            // bind auto submit on enter
            } else if (e.getCode().equals(KeyCode.ENTER)){
                onAddButtonClicked();
            }
        });
    }

    private void setUpSearchBar() {
        _searchBar.textProperty().addListener((observable, oldValue, newValue) ->{
            _filteredNamesList.setPredicate(element -> {
                String item = element.getName();
                String currentText = newValue;
                // check the index positions of space and hyphen characters
                int lastSpacePosition = currentText.lastIndexOf(' ');
                int lastHyphenPosition = currentText.lastIndexOf('-');
                if (lastSpacePosition > lastHyphenPosition){
                    // if space is the last separator
                    currentText = currentText.substring(lastSpacePosition+ 1);
                } else if (lastHyphenPosition > lastSpacePosition){
                    // if hyphen is the last separator
                    currentText = currentText.substring(lastHyphenPosition + 1);
                }

                if (item.length() >= currentText.length()){
                    if (item.toUpperCase().substring(0,currentText.length()).equals(currentText.toUpperCase())){
                        return true;
                    }
                }

                if (newValue == null || newValue.isEmpty()){
                    return true;
                }

                return false;
            });
            _allNamesList.setItems(_filteredNamesList);
        });
    }


    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
}


