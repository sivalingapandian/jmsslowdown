package jms.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.gxs.ics.j2ee.jms.dcts.SRPPostboxValueObject;
import com.gxs.ics.j2ee.threadmanagement.TGFeedQueueEvent;
import com.gxs.ics.util.AddressUtilities;


public class JMSLoader {
	
	public final static String JNDI_FACTORY="weblogic.jndi.WLInitialContextFactory";

	  // Defines the JMS context factory.
	 // public final static String JMS_FACTORY="weblogic.examples.jms.QueueConnectionFactory";

	  // Defines the queue.
	 // public final static String QUEUE="test.weblogic.jms.LoadQueue";

	  private QueueConnectionFactory qconFactory;
	  private QueueConnection qcon;
	  private QueueSession qsession;
	  private QueueSender qsender;
	  private Queue queue;
	  private ObjectMessage msg;

	  /**
	   * Creates all the necessary objects for sending
	   * messages to a JMS queue.
	   *
	   * @param ctx JNDI initial context
	   * @param queueName name of queue
	   * @exception NamingException if operation cannot be performed
	   * @exception JMSException if JMS fails to initialize due to internal error
	   */
	  public void init(Context ctx, String queueName, String queueJMSFactory)
	    throws NamingException, JMSException
	  {
	    qconFactory = (QueueConnectionFactory) ctx.lookup(queueJMSFactory);
	    qcon = qconFactory.createQueueConnection();
	    qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	    queue = (Queue) ctx.lookup(queueName);
	    qsender = qsession.createSender(queue);
	    qcon.start();
	  }

	  public void send(TGFeedQueueEvent message, long count, String status) throws JMSException {
	    msg = qsession.createObjectMessage();
	    msg.setObject(message);
		msg.setStringProperty("Status", status);
		msg.setIntProperty("Tries", 1);
		msg.setStringProperty("EventUID", java.util.UUID.randomUUID().toString());
		
		
		
		Date startTime = new Date();
		qsender.send(msg);
		Date endTime = new Date();
		long lapseTime = endTime.getTime() - startTime.getTime();
		System.out.println("[" + lapseTime + "]ms taken to place message [" + msg.getJMSMessageID() + "] [" + count + "] into queue");
	  }

	  /**
	   * Closes JMS objects.
	   * @exception JMSException if JMS fails to close objects due to internal error
	   */
	  public void close() throws JMSException {
	    qsender.close();
	    qsession.close();
	    qcon.close();
	  }
	 /** main() method.
	  *
	  * @param args WebLogic Server URL
	  * @exception Exception if operation fails
	  */
	  public static void main(String[] args) throws Exception {
		 
		  if( args.length < 6)
		  {
			  System.err.println("Usage: JMSLoader t3url numberofitems status queueconnectionfactory queuejndi AMQ");
			  System.err.println("Example: JMSLoader t3:\\localhost:7001 10 Pending weblogic.examples.jms.QueueConnectionFactory test.weblogic.jms.LoadQueue AMQ");
		  }
	   
	    int noOfItems = Integer.parseInt(args[1]);
	    String status = args[2];
	    String jmsFactory = args[3];
	    String queueName = args[4];
	    boolean activeMQ = "AMQ".equalsIgnoreCase(args[5]);
	    InitialContext ic = getInitialContext(activeMQ, args[0]);
	    JMSLoader qs = new JMSLoader();
	    qs.init(ic, queueName, jmsFactory);
	    
	    for(int i = 0; i < noOfItems; i++)
	    {
	    	qs.send(getTGEMessage(),i, status);
	    }
	    
	    qs.close();
	}
	  
	  private static InitialContext getInitialContext(boolean activeMQ, String url)
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
	  
	  private static TGFeedQueueEvent getTGEMessage() throws Exception
		{
				
				String serviceRef = "001210000003144132";
				
				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmssSS");
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				
				Date acceptDate = null;
				acceptDate = new Date();
				
				SRPPostboxValueObject evo = new SRPPostboxValueObject();
				evo.sType = "MB";
				evo.sVersion = "10";
				evo.sStatus = "0"  ;
				evo.sRemoteSessionID = serviceRef;
				cal.setTime( acceptDate );
				evo.sAcceptedDate = sdfDate.format(cal.getTime());
				evo.sAcceptedTime = sdfTime.format(cal.getTime());
				cal.setTime(new Date());
				evo.sDeliveryDate = sdfDate.format(cal.getTime());
				evo.sDeliveryTime = sdfTime.format(cal.getTime());
				evo.sCharCount = "1001" ;
				evo.sControlDate = "121212";
				evo.sControlTime = "000000";
				evo.sSNRF = "111111";
				evo.sCopyNo = "12000001";
				evo.sCorrelationID = "";
				evo.sDataType = "E";
				evo.sEnvelope = "ISA";
				evo.sFormatName = "T";
				evo.sICPosition = "1";
				evo.sILOG = "9042304406" ;
				evo.sInternalReceiverUserNumber = "12006202"; 
				evo.sInternalSenderUserNumber = "12006202"; 
				evo.sMFile = "";
				evo.sParentEventID = "";
				evo.sSegmentTerminator = "";
				cal.setTime( acceptDate );
				evo.setTimestamp(cal);
				
				evo.sSenderID = "AAA11202";
				
				evo.sSender = AddressUtilities.combineAddress("AAA11202", "");
				evo.sSessionID = "90418160489902";
				
				boolean isPropetry = false; // isMetaDataRecordAvailable(ic.getS_UserNo(), ic.getS_LogNo());
				
				evo.sAdditionalProps = isPropetry ? "Y" : "N";
				evo.sAPRF = "810";
				evo.sSNRF = "1111111";
				evo.sServiceReference = serviceRef;
				evo.sReceiver = AddressUtilities.combineAddress("AAA11202", "");
				
				evo.sReceiverID = "AAA11202";
				
				evo.sNode = "12" ;
				
				evo.sServiceInstanceID = "CSR" + " - " + "12" + " - Managed Server ID:" + 1;
				evo.sComponentInstanceID = "Session ID:" + "90418160489902";
				evo.sServerHost = "pandiantets";
				evo.sVersionMajor = "8";
				evo.sVersionMinor = "2";
				evo.sVersionBuild = "0";
				evo.sVersionRevision = "0";
				
				TGFeedQueueEvent event = new TGFeedQueueEvent(evo);
				
				
	            return event;
			
		}

}
