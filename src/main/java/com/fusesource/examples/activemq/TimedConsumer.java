package com.fusesource.examples.activemq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by bashburn on 12/14/15.
 */
public class TimedConsumer {
  private static final Log LOG = LogFactory.getLog(SimpleConsumer.class);

  private static final Boolean NON_TRANSACTED = false;
  private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
  private static final String DESTINATION_NAME = "queue/timed";
  private static final int MESSAGE_TIMEOUT_MILLISECONDS = 30000;

  public static void main(String args[]) {
    Connection connection = null;

    try {
      Context context = new InitialContext();
      ConnectionFactory factory = (ConnectionFactory)context.lookup(CONNECTION_FACTORY_NAME);
      Destination destination = (Destination)context.lookup(DESTINATION_NAME);

      connection = factory.createConnection("admin", "admin");
      connection.start();

      Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
      MessageConsumer consumer = session.createConsumer(destination);

      LOG.info("Start consuming messages");
      long startTime = System.currentTimeMillis();
      long messageCount = 0;
      long endTime = System.currentTimeMillis();

      while(true) {
        Message message = consumer.receive(MESSAGE_TIMEOUT_MILLISECONDS);
        if(message != null) {
          messageCount++;
          endTime = System.currentTimeMillis();
        } else {
          break;
        }
      }
      long diffTime = endTime - startTime;
      float msgPerSec = messageCount / (diffTime / 1000);

      LOG.info("Final messages per second: " + msgPerSec);

      //consumer.close();
      session.close();
    } catch(NamingException | JMSException e) {
      LOG.error(e);
    } finally {
      if(connection != null) {
        try {
          connection.close();
        } catch(JMSException e) {
          LOG.error(e);
        }
      }
    }
  }
}
