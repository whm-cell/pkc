package com.w.h.netty.inboundhandleroutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2022-01-13 15:57
 *
 * Void
 *  代表不需要状态管理
 **/
public class MyBtyeToLongDecder2 extends ReplayingDecoder<Void> {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyBtyeToLongDecder2 被调用");
        //  ReplayingDecoder 不需要判断数据是否足够处理 内部会进行判断
        out.add(in.readLong());
    }
}
