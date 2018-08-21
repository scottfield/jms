package com.jackie.jms.groupMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Created by jackie on 5/4/2017.
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
        int i = 0;
        sendSequenceMarker("START_SEQUENCE", i++);
        sendMessage("First Message", i++);
        sendMessage("Second Message", i++);
        sendMessage("Third Message", i++);
        sendSequenceMarker("END_SEQUENCE", i++);
        connection.close();
    }

    //send a simple text message within the group of messages
    private void sendMessage(String text, Integer sequence) throws JMSException {
        TextMessage msg = session.createTextMessage(text);
        msg.setStringProperty("JMSXGroupID", "GROUP1");
        msg.setIntProperty("JMSXGroupSeq", sequence);
        sender.send(msg);
    }

    //send an empty payload message containing the sequence marker
    private void sendSequenceMarker(String marker, Integer sequence) throws JMSException {
        BytesMessage msg = session.createBytesMessage();
        msg.setStringProperty("SequenceMarker", marker);
        msg.setStringProperty("JMSXGroupID", "GROUP1");
        msg.setIntProperty("JMSXGroupSeq", sequence);
        sender.send(msg);
    }
}
