package jana.java;

import jana.lang.java.JJavaSignature;
import jana.util.logging.JLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JJavaSystemLibraries
{
	private final static JLogger logger = JLogger.getLogger("jana.verbose");
	private Map<String,Object> classnames;
	
	public JJavaSystemLibraries(String aRepositoryName)
	{
		try
		{
			loadJavaSystemLibraryClassnames(aRepositoryName);
		}
		catch(IOException ioe)
		{		
			JLogger.getLogger("jana").warn("Error: " + ioe.toString() + "\nPlease analyze the Java System Libraries first!");
		}
	}
	
	private JJavaProject loadSystemLibrariesProject(String aRepositoryName) throws IOException
	{
		long start,end;		
		JJavaExistingProject ep;
				
		start = System.currentTimeMillis();
		ep = new JJavaExistingProject(JJavaSystemLibrariesProject.getSystemLibrariesProjectName(),aRepositoryName);
		end = System.currentTimeMillis();
				
		logger.verbose("Parsing " + ep.getProjectName() + " took " + (end - start) + "ms");
		
		return ep;
	}
	
	private void loadJavaSystemLibraryClassnames(String aRepositoryName) throws IOException
	{
		long start, end;
		JJavaProject project;
		
		start = System.currentTimeMillis();
		
		project = this.loadSystemLibrariesProject(aRepositoryName);		
		this.classnames = new HashMap<String, Object>(project.getProjectClasses().size());
		
		for(JJavaSignature signature : project.getProjectClasses())
			this.classnames.put(signature.qualifiedName(),null);
		
		// reset the signatures cache
		JJavaSignature.initialize(); 
		
		end = System.currentTimeMillis();
		
		logger.verbose("Loading System Library Classnames took " + (end - start) + "ms");		
	}
	
	public Map<String, Object> getJavaSystemLibraryClassnames()
	{
		return this.classnames;
	}
}
