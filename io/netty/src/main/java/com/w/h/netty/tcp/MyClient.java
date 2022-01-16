package com.w.h.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2022-01-13 15:00
 **/
public class MyClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();


        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new MyClientInitialzer());

            ChannelFuture channelFuture = bootstrap.connect("localhost", 7000).sync();

            ChannelFuture sync = channelFuture.channel().closeFuture().sync();


        }finally {
            group.shutdownGracefully();
        }

    }
}
