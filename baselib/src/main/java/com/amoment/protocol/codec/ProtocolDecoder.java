package com.amoment.protocol.codec;

public interface ProtocolEncoder {
    <T> T parseObject(Integer type, byte[] data, int length);
}
