package com.jackie.jms.p2p;

import javax.jms.ConnectionMetaData;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Enumeration;

/**
 * Title(文件名): MetaData<p>
 * Description(描述):<p>
 * Copyright(版权): Copyright (c) 2016<p>
 * Company(公司): 成都四方伟业软件股份有限公司<p>
 * CreateTime(生成日期):2017/5/4
 *
 * @author SF-2171
 */
public class MetaData {
    public static void main(String[] args) {
        try {
            Context ctx = new InitialContext();
            QueueConnectionFactory qFactory = (QueueConnectionFactory)
                    ctx.lookup("QueueCF");
            QueueConnection qConnect = qFactory.createQueueConnection();
            ConnectionMetaData metadata = qConnect.getMetaData();
            System.out.println("JMS Version: " +
                    metadata.getJMSMajorVersion() + "." +
                    metadata.getJMSMinorVersion());
            System.out.println("JMS Provider: " +
                    metadata.getJMSProviderName());
            System.out.println("JMSX Properties Supported: ");
            Enumeration e = metadata.getJMSXPropertyNames();
            while (e.hasMoreElements()) {
                System.out.println(" " + e.nextElement());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
