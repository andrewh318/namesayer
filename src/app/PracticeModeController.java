package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PracticeModeController {
    @FXML
    private Label _playlistName;
    private Playlist _playlist;
    // keeps track of current name in playlist
    private int _position = 0;
    private BorderPane _pane;
    private Name _currentName;

    @FXML
    private Label _nameLabel;
    @FXML
    private Label _recordingLabel;
    @FXML
    private JFXButton _nextButton;
    @FXML
    private JFXButton _previousButton;

    @FXML
    private JFXButton _recordButton;

    private NamesModel _model;

    @FXML
    private JFXComboBox<Recording> _userRecordings;

    private FrameController _frameController;

    public void setPlaylist(Playlist playlist){
        // Set the playlist
        _playlist = playlist;
        // set the text of the playlist
        _playlistName.setText("Playlist: " + playlist.getName());
        updateScreen();
    }

    public void setFrameController(FrameController controller){
        _frameController = controller;
    }

    public void setModel(NamesModel model){
        _model = model;
    }

    public void setPane(BorderPane pane){
        _pane = pane;
    }

    @FXML
    private void onNextButtonClicked(){
        _position = Math.floorMod(_position + 1, _playlist.getPlaylist().size());
        updateScreen();
    }

    @FXML
    private void onPreviousButtonClicked(){
        _position = Math.floorMod(_position - 1, _playlist.getPlaylist().size());
        updateScreen();
    }

    // Updates the current name and label showed up screen
    // Should also populate the database
    private void updateScreen(){
        _currentName = _playlist.getPlaylist().get(_position);
        _nameLabel.setText(_playlist.getPlaylistItemAt(_position));
        _userRecordings.setItems(_currentName.getUserRecordings());
        // display the first user recording on the top of the combo list (if it exists)
        _userRecordings.getSelectionModel().select(0);
        // display the current recording that is selected for name
        _recordingLabel.setText(_currentName.getBestRecording().toString());

    }



    @FXML
    // flag the current recording playing
    private void onFlagButtonClicked(){
        _currentName.flagRecording();
        updateScreen();
    }

    @FXML
    private void onPlayButtonClicked(){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                System.out.println(_currentName.getRecordingLength());
                _currentName.playRecording();
                return null;
            }
        };
        new Thread(task).start();
        // start progress indicator
        _frameController.startProgressBar(_currentName.getRecordingLength());

    }
    @FXML
    private void onChangePlaylistButton(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PracticeSetup.fxml"));
            Parent root = (Parent) loader.load();

            PracticeSetupController controller = loader.getController();
            controller.setModel(_model);
            controller.setUpComboBox();
            controller.setPane(_pane);

            _pane.setCenter(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordPressed() {

        Task<Void> task = new Task<Void>() {
            public Void call() {
                Recording recording = _currentName.createRecordingObject();
                _currentName.record(recording);
                _model.normaliseAndTrimAudioFile(new File(recording.getPath()));
                _currentName.addUserRecording(recording);

                return null;
            }
        };
        new Thread(task).start();
        task.setOnSucceeded(e -> {
            // update the view again to refresh combo box
            // this is done on the application thread
            updateScreen();
        });
        startRecordProgress();



    }

    @FXML
    private void setOnUserRecordingPlayButtonClicked(){
        // get the current recording that the user has selected
        Recording recording = _userRecordings.getSelectionModel().getSelectedItem();
        // check if the item selected is valid
        if (recording != null){
            // play the recording
            Task<Void> task = new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    recording.playRecording();
                    return null;
                }
            };
            new Thread(task).start();
            // start the progress bar
            _frameController.startProgressBar(recording.getRecordingLength());

        } else {
            System.out.println("no name selected");
        }
    }

    @FXML
    private void onUserRecordingDeleteButtonClicked(){
        // get the recording that the user has selected
        Recording recording = _userRecordings.getSelectionModel().getSelectedItem();
        // delete the recording if its value is not null
        if (recording != null){
            _currentName.removeUserRecording(recording);
            updateScreen();
        } else {
            System.out.println("no recording selected");
        }
    }

    private void startRecordProgress() {
        // call the progress bar with a 5 second timer
        _frameController.startProgressBar(5);
    }



}

