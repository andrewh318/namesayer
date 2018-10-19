package app.controllers;

import app.Main;
import app.models.NamesModel;
import app.models.ShopModel;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class ShopController {
    public static final String UNLOCK = "UNLOCK";
    public static final String ITEM_OWNED = "Owned";
    public static final int THEME_PRICE = 500;


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


    // reads in state from shop and sets up buttons correctly
    private void initialButtonSetup(){
        if (_shopModel.getPurpleUnlocked()){
            unlockPurple();
        }
        if (_shopModel.getBlueUnlocked()){
            unlockBlue();
        }

    }

    @FXML
    private void onPurpleUnlockClicked(){
        if (checkMoneyAvailable(THEME_PRICE)){
            unlockPurple();
            // set the global application state for shop
            _shopModel.setPurpleUnlock(true);
        } else {
            showAlert("Sorry: Not enough money", "This item costs: " + THEME_PRICE + " V Bucks \nYou" +
                    " only have: " + _shopModel.getMoney() + " V Bucks");
        }

    }

    @FXML
    private void onBlueUnlockClicked(){
        if (checkMoneyAvailable(THEME_PRICE)){
            unlockBlue();
            // set the global application state for shop
            _shopModel.setBlueUnlocked(true);
        } else {
            showAlert("Sorry: Not enough money", "This item costs: " + THEME_PRICE + " V Bucks \nYou" +
                    " only have: " + _shopModel.getMoney() + " V Bucks");
        }
    }

    private void unlockPurple(){
        purpleUnlock.setDisable(true);
        purpleUnlock.setText(ITEM_OWNED);
        purpleTheme.setDisable(false);
        purpleIcon.setGlyphName(UNLOCK);
    }

    private void unlockBlue(){
        blueUnlock.setDisable(true);
        blueUnlock.setText(ITEM_OWNED);
        blueTheme.setDisable(false);
        blueIcon.setGlyphName(UNLOCK);
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


    private void showAlert(String header, String content){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
}
