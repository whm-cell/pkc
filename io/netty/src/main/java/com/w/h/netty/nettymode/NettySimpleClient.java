package com.w.h.netty.nettymode;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-19 17:16
 **/
public class NettySimpleClient {

    public static void main(String[] args) throws InterruptedException {
        /**
         * 客户端使用的是 BootStrap
         *
         */

        // 客户端只需要一个时间循环组就可以了
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        try{
            // 创建一个启动对象
            Bootstrap bootstrap = new Bootstrap();

            //  需要对bootstrap设置相关参数
            //  先放进去事件   循环组  '线程组'
            bootstrap.group(nioEventLoopGroup)
                    //  设置客户端通道的实现类
                    .channel(NioSocketChannel.class)
                    /**
                     * 说 客户端也是不停循环的，然后需要 handler
                     */
                    .handler(new ChannelInitializer<SocketChannel>() {


                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            // 加入自己的处理器
                            ch.pipeline().addLast(new NettySimpleClientHandler());

                        }
                    });

            System.out.println("客户端 is  oj=k .. ");
            // 客户端启动
            //*************************客户端启动***************************
            // sync 让方法不会阻塞在这
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("127.0.0.1", 6668)).sync();
            //  涉及到netty的伊布模型

            // 给 channelFuture 注册监听器 ，监控我们关心的事件
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口6668成功");
                    }else {
                        System.out.println("监听失败");
                    }

                }
            });


            // 非阻塞的关闭监听.
            /**
             * 这里 不能直接使用 close()  这里关闭的时候，管道而不是通道 必须使用  closeFuture()
             */
            channelFuture.channel().closeFuture().sync();
        }finally {

            nioEventLoopGroup.shutdownGracefully();
        }

    }
}
