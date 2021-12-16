package com.w.h.netty.demo.nio.selector;

import org.springframework.expression.spel.ast.Selection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-16 16:24
 **/
public class NioSelectorServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 得到一个selector对象
        Selector selector = Selector.open();

        // 绑定端口 在服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        // 把我们的serversocketchannel注册到selector    关心事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("已经注册的所有的key : " + selector.keys().size());

        while (true){

            //  没有事件发生   selector.selectNow()
            if(selector.select(1000) == 0){
                // 等到一秒  如果没有事件发送 就返回 。
                System.out.println("服务器等待了一秒，无连接，");
                continue;
            }

            // 如果有事件  >0
            // 获取到相关的selectorkey集合 (有事件发生的key集合)
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()){
                // 获取到selectorKey
                SelectionKey key = iterator.next();

                // 根据这个key对应的通道发生的事件 做不同的处理。
                if(key.isAcceptable()){
                    // accept 有新的客户端连接我了
                    //  需要给该客户端生成一个socketCannel
                    // 由于事件驱动原理  到这里 实际上客户端已经在连接服务端了  所以这里不会阻塞
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    System.out.println("客户端连接成功，生成了一个socketchannel  hash: "+socketChannel.hashCode());

                    // 改变key的事件状态
                    // key.interestOps(SelectionKey.OP_CONNECT);

                    // 需要将channel设置为非阻塞
                    socketChannel.configureBlocking(false);

                    // 将当前的socketchannel注册到selector里
                    // 这里可以给这个通道绑定一个buffer缓存区
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                    System.out.println("已经注册的所有的key : " + selector.keys().size());
                    //else 通道注册好了 下一步就是发数据了
                }
                if (key.isReadable()) {
                    // op_read 事件   客户端开始从缓存区读数据了。
                    // 通过key反向获得缓存区的channel   再根据channel找buffer

                    // 强转   可以无障碍向下转型
                    SocketChannel socketChannel =(SocketChannel) key.channel();

                    socketChannel.configureBlocking(false);

                    // 获取到该channel关联的buffer
                    // 这个对象就是buffer
                    Object attachment = key.attachment();
                    // 强转
                    ByteBuffer buffer = (ByteBuffer) attachment;

                    socketChannel.read(buffer);

                    System.out.println("form 客户端发过来的数据为： " + new String(buffer.array()));



                }
                //**********************需要手动从集合中移除当前的key  防止重复操作*********************************
                //********************** 因为是多线程 防止重复消费***********************************************

                iterator.remove();

            }


        }



    }

}
