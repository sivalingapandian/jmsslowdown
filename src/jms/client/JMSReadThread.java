package jms.client;

import java.util.Date;

import javax.jms.Message;

public class JMSReadThread extends Thread{
	
	DestinationConnection dc = null;
	public String sQueueJndiPath = null; 
	public String connectionFactory = null;
	String messageSelector = null;
	public boolean shutDown = false;
	public boolean isComplete = false;
	boolean activeMQ = false;
	long selectorTimeOut = 60000l;
	long threadNumber = -1;
	
	public String url = null;
	
	public JMSReadThread(boolean activeMQ, String url, String messageSelector, String sQueueJndiPath, String connectionFactory, long selectorTimeOut, int threadNumber)
	{
		this.url = url;
		this.messageSelector = messageSelector; 
		this.sQueueJndiPath = sQueueJndiPath;
		this.connectionFactory = connectionFactory;
		this.activeMQ = activeMQ;
		this.selectorTimeOut = selectorTimeOut;
		this.threadNumber = threadNumber;
	}
			
	
	public void run()
	{
		boolean bLoop = true;
		while (bLoop)
		{
			try
			{
				openConnection();
				receiveMessages();
				bLoop = false;
			}
			catch (Throwable t)
			{
				t.printStackTrace();
			}
			finally
			{
				try{
					dc.getQueueConnection().stop();
					dc.closeDestinationConnection();
				}catch(Exception ignore){}
				isComplete = true;
			}
		}		
	}
	
	protected void openConnection()
	throws Exception
	{
		dc = new DestinationConnection(activeMQ, url, connectionFactory);
		dc.createReceiver(sQueueJndiPath, messageSelector);		
		dc.getQueueConnection().start();
	}
	
	protected void receiveMessages() 
	throws Exception 
	{
		try 
		{
			int messageCount = 0;
			// loop until explicit break
			while (true) 
			{
				
				if(shutDown)
					break;

				
				try
				{
						
					
					Date startTime = new Date();
					Message msg = dc.getQueueReceiver().receive(selectorTimeOut);
					Date endTime = new Date();
					long lapseTime = endTime.getTime() - startTime.getTime();
					commitTransaction();
					if(msg == null)
					{
						sleep(1000);
						break;
					}
					if(lapseTime >= 1000l)
					{
						System.out.println(this.getName() + " [" + lapseTime + "]ms to receive message [" + msg.getStringProperty("EventUID") +"] by message selector [" + messageSelector + "] Thread " + currentThread().getName() );
					}
					
					messageCount++;
					
					System.out.println(this.getName() + " [" + lapseTime + "]ms to receive message [" + msg.getJMSMessageID() + "] [" + msg.getStringProperty("EventUID") +"] by message selector [" + messageSelector + "] Thread " + currentThread().getName() );
					/*
					if(threadNumber == 0 && messageCount==1)
					{
						long sleepTime = 60000;
						System.err.println(this.getName() + " Sleeping for [" + sleepTime +"]" );
						Thread.sleep(sleepTime);
					}
					*/
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw e;
				}

				
			}
		} 
		catch (Exception e) 
		{
			throw e;
		}
	}
	
	private boolean commitTransaction()
	throws Exception
	{
		try
		{
			dc.getQueueSession().commit();
			
			return true;
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	
	
}
