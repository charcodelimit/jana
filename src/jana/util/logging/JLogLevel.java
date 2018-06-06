package jana.util.logging;

import org.apache.log4j.Level;

public class JLogLevel extends Level
{
	/**
	 * generated by Eclipse
	 */
	private static final long serialVersionUID = -9095192500652419232L;
	
	public static final String VERBOSE_STR = "VERBOSE";
	public static final int VERBOSE_INT = Level.INFO_INT - 10; 
	public static final Level VERBOSE = new JLogLevel(VERBOSE_INT, VERBOSE_STR, 6);
	
	protected JLogLevel(int level, String levelStr, int syslogEquivalent)
	{
		super(level, levelStr, syslogEquivalent);
	}


   public static Level toLevel(String sArg, Level defaultValue) 
   {
	   if(sArg == null) 
	   {
		   return defaultValue;
	   }
	   String stringVal = sArg.toUpperCase();

	   if(stringVal.equals(VERBOSE_STR)) 
	   {
		   return JLogLevel.VERBOSE;
	   }
	   
	   return Level.toLevel(sArg, (Level) defaultValue);
   }

   public static Level toLevel(int i) throws  IllegalArgumentException 
   {
	   switch(i) 
	   {
	   	case VERBOSE_INT: return JLogLevel.VERBOSE;
	   }
	   return Level.toLevel(i);
   }
}
