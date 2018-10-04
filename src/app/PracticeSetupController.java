package app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PracticeSetupController {
    @FXML
    private JFXButton _continueButton;
    @FXML
    private JFXComboBox<Playlist> _comboBox;
    private BorderPane _borderPane;

    private NamesModel _model;


    public void setModel(NamesModel model){
        _model = model;
        // populate combobox with playlist data from model
        _comboBox.setItems(_model.getPlaylists());
    }

    public void setPane(BorderPane pane){
      _borderPane = pane;
    }

    @FXML
    public void loadPracticeMode(){
        // only load practice mode if a playlist is selected
        Playlist playlist = _comboBox.getValue();
        if (playlist != null){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PracticeMode.fxml"));
                Parent root = (Parent) loader.load();
                PracticeModeController controller = loader.getController();
                controller.setPlaylist(playlist);
                _borderPane.setCenter(root);
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.out.println("no playlist selected");
        }

    }


}
