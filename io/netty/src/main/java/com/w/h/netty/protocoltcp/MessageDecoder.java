package com.w.h.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2022-01-16 11:37
 **/

public class MessageDecoder extends ReplayingDecoder<Void> {



    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MessageDecoder.decoder 方法被调用");

        // 这里需要将获取到的二进制字节码转成自己的  - > MessageProtocol 数据包

        int length = in.readInt();

        byte[] content = new byte[length];

        in.readBytes(content);

        // 下一步封装成MessageProtocol对象，，然后再放入到 out中  ，传递给下一个handler进行业务处理。   这里的话仅仅是数据解码

        MessageProtocol protocol = new MessageProtocol();

        protocol.setLen(length);
        protocol.setContent(content);

        out.add(protocol);

    }
}
