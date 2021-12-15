package com.w.h.netty.demo;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * @program: pkc
 * @description: nio 同步非阻塞
 * @author: WangHaiMing
 * @create: 2021-12-15 17:19
 **/
public class NioServerDemo_buffer {

    public static void main(String[] args) {
        // 举例说明buffer的使用

        // build a buffer
        // 默认大小5  可以存放五个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        // 向buffer中存放数据
        // intBuffer.capacity() length
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i*2);
        }

        // 将buffer 转换一下  ‘读写切换’          ************************************ 读写切换   重点*****************************
        /**
         *  public final Buffer flip() {
         *         limit = position;
         *         position = 0;
         *         mark = -1;
         *         return this;
         *     }
         */
         intBuffer.flip();

        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }


















    }

}
