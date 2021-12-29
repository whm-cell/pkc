package com.w.h.netty.groupchat_siliao;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-29 10:59
 **/
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s.trim());
    }
}
