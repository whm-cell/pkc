package com.fx.fxdemo.textarea;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TextAreaExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        TextArea textArea = new TextArea();

        textArea.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        String text = textArea.getText();

        Scene scene = new Scene(new Pane(textArea), 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();



    }
}