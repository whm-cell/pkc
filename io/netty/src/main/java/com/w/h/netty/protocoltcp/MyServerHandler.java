package com.w.h.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2022-01-05 21:19
 **/
public class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

        // 接收到数据并处理
        //1.先拿到产犊 准备读长度下的数据
        int len = msg.getLen();

        byte[] content = msg.getContent();

        System.out.println("服务端接收到的信息如下： ");
        System.out.println("长度= " + len);

        System.out.println("内容" + new String(content, CharsetUtil.UTF_8));

        System.out.println("服务器接收到消息数量" + (++this.count));


        // 服务端回复的消息
        String resMsg = UUID.randomUUID().toString();

        int resLen = resMsg.getBytes(CharsetUtil.UTF_8).length;


        byte[] msgBytes = resMsg.getBytes(CharsetUtil.UTF_8);

        MessageProtocol protocol = new MessageProtocol();

        protocol.setLen(resLen);
        protocol.setContent(msgBytes);

        ctx.writeAndFlush(protocol);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();

    }
}
