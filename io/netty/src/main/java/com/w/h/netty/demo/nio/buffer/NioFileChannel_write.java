package com.w.h.netty.demo.nio.buffer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-15 21:13
 **/
public class NioFileChannel_write {

    public static void main(String[] args) throws IOException {
        String text = "你好";

        // 创建一个输出流
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");

        // 通过一个输出流获取对应的文件 channel

        // 真实的类型是 FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        // 创建一个缓冲区  byteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 将string放入到bytebuffer
        byteBuffer.put(text.getBytes());

        // 需要放到channel里的时候，先进行反正，进行一个读操作
        byteBuffer.flip();

        // 将buffer的数据写入到channel
        fileChannel.write(byteBuffer);

        fileOutputStream.close();





    }
}
