package com.w.h.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-29 16:37
 **/
public class MyServer {

    public static void main(String[] args) throws Exception {
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
                /**
                 * 加入一个netty提供的IdleStateHandler
                 *
                 * IdleStateHandler :
                 *     netty提供的处理控件状态的处理器
                 *     第一个参数；
                 *          long readerIdleTime  多长时间没有读了 ，这个时候就会发送一个‘心跳检测包’检测是否还是连接的状态
                 *          long writerIdleTime  表示有多长时间没有写了，也会发送一个‘心跳检测包’检测是否还是连接的状态
                 *          long allIdleTime     表示多长时间，即没有读也没有写了，也会发送一个‘心跳检测包’检测是否还是连接的状态
                 *          TimeUnit unit
                 *
                 *     作用：
                 *          当remove无法感知的时候，就需要心跳检测来关闭通道了。
                 *          比如：  手机强制关机，并么有关闭应用
                 *
                 *     注意：   当 IdleStateHandler 触发后，就会传递管道（pipeLine）的下一个handler去处理 所有这里要让自定义的handler紧跟着IdleStateHandler。
                 *
                 *      通过调用/回调  触发下一个handler的userEventTriggered，然后在该方法中去处理IdleStateHandler事件
                 *      有可能是读空闲  写空闲   读写空闲
                 *
                 */
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG,128)
//                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    // 在BOSSGroup增加一个日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));

                            // 加入一个对空闲检测进一步处理的handler（自定义）
                            pipeline.addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(7000).sync();

            channelFuture.channel().closeFuture().sync();


        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
