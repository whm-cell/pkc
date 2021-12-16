package com.w.h.netty.demo.nio.buffer;

import java.nio.ByteBuffer;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-16 13:53
 **/
public class NioReadOnlyBuffer {


    public static void main(String[] args) {

        ByteBuffer allocate = ByteBuffer.allocate(64);

        for (int i = 0; i < allocate.capacity(); i++) {
            allocate.put((byte) i);
        }

        // 转读取
        allocate.flip();

        // 得到只读的buffer
        ByteBuffer readOnlyBuffer = allocate.asReadOnlyBuffer();

        //class java.nio.HeapByteBufferR  代表只读
        System.out.println(readOnlyBuffer.getClass());

        while (readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        // 因为已经是只读了  就不可以放数据了

        // java.nio.ReadOnlyBufferException 代表是只读的  不允许再添加数据了
        readOnlyBuffer.putInt(11);
    }
}
