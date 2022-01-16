package com.w.h.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2022-01-16 11:37
 **/

public class MessageEncoder extends MessageToByteEncoder<MessageProtocol> {


    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        System.out.println("MessageEncoder 被调用");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
