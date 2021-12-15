package com.fx.dense;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;

/**
 * @program: pkc
 * @description: 主启动文件
 * @author: WangHaiMing
 * @create: 2021-10-08 16:00
 **/
public class Dense extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{

        URL url = this.getClass().getClassLoader().getResource("home.fxml");

        Parent root = FXMLLoader.load(url);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });


        Scene scene = new Scene(root);
        primaryStage.setOnCloseRequest(evt -> Platform.exit());
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable (false);
        primaryStage.setScene(scene);
        primaryStage.show();

    }




    public static void main(String[] args) {
        launch(args);
    }
}
