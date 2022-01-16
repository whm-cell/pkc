package com.w.h.netty.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2022-01-13 15:02
 **/
public class MyClientInitialzer extends ChannelInitializer<SocketChannel> {



    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 加入一个出栈的handler 用于编码
//        pipeline.addLast(new MyLongToByteEncode());

//        pipeline.addLast(new MyBtyeToLongDecder2());
        // 再加入一个处理业务逻辑的hanlder


        pipeline.addLast(new MyClientHandler());

    }
}
