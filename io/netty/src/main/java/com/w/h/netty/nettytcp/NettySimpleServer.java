package com.w.h.netty.nettytcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-19 17:16
 **/
public class NettySimpleServer {

    public static void main(String[] args) throws InterruptedException {

        /**
         * 创建了俩线程组  分别是 bossGroup和 workergroup
         * boss只处理连接请求  真正的和客户端处理业务  会交给workerGroup 去处理
         * workerGroup
         *
         * 这两个对象都是无限循环   一直在循环中 。。。
         *-------------bossGroup 一般只需要一个线程 ----------------------
         */
        // 首先需要创建bossGroup和workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        //2.创建nio loop group
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try{

            // -- - 创建服务器端的启动对象 ， 可以去配置启动参数
            /**
             * 这个对象可以设置参数
             */
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 使用链式变成进行设置
            /**
             * 这是设置两个线程组
             */
            bootstrap.group(bossGroup,workerGroup)
                    //  使用 NioSocketChannel 作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //  设置线程队列  等待连接的个数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //  这是保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    // 该handler对应的是bossGroup ，也就是说这个handler会在bossGroup生效、
                    // .handler(null)
                    // 给workerGroup的EventLoop的对应管道设置处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        /**
                         * new ChannelInitializer<SocketChannel>() {  创建一个通道初始化对象   ‘匿名对象的方式’
                         * 实现:
                         *      给pipeline设置处理器
                         *
                         * @param ch
                         * @throws Exception
                         */
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("客户对应的channel：  hashcode: " + ch.hashCode());

                            /**
                             * 这里可以使用一个集合  管理所有的socketChannel
                             * 在需要推送消息时，可以将业务加入到各个channel对应的nioEventLoop的taskQueue
                             * 或者scheduleTaskQueue中
                             */

                            //  返回channel关联的pipeline
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast(new NettySimpleServerHandler()); // 向管道的最后，增加一个处理器

                        }
                    });

            System.out.println("服务器准备好了  is ready .. .");

            /**
             * 绑定一个端口并且同步   生成一个channelFuture对象
             * cf 后面回读取cf的结果
             * --------------------------这里绑定端口 并调用sync后 就直接启动了服务了-------------------------
             */
            ChannelFuture cf = bootstrap.bind(6668).sync();

            // 对关闭通道 进行监听
            /**
             * 这个不是立刻关闭    这是对关闭通道进行监听，当有关闭事件的时候，才会变比通道
             */
            cf.channel().closeFuture().sync();


        }finally {
            // 成为优雅的关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }



    }
}
