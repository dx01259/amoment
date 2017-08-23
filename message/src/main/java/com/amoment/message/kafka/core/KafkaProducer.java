package com.amoment.message.kafka.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Created by xudeng on 2017/6/29.
 */
@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Async
    public void sendMessage(String topic, String message) {

        ListenableFuture<SendResult<String, String> > future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(SendResult<String, String> objectObjectSendResult) {

                System.out.println(String.format("send topic of %s and value is %s to kafka success.",
                        objectObjectSendResult.getProducerRecord().topic(),
                        objectObjectSendResult.getProducerRecord().value()));
            }
        });
    }
}
