package com.w.h.netty.groupchat_siliao;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-29 10:59
 **/
public class GroupChatClient {


    private final String host;
    private final int port;


    public GroupChatClient(String host,int port) {
        this.host = host;
        this.port = port;
    }


    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {


                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("decoder",new StringDecoder());

                            pipeline.addLast("encoder",new StringEncoder());

                            pipeline.addLast(new GroupChatClientHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 得到channel

            Channel channel = channelFuture.channel();

            System.out.println("-------" + channel.localAddress() + "-----");

            // 客户段需要输入信息  因此需要创建一个扫描器
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNext()){
                // 输入的信息
                String content = scanner.nextLine();

                channel.writeAndFlush(content+" \n");

            }


        }catch (Exception e){
            System.out.println("client -> e: " + e.getMessage());
        }finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        GroupChatClient client = new GroupChatClient("127.0.0.1",7000);

        client.run();
    }

}
