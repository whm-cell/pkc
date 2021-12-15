package com.fx.dense.controller;

import com.fx.dense.base.Const;
import com.fx.dense.model.AesRequestModel;
import com.fx.dense.utils.encryption.AesUtil;
import com.fx.dense.utils.TooltipUtil;
import com.google.common.collect.Lists;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import javax.validation.Valid;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @program: pkc
 * @description: aes控制器
 * @author: WangHaiMing
 * @create: 2021-10-08 16:00
 **/
@Valid
public class AesController implements Initializable {

    @FXML private ChoiceBox encryptionMode;

    @FXML private ChoiceBox filling;

    @FXML  private ChoiceBox output;

    @FXML private ChoiceBox characterSet;

    @FXML private TextField ivOffset;

    @FXML private ChoiceBox dataBlock;

    @FXML private Button perkey;

    @FXML private Button addrekey;

    @FXML private Button commkey;

    @FXML private Button genKey;

    @FXML private TextField secretKey;

    @FXML private Button en;

    @FXML private Button de;

    @FXML private Button copyResult;

    @FXML private TextArea requText;

    @FXML private TextArea respoText;

    @FXML private Text errorText;

    @FXML private TextArea initGenLog;

    public void perKeyMethod(ActionEvent event){
        secretKey.setText("JSDGADSG");
        ivOffset.setText("JSDGADSG");
    }

    public void addKeyMethod(ActionEvent event){
        secretKey.setText("KQTMOJGM");
        ivOffset.setText("KQTMOJGM");
    }

    public void commKeyMethod(ActionEvent event){
        secretKey.setText("WIUDSDBA");
        ivOffset.setText("WIUDSDBA");
    }

    public void enMethod(ActionEvent event){
        try{
            String results = generateEncryptionAndDecryptionResults(Const.ENCRYPT_OR_DECRYPT[0]);
            System.out.println(results);
            respoText.setText(results);
            respoText.setStyle("-fx-text-fill: black");
        }catch (Exception e){
            respoText.setText("暂无结果");
            respoText.setStyle("-fx-text-fill:pink");
            TooltipUtil.showToast(e.getMessage());
        }
    }


    /**
     * 构建和生成加解密结果
     * @param enOrDe
     * @return
     */
    private String generateEncryptionAndDecryptionResults(int enOrDe) {
        // 待加密的文本
        String text = requText.getText();
        // 填充
        String fillingValue = (String)filling.getValue();
        // 输出
        String outputValue = (String) output.getValue();
        // 秘钥
        String keyText = secretKey.getText();
        // 填充
        String modeValue =  (String) encryptionMode.getValue();
        // 字符集
        String characterSetValue =  (String) characterSet.getValue();

        String ivOffsetText = ivOffset.getText();

        String blockValue =  (String) dataBlock.getValue();

        AesRequestModel build = AesRequestModel.builder()
                .type("AES")
                .context(text)
                .characterSet(characterSetValue)
                .key(keyText)
                .encryptionMode(modeValue)
                .output(outputValue)
                .filling(fillingValue)
                .dataBlock(blockValue)
                .ivOffset(ivOffsetText)
                .isItEncrypted(enOrDe)
                .build();

        String encrypt = AesUtil.aes(build);
        return encrypt;
    }

    public void deMethod(ActionEvent event){
        try{
            String results = generateEncryptionAndDecryptionResults(Const.ENCRYPT_OR_DECRYPT[1]);
            respoText.setText(results);
            respoText.setStyle("-fx-text-fill: black");
          //   new Alert(Alert.AlertType.ERROR, "Unable to load "  + "\n" + "23423", ButtonType.OK).show();
        }catch (Exception e){
            respoText.setText("暂无结果");
            respoText.setStyle("-fx-text-fill:pink");
            TooltipUtil.showToast(e.getMessage());
        }
    }

    public void copyResultMethod(ActionEvent event){
        respoText.selectAll();
        respoText.copy();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSelectContext();

        //2.设置基础样式

        requText.setWrapText(true);

        respoText.setWrapText(true);


    }

    private void initSelectContext() {
        ArrayList<Object> aesMode = Lists.newArrayList();
        aesMode.add("ECB");
        aesMode.add("CBC");
        aesMode.add("CRT");
        aesMode.add("OFB");
        aesMode.add("CFB");
        encryptionMode.setValue("CBC");
        encryptionMode.getItems().setAll(aesMode);


        ArrayList<Object> aesFill  = Lists.newArrayList();
        aesFill.add("PKCS5Padding");
        aesFill.add("PKCS7Padding");
        aesFill.add("ZeroPadding");
        aesFill.add("iso10126Padding");
        aesFill.add("ANSIX923");
        aesFill.add("NoPadding");
        filling.setValue("PKCS5Padding");
        filling.getItems().setAll(aesFill);

        ArrayList<Object> aesOutp  = Lists.newArrayList();
        aesOutp.add("base64");
        aesOutp.add("hex");
        output.setValue("hex");
        output.getItems().setAll(aesOutp);

        ArrayList<Object> aesCharacter  = Lists.newArrayList();
        aesCharacter.add("gb2312");
        aesCharacter.add("gbk");
        aesCharacter.add("gb18030");
        aesCharacter.add("utf-8");
        aesCharacter.add("iso-8859-1");
        characterSet.setValue("utf-8");
        characterSet.getItems().setAll(aesCharacter);

        ArrayList<Object> choiceBoxList = Lists.newArrayList();
        choiceBoxList.add("128");
        choiceBoxList.add("192");
        choiceBoxList.add("256");
        dataBlock.setValue("128");
        dataBlock.getItems().setAll(choiceBoxList);
    }
}
