package com.jackie.jms.groupMessage;

import javax.jms.BytesMessage;
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
 * Title(文件名): JMSSender<p>
 * Description(描述):<p>
 * Copyright(版权): Copyright (c) 2016<p>
 * Company(公司): 成都四方伟业软件股份有限公司<p>
 * CreateTime(生成日期):2017/5/5
 *
 * @author SF-2171
 */
public class JMSSender {
    private QueueConnection connection = null;
    private QueueSession session = null;
    private QueueSender sender = null;

    public static void main(String[] args) {
        try {
            JMSSender app = new JMSSender();
            app.sendMessageGroup();
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JMSSender() {
        try {
//connect to the jms provider and create the
//connection, session, and sender
            Context ctx = new InitialContext();
            QueueConnectionFactory factory = (QueueConnectionFactory)
                    ctx.lookup("QueueCF");
            connection = factory.createQueueConnection();
            connection.start();
            session =
                    connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) ctx.lookup("queue1");
            sender = session.createSender(queue);
        } catch (Exception jmse) {
            jmse.printStackTrace();
        }
    }

    public void sendMessageGroup() throws JMSException {
//send the messages as a group
        sendSequenceMarker("START_SEQUENCE");
        sendMessage("First Message");
        sendMessage("Second Message");
        sendMessage("Third Message");
        sendSequenceMarker("END_SEQUENCE");
        connection.close();
    }

    //send a simple text message within the group of messages
    private void sendMessage(String text) throws JMSException {
        TextMessage msg = session.createTextMessage(text);
        msg.setStringProperty("JMSXGroupID", "GROUP1");
        sender.send(msg);
    }

    //send an empty payload message containing the sequence marker
    private void sendSequenceMarker(String marker) throws JMSException {
        BytesMessage msg = session.createBytesMessage();
        msg.setStringProperty("SequenceMarker", marker);
        msg.setStringProperty("JMSXGroupID", "GROUP1");
        sender.send(msg);
    }
}
