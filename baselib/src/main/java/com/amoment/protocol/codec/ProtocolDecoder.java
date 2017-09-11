package com.amoment.protocol.codec;

import com.amoment.netty.core.SocketInbound;

public interface ProtocolDecoder extends SocketInbound {
    <T> T parseObject(Integer type, byte[] data, int length);
}
