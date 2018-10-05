package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.script.Bindings;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ListenController {
    @FXML
    private JFXListView<Name> _allNamesList;
    @FXML
    private JFXListView<List<Name>> _currentPlaylistList;
    @FXML
    private JFXListView<Playlist> _allPlaylists;
    @FXML
    private Label _currentPlaylistName;


    @FXML
    private JFXButton _addButton;
    private NamesModel _model;

    @FXML
    private JFXButton _newPlaylistButon;
    @FXML
    private JFXButton _deleteButton;

    @FXML
    private TextField _searchBar;

    private Playlist _currentPlaylist;

    // injects the model into listen controller from frame
    // passes a reference of 'this' controller into the controller
    // sets up the required bindings
    public void setModel(NamesModel model){
        _model = model;
        setUpListBindings();
        setUpDoubleClickListeners();
        setUpEditableCells();
        setUpSearchBar();
        setUpCurrentPlaylistCellFactory();
        setUpEnterBinding();
    }

    private void setUpListBindings(){
        ObservableList<Name> nameList = _model.getDatabaseNames();
        ObservableList<Playlist> allPlaylists = _model.getPlaylists();
        _allNamesList.setItems(nameList);
        _allPlaylists.setItems(allPlaylists);
        // by default set current playlist to the first playlist in all playlists
        _currentPlaylist = allPlaylists.get(0);
        updateCurrentPlaylist();
        // select first playlist
        _allPlaylists.getSelectionModel().select(0);
    }

    // bind enter key to automatically add the name in search bar to the playlist
    private void setUpEnterBinding(){
            _searchBar.setOnKeyPressed(new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent ke)
                {
                    if (ke.getCode().equals(KeyCode.ENTER))
                    {
                        onAddButtonClicked();
                    }
                }
            });
    }


    private void setUpDoubleClickListeners(){
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
        // get currently selected name
        List<Name> namesList = _model.generateListOfNames(_searchBar.getText());

        if (namesList == null){
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
                playlist.addName(namesList);
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
        _currentPlaylistList.setCellFactory(param -> new ListCell<List<Name>>(){
            @Override
            protected void updateItem(List<Name> names, boolean empty){
                super.updateItem(names, empty);
                // need this code so the list view knows the correct behaviour when the cell is empty
                // throws null pointer otherwise
                if (empty || names == null){
                    setText(null);
                } else {
                    String string = "";
                    for (Name name:names){
                        string = string + name.getName() + " ";
                    }
                    setText(string);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewPlaylist.fxml"));
            root = (Parent) loader.load();
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
    public List<Name> onPlayCLicked() {
        List<Name> selectedName = _currentPlaylistList.getSelectionModel().getSelectedItem();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                try {
                    for (Name name : selectedName) {
                        name.playRecording();
                    }
                    return null;
                } finally {

                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();

        return selectedName;
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
        List<Name> name = _currentPlaylistList.getSelectionModel().getSelectedItem();
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

    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
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
                    if (name.getName().toLowerCase().startsWith(text.toLowerCase())) {
                        updatedNames.add(name);
                    }
                }
            }

        });
    }
}


