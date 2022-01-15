package com.w.h.netty.inboundhandleroutboundhandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2022-01-13 15:07
 **/
public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("服务器ip" + ctx.channel().remoteAddress());

        System.out.println("收到的消息： "+msg);

    }


    /**
     * 重写channelActive 发送数据
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


        /**
         * if (acceptOutboundMessage(msg)) {
         *                 @SuppressWarnings("unchecked")
         *                 I cast = (I) msg;
         *                 buf = allocateBuffer(ctx, cast, preferDirect);
         *                 try {
         *                     encode(ctx, cast, buf);
         *                 } finally {
         *                     ReferenceCountUtil.release(cast);
         *                 }
         *
         *                 if (buf.isReadable()) {
         *                     ctx.write(buf, promise);
         *                 } else {
         *                     buf.release();
         *                     ctx.write(Unpooled.EMPTY_BUFFER, promise);
         *                 }
         *                 buf = null;
         *             } else {
         *                 ctx.write(msg, promise);
         *             }
         *
         *
         *
         *             MessageToByteEncoder 有个write方法，判断是不是他能处理的类型，能处理的话，就编码
         *             如果不是，就跳过encoder方法
         */

        System.out.println("MyClientHandler 发送数据");
//        ctx.writeAndFlush(Unpooled.copiedBuffer("ABCDABCDABCDABCD", CharsetUtil.UTF_8));
        ctx.writeAndFlush(123456L); // 直接发送的是一个long
    }
}
