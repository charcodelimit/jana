package example.jana.classes;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records the Events it Listens to
 * 
 * @author chr
 *
 */
public class RecordingEventListenerExample extends EventListenerExample
{
	private final List<Event> eventRecord;
	
	public RecordingEventListenerExample(EventSource eventSource)
	{
		super(eventSource);
		
		try
		{
			Thread.sleep((long) Math.round(Math.random()*100));
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		eventRecord = Collections.synchronizedList(new ArrayList<Event>());
	}
	
	public void eventReceived(Event e) 
	{
		eventRecord.add(e);
		System.out.print(" " + this.eventRecord.size());
	}
	
	public Event[] getEvents() {
		return (Event[]) eventRecord.toArray(new Event[eventRecord.size()]);
	}
}
