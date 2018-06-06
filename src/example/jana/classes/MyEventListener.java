package example.jana.classes;

import java.awt.Event;
import java.util.EventListener;

public interface MyEventListener extends EventListener
{
	public void onEvent(Event e);
}
