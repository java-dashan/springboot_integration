package com.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

public class MyChannelOutboundHandlerAdapter extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HttpRequest){
                FullHttpResponse response = new DefaultFullHttpResponse(
                        // 设置http版本为1.1
                        HttpVersion.HTTP_1_1,
                        // 设置响应状态码
                        HttpResponseStatus.OK,
                        // 将输出值写出 编码为UTF-8
                        Unpooled.wrappedBuffer("the first".getBytes("UTF-8")));
                response.headers().set("Content-Type", "text/html;");
                ctx.write(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ctx.flush();
            ctx.close();
        }

//        /**
//         * 给客户端回复消息
//         */
//        ctx.writeAndFlush("content-type: application/json; charset=utf-8\n" +
//                "date: Sun, 04 Oct 2020 10:06:42 GMT\n" +
//                "status: 200\n" +
//                "strict-transport-security: max-age=2592000; includeSubDomains; preload\n +"
//               );
    }

    /**
     * 连接成功后，自动执行该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器首次处理!");
        /**
         * 这种发送的消息格式是错误的!!!!!
         * 消息格式必须是ByteBuf才行!!!!!
         */
        ctx.writeAndFlush("Hello is server !");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        /**
         * 异常捕获
         */
        cause.printStackTrace();
        ctx.close();
    }
}
