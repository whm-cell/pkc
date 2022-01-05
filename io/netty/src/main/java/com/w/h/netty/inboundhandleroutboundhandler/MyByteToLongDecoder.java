package com.w.h.netty.inboundhandleroutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2022-01-05 21:25
 **/
public class MyByteToLongDecoder extends ByteToMessageDecoder {


    /**
     *
     * @param ctx
     * @param in 入栈的bytebuf
     * @param out list集合  将解码后的数据专递给下一个handler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 一个Long 是八个字节  需要判断有八个字节  才能读取一个long
        if (in.readableBytes() >=8 ) {
            out.add(in.readLong());
        }
    }
}
