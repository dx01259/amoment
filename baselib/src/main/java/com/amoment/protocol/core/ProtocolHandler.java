package com.amoment.protocol.core;

public interface ProtocolHandler {
    <T> T parseObject(Integer type, byte[] data);
    String toString(Object value);
}
