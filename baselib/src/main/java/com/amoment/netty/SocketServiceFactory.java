package com.amoment.netty;

import com.amoment.netty.core.SocketService;
import com.amoment.netty.core.SocketServiceHandler;
import io.netty.channel.ChannelHandler;

import java.net.InetSocketAddress;

public class SocketServiceFactory {

    private SocketServiceHandler socketServiceHandler;

    private SocketServiceFactory() {
        socketServiceHandler = new SocketServiceHandler();
    }

    public static SocketServiceFactory newInstance() {
        return new SocketServiceFactory();
    }

    class SocketServiceImpl extends SocketService {

        public SocketServiceImpl(InetSocketAddress localAddress, ChannelHandler handler) {
            super(localAddress, handler);
        }

        public SocketServiceImpl(int iPort, ChannelHandler handler) {
            super(iPort, handler);
        }
    }

    public SocketService create(final InetSocketAddress localAddress) {

        return new SocketServiceImpl(localAddress, socketServiceHandler);
    }

    public SocketService create(final int iPort) {
        return new SocketServiceImpl(iPort, socketServiceHandler);
    }
}
