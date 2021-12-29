package com.w.h.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-29 17:16
 *
 * 这里看到的泛型表示的是一个 文本帧 即frame
 **/
public class MyTextWebsocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        System.out.println("服务器端收到消息" + msg.text());

        // 准备回复客户端了
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间"+ LocalDate.now() + msg.text()));
    }

    /**
     * 当web客户端连接后，立马调用此方法
     * @param ctx
     * @throws Exception
     *
     * ctx.channel().id().asLongText()  表示一个唯一的一个值，
     * 一个是longText()
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // asLongText 唯一
        System.out.println("handlerAdded 被调用"+ctx.channel().id().asLongText());
        // asShortText 不唯一
        System.out.println("handlerAdded 被调用"+ctx.channel().id().asShortText());
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        System.out.println("handlerRemoved 被调用"+ctx.channel().id().asLongText());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发送 ： " + cause.getMessage());
        // 关闭连接
        ctx.close();

    }
}
