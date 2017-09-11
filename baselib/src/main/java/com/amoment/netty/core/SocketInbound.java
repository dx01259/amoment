package com.amoment.netty.core;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface SocketInbound {
    void read(Object message) throws IOException;
}
