package app.controllers;

import app.models.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PracticeModeController {
    @FXML private Label _playlistName;
    private BorderPane _pane;
    private PracticeMode _practiceMode;

    @FXML private Label _nameLabel;
    @FXML private Label _recordingLabel;
    @FXML private JFXButton _nextButton;
    @FXML private JFXButton _previousButton;

    @FXML private JFXButton _recordButton;
    @FXML private JFXButton _playButton;
    @FXML private JFXButton _compareButton;
    @FXML private JFXButton _playUserRecordingButton;
    @FXML private JFXButton _deleteUserRecordingButton;
    @FXML private JFXButton _flagButton;

    private NamesModel _model;

    @FXML
    private JFXComboBox<Recording> _userRecordings;

    private FrameController _frameController;

    public void setFrameController(FrameController controller){
        _frameController = controller;
    }

    public void setModel(NamesModel model){
        _model = model;
    }

    public void setPane(BorderPane pane){
        _pane = pane;
    }


    public void setPlaylist(Playlist playlist){
        _practiceMode = new PracticeMode(playlist);
        _playlistName.setText("Playlist: " + playlist.getName());
        updateScreen();
    }


    @FXML
    private void onNextButtonClicked(){
        _practiceMode.nextName();
        updateScreen();
    }

    @FXML
    private void onPreviousButtonClicked(){
        _practiceMode.previousName();
        updateScreen();
    }

    // Updates the current name and label showed up screen
    // Should also populate the database
    private void updateScreen(){
        int position = _practiceMode.getPosition();
        int playlistItems = _practiceMode.getNumPlaylistItems();
        Name currentName =_practiceMode.getCurrentName();

        // if the number of items is 1 then both buttons should be disabled
        if (playlistItems == 1){
          _nextButton.setDisable(true);
          _previousButton.setDisable(true);
        } else if (position == playlistItems-1){
            _previousButton.setDisable(false);
            _nextButton.setDisable(true);
        } else if (position == 0){
            _previousButton.setDisable(true);
            _nextButton.setDisable(false);
        } else {
            _nextButton.setDisable(false);
            _previousButton.setDisable(false);
        }

        _nameLabel.setText(currentName.getCleanName());

        _userRecordings.setItems(currentName.getUserRecordings());
        // display the first user recording on the top of the combo list (if it exists)
        _userRecordings.getSelectionModel().select(0);

        // display the current recording that is selected for name
        Recording recording = currentName.getBestRecording();

        // if recording is null then the current name is a combined name (no database recording exists)
        if (recording != null){
            _recordingLabel.setText(currentName.getBestRecording().toString());
        } else {
            _recordingLabel.setText("Custom Name");
        }

    }



    @FXML
    // flag the current recording playing
    private void onFlagButtonClicked(){
        Name currentName = _practiceMode.getCurrentName();
        Recording currentRecording = currentName.getBestRecording();
        if (!currentName.flagRecording()){
            showAlert("Error: Cannot flag this item", "Custom names cannot be flagged");
        } else {
            // show confirmation of flag
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Alert");
            alert.setHeaderText("Successful flag");
            alert.setContentText("The recording " + currentRecording.toString() + "\nhas been flagged");

            alert.showAndWait();
            updateScreen();
        }
    }

    @FXML
    private void onPlayButtonClicked(){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                _practiceMode.getCurrentName().getBestRecording().normaliseAndTrimAudioFile();
                return null;
            }
        };
        new Thread(task).start();
        task.setOnSucceeded(e -> {
                _practiceMode.playCurrentName(_frameController.getVolume());
                // start progress indicator
                _frameController.startProgressBar(_practiceMode.getCurrentName().getRecordingLength());
            }
        );

    }
    @FXML
    private void onChangePlaylistButton(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PracticeSetup.fxml"));
            Parent root = (Parent) loader.load();

            PracticeSetupController controller = loader.getController();
            controller.setModel(_model);
            controller.setFrameController(_frameController);
            controller.setUpComboBox();
            controller.setPane(_pane);

            _pane.setCenter(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void onRecordPressed() {
        // disable all buttons
        setButtonsDisable(true);

        Task<Void> task = _practiceMode.recordName(_model);
        task.setOnSucceeded(e -> {
            // update the view again to refresh combo box
            // this is done on the application thread
            setButtonsDisable(false);
            // after recording completes update the user money
            _model.setMoney(_model.getMoney() + 100);
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
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    recording.normaliseAndTrimAudioFile();
                    return null;
                }
            };
            new Thread(task).start();

            task.setOnSucceeded(event -> {
                        _practiceMode.playRecording(recording, _frameController.getVolume());
                        // start the progress bar
                        _frameController.startProgressBar(recording.getRecordingLength());
                    }
            );
        } else {
            showAlert("Error: No recording selected", "Please select a recording to play");
        }
    }

    @FXML
    private void onUserRecordingDeleteButtonClicked(){
        Recording recording = _userRecordings.getSelectionModel().getSelectedItem();
        // delete the recording if its value is not null
        if (recording != null){
            _practiceMode.deleteRecording(recording);
            updateScreen();
        } else {
            showAlert("Error; No recording selected", "Please select a recording to delete");
        }
    }

    @FXML
    // play the database recording followed by the currently selected user recording
    private void onCompareButtonClicked(){
        Recording recording = _userRecordings.getSelectionModel().getSelectedItem();
        // a recording must be selected for comparison
        if (recording != null){
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    recording.normaliseAndTrimAudioFile();
                    _practiceMode.getCurrentName().normaliseBestRecording();
                    return null;
                }
            };
            new Thread(task).start();

            task.setOnSucceeded(e -> {_practiceMode.compareNames(recording, _frameController.getVolume());});

        } else {
            showAlert("Error: No recordings selected", "Please select a recording to compare");
        }
    }

    private void startRecordProgress() {
        // call the progress bar with a 5 second timer
        setButtonsDisable(true);
        _frameController.startProgressBar(5);


    }

    private void setButtonsDisable(Boolean disable) {
        _recordButton.setDisable(disable);
        _nextButton.setDisable(disable);
        _previousButton.setDisable(disable);
        _playButton.setDisable(disable);
        _compareButton.setDisable(disable);
        _deleteUserRecordingButton.setDisable(disable);
        _playUserRecordingButton.setDisable(disable);
        _flagButton.setDisable(disable);
    }

    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }




}

