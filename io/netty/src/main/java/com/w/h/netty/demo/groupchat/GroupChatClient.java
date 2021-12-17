package com.w.h.netty.demo.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-17 15:38
 **/
public class GroupChatClient {


    /**
     * 定义要链接的主机
     */
    private static final String host = "127.0.0.1";

    /**
     * 需要知道服务器的主机和端口
     */
    private static final int port = 6667;

    private Selector selector;

    private SocketChannel socketChannel;

    private String userName;

    public GroupChatClient (){

        try {
            selector = Selector.open();

            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            // 设置非阻塞
            socketChannel.configureBlocking(false);

            socketChannel.register(selector,SelectionKey.OP_READ);

            userName = socketChannel.getLocalAddress().toString().substring(1);

            System.out.println(userName + "is ok...");


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 准备向服务器发送消息了
     */
    public void sendInfo(String info){

        info = userName+"说： "+info;

        try {

            ByteBuffer wrap = ByteBuffer.wrap(info.getBytes());
            socketChannel.write(wrap);

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }

    }

    /**
     * 读取从服务端恢复的消息
     */
    public void readFromInfo(){
        try {

            //  读取前，要查看这个通道有没有时间发生
            int select = selector.select();
            // select 大于 0 代表有可用的通道
            if (select>0) {

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()){

                    SelectionKey key = iterator.next();

                    if(key.isReadable()){
                        // 得到相关的通道
                        SocketChannel sc = (SocketChannel) key.channel();

                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        //  ************ 每次读的时候，要加入循环读取，不要认为第一次读取就是可以把所有的内容都可以读完的。************
                        while (true){
                            byteBuffer.clear();
                            // 这里就不用返回了  直接拿着缓冲区的数据，转换成字符串
                            int read = sc.read(byteBuffer);

                            if(read == -1){
                                // 代表已经读完了
                                break;
                            }
                            byteBuffer.flip();
                        }

                        System.out.println(new String(byteBuffer.array()).trim());

                    }

                    //  必须将当前的key移除调   防止一个重读读写
                    iterator.remove();

                }

            } else {
//                System.out.println("没有可用的通道");
            }


        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }



    public static void main(String[] args) {


        // 第一步，先启动客户端
        GroupChatClient chatClient = new GroupChatClient();

        new Thread(() -> {
            while (true){
                chatClient.readFromInfo();
                try {
                    Thread.sleep(3000);
                }catch (Exception e){
                    e.printStackTrace();
                    e.getCause();
                }
            }
        }).start();


        //  客户端发送数据给服务端
        // 创建一个扫描器

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }


    }























}
