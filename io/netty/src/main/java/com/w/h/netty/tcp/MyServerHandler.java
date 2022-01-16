package com.w.h.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2022-01-05 21:19
 **/
public class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        // 截数据 先把msg转换成字节数组
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);

        // 字节数组转为字符串

        String message = new String(bytes, Charset.forName("utf-8"));

        System.out.println("服务器端接收数据： "+message);

        System.out.println("检测服务端收了几次 ， 服务端接收到的消息量= "+(++this.count));


        //  回一些数据给客户端  回送一个随机id
        System.out.println("准备回送数据给客户端");

        ByteBuf response = Unpooled.copiedBuffer(UUID.randomUUID().toString() +" ", CharsetUtil.UTF_8);

        ctx.writeAndFlush(response);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();

    }
}
