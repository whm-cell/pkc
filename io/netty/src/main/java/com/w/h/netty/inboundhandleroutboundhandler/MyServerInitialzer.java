package com.w.h.netty.inboundhandleroutboundhandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2022-01-05 21:19
 **/
public class MyServerInitialzer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {


        // 先去获取到channelpipeline

        ChannelPipeline pipeline = ch.pipeline();

        // 入栈的handler进行解码 MyByteToLongDecoder
        pipeline.addLast(null);

    }
}
