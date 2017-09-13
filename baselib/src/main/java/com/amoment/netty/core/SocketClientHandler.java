package com.amoment.netty.core;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


public class SocketClientHandler extends ChannelInitializer<SocketChannel> implements SocketHandler {
    @Override
    public void addChannelHandler(ChannelHandler handler) {

    }

    @Override
    public void deleteChannelHandler(ChannelHandler handler) {

    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

    }
}
