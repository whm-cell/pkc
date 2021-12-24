package com.w.h.netty.hetthttp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-24 17:02
 **/
public class TestServerInit extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {


        //  向管道加入处理器
        // 得到管道

        ChannelPipeline pipeline = socketChannel.pipeline();

        // 加入一个netty提供的httpServerCodeC   CodeC = > [code  -> decoder ]

    }
}
