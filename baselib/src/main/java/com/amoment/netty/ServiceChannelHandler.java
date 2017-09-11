package com.amoment.netty;

import com.amoment.netty.core.SocketInbound;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.IOException;

public class ServiceChannelHandler extends ChannelInitializer<SocketChannel> {

    private static EventExecutorGroup businessInGroup = new DefaultEventExecutorGroup(16);
    private static EventExecutorGroup businessOutGroup = new DefaultEventExecutorGroup(16);

    public class InboundChannelHandlerInbound extends SimpleChannelInboundHandler implements SocketInbound {

        @Override
        public void read(Object message) throws IOException {

        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.executor().execute(() -> {
                try {
                    read(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(businessInGroup, new InboundChannelHandlerInbound());
    }
}
