package com.w.h.netty.demo.nio.buffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.RandomAccess;

/**
 * @program: pkc
 * @description:
 *
 * MappedByteBuffer
 * 1. 可以让文件直接在内存中（堆外内存）修改  这样操作系统不需要拷贝一次文件。
 *
 *
 *
 * @author: WangHaiMing
 * @create: 2021-12-16 13:58
 **/
public class NioMappedByteBuffer {

    public static void main(String[] args) throws IOException {


//        MappedByteBuffer
        // rw 代表模式 ： 读写
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt","rw");

        // 获取对应的文件通道
        FileChannel randomAccessFileChannel = randomAccessFile.getChannel();

        /**
         *  参数1 ： FileChannel.MapMode.READ_WRITE  使用的读写模式
         *  参数2 ： 可以直接修改的起始位子
         *  参数3 ：  映射到内存到大小 ， 及将文件1.txt的多少个字节映射到内存
         *  可以直接的范围  就是我们所说的0-5   (最多修改五个字节  size )
         *
         * */


        MappedByteBuffer mappedByteBuffer = randomAccessFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0,(byte)'h');

        mappedByteBuffer.put(3,(byte) '9');

        randomAccessFile.close();
        System.out.println("修改成功");


    }
}
