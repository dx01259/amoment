package com.amoment.protocol.codec;

public interface ProtocolDecoder {
    <T> T parseObject(Integer type, byte[] data, int length);
}
