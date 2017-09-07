package com.amoment.netty.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by xudeng on 2017/5/19.
 */
public abstract class SocketClient {

    private InetSocketAddress socketAddress = null;
    private ChannelHandler channelHandler = null;
    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup loopGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture = null;

    public SocketClient(final InetSocketAddress address, ChannelHandler handler) {

        socketAddress = address;
        channelHandler = handler;
    }

    public SocketClient(final String hostname, final int iPort, ChannelHandler handler) {
        this(new InetSocketAddress(hostname, iPort), handler);
    }

    public void start() throws Exception {
        try {
            bootstrap.group(loopGroup).channel(NioSocketChannel.class).handler(channelHandler);

            channelFuture = bootstrap.connect(socketAddress).sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()){
                    throw new Exception(String.format("connect to server by ip of %s and " +
                            "port of %d is failed", socketAddress.getHostName(), socketAddress.getPort()));
                }
            });
        } catch (Exception e){
            throw e;
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    SocketClient.this.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }
    }

    public void destroy() throws Exception {
        if (channelFuture != null) {
            channelFuture.channel().closeFuture().sync();
        }
        loopGroup.shutdownGracefully().sync();
    }
}
