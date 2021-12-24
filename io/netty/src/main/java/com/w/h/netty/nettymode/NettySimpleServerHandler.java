package com.w.h.netty.nettymode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

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
       /* System.out.println("服务器读取现场: " + Thread.currentThread().getName());

        System.out.println(" server ctx" + ctx);

        System.out.println("看看channel和pipeline的关系: ");

        Channel channel = ctx.channel();

        // 本质是一个双向链表 涉及到一个出站入站
        ChannelPipeline pipeline = ctx.pipeline();



        // 将msg转成byteBuffer
        // ByteBuffer.wrap(msg)
        // ByteBuf 这里的byteF性能更好
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是： " + byteBuf.toString(CharsetUtil.UTF_8));

        System.out.println("客户端地址：" + ctx.channel().remoteAddress());*/

        // 加入注释代码非常耗时
        // 1. 希望异步执行   -> 提交到channel对应的 NIOEventLoop的taskQueue中->

        /**
         * 解决方案1  用户自定义的普通任务
         * 如果此时在运行一个taskQueue  ，其实是一个线程在处理，这个线程会继续休眠20秒，然后继续执行其他的taskQueue
         *
         * 是同一个线程在运行
         *
         * 如果再调用  ctx.channel().eventLoop().execute(new Runnable() ，会同时存在两个taskQueue
         */

        /*ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try{

                    Thread.sleep(10000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("回送的话  客户端 瞄 2 ~~~~~",CharsetUtil.UTF_8));
                    System.out.println("go on ... ");
                }catch (Exception e){
//                    e.getCause();
//                    e.printStackTrace();
                    System.out.println("发送异常: "+e.getMessage());
                }
            }
        });*/


        /**
         * 解决方案二：  用户自定义定时任务
         *    这种任务是提交到scheduleTashQueue
         *
         */
        /*ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try{

                    Thread.sleep(10000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("回送的话  客户端 瞄 4 ~~~~~",CharsetUtil.UTF_8));
                    System.out.println("go on ... ");
                }catch (Exception e){
                    System.out.println("发送异常: "+e.getMessage());
                }
            }
        },5, TimeUnit.SECONDS);*/




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
