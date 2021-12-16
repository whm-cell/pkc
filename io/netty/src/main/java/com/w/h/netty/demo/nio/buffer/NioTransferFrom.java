package com.w.h.netty.demo.nio.buffer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-16 13:40
 **/
public class NioTransferFrom {

    public static void main(String[] args) throws IOException {


        FileInputStream fileInputStream = new FileInputStream("f:\\背带牛仔裤美女宋昕冉 居家写真4k手机壁纸_彼岸图网.jpg");

        FileOutputStream fileOutputStream = new FileOutputStream("f:\\b.jpg");


        // 获取对应的通道
        FileChannel sourceChannel = fileInputStream.getChannel();

        FileChannel destChannel = fileOutputStream.getChannel();

        // 使用  transferFrom    将元数据拷贝进 方法调用者。

        destChannel.transferFrom(sourceChannel,0,sourceChannel.size());

        // 关闭相关的通道和流

        sourceChannel.close();
        destChannel.close();
        fileInputStream.close();
        fileOutputStream.close();



    }

}
