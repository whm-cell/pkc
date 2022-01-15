package com.w.h.netty.inboundhandleroutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2022-01-13 15:04
 **/
public class MyLongToByteEncode extends MessageToByteEncoder<Long> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
        System.out.println("MyLongToByteEncode.encoder 被调用 ");
        System.out.println("msg= "+msg);

       //  out.writeBytes(msg.toString().getBytes(StandardCharsets.UTF_8));
        out.writeLong(msg);
    }
}
