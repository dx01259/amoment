package com.amoment.netty.core;


import io.netty.channel.ChannelHandler;

public interface SocketHandler {
    void addChannelHandler(ChannelHandler handler);
    void deleteChannelHandler(ChannelHandler handler);
}
