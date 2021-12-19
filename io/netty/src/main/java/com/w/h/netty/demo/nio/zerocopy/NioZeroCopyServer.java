package com.w.h.netty.demo.nio.zerocopy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-19 11:40
 **/
public class NioZeroCopyServer {

    public static void main(String[] args) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7001);


        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();


        ServerSocket socket = serverSocketChannel.socket();

        socket.bind(inetSocketAddress);

        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);

        while (true){
            // 这个返回跟客户端对应的channel
            SocketChannel socketChannel = serverSocketChannel.accept();

            int readCount = 0 ;

            while (-1 != readCount){
                try {
                    readCount = socketChannel.read(byteBuffer);

                }catch (Exception e){
//                    e.printStackTrace();
//                    e.getCause();

                    break;
                }

                // 将buffer进行倒带
                //  rewind 和clear 没理解到位
                byteBuffer.rewind();

            }


        }

    }

}
