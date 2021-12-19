package com.w.h.netty.demo.nio.zerocopy;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-19 11:40
 **/
public class NioZeroCopyClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();

//        socketChannel.bind(new InetSocketAddress(7001));

        socketChannel.connect(new InetSocketAddress("localhost",7001));

        String  fileName = "a.zip";

        FileInputStream fileInputStream = new FileInputStream(fileName);

        // 这样就可以得到一个文件channel
        FileChannel fileChannel = fileInputStream.getChannel();

        long startTime = System.currentTimeMillis();


        // 在linux下 一个transferTo 就可以完成传输  但是在windows下，此方法调用一次，最大只能发送八兆的文件
        // 在windows下的话，就需要分段传输了，而且传输时的位置需要注意！

        //  这是linux的写法   windows的需要分段
        // 从0 开始传递   第二个参数：要传多少个
        long transferToCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println("发送的总的字节数: " + transferToCount + "耗时: " + (System.currentTimeMillis() - startTime));




    }

}
