package jana.java;

import java.io.IOException;

/**
 * A "Dummy" project
 * 
 * @author chr
 *
 */
public class JJavaImaginaryProject extends JJavaProject
{		
	public JJavaImaginaryProject(String aProjectClasspath) throws IOException
	{
		super("", "", aProjectClasspath);
	}
	
	public JJavaImaginaryProject() throws IOException
	{
		super("","",new JJavaDefaultClasspath());
	}
	
	/**
	 * Imaginary project! Don't save the classname map.
	 */
	public final void saveClassnameMap() throws IOException
	{
		logger.error("Trying to save the classname map of an imaginary project!");
	}
	
	/**
	 * Imaginary Projects can't be saved!
	 */
	public final void saveProjectInfo() 
	{
		logger.error("Trying to save an imaginary project!");
	}
}
