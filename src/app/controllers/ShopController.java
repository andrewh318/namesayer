package app.controllers;

import app.Main;
import app.models.NamesModel;
import app.models.ShopModel;
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
        Main.setTheme(Main.Theme.BLUE, Main.programRoot);
    }

    @FXML
    public void onGreenThemeClicked(){
        Main.setTheme(Main.Theme.GREEN, Main.programRoot);
    }

    @FXML
    public void onPurpleThemeClicked(){
        Main.setTheme(Main.Theme.PURPLE, Main.programRoot);
    }

    private NamesModel _model;
    private ShopModel _shopModel;


    public void setUp(NamesModel model, ShopModel shopModel){
        _model = model;
        _shopModel = shopModel;
        initialButtonSetup();
    }


    // green button should be enabled while other two as disabled
    private void initialButtonSetup(){
        if (!_shopModel.getPurpleUnlocked()){
            purpleTheme.setDisable(true);
        }
        if (!_shopModel.getBlueUnlocked()){
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
        if (_shopModel.getMoney() >= price){
            int currentMoney = _shopModel.getMoney();
            int newMoney = currentMoney - price;
            _shopModel.setMoney(newMoney);
            return true;
        } else {
            return false;
        }
    }
}
