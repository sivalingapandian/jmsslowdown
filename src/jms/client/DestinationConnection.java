package jms.client;

import java.util.Hashtable;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

class DestinationConnection
{
	
	
	public DestinationConnection(boolean activeMQ, String url, String sQueueConnectionFactory)
	throws Exception
	{
		ctx = getInitialContext(activeMQ, url);
		System.out.println("Creating Queue Connection Factory: " + sQueueConnectionFactory);
		qconFactory = (QueueConnectionFactory) ctx.lookup(sQueueConnectionFactory);
		
	}

	// call this method to close temporary connections
	protected void closeDestinationConnection()
	throws Exception
	{
		System.out.println("Closing queue connection");
		if (qreceiver != null)
			qreceiver.close();
		if (qsession != null)
			qsession.close();
		if (qcon != null)
			qcon.close();
		
		qreceiver = null;
		qsession = null;
		qcon = null;
		queue = null;
	}

	// call this method to create a receiver
	protected void createReceiver( String sQueueJndiPath, String sMessageSelector)
	throws Exception 
	{
		qcon = qconFactory.createQueueConnection();
		qsession = qcon.createQueueSession(/*false*/true, Session.AUTO_ACKNOWLEDGE);
		queue = (Queue) ctx.lookup(sQueueJndiPath);
		System.out.println("Created queue : " + queue);
		System.out.println("Created queue session : " + qsession);
		if (sMessageSelector != null)
			qreceiver = qsession.createReceiver(queue, sMessageSelector);
		else
			qreceiver = qsession.createReceiver(queue);			
	}
	
	private InitialContext getInitialContext(boolean activeMQ, String url)
	    throws NamingException
	  {
		if(activeMQ)
		  {
			  Hashtable<String,String> env = new Hashtable<String,String>();
			    env.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			    env.put(Context.PROVIDER_URL, url);
			    env.put("queue.com.gxs.tgms.pandian.TestQueue", "com.gxs.tgms.pandian.TestQueue");
			    env.put("QueueJNDI", "com.gxs.tgms.pandian.TestQueue");
			    env.put("QueueConnectionFactory", "ConnectionFactory");
			    return new InitialContext(env);
		  }
		  else
		  {
			System.out.println("Connecting : " + url);
		    Hashtable<String,String> env = new Hashtable<String,String>();
		    env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		    env.put(Context.PROVIDER_URL, url);
		    return new InitialContext(env);
		  }
	  }
	
	protected QueueConnection getQueueConnection()
	{
		return qcon;
	}
	
	
	protected QueueReceiver getQueueReceiver()
	{
		return qreceiver;
	}
	
	protected void setQueueReceiver(QueueReceiver qreceiver)
	{
		this.qreceiver = qreceiver;
	}
	
	protected QueueSession getQueueSession()
	{
		return qsession;
	}
	
	protected Queue getQueue()
	{
		return queue;
	}

	public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";
	
	private InitialContext ctx;
	private QueueConnectionFactory qconFactory;
	private QueueConnection qcon;
	private Queue queue;
	private QueueSession qsession;
	private QueueReceiver qreceiver;
}