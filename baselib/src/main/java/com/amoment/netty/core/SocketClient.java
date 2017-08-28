package com.amoment.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by xudeng on 2017/5/19.
 */
public abstract class SocketClient implements SocketHandler {

    private Bootstrap m_Bootstrap = new Bootstrap();
    private EventLoopGroup m_EventLoopGroup = new NioEventLoopGroup();
    private ChannelFuture m_ChannelFuture = null;

    public SocketClient(final InetSocketAddress address, ChannelHandler handler) throws Exception {

        try {
            m_Bootstrap.group(m_EventLoopGroup).channel(NioSocketChannel.class).handler(handler);

            m_ChannelFuture = m_Bootstrap.connect(address);
            m_ChannelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()){
                        throw new Exception(String.format("connect to server by ip of %s and " +
                                "port of %d is failed", address.getHostName(), address.getPort()));
                    }
                }
            });
        } catch (Exception e){
            if (m_ChannelFuture != null) {
                m_ChannelFuture.channel().close();
            }
            throw new Exception(e.getMessage());
        }
    }

    public SocketClient(final String hostname, final int iPort, ChannelHandler handler) throws Exception {
        this(new InetSocketAddress(hostname, iPort), handler);
    }
}
