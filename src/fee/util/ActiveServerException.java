package fee.util;

/***
 * Thrown when a server is already found running.
 * 
 * @author chr
 */
public class ActiveServerException extends Exception
{
	private static final long serialVersionUID = -8266020969950614115L;
	
	public ActiveServerException(String string)
	{
		super(string);
	}
}
