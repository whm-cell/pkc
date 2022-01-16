package com.w.h.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2022-01-13 15:07
 **/
public class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int count;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];

        msg.readBytes(buffer);

        String response = new String(buffer, CharsetUtil.UTF_8);

        System.out.println("客户端接收到服务端发来的消息= " + response);

        System.out.println("客户端接收到的消息数量" + (++this.count));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // 使用客户端发送10条数据
        for (int i = 0; i < 10; i++) {

            ByteBuf buf = Unpooled.copiedBuffer("hello server " + i, CharsetUtil.UTF_8);
            ctx.writeAndFlush(buf);
        }

    }


}
