package jana.util.logging;

import org.apache.log4j.Logger;

public class JLogger extends Logger
{
	/**
    The fully qualified name of the Logger class. See also the
    getFQCN method. */
	private static final String FQCN = JLogger.class.getName() + ".";
	
	private static JLoggerFactory janaLoggerFactory = new JLoggerFactory(); 
	
	protected JLogger(String name)
	{
		super(name);
	}
	
	/**
     * Log a message object with the {@link org.apache.log4j.Level#VERBOSE VERBOSE} level.
     *
     * @param message the message object to log.
     * @see #debug(Object) for an explanation of the logic applied.
     * @since 1.2.12
     */
    public void verbose(Object message) {
      if (repository.isDisabled(JLogLevel.VERBOSE_INT)) {
        return;
      }

      if (JLogLevel.VERBOSE.isGreaterOrEqual(this.getEffectiveLevel())) {
        forcedLog(FQCN, JLogLevel.VERBOSE, message, null);
      }
    }

    /**
     * Log a message object with the <code>VERBOSE</code> level including the
     * stack verbose of the {@link Throwable}<code>t</code> passed as parameter.
     *
     * <p>
     * See {@link #debug(Object)} form for more detailed information.
     * </p>
     *
     * @param message the message object to log.
     * @param t the exception to log, including its stack verbose.
     * @since 1.2.12
     */
    public void verbose(Object message, Throwable t) {
      if (repository.isDisabled(JLogLevel.VERBOSE_INT)) {
        return;
      }

      if (JLogLevel.VERBOSE.isGreaterOrEqual(this.getEffectiveLevel())) {
        forcedLog(FQCN, JLogLevel.VERBOSE, message, t);
      }
    }
    
    public static JLogger getLogger(String name) {
      return (JLogger) Logger.getLogger(name, janaLoggerFactory);
    }

}
