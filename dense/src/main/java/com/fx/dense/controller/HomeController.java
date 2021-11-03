package com.fx.dense.controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-02 16:53
 **/
public class HomeController implements Initializable {


    /**
     * 动态tab页
     */
    @FXML
    TabPane dynamicTabId;

    @FXML
    MenuItem aes;

    @FXML
    MenuItem des;



    public void goDes(ActionEvent event){

        try{

            URL url = this.getClass().getClassLoader().getResource("dense.fxml");

            Parent blah = FXMLLoader.load(url);

            Tab tab = new Tab("Des",blah);

            dynamicTabId.getTabs().add(tab);

            if (event != null) {
                dynamicTabId.getSelectionModel().select(tab);
            }
            tab.setOnCloseRequest((Event event1) -> {
            });
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }



    }

    public void goAes(ActionEvent event){
        try{

            URL url = this.getClass().getClassLoader().getResource("Aes.fxml");

            Parent blah = FXMLLoader.load(url);

            Tab tab = new Tab("Aes",blah);

            dynamicTabId.getTabs().add(tab);

            if (event != null) {
                dynamicTabId.getSelectionModel().select(tab);
            }
            tab.setOnCloseRequest((Event event1) -> {
            });
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }


    public void  about(ActionEvent event){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
