package com.w.h.netty.demo.nio.buffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-15 21:13
 **/
public class NioFileChannel_read {

    public static void main(String[] args) throws IOException {

        File file = new File("d:\\file01.txt");
        // 创建一个输出流
        FileInputStream fileInputStream = new FileInputStream(file);

        //  读文件的时候  文件的长度是可以知道的  直接file.length   防止字节数组资源浪费
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());

        FileChannel fileChannel = fileInputStream.getChannel();

        // 将通道里的数据读到 缓冲区里。
        // read可以理解为 readTo   write可以理解为writefrom
        fileChannel.read(byteBuffer);

        // 以channel为基准，往缓冲区写了值后，缓冲区buffer要翻转  调用fiip的方式，重置索引位，才可以循环输出
        byteBuffer.flip();

        // 将字节转成字符串
        // array[] 说白了就是返回 bytebuffer里的  hd ‘对应的字节数组’
        System.out.println(new String(byteBuffer.array()));

    }
}
