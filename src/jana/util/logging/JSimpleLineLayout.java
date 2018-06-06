package jana.util.logging;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class JSimpleLineLayout extends Layout
{
	StringBuffer sbuf = new StringBuffer(128);

	public JSimpleLineLayout() 
	{
	}

	public void activateOptions()
	{
	}

	/**
	     Returns the log statement in a format consisting of the
	     <code>level</code>, followed by " - " and then the
	     <code>message</code>. For example, <pre> INFO - "A message"
	     </pre>
	     Each log statement is printed on a new line.

	     <p>The <code>category</code> parameter is ignored.
	     <p>
	     @return A byte array in SimpleLayout format.
	 */
	public String format(LoggingEvent event) 
	{
		sbuf.setLength(0);
		if(Level.WARN.toInt() <= event.getLevel().toInt())
			sbuf.append("\n");
		sbuf.append(event.getLevel().toString());
		sbuf.append(" - ");
		sbuf.append(event.getRenderedMessage());
		sbuf.append(LINE_SEP);
		return sbuf.toString();
	}

	/**
	     The SimpleLayout does not handle the throwable contained within
	     {@link LoggingEvent LoggingEvents}. Thus, it returns
	     <code>true</code>.

	     @since version 0.8.4 */
	public boolean ignoresThrowable()
	{
		return true;
	}

}
