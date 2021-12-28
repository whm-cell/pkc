package com.w.h.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-28 15:53
 **/
public class NettyByteBuffer02 {


    public static void main(String[] args) {

        ByteBuf byteBuf = Unpooled.copiedBuffer("hello world ", Charset.forName("utf-8"));

        // 使用相关的api
        /**
         * byteBuf.hasArray()  看看buffer里有没有内容
         */
        if (byteBuf.hasArray()) {

            byte[] content = byteBuf.array();

            System.out.println(new String(content, Charset.forName("utf-8")));

            // 返回可读的字节数  如果用readerIndex读取一个自己后，数组实际上可用要减去1
            int i = byteBuf.readableBytes();

            // 从什么位置 读取到什么位置
            //  0 索引下标  4 是个数
            System.out.println(byteBuf.getCharSequence(0, 4, Charset.forName("utf-8")));

        }


    }

}
