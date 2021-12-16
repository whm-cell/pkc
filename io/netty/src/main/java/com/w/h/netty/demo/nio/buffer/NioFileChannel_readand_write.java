package com.w.h.netty.demo.nio.buffer;

import java.io.File;
import java.io.FileInputStream;
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
public class NioFileChannel_readand_write {

    public static void main(String[] args) throws IOException {

        File file = new File("d:\\file01.txt");

        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fileChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file02.txt");

        FileChannel fileOutputStreamChannel = fileOutputStream.getChannel();
        // 不知道文件多大的时候  循环读取

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);

        while (true){

            // 清除相当于重置bytebuffer数组。
             byteBuffer.clear();

            // 如果不加clear  那么第一次读完后，第二次读取相当于拿上一次的buffer从通道再次读取。
            // 所以需要复位  那么是下次的

            // 不加clear的话，读到最后一次的时候，即当前索引位置等于
            // 不clear的   永远是读的上一次的bytebuffer 如果当前次已经到达了通道里的字节最大长度，那么会输出read就是0 并且会漏掉不满足bytebuffer默认长度的字节。 造成数据丢失

            int read = fileChannel.read(byteBuffer);

            if(read == -1){
                break;
            }

            // 将buffer中的数据写入到channel2中
            byteBuffer.flip();
            fileOutputStreamChannel.write(byteBuffer);
        }

        fileInputStream.close();
        fileOutputStream.close();



        System.out.println(new String(byteBuffer.array()));

    }
}
