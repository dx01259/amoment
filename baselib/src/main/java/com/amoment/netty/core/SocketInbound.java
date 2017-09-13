package com.amoment.netty.core;


import java.io.IOException;

public interface SocketInbound {
    void read(Object message) throws IOException;
}
