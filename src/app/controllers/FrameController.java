package app.controllers;

import app.Main;
import app.models.BashCommand;
import app.models.NamesModel;
import app.models.ShopModel;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * Enum representing the different states of the application.
 */
enum Screen {
    LISTEN, PRACTICE, SHOP
}

public class FrameController {
    @FXML private BorderPane borderPane;

    private NamesModel _model;
    private ShopModel _shopModel;
    private ListenController _listenController;
    private Timeline _progressTimeline;

    // reference to the stage used throughout the application
    private Stage _stage;

    @FXML private JFXProgressBar _progressBar;
    @FXML private JFXSlider _volumeSlider;

    @FXML private Label _moneyLabel;

    private Parent shopScreen;

    private Screen _currentScreen;

    public void initialize(){
        setUpNamesModel();
        setUpMoneyModel();
        setUpVolumeSlider();
        initializeMoney();
        loadListen(_model);
        _currentScreen = Screen.LISTEN;
    }

    /**
     * Sets up the global application money state by binding the text label to the money field in Shop Model
     */
    private void initializeMoney(){
        SimpleIntegerProperty startingMoney = _shopModel.getMoneyBinding();
        _moneyLabel.textProperty().bind(startingMoney.asString());
    }

    private void setUpNamesModel(){
        _model = new NamesModel();
        _model.setUp();
    }

    private void setUpMoneyModel(){ _shopModel = new ShopModel(); }

    public void setStage(Stage stage){
        _stage = stage;
        setUpOnClose();
    }

    private void setUpOnClose(){
        _stage.setOnCloseRequest(e ->{
            e.consume();
            closeRequest();
        });
    }

    /**
     * On close request, application prompts user to save playlists created in the session
     * Application also saves the money user has accumulated and what themes they have unlocked
     */
    private void closeRequest(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to save all created playlists?");

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        ButtonType cancel = new ButtonType("Cancel");

        // Remove default ButtonTypes
        alert.getButtonTypes().clear();

        alert.getButtonTypes().addAll(yes, no, cancel);


        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == yes){
            _model.savePlaylists();
            _stage.close();
        } else if(action.get() == no){
            _stage.close();
        } else if (action.get() == cancel) {
            alert.close();
        }
        // always save money on close
        _shopModel.saveStateToFile();
        _model.deleteFolder(new File(NamesModel.TRIMMED_NORMALISED_DIRECTORY));
    }

    @FXML
    private void onPracticeButtonClicked(){
        if (!_currentScreen.equals(Screen.PRACTICE)){
            loadPractice(_model, borderPane);
            _currentScreen = Screen.PRACTICE;
        }
    }

    @FXML
    private void onListenButtonClicked(){
        if (!_currentScreen.equals(Screen.LISTEN)) {
            loadListen(_model);
            _currentScreen = Screen.LISTEN;
        }
    }

    @FXML
    private void onShopButtonClicked(){
        if (!_currentScreen.equals(Screen.SHOP)){
            loadShop();
            _currentScreen = Screen.SHOP;
        }
    }

    @FXML
    private void onTestMicButtonClicked(){ loadTestMic(); }

    @FXML
    /**
     * Opens a file chooser to allow user to select a playlist to upload (only text files acepteD)
     */
    private void onUploadButtonClicked(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Playlist File");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(_stage);


        if (f != null){
            // parse the txt file, and create an error message including all names that could not be read in
            List<String> invalidNames = _model.readPlaylist(f);
            if (!(invalidNames.isEmpty())) {
                String fileName = f.getName().substring(0, f.getName().lastIndexOf("."));

                String invalidNamesString = "";
                for (String invalidName : invalidNames) {
                    invalidNamesString = invalidNamesString + invalidName.replaceAll("%"," ") + "\n";
                }

                showAlert("Error: the following names from playlist: " + fileName +
                        " could not be found in the database", invalidNamesString);
            }
        }
    }

    /**
     * Loads the manage screen and injects the model into it.
     * @param model The manage screen requires the application model so user can view and edit playlists
     */
    private void loadListen(NamesModel model){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/Listen.fxml"));
            Parent root = loader.load();
            _listenController = loader.getController();
            _listenController.setModel(model, this);
            borderPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads the practice set up screen and injects the model and border pane into it
     * @param model Practice mode requires the application model so user can select what playlist they want to practice
     * @param borderPane Practice mode requires reference to the border pane so it can switch scenes to the practice mode
     */
    private void loadPractice(NamesModel model, BorderPane borderPane){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PracticeSetup.fxml"));
            Parent root = loader.load();
            PracticeSetupController controller = loader.getController();

            // initializes practice set up by passing it the required objects it needs
            controller.setModels(model, _shopModel);
            controller.setPane(borderPane);
            controller.setUpComboBox();
            controller.setFrameController(this);
            borderPane.setCenter(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Loads the shop screen. We only want to instantiate the shop once so if it has already been loaded before we
     * simply get the old reference to it.
     */
    private void loadShop(){
        if (shopScreen == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/Shop.fxml"));
                Parent root = (Parent) loader.load();
                ShopController controller = loader.getController();
                controller.setUp(_model, _shopModel);
                shopScreen = root;
                borderPane.setCenter(root);
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            borderPane.setCenter(shopScreen);
        }
    }

    /**
     * Loads the test mic screen and applies the appropriate css file to it
     */
    private void loadTestMic(){
        // load new playlist FXML
        Stage stage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/TestMic.fxml"));
            root = loader.load();

            // need to set CSS for this node as its a new stage
            Main.setTheme(Main.currentTheme, root);

            stage.setTitle("Test Mic");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method is called from the listen screen and practice screen when a user plays or makes a recording
     * @param duration Duration of the progress bar in seconds
     */
    public void startProgressBar(float duration){
        _progressTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(_progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(duration), e-> {

                }, new KeyValue(_progressBar.progressProperty(), 1))
        );
        _progressTimeline.setCycleCount(1);
        _progressTimeline.play();
    }

    /**
     * Calling this method stops the progress bar if it is running, and sets the progress to full.
     */
    public void resetProgressBar() {
        if (_progressTimeline != null) {
            _progressTimeline.stop();
        }
        _progressBar.setProgress(1d);
    }

    /**
     * Sets up volume slider for the application. Slider controls system volume.
     * Reference: https://www.youtube.com/watch?v=X9mEBGXX3dA
     */
    private void setUpVolumeSlider(){
        // command to get the current system volume
        String getVolume = "amixer get Master | awk '$0~/%/{print $4}' | tr -d '[]%'";
        BashCommand cmd = new BashCommand(getVolume);
        cmd.startProcess();

        Process volumeInit = cmd.getProcess();

        InputStream inputStream = volumeInit.getInputStream();
        BufferedReader reader = new BufferedReader((new InputStreamReader(inputStream)));
        String volumeLevel = null;
        try {
            volumeLevel = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double volume = Double.parseDouble(volumeLevel);
        // set the application volume on start up to reflect the system volume
        _volumeSlider.setValue(volume);

        // listener to change the system volume every time the application volume changse
        _volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double volume = newValue.doubleValue();
                String cmd = "amixer set 'Master' " + volume + "%";
                BashCommand setMaster = new BashCommand(cmd);
                setMaster.startProcess();
            }
        });
    }

    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
}
