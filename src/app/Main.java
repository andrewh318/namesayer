package app;

import app.controllers.FrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {
    public enum Theme {
        GREEN, PURPLE, BLUE
    }

    public static Theme currentTheme = null;
    // this field allows the shop class to change the CSS for the overall application
    public static Parent programRoot = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Frame.fxml"));
        Parent root = (Parent) loader.load();

        programRoot = root;

        FrameController controller = (FrameController) loader.getController();
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(new Scene(root));
        controller.setStage(primaryStage);
        setTheme(Theme.GREEN, programRoot);
        primaryStage.show();
    }

    // takes a node in the application and changes the css file for it
    public static void setTheme(Theme theme, Parent root){
        switch (theme){
            case GREEN:
                root.getStylesheets().clear();
                root.getStylesheets().add("/resources/styles/green.css");
                currentTheme = Theme.GREEN;
                break;
            case PURPLE:
                root.getStylesheets().clear();
                root.getStylesheets().add("/resources/styles/purple.css");
                currentTheme = Theme.PURPLE;
                break;
            case BLUE:
                root.getStylesheets().clear();
                root.getStylesheets().add("/resources/styles/blue.css");
                currentTheme = Theme.BLUE;
                break;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
