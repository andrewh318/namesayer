package app;

import com.jfoenix.controls.JFXButton;
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
    private JFXButton _nextButton;
    @FXML
    private JFXButton _previousButton;

    @FXML
    private JFXButton _recordButton;

    private NamesModel _model;

    @FXML
    private ProgressIndicator _recordIndicator;


    public void setPlaylist(Playlist playlist){
        // Set the playlist
        _playlist = playlist;
        // set the text of the playlist
        _playlistName.setText("Playlist: " + playlist.getName());
        updateScreen();
    }

    public void setModel(NamesModel model){
        _model = model;
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
    }

    public void setPane(BorderPane pane){
        _pane = pane;
    }

    @FXML
    // flag the current recording playing
    private void onFlagButtonClicked(){
        _currentName.flagRecording();
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
                String nonTrimmedPath = recording.getPath();
                nonTrimmedPath = nonTrimmedPath.substring(nonTrimmedPath.indexOf("/"));
                _model.normaliseAndTrimAudioFile(new File(nonTrimmedPath));

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        _currentName.addUserRecording(recording);
                    }
                });
                return null;
            }
        };
        new Thread(task).start();
        //startRecordProgress();
    }


    private void startRecordProgress() {
        Timer timer = new Timer();
        _recordIndicator.setProgress(0d);
        _recordButton.setDisable(true);

        // Personal: Threading explanation
        // a JavaFX application runs on the Application thread which handles all the UI elements
        // the Java Timer runs on its own thread
        // due to this, calling Thread.sleep inside the Timer method won't affect the Application thread
        // runLater() will run the code inside it on the Application thread
        // generally used when you are updating UI components
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (_recordIndicator.getProgress() <= 1){
                    Platform.runLater(() -> {
                        _recordIndicator.setProgress(_recordIndicator.getProgress() + 0.01);
                    });
                } else {
                    timer.cancel();
                    _recordButton.setDisable(false);
                }
            }
        }, 0, 50);

    }



}

