package com.fx.dense.controller.rsa;

import com.google.common.collect.Lists;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-06 14:14
 **/
public class GenerateKeyPairController implements Initializable {


    @FXML
    JFXComboBox secretKeyBits;

    @FXML
    JFXComboBox secretKeyFormat;

    @FXML
    JFXComboBox outputFormat;

    @FXML
    JFXTextField secretKey;


    public void generate(ActionEvent event){
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        secretKeyBits.setItems(FXCollections.observableArrayList("512","1024","2048","4096"));
        secretKeyBits.setValue("1024");


        secretKeyFormat.setItems(FXCollections.observableArrayList("PKCS#1","PKCS#8"));
        secretKeyFormat.setValue("PKCS#8");

        outputFormat.setItems(FXCollections.observableArrayList("PEM/Base64","Hex","Hex仅公钥","Hex仅私钥"));
        outputFormat.setValue("PEM/Base64");

    }
}
