package com.w.h.netty.demo.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-17 15:38
 **/
public class GroupChatServer {


    /**
     * 1. 先去定义相关的属性
     * 2. serversocket
     * @param args
     */
    private Selector selector;

    /**
     * 专门做监听的channel
     */
    private ServerSocketChannel listenChannel;

    private static final int port = 6667;

    public GroupChatServer (){
        // 在构造器里处理初始化任务

        try {
            // 得到选择器
            selector= Selector.open();

            //
            listenChannel = ServerSocketChannel.open();

            // 绑定端口
            listenChannel.socket().bind(new InetSocketAddress(port));

            // 必须设置非阻塞
            listenChannel.configureBlocking(false);

            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }

    }


    /**
     * 开始完成监听的代码
     */
    public void listen(){

        try {

            while (true){
                //  不输入  一直阻塞
                int count = selector.select();
                if(count>0){
                    // 说明有事件要处理
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()){
                        SelectionKey selectionKey = iterator.next();


                        // 转换
                        if (selectionKey.isAcceptable()) {
                            // 处理连接
                            // 处理连接的时候才用  socketChannel
                            // SelectableChannel channel = selectionKey.channel();
                            SocketChannel sc = listenChannel.accept();

                            sc.configureBlocking(false);

                            sc.register(selector,SelectionKey.OP_READ);

                            // 提示  上线
                            System.out.println(sc.getRemoteAddress() + "上线");


                        }

                        if(selectionKey.isReadable()){
                            // 代表数据是可读的状态
                            readData(selectionKey);
                        }

                        // 要把当前的key删除  方式多线程情况下  重复处理
                        iterator.remove();

                    }

                }else {
                    System.out.println("等待中。。。");
                }


            }

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }finally {

        }
    }


    public void readData(SelectionKey selectionKey){
        // 根据获取到selectkey反向获取数据
        // 定义一个socketChannel
        SocketChannel socketChannel = null;

        try {
            // 取到关联的channel
            socketChannel = (SocketChannel) selectionKey.channel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            socketChannel.configureBlocking(false);

            int count = socketChannel.read(buffer);

            if(count > 0 ){
                //  确实读取到数据了  输出该消息
                String s = new String(buffer.array());
                System.out.println("from 客户端 ： "+ new String(buffer.array()));

                // ***********向其他客户端转发消息**************
                sendInfoToOtherClients(s,socketChannel);
            }

            buffer.clear();
        }catch (Exception e){
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了");
                // 用户离线了，1. 先取消注册  然后 2. 需要关闭通道
                selectionKey.cancel();
                // 2.
                socketChannel.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    /**
     * 转发消息，相当于给通道发消息
        给其他客户端发 要排除自己

     */
    public void sendInfoToOtherClients(String msg , SocketChannel self) throws IOException {

        System.out.println("服务器转发消息中..");

        // 给其他人发的时候，找所有的selectkeys
        // 这里直接从成员变量里取就行了
        for (SelectionKey key: selector.keys()
             ) {

            // 通过key取出对应的socketCahnnel
            Channel channel =  key.channel();
            if (channel instanceof SocketChannel && channel != self) {
                // 在讲文本存储到buffer
                SocketChannel socketChannel = (SocketChannel) channel;

                // 将字符串转成bytebuffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());

                socketChannel.write(buffer);

            }

        }

    }

    public static void main(String[] args) {
        // 服务器端的启动
        GroupChatServer server = new GroupChatServer();
        //  启动 直接调用监听方法即可
        server.listen();
    }

}
