package com.hm.oauth.base;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.text.StringEscapeUtils;

import javax.swing.*;
import java.awt.*;

public class demo {

    public static void main(String[] args) {

        JFrame jf=new JFrame();
        jf.setTitle("简易工具  by hm");
        jf.setSize(800,500);
        jf.setLocationRelativeTo(null);
        jf.setLayout(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel=new JPanel();

        final JTextArea de_txt=new JTextArea("目前可用秘钥  人事 ：KQTMOJGM 绩效：JSDGADSG  ， common项目： WIUDSDBA ");
        de_txt.setPreferredSize(new Dimension(750,30));

        JLabel des_label=new JLabel("<html>des秘钥：");
        des_label.setFont(new Font("楷体",Font.PLAIN,18));

        //设置文本域
        final JTextField txt=new JTextField("JSDGADSG");
        txt.setPreferredSize(new Dimension(130,30));
        txt.setFont(new Font("楷体",Font.PLAIN,18));


//        JLabel str_=new JLabel("<html>hello <br> world!</html>",JLabel.CENTER);
//        str_.setFont(new Font("楷体",Font.PLAIN,18));






        //设置文本域
        final JTextArea str_txt=new JTextArea("准备加密的json",10,70);
        str_txt.setLineWrap(true);
        str_txt.setWrapStyleWord(true);


        JTextArea sres_txt = new JTextArea("result...",10,70);

        sres_txt.setLineWrap(true);
        sres_txt.setWrapStyleWord(true);




        JButton btn_en = new JButton("加密");

        JButton btn_de = new JButton("解密");

        //测试复制粘贴的按钮
        JButton btnCopy=new JButton("结果复制");
        btnCopy.setBackground(Color.RED);
        btnCopy.addActionListener(e -> {
            sres_txt.selectAll();
            sres_txt.copy();//复制

        });
        //点击事件  加密
        btn_en.addActionListener(e -> {
            String java = StringEscapeUtils.unescapeJava(str_txt.getText());
            String s = EnDecoderUtil.desEncrypt(java, txt.getText());
            sres_txt.setText(s);
        });

        //点击事件  解密
        btn_de.addActionListener(e -> {
            String java = StringEscapeUtils.unescapeJava(str_txt.getText());
            String s = EnDecoderUtil.desDecrypt(java, txt.getText());
            sres_txt.setText(s);
        });


        panel.add(de_txt);
        panel.add(des_label);
        panel.add(txt);

        panel.add(btn_en);
        panel.add(btn_de);
        panel.add(btnCopy);
        //panel.add(str_);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setContentPane(panel);

        jf.add(new JScrollPane(str_txt));
        jf.add(new JScrollPane(sres_txt));

        jf.setVisible(true);


    }
}
