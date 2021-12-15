package com.fx.dense.controller.rsa;

import com.fx.dense.base.Const;
import com.fx.dense.model.rsa.GenerateKeyPairDto;
import com.fx.dense.utils.encryption.rsa.GenerateKeyPairUtils;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.Map;
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

    @FXML private TextArea requText;

    @FXML private TextArea respoText;

    @FXML
    Button publicCopy;

    @FXML
    Button privateCopy;


    public void generate(ActionEvent event){

        try{
            String value = (String)secretKeyBits.getValue();
            int bitsNum = Integer.parseInt(value);

            String outputFormatValue = (String)outputFormat.getValue();


            GenerateKeyPairDto build = GenerateKeyPairDto.builder()
                    .headType("")
                    .secretKeyBits(bitsNum)
                    .secretKeyFormat((String) secretKeyFormat.getValue())
                    .secretKey(secretKey.getText()).outputFormat(outputFormatValue)
                    .build();

            Map<String, Object> map = GenerateKeyPairUtils.initKey(build);

            map.forEach((k,v)->{
                if(k.equals(Const.PUBLIC_KEY_RSA)){
                    requText.setText((String) v);

                }else {
                    respoText.setText((String) v);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }


    public void publicCopyAction(ActionEvent event){
        requText.selectAll();
        requText.copy();
    }



    public void privateCopyAction(ActionEvent event){
        respoText.selectAll();
        respoText.copy();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        requText.setWrapText(true);
        respoText.setWrapText(true);

        secretKeyBits.setItems(FXCollections.observableArrayList("512","1024","2048","4096"));
        secretKeyBits.setValue("1024");


        secretKeyFormat.setItems(FXCollections.observableArrayList("PKCS#1","PKCS#8"));
        secretKeyFormat.setValue("PKCS#8");

        outputFormat.setItems(FXCollections.observableArrayList("PEM/Base64","Hex","Hex仅公钥","Hex仅私钥"));
        outputFormat.setValue("PEM/Base64");

    }
}
