package com.w.h.netty.nettytcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;


/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-19 17:16
 **/
public class NettySimpleClientHandler extends ChannelInboundHandlerAdapter{

    /**
     * 入栈 出栈
     * Inbound
     *
     * 重写：   当通道就绪对的时候，就会触发改方法
     *
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" client : " + ctx);

        ctx.writeAndFlush(Unpooled.copiedBuffer("hallo server ... 喵  ", CharsetUtil.UTF_8));

    }

    /**
     * 代表有数据可读了  当通道有读取时间时，就会出发该事件
     *
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf message = (ByteBuf) msg;

        System.out.println("服务器回复的消息: "+ message.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址： " + ctx.channel().remoteAddress());

    }

    /**
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
