package com.w.h.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-28 15:53
 **/
public class NettyByteBuffer {


    public static void main(String[] args) {
        
        
        // 创建一个缓冲区
        /**
         * 该对象会创建一个数组  数组的长度是10     byte[10]
         * 在netty的buffer中不需要使用flip功能
         * 因为Unpooled.buffer 底层维护了一个readIndex 和一个 writeIndex
         */
        ByteBuf buffer = Unpooled.buffer(10);


        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }


        for (int i = 0; i < 10; i++) {
            System.out.println(buffer.readByte());
        }

        // 直接用下标  不会导致 readindex变化
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.getByte(i));
        }

    }

}
