package com.fx.dense.controller.rsa;

import com.fx.dense.base.Const;
import com.fx.dense.model.DesRequestModel;
import com.fx.dense.model.rsa.PublicKeyEncryptionDto;
import com.fx.dense.utils.DesUtil;
import com.fx.dense.utils.TooltipUtil;
import com.fx.dense.utils.rsa.RsaUtils;
import com.google.common.collect.Lists;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-11-04 15:57
 **/
public class RsaController implements Initializable {

    @FXML
    ChoiceBox filling;

    @FXML
    ChoiceBox veryLongText;

    @FXML
    TextField extraLongSplitCharacter;

    @FXML
    ChoiceBox output;

    @FXML
    ChoiceBox characterSet;

    @FXML
    TextArea publicKey;

    @FXML
    TextArea privateKey;

    @FXML
    TextArea context;

    @FXML
    Button en;

    @FXML
    Button de;

    @FXML
    Button copyResult;

    @FXML
    CheckBox pkcs1;

    @FXML
    TextField secretKey;


    public void enMethod(ActionEvent event){

        String fillingValue = (String) filling.getValue();

        String characterSetValue = (String) characterSet.getValue();

        String outputValue = (String) output.getValue();

        String publicKeyText = publicKey.getText();

        String textText = context.getText();

        boolean pkcs1Selected = pkcs1.isSelected();

        PublicKeyEncryptionDto build = PublicKeyEncryptionDto.builder()
                .filling(fillingValue)
                .characterSet(characterSetValue)
                .output(outputValue)
                .publicKey(publicKeyText)
                .text(textText)
                .type(pkcs1Selected)
                .build();
        try{
            String s = RsaUtils.buildParametersAndResultSetsPublicKey(build);
            context.setText(s);
        }catch (Exception e){
            TooltipUtil.showToast(e.getMessage());
            context.setText("");
            context.setStyle("-fx-text-fill:red");
        }
    }


    public void deMethod(ActionEvent event){

        String fillingValue = (String) filling.getValue();

        String characterSetValue = (String) characterSet.getValue();

        String outputValue = (String) output.getValue();

        String privateKeyText = privateKey.getText();

        String textText = context.getText();

        String secretKeyText = secretKey.getText();

        boolean pkcs1Selected = pkcs1.isSelected();

        PublicKeyEncryptionDto build = PublicKeyEncryptionDto.builder()
                .filling(fillingValue)
                .characterSet(characterSetValue)
                .output(outputValue)
                .privateKey(privateKeyText)
                .text(textText)
                .secretKey(secretKeyText)
                .type(pkcs1Selected)
                .build();
        try{
            String s = RsaUtils.privateKeyDecryption(build);
            context.setText(s);
        }catch (Exception e){
            TooltipUtil.showToast(e.getMessage());
            e.printStackTrace();
            context.setText("");
            context.setStyle("-fx-text-fill:red");
        }
    }

    public void copyResultMethod(ActionEvent event){
        context.selectAll();
        context.copy();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        privateKey.setWrapText(true);
        publicKey.setWrapText(true);
        context.setWrapText(true);

        filling.setItems(FXCollections.observableArrayList("RSA/ECB/PKCS1Padding","RSA/ECB/OAEPWithSHA-1AndMGF1Padding","RSA/None/PKCS1Padding","RSA/ECB/NoPadding"));
        filling.setValue("RSA/ECB/PKCS1Padding");

        characterSet.setItems(FXCollections.observableArrayList("gb2312","gbk","gb18030","utf-8","iso-8859-1"));
        characterSet.setValue("utf-8");


        output.setItems(FXCollections.observableArrayList("base64","hex"));
        output.setValue("base64");

    }
}
