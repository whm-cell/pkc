package com.w.h.netty.hetthttp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

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
        /**
         * 1. HttpServerCodec  这是netty提供的处理http的编码解码器
         *
         * 2、增加一个自定义的处理器
         */
        pipeline.addLast("myHttpServerCodeC",new HttpServerCodec());
        pipeline.addLast("myTestHeepServerHandler",new TestHeepServerHandler());


    }
}
