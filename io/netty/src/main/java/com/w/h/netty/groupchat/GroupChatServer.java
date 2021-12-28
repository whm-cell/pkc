package com.w.h.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-28 16:46
 **/
public class GroupChatServer {

    private int port ;

    public GroupChatServer(int port){
        this.port = port;
    }

    /**
     * 编写一个run方法 处理一个客户端请求
     */
    public void run() throws InterruptedException {

        //创建两个线程组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

        NioEventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE)
                    .childHandler(new ChannelInitializer<SocketChannel>() {


                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            // 先获取到pipeLine  然后添加相关的处理器
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            // 第一个处理器
                            // 向pipeline里加入一个解码器
                            pipeline.addLast("decoder",new StringDecoder());
                            // 再向pipeline加入一个编码器
                            pipeline.addLast("encoder",new StringEncoder());

                            // 还得加入自己的handler   用于业务处理
                            pipeline.addLast(null);


                        }
                    });
            System.out.println("netty服务器已启动");
            ChannelFuture future = bootstrap.bind(port).sync();

            // 仍然要去监听关闭时间
            future.channel().closeFuture().sync();

        }catch (Exception e){
            System.out.println("e:  " + e.getMessage());
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }





    }

    public static void main(String[] args) throws InterruptedException {

        GroupChatServer server = new GroupChatServer(7000);

        server.run();


    }
}
