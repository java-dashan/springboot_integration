package com.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

public class Test {
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
                        pipeline.addLast(new HttpRequestDecoder());
                        pipeline.addLast(new HttpResponseEncoder());
                        pipeline.addLast(new MyChannelOutboundHandlerAdapter());
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
