package app;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class FrameController {
    @FXML
    private BorderPane borderPane;
    private NamesModel _model;

    private boolean isListen;
    private boolean isPractice;

    // reference to the stage used throughout the application
    private Stage _stage;

    @FXML
    private JFXButton _practiceButton;
    @FXML
    private JFXButton _listenButton;

    public void initialize(){
        setUpModel();
        loadListen(_model);
        isListen = true;
        isPractice = false;

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
            System.out.println("Saving playlists");
            _stage.close();
        } else {
            System.out.println("Thanks for coming");
            _stage.close();
        }
    }
    @FXML
    private void onPracticeButtonClicked(){
        if (isPractice == false){
            loadPractice(_model);
            isListen = false;
            isPractice = true;
        }

    }

    @FXML
    private void onListenButtonClicked(){
        if (isListen == false){
            loadListen(_model);
            isListen = true;
            isPractice = false;
        }
    }


    private void loadListen(NamesModel model){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Listen.fxml"));
            Parent root = (Parent) loader.load();
            ListenController controller = (ListenController) loader.getController();
            controller.setModel(model);
            borderPane.setCenter(root);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadPractice(NamesModel model){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PracticeSetup.fxml"));
            Parent root = (Parent) loader.load();
            PracticeSetupController controller = (PracticeSetupController) loader.getController();
            controller.setModel(model);
            borderPane.setCenter(root);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
