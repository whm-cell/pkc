package com.fx.dense.controller;

import com.fx.dense.model.DesRequestModel;
import com.fx.dense.utils.EnDecoderUtil;
import com.google.common.collect.Lists;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @author Administrator
 */
public class DenseController implements Initializable {

    /**
     * 加密模式
     */
    @FXML
    private ChoiceBox encryptionMode;

    /**
     * 填充
     */
    @FXML
    private ChoiceBox filling;

    /**
     * 输出
     */
    @FXML
    private ChoiceBox output;

    /**
     * 字符集
     */
    @FXML
    private ChoiceBox characterSet;

    /**
     * 自动生成绩效key
     */
    @FXML
    private Button perkey;

    /**
     * 自动生成人事key
     */
    @FXML
    private Button addrekey;


    /**
     * 自动生成commonkey
     */
    @FXML
    private Button commkey;

    /**
     * 常用配置生成
     */
    @FXML
    private Button genKey;


    /**
     * 加密秘钥
     */
    @FXML
    private TextField secretKey;

    /**
     * 加密按钮
     */
    @FXML
    private Button en;

    /**
     * 解密按钮
     */
    @FXML
    private Button de;

    /**
     * 复制结果按钮
     */
    @FXML
    private Button copyResult;

    @FXML
    private TextArea requText;

    @FXML
    private TextArea respoText;

    @FXML
    private Text errorText;

    public void enMethod(ActionEvent event){
        try{
            DesRequestModel build = getDesRequestModel();
            String encrypt = EnDecoderUtil.desEncrypt(build);
            respoText.setText(encrypt);

            //清空错误信息
            errorText.setText("");
        }catch (Exception e){
            respoText.setText("暂无结果");
            respoText.setStyle("-fx-text-fill:pink");
            errorText.setText(e.getMessage());
        }
    }


    public void deMethod(ActionEvent event){
        try {
            DesRequestModel model = getDesRequestModel();
            String desDecrypt = EnDecoderUtil.desDecrypt(model);
            respoText.setText(desDecrypt);
        }catch (Exception e){
            errorText.setText(e.getMessage());
        }
    }

    public void copyResultMethod(ActionEvent event){
        respoText.selectAll();
        respoText.copy();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //1.1 初始化下拉框内容
        initSelectContext();

        //2.设置基础样式

        requText.setWrapText(true);

        respoText.setWrapText(true);

//        filling.setDisable(true);
//
//        encryptionMode.setDisable(true);

    }

    private void initSelectContext() {
        ArrayList<Object> mode  = Lists.newArrayList();
        mode.add("ECB");
        mode.add("CBC");
        mode.add("CRT");
        mode.add("OFB");
        mode.add("CFB");
        encryptionMode.setValue("CBC");
        encryptionMode.getItems().setAll(mode);


        ArrayList<Object> fill  = Lists.newArrayList();
        fill.add("PKCS5Padding");
        fill.add("PKCS7Padding");
        fill.add("zeropadding");
        fill.add("iso10126");
        fill.add("ansix923");
        fill.add("no padding");
        filling.setValue("PKCS5Padding");
        filling.getItems().setAll(fill);

        ArrayList<Object> outp  = Lists.newArrayList();
        outp.add("base64");
        outp.add("hex");
        output.setValue("hex");
        output.getItems().setAll(outp);

        ArrayList<Object> character  = Lists.newArrayList();
//        character.add("gb2312编码（简体）");
//        character.add("gbk编码（简繁体）");
//        character.add("gb18030编码（中日韩）");
//        character.add("utf8编码（unicode编码）");
//        character.add("iso-885-1（单字节）");
//        characterSet.setValue("utf8编码（unicode编码）");

        character.add("gb2312");
        character.add("gbk");
        character.add("gb18030");
        character.add("utf-8");
        character.add("iso-8859-1");
        characterSet.setValue("utf-8");
        characterSet.getItems().setAll(character);
    }

    private DesRequestModel getDesRequestModel() {
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

        DesRequestModel build = DesRequestModel.builder()
                .type("DES")
                .context(text)
                .characterSet(characterSetValue)
                .key(keyText)
                .encryptionMode(modeValue)
                .output(outputValue)
                .filling(fillingValue)
                .build();
        return build;
    }
    public void perKeyMethod(ActionEvent event){
        secretKey.setText("JSDGADSG");
    }

    public void addKeyMethod(ActionEvent event){
        secretKey.setText("KQTMOJGM");
    }

    public void commKeyMethod(ActionEvent event){
        secretKey.setText("WIUDSDBA");
    }
}
