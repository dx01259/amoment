package com.amoment.netty.core;

import com.amoment.protocol.core.ProtocolFactory;
import com.amoment.protocol.core.ProtocolHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketServiceHandler extends ChannelInitializer<SocketChannel> {

    private Queue<ChannelHandler> channelHandlers = new ConcurrentLinkedQueue<>();

    class ServiceChannelHandler extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            if (in.readableBytes() < PackageConfig.HEAD_LENGTH) {
                return;
            }
            in.markReaderIndex();

            int dataLength = in.readInt();
            if (dataLength <= 0) {
                ctx.close();
                return;
            }
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            int iProtoType = in.readInt();
            int iBodyLength = dataLength - PackageConfig.PROTOCOL_TYPE_LENGTH;
            ProtocolFactory.ProtocolObject object = ProtocolFactory.instance().getProtocolMap().get(iProtoType);
            ByteBuf byteBuf = in.readBytes(iBodyLength);
            out.add(((ProtocolHandler)Class.forName(object.impl).newInstance()).parseObject(iProtoType, byteBuf.array()));
            ctx.fireChannelRead(out);
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if (channelHandlers.isEmpty()) {
            ch.pipeline().addLast(new ServiceChannelHandler());
        } else {
            for (ChannelHandler handler : channelHandlers
                    ) {
                ch.pipeline().addLast(handler);
            }
        }
    }

    public void addChannelHandler(ChannelHandler handler) {
        channelHandlers.add(handler);
    }

    public void deleteChannelHandler(ChannelHandler handler) {
        channelHandlers.remove(handler);
    }
}
