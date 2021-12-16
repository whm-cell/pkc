package com.w.h.netty.demo.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-16 16:24
 **/
public class NioSelectorClient {

    public static void main(String[] args) throws IOException {

        // 得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();

        // 准备和服务端通讯  设置非阻塞
        socketChannel.configureBlocking(false);

        // 通过服务端的ip和端口
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 6666);

        // 连接服务器
        if (!socketChannel.connect(socketAddress)) {
//            System.out.println("服务器连接失败");
            while (!socketChannel.finishConnect()){
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作");
            }
        }

        // 如果连接成功  就准备发送数据了

        String message = "牛逼plus";

        // 直接可以根据字节数组的大小  初始化buffer的大小
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

        // 对channel来说，实际上就是将buffer的数据写入到channel里
        socketChannel.write(buffer);
        System.in.read();


    }

}
