package com.amoment.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * Created by xudeng on 2017/5/19.
 */
public abstract class SocketService implements SocketHandler {

    private ServerBootstrap m_serverBootstrap = new ServerBootstrap();
    private EventLoopGroup m_bossGroup= new NioEventLoopGroup();
    private EventLoopGroup m_workGroup = new NioEventLoopGroup();
    private ChannelFuture m_ChannelFuture = null;

    public SocketService(final InetSocketAddress localAddress, ChannelHandler handler) throws Exception {

        try {
            m_serverBootstrap.group(m_bossGroup, m_workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(handler)
                    .option(ChannelOption.TCP_NODELAY, true);

            m_ChannelFuture = m_serverBootstrap.bind(localAddress);
            m_ChannelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        throw new Exception(String.format("Bind the port of %d is failed.", localAddress.getPort()));
                    }
                }
            });
        } catch (Exception e ) {
            if (m_ChannelFuture != null) {
                m_ChannelFuture.channel().close();
            }
            throw new Exception(e.getMessage());
        }
    }

    public SocketService(final int iPort, ChannelHandler handler) throws Exception {

        this(new InetSocketAddress(iPort), handler);
    }
}
