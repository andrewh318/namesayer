package app.controllers;

import app.models.MoneySingleton;
import app.models.NamesModel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

enum Screen {
    LISTEN, PRACTICE, SHOP
}

public class FrameController {
    @FXML
    private BorderPane borderPane;

    private NamesModel _model;

    private boolean isListen;
    private boolean isPractice;
    private ListenController _listenController;

    @FXML private JFXButton _playButton;

    // reference to the stage used throughout the application
    private Stage _stage;

    @FXML private JFXProgressBar _progressBar;
    @FXML private JFXSlider _volumeSlider;

    @FXML private Label _currentNameLabel;
    @FXML private Label _moneyLabel;

    private Parent shopScreen;

    private Screen _currentScreen;

    public void initialize(){
        setUpModel();
        initializeMoney();
        loadListen(_model);
        _currentScreen = Screen.LISTEN;

    }

    private void initializeMoney(){
        int startingMoney = MoneySingleton.getInstance().getMoney();
        _moneyLabel.setText(String.valueOf(startingMoney));

    }


    private void setUpModel(){
        _model = new NamesModel();
        _model.setUp();
    }

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

    private void closeRequest(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to save all created playlists?");


        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK){
            _model.savePlaylists();
            _stage.close();
        } else {
            _stage.close();
        }
        // always save money on close
        _model.saveMoney();
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
    private void onUploadButtonClicked(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Playlist File");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(_stage);


        if (f != null){
            // parse the txt file
            List<String> invalidNames = _model.readPlaylist(f);
            if (invalidNames.size() > 0) {
                String fileName = f.getName().substring(0, f.getName().lastIndexOf("."));

                String invalidNamesString = "";
                for (String invalidName : invalidNames) {
                    invalidNamesString = invalidNamesString + invalidName + "\n";
                }

                showAlert("Error: the following names from playlist: " + fileName + "could not be found in the database", invalidNamesString);
            }
        }
    }


    private void loadListen(NamesModel model){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/Listen.fxml"));
            Parent root = (Parent) loader.load();
            _listenController = (ListenController) loader.getController();
            _listenController.setModel(model, this);
            borderPane.setCenter(root);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // pass a reference of the border pane into the practice set up controller so it can change the scene to
    // practice mode
    private void loadPractice(NamesModel model, BorderPane borderPane){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/PracticeSetup.fxml"));
            Parent root = (Parent) loader.load();
            PracticeSetupController controller = (PracticeSetupController) loader.getController();
            controller.setModel(model);
            controller.setPane(borderPane);
            controller.setUpComboBox();
            controller.setFrameController(this);
            borderPane.setCenter(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    // we only want to instantiate the shop screen once, if its already been laoded before just get the old reference
    private void loadShop(){
        if (shopScreen == null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/views/Shop.fxml"));
                Parent root = (Parent) loader.load();
                shopScreen = root;
                borderPane.setCenter(root);
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            borderPane.setCenter(shopScreen);
        }


    }

    // duration is duration in seconds
    public void startProgressBar(float duration){

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < duration*1000; i++){
                    updateProgress(i+1, duration*1000);
                    Thread.sleep(1);
                }
                return null;
            }
        };
        _progressBar.progressProperty().unbind();
        _progressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();

        //task.setOnSucceeded();
    }

    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
    public void setVolume(float volume){
    }

    public double getVolume() {
        return _volumeSlider.getValue();
    }



}
