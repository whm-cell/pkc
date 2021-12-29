package com.w.h.netty.websocket;

import com.w.h.netty.heartbeat.MyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program: pkc
 * @description:
 * @author: WangHaiMing
 * @create: 2021-12-29 17:16
 **/
public class MyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG,128)
//                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    // 在BOSSGroup增加一个日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 因为基于http协议  要使用http的编码和解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 是以块方式写的  ，添加一个ChunkedWriteHandler
                            pipeline.addLast(new ChunkedWriteHandler());
                            //
                            /*
                            说明：
                                1.因为http的数据在传输过程中，是分段的。 所以HttpObjectAggregator就是可以将多个段聚合起来
                                2.这就是为啥当浏览器发送大量数据时，会出现多次http请求的效果。
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /*
                             说明
                                1.对于websocket，它的数据是以’帧（frame）‘的形式传递的
                                2.可以看到websocketframe 有留个子类
                                3.浏览器发送请求时
                                    例子：
                                        ws://localhost:7000/hello          --  表示请求的uri
                                4.WebSocketServerProtocolHandler的核心功能是 将http协议升级为ws协议
                                    即websocket协议，即保持长连接



                                为什么可以升级到ws协议
                                5. 是根据状态码101 切换的
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            // 再写一个自定义的handler    用于 处理业务逻辑
                            pipeline.addLast(new MyTextWebsocketFrameHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(7000).sync();

            channelFuture.channel().closeFuture().sync();


        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
