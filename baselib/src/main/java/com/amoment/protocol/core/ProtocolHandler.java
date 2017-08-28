package com.amoment.protocol;

public interface ProtocolHandler {
    <T> T parseObject(Integer type, byte[] data);
}
