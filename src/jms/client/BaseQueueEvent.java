/*
 * BaseQueueEvent.java
 *
 * Created on April 25, 2003, 8:32 AM
 */

package jms.client;

import java.util.Date;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.gxs.ics.util.IDGenerator;

/**
 *
 * @author  moyersd
 */
public abstract class BaseQueueEvent extends Object implements  Serializable
{
	static final long serialVersionUID = 2003070200000000000L;
	
	public static final byte C_STATUS_PENDING = 0x00;
	public static final byte C_STATUS_ACTIVE = 0x01;
	public static final byte C_STATUS_RETRY = 0x02;
	public static final byte C_STATUS_ACTIVE_RETRY = 0x03;
	public static final byte C_STATUS_SUSPEND = 0x04;
	// hold status is for events which have realized the maximum number of retries
	// and should be placed on "hold" until a resolution occurs
	public static final byte C_STATUS_HOLD = 0x05;
	// this status is for events that are sent to remote services who want
	// to delay response until after they have performed their work
	public static final byte C_STATUS_PENDING_REMOTE_SERVICE = 0x10;
	
	public static final String C_STATUS_PENDING_STRING = "Pending";
	public static final String C_STATUS_ACTIVE_STRING = "Active";	
	public static final String C_STATUS_RETRY_STRING = "Retry";	
	public static final String C_STATUS_ACTIVE_RETRY_STRING = "Active Retry";
	public static final String C_STATUS_SUSPEND_STRING = "Suspend";
	public static final String C_STATUS_HOLD_STRING = "Hold";
	public static final String C_STATUS_PENDING_REMOTE_SERVICE_STRING = "Pending Remote";
	
	
	// default no args CTor
	public BaseQueueEvent()
	{
		super();
		// assign identifier uniquely identifying this event in the cluster
		assignEventUID();
		// keep track of when the event was originated
		dtCreated = new Date();
		byteStatus = C_STATUS_PENDING;
	}
	
	/** Creates a new instance of BaseQueueEvent */
	public BaseQueueEvent(Object o) 
	{
		// assign identifier uniquely identifying this event in the cluster
		assignEventUID();
		// keep track of when the event was originated
		dtCreated = new Date();
		byteStatus = C_STATUS_PENDING;
			// see if we can clone the object rather than referencing it
							// could not clone it
				serializableObject = o;
			
		
	}
	
	public Object getSerializableObject()
	{
		return serializableObject;
	}
	
	public void setForRetry(boolean bRetry)
	{
		this.bRetry = bRetry;
	}
	
	public void setPersistenceMoniker(String sMoniker)
	{
		this.sMoniker = sMoniker;
	}
	
	public String getPersistenceMoniker()
	{
		return sMoniker;
	}
	
	public byte getStatus()
	{
		return byteStatus;
	}

	public void setStatus(byte byteVal)
	{
		this.byteStatus = byteVal;
	}

	public Date getCreationDate()
	{
		return dtCreated;
	}

	public void setCreationDate(Date dt)
	{
		this.dtCreated = dt;
	}


	public String getStatusDescription()
	{
		switch (byteStatus)
		{
			case	C_STATUS_PENDING:
				return C_STATUS_PENDING_STRING;
				
			case	C_STATUS_ACTIVE:
				return C_STATUS_ACTIVE_STRING;
				
			case	C_STATUS_RETRY:
				return C_STATUS_RETRY_STRING;
				
			case	C_STATUS_ACTIVE_RETRY:
				return C_STATUS_ACTIVE_RETRY_STRING;
				
			case	C_STATUS_SUSPEND:
				return C_STATUS_SUSPEND_STRING;
				
			case	C_STATUS_HOLD:
				return C_STATUS_HOLD_STRING;

			case	C_STATUS_PENDING_REMOTE_SERVICE:
				return C_STATUS_PENDING_REMOTE_SERVICE_STRING;
				
			default:
				return "Unknown";
		}
	}
	
	public String getEventUID()
	{
		return sEventUID;
	}
	
	protected void assignEventUID()
	{
		long lIP = 127000000001L;
		
		InetAddress hostaddr = null;
		
		// re-use the IDGenerator getSessionID method to assign a cluster wide unique event ID		
		try
		{
			hostaddr = InetAddress.getLocalHost();
			lIP = convertIP(hostaddr.getHostAddress());
		}
		catch (UnknownHostException uhe)
		{
			// eat it
		}
		
		// make "long" out of IP address
		String sIPBase32 = Long.toString(lIP, 32);
		String tmp = Long.toString(IDGenerator.getSessionID(), 32);
		
		sEventUID = sIPBase32 + tmp;
		
		if (sEventUID.length() > 15)
		{
			System.err.println("Warning: truncating EventUID from: " + sEventUID);
			sEventUID = sEventUID.substring(sEventUID.length() - 15, sEventUID.length());
		}
	}
	
	public void incrementRetryCount()
	{
		nRetryCount ++;
	}
	
	public short getRetryCount()
	{
		return nRetryCount;
	}
	
	private long convertIP(String ipaddress)
	{
		if (ipaddress == null || ipaddress.length() == 0)
			return 0;
		char c = 0x2E;
		
		ipaddress = ipaddress.replace('.', 'X');
		String[] sarrDigits = ipaddress.split("X");
		
		long lVal1 = Integer.parseInt(sarrDigits[1]);
		long lVal0 = Integer.parseInt(sarrDigits[0]);
		
		lVal1 *= 1000L;
		return lVal0 + lVal1;
	}
	
	protected Date dtCreated;	
	protected byte byteStatus;
	protected Object serializableObject;
	protected boolean bRetry = false;
	protected String sMoniker = null;
	protected String sEventUID = null;
	protected short nRetryCount = 0;
	// Bug 10095
	protected long lRetryPeriod = 0L;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" dtCreated: " + dtCreated);
		sb.append(" byteStatus: " + byteStatus);
		sb.append(" serializableObject: " + serializableObject);
		sb.append(" bRetry: " + bRetry);
		sb.append(" sMoniker: " + sMoniker);
		sb.append(" sEventUID: " + sEventUID);
		sb.append(" nRetryCount: " + nRetryCount);
		sb.append(" lRetryPeriod: " + lRetryPeriod);
		sb.append(" Class Name: " + this.getClass().getName()  );
				return sb.toString();
	}
	
	// Bug 10095
	public Date getDtCreated(){
		return dtCreated;
	}
	public void setRetryPeriod(long retry){
		lRetryPeriod=retry;
	}
	public long getRetryPeriod(){
		return lRetryPeriod;
	}
}
