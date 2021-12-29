package com.w.h.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-29 16:37
 **/
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *
     * @param ctx 上下文
     * @param evt 事件  读/写/读写
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        /**
         * 先判断evt的类型
         */
        if (evt instanceof IdleStateEvent) {

            // 向下转型
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = "";

            // 判断到底是什么事件
            switch (event.state()){

                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;

            }
            // 拿到到底是那一个客户端出现了超时事件
            System.out.println(ctx.channel().remoteAddress() +"---超时事件--"+ eventType);

            System.out.println("服务器就会做相应处理");

//            ctx.close();
        }
    }
}
