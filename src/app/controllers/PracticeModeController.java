package app.controllers;

import app.models.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

/**
 * This class is the controller for the practice mode. it is responsible for handling user interaction when
 * listening to database recordings, recording attempts, flagging/comparing recordings.
 * @author: Andrew Hu and Vincent Tunnell
 */
public class PracticeModeController {
    @FXML private Label _playlistName;
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
    @FXML private FontAwesomeIconView _recordIcon;
    @FXML private JFXComboBox<Recording> _userRecordings;

    private BorderPane _pane;
    private PracticeMode _practiceMode;
    private NamesModel _model;
    private ShopModel _shopModel;
    private FrameController _frameController;

    public void initialize(){
        setUpToolTips();
    }

    public void setFrameController(FrameController controller){ _frameController = controller; }

    public void setModels(NamesModel model, ShopModel shopModel){
        _model = model;
        _shopModel = shopModel;
    }

    public void setPane(BorderPane pane){ _pane = pane; }


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

    /**
     * Updates the current name and label showed on screen
     * Also populates the combo box to reflect the current name
     */
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


    /**
     * Flags the current name and shows a confirmation alert.
     */
    @FXML
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

    /**
     * Normalises the recording then calls play in the practice mode class and starts the progress bar
     */
    @FXML
    private void onPlayButtonClicked(){
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                // trim the name on a new thread
                _practiceMode.getCurrentName().normaliseBestRecording();
                return null;
            }
        };
        new Thread(task).start();
        task.setOnSucceeded(e -> {
                _practiceMode.playCurrentName();
                // start progress indicator
                _frameController.startProgressBar(_practiceMode.getCurrentName().getRecordingLength());
            }
        );

    }

    /**
     * Navigate user back to the previous screen so they can change their playlist
     */
    @FXML
    private void onChangePlaylistButton(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PracticeSetup.fxml"));
            Parent root = (Parent) loader.load();

            PracticeSetupController controller = loader.getController();
            controller.setModels(_model, _shopModel);
            controller.setFrameController(_frameController);
            controller.setUpComboBox();
            controller.setPane(_pane);

            _pane.setCenter(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Records the audio, changes the behaviour and icon of the record button, handles progress bar
     */
    @FXML
    private void onRecordPressed(){
        // disable all buttons and cha
        setButtonsDisable(true);
        //Chance the icon on the record button to a stop icon
        _recordIcon.setGlyphName("STOP");

        //Start the recording task
        Task<Void> task = _practiceMode.recordName(_recordButton);

        //Start the progress bar
        startRecordProgress();

        task.setOnSucceeded(e -> {
            // update the view again to refresh combo box
            // this is done on the application thread
            setButtonsDisable(false);
            //Change the button back to a microphone icon
            _recordIcon.setGlyphName("MICROPHONE");
            //Change the action of the button back to start recording
            _recordButton.setOnAction(e2 -> {onRecordPressed();});
            // after recording completes update the user money
            _shopModel.setMoney(_shopModel.getMoney() + 100);
            //Reset the progress bar to 0
            _frameController.resetProgressBar();
            updateScreen();
        });
    }

    /**
     * Play the current user recording.
     */
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

            task.setOnSucceeded(e -> {_practiceMode.playRecording(recording);
                // start the progress bar
                _frameController.startProgressBar(recording.getRecordingLength());});

        } else {
            showAlert("Error: No recording selected", "Please select a recording to play");
        }
    }

    /**
     * Delete the current user recording and ask for confirmation
     */
    @FXML
    private void onUserRecordingDeleteButtonClicked(){
        Recording recording = _userRecordings.getSelectionModel().getSelectedItem();
        // delete the recording if its value is not null
        if (recording != null){
            if (isDeleteConfirmed()) {
                _practiceMode.deleteRecording(recording);
                updateScreen();
            }
        } else {
            showAlert("Error; No recording selected", "Please select a recording to delete");
        }
    }

    /**
     * Play the database recoding followed by the currently selected user recording
     */
    @FXML
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
            // after both the database and user name has been normalized, play them consecutively
            task.setOnSucceeded(e -> {_practiceMode.compareNames(recording);
                // need to get length of both the user recording and the database recording
                float databaseNameLength = _practiceMode.getCurrentName().getRecordingLength();
                float userRecordingLength = recording.getRecordingLength();
                float totalLength = databaseNameLength + userRecordingLength;
                // start the progress bar
                _frameController.startProgressBar(totalLength);});
        } else {
            showAlert("Error: No recordings selected", "Please select a recording to compare");
        }
    }

    private void setUpToolTips(){
        final Tooltip recordToolTip = new Tooltip();
        recordToolTip.setText("Click to start recording a maximum of " + NamesModel.MAX_RECORDING_SECS + " seconds");
        _recordButton.setTooltip(recordToolTip);
    }

    private void startRecordProgress(){
        // call the progress bar to run for the length of the max recording time
        _frameController.startProgressBar(NamesModel.MAX_RECORDING_SECS);
    }

    private void setButtonsDisable(Boolean disable){
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

    private boolean isDeleteConfirmed(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete your recording " + _userRecordings.getSelectionModel().getSelectedItem() +"?");
        Optional<ButtonType> action = alert.showAndWait();
        if (action.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }
}

