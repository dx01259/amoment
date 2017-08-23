package com.amoment.message.kafka.core;


import java.util.concurrent.CountDownLatch;

/**
 * Created by xudeng on 2017/6/29.
 */
public abstract class KafkaConsumer {

    public KafkaConsumer() {}

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }
}
