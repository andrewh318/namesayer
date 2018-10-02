package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class FrameController {
    @FXML
    private BorderPane borderPane;


    public void initialize(){
        loadListen();
    }


    private void loadListen(){
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("Listen.fxml"));
            borderPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
