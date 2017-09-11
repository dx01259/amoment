package com.amoment.netty.core;

import java.io.IOException;

public interface SocketOutbound {
    void write(final byte[] message, final long size) throws IOException;
}
