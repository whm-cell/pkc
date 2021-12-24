package com.w.h.netty.hetthttp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-24 17:00
 **/
public class TestServer {



    public static void main(String[] args) {
        // 首先需要创建bossGroup和workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        //2.创建nio loop group
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(null);

            // 异步处理
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();


            channelFuture.channel().closeFuture().sync();


        }catch (Exception e){

        }finally {
            // 成为优雅的关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
