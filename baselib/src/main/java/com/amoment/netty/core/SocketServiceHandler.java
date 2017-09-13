package com.amoment.netty.core;

import com.amoment.protocol.core.ProtocolFactory;
import com.amoment.protocol.codec.ProtocolDecoder;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SocketServiceHandler extends ChannelInitializer<SocketChannel> implements SocketHandler{

    private Queue<ChannelHandler> channelHandlers = new ConcurrentLinkedQueue<>();

    class ServiceChannelDecoder extends ByteToMessageDecoder {

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
            } else if (dataLength > PackageConfig.MAX_DATA_LENGTH) {
                in.skipBytes(in.readableBytes());
                throw new TooLongFrameException("the length of data body is too big");
            }
            if (in.readableBytes() < dataLength) {
                in.resetReaderIndex();
                return;
            }
            int iProtoType = in.readInt();
            int iBodyLength = dataLength - PackageConfig.PROTOCOL_TYPE_LENGTH;
            ProtocolFactory.ProtocolObject object = ProtocolFactory.instance().getProtocolMap().get(iProtoType);
            ByteBuf byteBuf = in.readBytes(iBodyLength);
            if (byteBuf.hasArray()) {
                out.add(((ProtocolDecoder)Class.forName(object.impl).newInstance()).
                        parseObject(iProtoType, byteBuf.array(), byteBuf.readableBytes()));
            } else {
                out.add(((ProtocolDecoder)Class.forName(object.impl).newInstance()).
                        parseObject(iProtoType, ByteBufUtil.getBytes(byteBuf), byteBuf.readableBytes()));
            }
            ctx.fireChannelRead(out);
            byteBuf.release();
        }
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if (channelHandlers.isEmpty()) {
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(PackageConfig.MAX_DATA_LENGTH, 0, PackageConfig.HEAD_LENGTH));
            ch.pipeline().addLast(new ServiceChannelDecoder());
        } else {
            for (ChannelHandler handler : channelHandlers
                    ) {
                ch.pipeline().addLast(handler);
            }
        }
        ch.pipeline().addLast(new ServiceChannelHandler());
    }

    @Override
    public void addChannelHandler(ChannelHandler handler) {
        channelHandlers.add(handler);
    }

    @Override
    public void deleteChannelHandler(ChannelHandler handler) {
        channelHandlers.remove(handler);
    }
}
