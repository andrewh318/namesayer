package app.controllers;

import app.Main;
import app.models.NamesModel;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;

public class ShopController {
    public static final String UNLOCK = "UNLOCK";
    public static final String LOCK = "LOCK";

    @FXML private JFXButton purpleTheme;
    @FXML private JFXButton greenTheme;
    @FXML private JFXButton blueTheme;

    @FXML private JFXButton purpleUnlock;
    @FXML private JFXButton blueUnlock;

    @FXML private FontAwesomeIconView purpleIcon;
    @FXML private FontAwesomeIconView blueIcon;

    @FXML
    public void onBlueThemeClicked(){
        Main.setBlue();
    }

    @FXML
    public void onGreenThemeClicked(){
        Main.setGreen();
    }

    @FXML
    public void onPurpleThemeClicked(){
        Main.setPurple();
    }

    private NamesModel _model;


    public void setUp(NamesModel model){
        _model = model;
        initialButtonSetup();
    }


    // green button should be enabled while other two as disabled
    private void initialButtonSetup(){
        if (!_model.getPurpleUnlocked()){
            purpleTheme.setDisable(true);
        }
        if (!_model.getBlueUnlocked()){
            blueTheme.setDisable(true);
        }

    }

    @FXML
    private void onPurpleUnlockClicked(){
        if (checkMoneyAvailable(500)){
            purpleUnlock.setText("Owned");
            purpleUnlock.setDisable(true);
            purpleTheme.setDisable(false);
            purpleIcon.setGlyphName(UNLOCK);
        }

    }

    @FXML
    private void onBlueUnlockClicked(){
        if (checkMoneyAvailable(500)){
            blueUnlock.setText("Owned");
            blueUnlock.setDisable(true);
            blueTheme.setDisable(false);
            blueIcon.setGlyphName(UNLOCK);
        }


    }

    private boolean checkMoneyAvailable(int price){
        if (_model.getMoney() >= price){
            int currentMoney = _model.getMoney();
            int newMoney = currentMoney - price;
            _model.setMoney(newMoney);
            return true;
        } else {
            return false;
        }
    }
}
