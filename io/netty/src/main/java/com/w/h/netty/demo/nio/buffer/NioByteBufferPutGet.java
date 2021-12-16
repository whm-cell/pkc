package com.w.h.netty.demo.nio.buffer;

import java.nio.ByteBuffer;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-16 13:46
 **/
public class NioByteBufferPutGet {


    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(64);

        // 按照类型化的方式放入数据
        buffer.putInt(100);
        buffer.putLong(9);
        buffer.putChar('尚');
        buffer.putShort((short) 4);

        // 取出 buffer的内容

        buffer.flip();

        // 获取  因为插入的数据类型不一致  获取的时候，也要根据类型获取
        // 还得按照插入的顺序进行获取  如果不按照顺序获取  会自动转型
        System.out.println(buffer.getInt());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getShort());
        System.out.println(buffer.getLong());


    }
}
