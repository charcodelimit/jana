package example.jana.classes;

import java.awt.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventSource implements Runnable
{
	private final List<MyEventListener> listeners;
	
	public EventSource()
	{
		this.listeners = Collections.synchronizedList(new ArrayList<MyEventListener>());
	}
	
	public void registerListener(MyEventListener listener)
	{
		synchronized(this)
		{
			this.listeners.add(listener);
		}
		
		System.out.println("Added a listener! " + listener.toString());
	}
	
	public synchronized void publishEvent()
	{
		Event e = new Event(this,Event.ACTION_EVENT,null);
		
		for(MyEventListener eventListener : this.listeners)
			eventListener.onEvent(e);
	}
	
	public void run()
	{
		for(int i = 0; i < 100; i++)
		{
			try
			{
				Thread.sleep((long) Math.round(Math.random()*1000));
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
			}
			
			publishEvent();
		}
	}
	
	public static void main(String[] args)
	{
		EventSource source;
		List<EventListenerExample> listeners;
		
		source = new EventSource();
		
		Thread t = new Thread(source);
		t.start();
		
		listeners = new ArrayList<EventListenerExample>();
		
		for(int i = 0; i < 64; i++)
		{
			try
			{
				Thread.sleep((long) Math.round(Math.random() * 2000));
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		
			listeners.add(new RecordingEventListenerExample(source));
		}
	}
}
