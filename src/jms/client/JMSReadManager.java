package jms.client;

public class JMSReadManager {
	
	public static void main(String[] args) throws InterruptedException
	{
		long startTime = System.currentTimeMillis();
		if( args.length < 6 )
		{
			System.err.println("Usage: JMSReadManager t3url noofpendingthread noofretrythread queuejndi connectionfactory AMQ");
			System.err.println("Example: JMSReadManager t3:\\localhost:7001 10 2 test.weblogic.jms.LoadQueue weblogic.examples.jms.QueueConnectionFactory AMQ");
		}
		String url = args[0];
		int noOfPendingThread = Integer.parseInt(args[1]);
		int noOfRetryThread = Integer.parseInt(args[2]);
		boolean activeMQ = "AMQ".equalsIgnoreCase(args[5]);
		
		JMSReadThread[] pendingThread = new JMSReadThread[noOfPendingThread];
		JMSReadThread[] retryThread = new JMSReadThread[noOfRetryThread];
		
		for(int i = 0; i < noOfPendingThread ; i++)
		{
			pendingThread[i] = new JMSReadThread(activeMQ, url,"(Status = 'Pending')", args[3], args[4], 600000L, i);
			pendingThread[i].setName("Pending Thread [" + i +"] " );
			pendingThread[i].start();
			Thread.sleep(2000L);
		}
		
		for(int i = 0; i < noOfRetryThread ; i++)
		{
			retryThread[i] = new JMSReadThread(activeMQ, url,"(Status = 'Retry')", args[3], args[4],600000L, i);
			retryThread[i].setName("Retry Thread [" + i +"] " );
			retryThread[i].start();
		}
		long count = 0;
		do
		 {
			 boolean alive = false;
			 for(int iLoop=0; iLoop<noOfPendingThread; iLoop++)
			 {
			     if( !pendingThread[iLoop].isComplete )
			     {
			    	 alive = true;
			    	 break;
			     }   
			 }
			 
			 if( !alive )
			 {
				 for(int iLoop=0; iLoop<noOfRetryThread; iLoop++)
				 {
				     if( !retryThread[iLoop].isComplete )
				     {
				    	 alive = true;
				    	 break;
				     }   
				 }
			 }
			 if( !alive )
		     {
		    	 break;
		     }
			 Thread.sleep(1000L);
			 count++;
			 long timeTaken = System.currentTimeMillis() - startTime;
			 if( count % 60 == 0)
			 {
				 System.err.println("System is running for [" + timeTaken + "] ms");
			 }
		 }while(true);
		
	}

}
