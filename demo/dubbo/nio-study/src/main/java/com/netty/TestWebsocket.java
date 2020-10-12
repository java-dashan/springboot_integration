package com.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class TestWebsocket {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = null;
        bootstrap = new ServerBootstrap();
//        selector 选择器线程
        EventLoopGroup boss = new NioEventLoopGroup();
//        工作线程
        EventLoopGroup worker = new NioEventLoopGroup();
//        初始化
//        1.把处理线程配置到启动类
        bootstrap.group(boss, worker)
//        2.设置通道的简历方式,这里采用Nio的通道方式来建立请求连接
                .channel(NioServerSocketChannel.class)
//               构建通道处理器 (以流水线方式)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//                        添加通道处理器
//                         http编解码器
                        pipeline.addLast(new HttpServerCodec());
//                         支持大数据流
                        pipeline.addLast(new ChunkedWriteHandler());
//                         聚合器,作用是将httpMessage 聚合成 FullHttpRequest/FullHttpResponse
                        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
//                         websocket协议支持 以/ws开头的才能访问 并且将byteBuffer转成 TextSocketFrame
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
//                        自定义 channelHandler
                        pipeline.addLast(new CostumHandler());
                    }
                })
//                针对主线程的配置 分配线程最大数量 128
                .option(ChannelOption.SO_BACKLOG, 128)
//                tcp套接字建立连接后进入ESTABLISHED状态,是否启用心跳机制
                .childOption(ChannelOption.SO_KEEPALIVE, true)
        ;

        try {
            ChannelFuture future = bootstrap.bind(8080).sync();
            if (future.isSuccess()) {
                System.out.println("服务端启动成功");
            }
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
