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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.script.Bindings;
import java.io.IOException;
import java.util.List;

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

    public void initialize(){

    }

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
    }

    private void setUpListBindings(){
        ObservableList<Name> nameList = _model.getDatabaseNames();
        ObservableList<Playlist> allPlaylists = _model.getPlaylists();
        _allNamesList.setItems(nameList);
        _allPlaylists.setItems(allPlaylists);
        // by default set current playlist to the first playlist in all playlists
        _currentPlaylistList.setItems(allPlaylists.get(0).getPlaylist());
        // change text for playlist header
        _currentPlaylistName.setText("Playlist: " + allPlaylists.get(0).toString());
        // select first playlist
        _allPlaylists.getSelectionModel().select(0);
        // set up binding for GUI

    }



    private void setUpDoubleClickListeners(){
        _allNamesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2){

                    String nameString = _allNamesList.getSelectionModel().getSelectedItem().getName();
                    autoCompleteText(nameString);

                    _searchBar.requestFocus();
                    _searchBar.end();

                }
            }
        });
    }

    private void autoCompleteText(String nameString) {
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

        if (nameString.toLowerCase().startsWith(lastNameofSearchText.toLowerCase())) {
            _searchBar.setText(beginningOfSearchText + nameString + " ");
        }
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
            // bind selected playlist to current playlist
            _currentPlaylistList.setItems(playlist.getPlaylist());
            // update name of current playlist
            _currentPlaylistName.setText("Playlist: " + playlist.toString());

        }
    }



    @FXML
    private void onAddButtonClicked(){
        // get currently selected name
        List<Name> namesList = _model.generateListOfNames(_searchBar.getText());

        if (namesList == null){
            System.out.println(_searchBar.getText() + "Is not a valid name");
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
            _currentPlaylistName.setText("Playlist: " + playlist.toString());
        });
    }

    private void setUpCurrentPlaylistCellFactory(){
        _currentPlaylistList.setCellFactory(param -> new ListCell<List<Name>>(){
            @Override
            protected void updateItem(List<Name> names, boolean empty){
                super.updateItem(names, empty);
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

        // bind the current playlist list view to the newly created playlist
        _currentPlaylistList.setItems(playlist.getPlaylist());
        // temporarily set playlist name to empty
        _currentPlaylistName.setText("Playlist: " + name);
    }


    @FXML
    private void onDeleteButtonClicked(){
        List<Name> name = _currentPlaylistList.getSelectionModel().getSelectedItem();
        Playlist playlist = _allPlaylists.getSelectionModel().getSelectedItem();
        if (name != null){
            playlist.deleteName(name);
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

                String lastName;
                if (text.contains(" ")) {
                    lastName = text.substring(text.lastIndexOf(" ") + 1);
                } else {
                    lastName = text;
                }

                ObservableList<Name> updatedNames = FXCollections.observableArrayList();

                if (lastName.equals("")) {
                    _allNamesList.setItems(_model.getDatabaseNames());
                } else {
                    _allNamesList.setItems(updatedNames);
                }

                for (Name name : _model.getDatabaseNames()) {
                    if (name.getName().toLowerCase().startsWith(lastName.toLowerCase())) {
                        updatedNames.add(name);
                    }
                }
            }

        });
    }
}


