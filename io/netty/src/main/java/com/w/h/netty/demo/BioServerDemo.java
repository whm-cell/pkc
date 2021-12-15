package com.w.h.netty.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: pkc
 * @description:    原始io  同步阻塞
 * @author: WangHaiMing
 * @create: 2021-12-15 15:54
 **/
public class BioServerDemo {

    public static void main(String[] args) throws IOException {
        // 创建线程池
        // 如果有客户端连接，就床加一个线程与之通讯 （单独写一个方法）
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();


        // 创建一个server socker
        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器启动完毕");

        while (true) {
            System.out.println(("线程id ： " + Thread.currentThread().getId() + "   线程名字：" + Thread.currentThread().getName()));

            System.out.println("等待连接");
            // 监听  等待客户端连接
            final Socket socket = serverSocket.accept();
            System.out.println("连接到一个客户端了");

            // 启动一个线程
            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() { // run方法可以冲洗
                    // 这里就是可以和客户端通讯的
                    handler(socket);

                }
            });
        }


    }


    /**
     * 编写一个客户端方法
     */
    public static void handler(Socket socket) {
        try {


            // 判断 bio是不是一个客户端一个线程

            System.out.println(("线程id ： " + Thread.currentThread().getId() + "   线程名字：" + Thread.currentThread().getName()));

            byte[] bytes = new byte[1024];

            // 通过socket获取输入流  通过管道读取数据
            InputStream inputStream = socket.getInputStream();

            while (true) {
                // 判断 bio是不是一个客户端一个线程

                // 再次打印  判断输出时候的线程是不是socker连接时候的线程
                System.out.println(("线程id ： " + Thread.currentThread().getId() + "   线程名字：" + Thread.currentThread().getName()));

                //  read 返回读取的量  inputStream 把流写到byte里
                // read 是阻塞当前线程   客户端1 会一直卡在这 ，等待着数据读写
                System.out.println("read...  读完第一次数据后，下次会一直卡着在 ");
                int read = inputStream.read(bytes);

                if (read != -1) {
                    // 输入客户端发送的数据
                    System.out.println(new String(bytes, 0, read));
                } else {
                    System.out.println("break 了");
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        } finally {
            // 需要关闭流
            System.out.println("关闭和clent的连接");
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        }
    }

}
