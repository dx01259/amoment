package com.amoment.netty.core;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface SocketHandler {
    void read(ChannelHandlerContext ctx, byte[] message) throws IOException;
    void write(ChannelHandlerContext ctx, byte[] message) throws IOException;
    ChannelHandlerContext getChannelHandlerContext();
}
