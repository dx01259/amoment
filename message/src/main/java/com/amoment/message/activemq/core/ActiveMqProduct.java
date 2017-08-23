package com.amoment.message.activemq.core;

import javax.jms.*;

/**
 * Created by xudeng on 2017/6/29.
 */
public class ActiveMqProduct extends ActiveMqObject {

    protected MessageProducer producer = null;

    public ActiveMqProduct(String topic) throws Exception{
        super();
        Destination dest = session.createTopic(topic);
        producer = session.createProducer(dest);
    }

    public void SendMessage(final String message) throws JMSException {
        Message msg = session.createTextMessage(message);
        producer.send(msg);
    }
}
