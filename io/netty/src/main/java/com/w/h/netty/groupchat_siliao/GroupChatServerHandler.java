package com.w.h.netty.groupchat_siliao;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-28 16:46
 *
 *
 * <String>  发送数据的方式
 **/
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {


    /**
     * // 私聊部分 ：  channel使用hashMap管理
     */
    public static Map<String,Channel> channels = new HashMap<>();
    public static Map<User,Channel> channels2 = new HashMap<>();

    //    先定义一个channel组  管理所有的channel
    /**
     * DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
     * 全局的时间执行器   单例  ！
     */
     private static ChannelGroup channelGroup = new  DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 表示连接建立  该方法  第一个被执行
     * 将当前的channel加入到channelGroup
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //  System.out.println("将该客户加入到聊天的信息推送给其他在线的客户");

        /*
        channelGroup.writeAndFlush 会将channelGroup中所有的channel遍历并发送该消息
         因此无需自己遍历
         */
        // channelGroup.writeAndFlush("【客户端】"+channel.remoteAddress()+"加入聊天 "+sdf.format(new Date())+" \n");

        //channelGroup.add(channel);

        channels.put("id100",channel);

        // 这里引入无状态连接
//        channels2.put(new User(10,"123"),channel);

    }
    /**
     * 表示断开连接了 , 将某某客户离开的信息推送给当前在线的所有人
     *
     * handlerRemoved 被调用了 会导致channelGroup里的当前的channel自动给去掉了。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress() + "离开了 \n" );
        System.out.println("channelGroup的大小 size: " + channelGroup.size());

    }
    /**
     * 表示channel处于活动状态
     * 表示某某某上线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了 ~~");
    }

    /**
     * channel
     * 处于不活动状态的时候  提示下线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println(ctx.channel().remoteAddress() + " 离线了 ~~");

    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        Channel channel = ctx.channel();

        // 这是遍历一下channelGroup 根据不同的情况，会送不同的消息
       channelGroup.forEach(v ->{

           if (v!= channel) { //  如果不是当前的channel
                v.writeAndFlush("[客户]"+channel.remoteAddress()+ " 发送的消息是： "+s+" \n");
           }else {
                v.writeAndFlush("[自己]发送了消息"+s+"\n");
           }
       });
    }

    /**
     * 该方法是处理异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("关闭了通道");
        ctx.close();

    }
}
