package jms.client;

import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.util.StringTokenizer;
import javax.jms.Message;

import com.gxs.ics.j2ee.jms.BaseQueueEvent;

public class EventQueueRelayQueueEvent extends BaseQueueEvent {

	static final long serialVersionUID = 2003070200000000000L;	

	public EventQueueRelayQueueEvent(String jmsMsg, Map mapObjects) {
		super(jmsMsg);
		
		this.mapObjects = mapObjects;
	}

	public ArrayList displayNames() {
		ArrayList arrListNames = new ArrayList();
		String dispNames = null;
		
		try {
			dispNames = jmsMsg.getStringProperty("DisplayNames");
		}
		catch (Exception e) {
			System.err.println("Failed to get display names in JMSEventQueueRelayQueueEvent");
			return arrListNames;
		}
		
		StringTokenizer st = new StringTokenizer(dispNames, ",");
		while (st.hasMoreTokens())
			arrListNames.add(st.nextToken());
		return arrListNames;
	}

	public ArrayList displayValues() {
		ArrayList arrListVals = new ArrayList();
		String dispVals = null;
		
		try {
			dispVals = jmsMsg.getStringProperty("DisplayValues");
		}
		catch (Exception e) {
			System.err.println("Failed to get display values in JMSEventQueueRelayQueueEvent");
			return arrListVals;
		}
		
		StringTokenizer st = new StringTokenizer(dispVals, ",");
		while (st.hasMoreTokens())
			arrListVals.add(st.nextToken());
		return arrListVals;
	}

	public void setObjects(Map mapObjects) {
		this.mapObjects = mapObjects;
	}

	public Map getObjects() {
		return mapObjects;
	}

	private javax.jms.Message jmsMsg = null;
	private Map mapObjects = null;
	
}
