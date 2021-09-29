package com.fx.dense;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;

/**
 * @author Administrator
 */
public class Dense extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        URL url = this.getClass().getClassLoader().getResource("dense.fxml");
        Parent root = FXMLLoader.load(Objects.requireNonNull(url));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root, 1130, 644));
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
