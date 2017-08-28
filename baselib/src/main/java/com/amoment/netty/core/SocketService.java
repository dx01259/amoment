package com.amoment.netty.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;


/**
 * Created by xudeng on 2017/5/19.
 */
public abstract class SocketService {

    private InetSocketAddress socketAddress = null;
    private ChannelHandler channelHandler = null;
    private ChannelFuture channelFuture = null;
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private EventLoopGroup bossGroup= new NioEventLoopGroup();
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    public SocketService(final InetSocketAddress socketAddress, ChannelHandler handler) {
        channelHandler = handler;
        this.socketAddress = socketAddress;
    }

    public SocketService(final int iPort, ChannelHandler handler) {

        this(new InetSocketAddress(iPort), handler);
    }

    public void start() throws Exception {
        try {
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(channelHandler)
                    .option(ChannelOption.TCP_NODELAY, true);

            channelFuture = serverBootstrap.bind(socketAddress).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        throw new Exception(String.format("Bind the port of %d is failed.", socketAddress.getPort()));
                    }
                }
            });
        } catch (Exception e ) {
            throw e;
        } finally {
            if (channelFuture != null) {
                channelFuture.channel().closeFuture().sync();
            }
            bossGroup.shutdownGracefully().sync();
            workGroup.shutdownGracefully().sync();
        }
    }
}
