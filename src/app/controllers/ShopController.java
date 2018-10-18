package app.controllers;

import app.Main;
import app.models.MoneySingleton;
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

    public void initialize(){
        initialButtonSetup();
    }
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


    // green button should be enabled while other two as disabled
    private void initialButtonSetup(){
        // get money singleton instance which contains information about what buttons are unlocked
        MoneySingleton singleton = MoneySingleton.getInstance();
        if (!singleton.getPurpleUnlocked()){
            purpleTheme.setDisable(true);
        }
        if (!singleton.getBlueUnlocked()){
            blueTheme.setDisable(true);
        }

    }

    @FXML
    private void onPurpleUnlockClicked(){
        purpleUnlock.setText("Owned");
        purpleUnlock.setDisable(true);
        purpleTheme.setDisable(false);

        purpleIcon.setGlyphName(UNLOCK);
    }

    @FXML
    private void onBlueUnlockClicked(){
        blueUnlock.setText("Owned");
        blueUnlock.setDisable(true);
        blueTheme.setDisable(false);

        blueIcon.setGlyphName(UNLOCK);

    }
}
