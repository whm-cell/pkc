package com.w.h.netty.hetthttp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-24 17:01
 *
 * HttpObject 客户端和服务器端他们相互通讯的数据  被封装成httpObject类型
 * SimpleChannelInboundHandler 其实是 ChannelInboundHandlerAdapter的一个子类
 *
 **/
public class TestHeepServerHandler extends SimpleChannelInboundHandler<HttpObject> {


    /**
     * 读取客户端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        // 判断msg是不是 HttpRequest 请求嘞
        if (msg instanceof HttpRequest) {

            System.out.println("pipeline hashCode : " + ctx.pipeline().hashCode());
            System.out.println("TestHeepServerHandler hash : " + this.hashCode());


            System.out.println("msg 的类型： " + msg.getClass());
            System.out.println("客户端地址; " + ctx.channel().remoteAddress());

            /**
             * 对特定资源进行请求过滤
             *
             * msg 其实就是客户端发来的数据，那么能从msg里能拿到uri
             *  通过uri过滤特定资源
             */
            HttpRequest httpRequest = (HttpRequest) msg;
            // 获取uri
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("uri: " + uri.getPath());
                System.out.println("请求了 http://localhost:8887/favicon.ico  不做响应  直接return");
                return;
            }

            // 回复信息给浏览器
            //  这里需要将文本做成http协议的内容，才可以回复
            ByteBuf context = Unpooled.copiedBuffer("hello 我是服务器", CharsetUtil.UTF_8);

            //  构造一个http响应的response
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, context);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain;charset=utf-8");
            // 统计返回内容的文本长度
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,context.readableBytes());

            // 将response返回
            ctx.writeAndFlush(response);




        }

    }
}
