package com.w.h.netty.protoBu2;

import com.w.h.netty.protoBuf.studentPOJO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Random;


/**
 * @program: pkc
 * @description:
 * @author: whm
 * @create: 2021-12-19 17:16
 **/
public class NettySimpleClientHandler extends ChannelInboundHandlerAdapter{

    /**
     * 入栈 出栈
     * Inbound
     *
     * 重写：   当通道就绪对的时候，就会触发改方法
     *
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // 随机的发送student 或者woker对象
        int random = new Random().nextInt(3);

        MyDataInfo.MyMessage myMessage = null ;

        // 根据产生的random 选择不同的对象
        if (random == 0) {
            myMessage = MyDataInfo.MyMessage.newBuilder().setDataType(MyDataInfo.MyMessage.DataType.StudentType)
                    .setStudent(MyDataInfo.Student.newBuilder().setId(5).setName("王  莎").build()).build();
        }else {
            // 发送一个worker对象
            myMessage = MyDataInfo.MyMessage.newBuilder().setDataType(MyDataInfo.MyMessage.DataType.WorkerType)
                    .setWorker(MyDataInfo.Worker.newBuilder().setAge(5).setName("王       莎").build()).build();
        }

        ctx.writeAndFlush(myMessage);

    }

    /**
     * 代表有数据可读了  当通道有读取时间时，就会出发该事件
     *
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf message = (ByteBuf) msg;

        System.out.println("服务器回复的消息: "+ message.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址： " + ctx.channel().remoteAddress());

    }

    /**
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
