package app.controllers;

import app.Main;
import app.models.NamesModel;
import app.models.ShopModel;
import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

/**
 * This is the controller for the shop screen. It is responsible for handling user purchasing themes and switching
 * global application design.
 * @author: Andrew Hu and Vincent Tunnell
 */
public class ShopController {
    public static final String UNLOCK = "UNLOCK";
    public static final String ITEM_OWNED = "Owned";
    public static final int THEME_PRICE = 500;

    // buttons that change the application theme
    @FXML private JFXButton purpleTheme;
    @FXML private JFXButton blueTheme;
    @FXML private JFXButton redTheme;
    @FXML private JFXButton aquaTheme;
    @FXML private JFXButton yellowTheme;

    // button that unlocks the theme
    @FXML private JFXButton purpleUnlock;
    @FXML private JFXButton blueUnlock;
    @FXML private JFXButton redUnlock;
    @FXML private JFXButton aquaUnlock;
    @FXML private JFXButton yellowUnlock;

    // icon that either shows if a theme is locked or unlocked
    @FXML private FontAwesomeIconView purpleIcon;
    @FXML private FontAwesomeIconView blueIcon;
    @FXML private FontAwesomeIconView redIcon;
    @FXML private FontAwesomeIconView aquaIcon;
    @FXML private FontAwesomeIconView yellowIcon;

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

    @FXML
    public void onRedThemeClicked(){
        Main.setTheme(Main.Theme.RED, Main.programRoot);
    }

    @FXML
    public void onAquaThemeClicked(){
        Main.setTheme(Main.Theme.AQUA, Main.programRoot);
    }

    @FXML
    public void onYellowThemeClicked(){
        Main.setTheme(Main.Theme.YELLOW, Main.programRoot);
    }


    private NamesModel _model;
    private ShopModel _shopModel;


    public void setUp(NamesModel model, ShopModel shopModel){
        _model = model;
        _shopModel = shopModel;
        initialButtonSetup();
    }

    /**
     * Reads in the state from the Shop model and then sets up the buttons correctly to reflect it.
     */
    private void initialButtonSetup(){
        if (_shopModel.getPurpleUnlocked()){
            unlockPurple();
        }
        if (_shopModel.getBlueUnlocked()){
            unlockBlue();
        }
        if (_shopModel.getRedUnlocked()){
            unlockRed();
        }
        if (_shopModel.getAquaUnlocked()){
            unlockAqua();
        }
        if (_shopModel.getYellowUnlocked()){
            unlockYellow();
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

    @FXML
    private void onRedUnlockClicked(){
        if (checkMoneyAvailable(THEME_PRICE)){
            unlockRed();
            // set up global application state for shop
            _shopModel.setRedUnlocked(true);
        } else {
            showAlert("Sorry: Not enough money", "This item costs: " + THEME_PRICE + " V Bucks \nYou" +
                    " only have: " + _shopModel.getMoney() + " V Bucks");
        }
    }

    @FXML
    private void onAquaUnlockClicked(){
        if (checkMoneyAvailable(THEME_PRICE)){
            unlockAqua();
            // set up global application state for shop
            _shopModel.setAquaUnlocked(true);
        } else {
            showAlert("Sorry: Not enough money", "This item costs: " + THEME_PRICE + " V Bucks \nYou" +
                    " only have: " + _shopModel.getMoney() + " V Bucks");
        }
    }

    @FXML
    private void onYellowUnlockClicked(){
        if (checkMoneyAvailable(THEME_PRICE)){
            unlockYellow();
            // set up global application state for shop
            _shopModel.setYellowUnlocked(true);
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

    private void unlockRed(){
        redUnlock.setDisable(true);
        redUnlock.setText(ITEM_OWNED);
        redTheme.setDisable(false);
        redIcon.setGlyphName(UNLOCK);
    }

    private void unlockAqua(){
        aquaUnlock.setDisable(true);
        aquaUnlock.setText(ITEM_OWNED);
        aquaTheme.setDisable(false);
        aquaIcon.setGlyphName(UNLOCK);
    }

    private void unlockYellow(){
        yellowUnlock.setDisable(true);
        yellowUnlock.setText(ITEM_OWNED);
        yellowTheme.setDisable(false);
        yellowIcon.setGlyphName(UNLOCK);
    }

    /**
     * Checks if the user can afford the theme they selected
     * @param price Price of the theme they want to purchase
     * @return Returns true if they can afford it, false otherwise
     */
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
