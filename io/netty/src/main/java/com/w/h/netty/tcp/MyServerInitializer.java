package com.w.h.netty.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2022-01-05 21:19
 **/
public class MyServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();


        pipeline.addLast(new MyServerHandler());
    }
}
