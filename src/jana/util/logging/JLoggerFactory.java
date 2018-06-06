package jana.util.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * A factory that makes new  {@link jana.util.logging.JLogger} instances.
 * 
 * @author chr
 *
 */
public class JLoggerFactory implements LoggerFactory
{

	public JLoggerFactory()
	{
	}

	public Logger makeNewLoggerInstance(String name)
	{
		return new JLogger(name);
	}

}
