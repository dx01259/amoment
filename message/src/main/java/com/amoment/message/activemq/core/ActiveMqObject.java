package com.amoment.message.activemq.core;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Created by xudeng on 2017/6/29.
 */
public abstract class ActiveMqObject {

    protected ConnectionFactory connectionFactory = null;
    protected Connection connection = null;
    protected Session session = null;

    public ActiveMqObject() throws JMSException {
        try {
            connectionFactory = new ActiveMQConnectionFactory();
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }catch (JMSException e) {
            e.printStackTrace();
            if (connection != null) {
                connection.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
