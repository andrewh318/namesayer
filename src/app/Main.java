package app;

import app.controllers.FrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Parent rootScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Frame.fxml"));
        Parent root = (Parent) loader.load();
        rootScene = root;
        FrameController controller = (FrameController) loader.getController();
        primaryStage.setTitle("Name Sayer");
        primaryStage.setScene(new Scene(root));
        controller.setStage(primaryStage);
        setGreen();
        primaryStage.show();
    }

    public static void setPurple(){
        rootScene.getStylesheets().clear();
        rootScene.getStylesheets().add("/resources/styles/purple.css");
    }

    public static void setGreen(){
        rootScene.getStylesheets().clear();
        rootScene.getStylesheets().add("/resources/styles/green.css");
    }

    public static void setBlue(){
        rootScene.getStylesheets().clear();
        rootScene.getStylesheets().add("/resources/styles/blue.css");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
