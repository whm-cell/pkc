package com.w.h.netty.demo.nio.buffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @program: pkc
 * @description:
 *
 * .1 Scatting 将数据写入到buffer时，可以采用buffer数组 依次写入 【分散】
 * 2. Gathering 从buffer读取数据时，也可以采用buffer数据 依次读出 【聚合】
 *
 * @author: WangHaiMing
 * @create: 2021-12-16 14:36
 **/
public class NioScattingandgathering {


    public static void main(String[] args) throws IOException {

        // 使用 serversocketchannel   涉及到socket时，涉及到了网络

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 创建一个server
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        // 绑定端口到socket 并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        // 创建buffer数组  ‘理解为在服务器端’

        ByteBuffer[] byteBuffers = new ByteBuffer[2];

        // 为每一个
        byteBuffers[0] = ByteBuffer.allocate(5);

        byteBuffers[1] = ByteBuffer.allocate(3);

        //等到客户端连接
        SocketChannel socketChannel = serverSocketChannel.accept();


        // 假定从客户端最多接受八个   因为字节数组  最大长度只有8
        int messageLength = 8;

        // 循环读取
        while (true){
            int byteRead = 0 ;
            while (byteRead < messageLength){
                // 这里的buffers是数组  这里读取到第六个的字节的时候 ，会自动往第二个集合里面填充数据
                socketChannel.read(byteBuffers);
                byteRead+=1;
                System.out.println("byteread : "+ byteRead);
                // 使用流打印   看看当前的buffer的position和limit
                for (ByteBuffer byteBuffer : Arrays.asList(byteBuffers)) {
                    String s = "position=" + byteBuffer.position() + "limit=" + byteBuffer.limit();
                    System.out.println(s);
                }
            }

            // 将数据 读出  显示到客户端
            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.flip());

            long bytewrite = 0;

            while (bytewrite<messageLength){
                socketChannel.write(byteBuffers);
                bytewrite+=1;
            }

       /*     Object[] array = Arrays.asList(byteBuffers).toArray();
            System.out.println(new String(String.valueOf(array)));
            */


            // 将所有的buffer进行一个clear操作
            Arrays.asList(byteBuffers).forEach(byteBuffer -> {
                byteBuffer.clear();
            });
            System.out.println(("byteRead=" + byteRead + "bytewrite=" + bytewrite));

        }







    }
}
