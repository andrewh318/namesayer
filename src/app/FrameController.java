package app;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class FrameController {
    @FXML
    private BorderPane borderPane;
    private NamesModel _model;

    private boolean isListen;
    private boolean isPractice;

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
