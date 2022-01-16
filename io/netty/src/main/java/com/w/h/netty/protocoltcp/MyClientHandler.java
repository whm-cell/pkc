package com.w.h.netty.protocoltcp;

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
public class MyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
        int len = msg.getLen();

        byte[] msgContent = msg.getContent();

        System.out.println("客户端接受消息如下： ");
        System.out.println("长度： " + len);
        System.out.println("内容： "+ new String(msgContent, CharsetUtil.UTF_8));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        System.out.println("异常信息： "+cause.getMessage());

        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

      // 还是要客户端发送10条数据

        for (int i = 0; i < 5; i++) {

            String msg = "莎莎傻傻";

            byte[] content = msg.getBytes(CharsetUtil.UTF_8);

            int length = msg.getBytes(CharsetUtil.UTF_8).length;

            // 这时候加入自定义协议包
            MessageProtocol protocol = new MessageProtocol();

            protocol.setContent(content);

            protocol.setLen(length);

            ctx.writeAndFlush(protocol);

        }

    }


}
