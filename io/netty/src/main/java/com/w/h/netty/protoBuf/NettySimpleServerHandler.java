package com.w.h.netty.protoBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-19 17:16
 **/
public class NettySimpleServerHandler extends ChannelInboundHandlerAdapter{


    /**
     *  1. 需要先继承一个ChannelInboundHandlerAdapter  （netty规定好的）
     *     这时候，咱们自定义的handler才能称为handler
     *      作用：
     *
     *  channelRead  我们可以读取到客户端发来的信息
     *  ChannelHandlerContext ctx 上下文对象
     *
     *          管道pipeline（注重逻辑处理）、通道channel（注重读写）、地址
     *  Object msg
     *          客户端发送的数据
     *
     *
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("读取客户端发送的student");
        studentPOJO.Student student = (studentPOJO.Student) msg;
        System.out.println("客户端发送的数据为： " + student.getId() +"， 名字为：  "+student.getName());
    }

    /**
     * 代表数据读取完毕
     数据读取完毕 直接回复消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /**
         * 写入到缓冲区， 并且刷新缓冲区
         需要发送的文本或者数据 需要进行编码

         需要将字符串编码
         */

        ctx.writeAndFlush(Unpooled.copiedBuffer("hallo 客户端~~~~1",CharsetUtil.UTF_8));

    }

    /**
     * 一般需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();

    }
}
