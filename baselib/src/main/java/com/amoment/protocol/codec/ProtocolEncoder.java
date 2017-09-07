package com.amoment.protocol.codec;

public interface ProtocolEncoder<T> {
    byte[] toBytes(Integer type, T data);
}
