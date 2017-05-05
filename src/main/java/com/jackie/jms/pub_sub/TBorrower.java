package com.jackie.jms.pub_sub;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Title(文件名): TBorrower<p>
 * Description(描述):<p>
 * Copyright(版权): Copyright (c) 2016<p>
 * Company(公司): 成都四方伟业软件股份有限公司<p>
 * CreateTime(生成日期):2017/5/4
 *
 * @author SF-2171
 */
public class TBorrower implements MessageListener {
    private TopicConnection tConnect = null;
    private TopicSession tSession = null;
    private Topic topic = null;
    private double currentRate;

    public TBorrower(String topiccf, String topicName, String rate) {
        try {
            currentRate = Double.valueOf(rate);
// Connect to the provider and get the JMS connection
            Context ctx = new InitialContext();
            TopicConnectionFactory tFactory = (TopicConnectionFactory)
                    ctx.lookup(topiccf);
            tConnect = tFactory.createTopicConnection();
// Create the JMS Session
            tSession = tConnect.createTopicSession(
                    false, Session.AUTO_ACKNOWLEDGE);
// Lookup the request and response queues
            topic = (Topic) ctx.lookup(topicName);
// Create the message listener
            String filter = "newRate<=" + (currentRate - 1.0);
            TopicSubscriber subscriber = tSession.createSubscriber(topic, filter, true);
            subscriber.setMessageListener(this);
// Now that setup is complete, start the Connection
            tConnect.start();
            System.out.println("Waiting for loan requests...");
        } catch (JMSException jmse) {
            jmse.printStackTrace();
            System.exit(1);
        } catch (NamingException jne) {
            jne.printStackTrace();
            System.exit(1);
        }
    }

    public void onMessage(Message message) {
        BytesMessage msg = (BytesMessage) message;
        double newRate = 0;
        try {
            newRate = msg.readDouble();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("New rate = " + newRate + " - Consider refinancing loan");
    }

    private void exit() {
        try {
            tConnect.close();
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String argv[]) {
        String topiccf = null;
        String topicName = null;
        String rate = null;
        if (argv.length == 3) {
            topiccf = argv[0];
            topicName = argv[1];
            rate = argv[2];
        } else {
            System.out.println("Invalid arguments. Should be: ");
            System.out.println("java TBorrower factory topic rate");
            System.exit(0);
        }
        TBorrower borrower = new TBorrower(topiccf, topicName, rate);
        try {
// Run until enter is pressed
            BufferedReader stdin = new BufferedReader
                    (new InputStreamReader(System.in));
            System.out.println("TBorrower application started");
            System.out.println("Press enter to quit application");
            stdin.readLine();
            borrower.exit();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
