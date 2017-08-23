package com.amoment.message.activemq.core;

import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

/**
 * Created by xudeng on 2017/6/29.
 */
public abstract class ActiveMqCustomer extends ActiveMqObject implements MessageListener {

    private MessageConsumer consumer = null;

    public ActiveMqCustomer(String topic) throws Exception {
        super();
        Destination dest = session.createTopic(topic);
        consumer = session.createConsumer(dest);
        consumer.setMessageListener(this);
    }
}
