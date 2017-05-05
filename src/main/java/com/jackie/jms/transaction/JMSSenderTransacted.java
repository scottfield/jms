package com.jackie.jms.transaction;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Title(文件名): JMSSenderTransacted<p>
 * Description(描述):<p>
 * Copyright(版权): Copyright (c) 2016<p>
 * Company(公司): 成都四方伟业软件股份有限公司<p>
 * CreateTime(生成日期):2017/5/5
 *
 * @author SF-2171
 */
public class JMSSenderTransacted {
    private QueueConnection connection = null;
    private QueueSession session = null;
    private QueueSender sender = null;

    public void sendMessages() {
        try {
//send the messages in a transaction
            System.out.println("Session Transacted: " + session.getTransacted());
            sendMessage("First Message");
            sendMessage("Second Message");
            sendMessage("Third Message");
            session.commit();
            connection.close();
        } catch (Exception ex) {
            try {
                System.out.println("Exception caught, rolling back session");
                session.rollback();
            } catch (JMSException jmse) {
                jmse.printStackTrace();
            }
        }
    }

    private void sendMessage(String text) throws Exception {
//send a simple text message within the group of messages
        TextMessage msg = session.createTextMessage(text);
        sender.send(msg);
    }

    public static void main(String[] args) {
        try {
            JMSSenderTransacted app = new JMSSenderTransacted();
            app.sendMessages();
            System.exit(0);
        } catch (Exception up) {
            up.printStackTrace();
        }
    }

    public JMSSenderTransacted() {
        try {
//create the connection, session, and sender
            Context ctx = new InitialContext();
            QueueConnectionFactory factory = (QueueConnectionFactory)
                    ctx.lookup("QueueCF");
            connection = factory.createQueueConnection();
            connection.start();
            session =
                    connection.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) ctx.lookup("queue1");
            sender = session.createSender(queue);
        } catch (Exception jmse) {
            jmse.printStackTrace();
            System.exit(0);
        }
    }
}
