package example.jana.classes;

import java.awt.Event;

public class EventListenerExample
{
	public EventListenerExample(EventSource eventSource)
	{
		eventSource.registerListener(
				new MyEventListener() 
				{
					public void onEvent(Event e)
					{
						eventReceived(e);
					}
				}
				);
	}
	
	public void eventReceived(Event e)
	{
		System.out.println("Received Event! " + e.toString());
	}
}
