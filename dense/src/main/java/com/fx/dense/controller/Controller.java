package com.fx.dense.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Administrator
 */
public class Controller implements Initializable {

    @FXML ImageView ic;
    @FXML Circle pic;
    @FXML Circle min;
    @FXML Circle close;
    @FXML Label login;

    @FXML TextField name;

    @FXML TextField pwd;


    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


      /*  Image image = new Image(getClass().getResource("/images/pic.jpg").toExternalForm());
        pic.setFill(new ImagePattern(image));

        login.setText("Login");*/

    }

    @FXML
    public void back(MouseEvent event) throws IOException {

        URL url = this.getClass().getClassLoader().getResource("dense.fxml");

        Parent blah = FXMLLoader.load(url);

        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

       /*

        blah.setOnMousePressed(event1 -> {
            xOffset = event1.getSceneX();
            yOffset = event1.getSceneY();
        });
        blah.setOnMouseDragged(event12 -> {
            appStage.setX(event12.getScreenX() - xOffset);
            appStage.setY(event12.getScreenY() - yOffset);
        });
        */

        Scene scene = new Scene(blah);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        appStage.setX((screenBounds.getWidth() - 1130) / 2);
        appStage.setY((screenBounds.getHeight() - 644) / 2);
        appStage.setScene(scene);
        appStage.show();

    }

    /**** minimize ****/
    @FXML
    public void minclick(MouseEvent event) throws IOException {

        ((Stage) ((Circle) event.getSource()).getScene().getWindow()).setIconified(true);


    }

    /**** close screen ****/
    @FXML
    public void closeclick(MouseEvent event) throws IOException {


        System.exit(0);


    }
}
