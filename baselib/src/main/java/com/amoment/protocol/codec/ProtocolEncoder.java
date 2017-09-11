package com.amoment.protocol.codec;

import com.amoment.netty.core.SocketOutbound;

public interface ProtocolEncoder<T> extends SocketOutbound {
    byte[] toBytes(Integer type, T data);
}
