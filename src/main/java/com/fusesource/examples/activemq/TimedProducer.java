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
public class TimedProducer {
  private static final Log LOG = LogFactory.getLog(TimedProducer.class);
  private static final String CONNECTION_FACTORY_NAME = "myJmsFactory";
  private static final String DESTINATION_NAME = "queue/timed";
  private static final boolean NON_TRANSACTED = false;
  private static final long MESSAGE_TIME_TO_LIVE_MILLISECONDS = 0;
  private static final int NUM_MESSAGES_TO_BE_SENT = 100000;
  private static final long MESSAGE_DELAY_MILLISECONDS = 0;

  public static void main(String[] args) {
    Connection connection = null;
    try {
      Context context = new InitialContext();
      ConnectionFactory factory = (ConnectionFactory)context.lookup(CONNECTION_FACTORY_NAME);
      Destination destination = (Destination)context.lookup(DESTINATION_NAME);

      connection = factory.createConnection("admin", "admin");
      connection.start();

      Session session = connection.createSession(NON_TRANSACTED, Session.AUTO_ACKNOWLEDGE);
      MessageProducer producer = session.createProducer(destination);

      producer.setTimeToLive(MESSAGE_TIME_TO_LIVE_MILLISECONDS);
      int numMessagesSent = 0;
      long startTime = System.currentTimeMillis();
      String baseString = new String(new char[3 * 1024]).replace("\0", "-");

      for(int i = 1; i <= NUM_MESSAGES_TO_BE_SENT; i++) {
        TextMessage message = session.createTextMessage(i + ". message sent" + baseString);
        message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
        producer.send(message);
        numMessagesSent++;
        Thread.sleep(MESSAGE_DELAY_MILLISECONDS);
      }
      long endTime = System.currentTimeMillis();
      long diffTime = endTime - startTime;
      float msgPerSec = numMessagesSent / (diffTime / 1000);

      LOG.info("Start time: " + startTime);
      LOG.info("End time: " + endTime);
      LOG.info("Diff time: " + diffTime);
      LOG.info("Messages sent: " + numMessagesSent);
      LOG.info("Messages per second: " + msgPerSec);

      producer.close();
      session.close();
    } catch(NamingException | JMSException | InterruptedException e) {
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
