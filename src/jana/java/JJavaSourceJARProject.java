package jana.java;

import jana.util.jar.JJarFile;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

public class JJavaSourceJARProject extends JJavaSourceProject
{
	private JJarFile projectSourceJarFile;
	
	public JJavaSourceJARProject(String aProjectName, String aJARFileName, String aProjectSourceJarFileName, String aProjectSourceClasspath) throws IOException
	{
		super(aProjectName,aJARFileName,aProjectSourceClasspath); 
		
		this.projectSourceBaseDirectory = null;
		this.projectSourceDirectory = null;
		
		this.projectSourceJarFile = new JJarFile( aProjectSourceJarFileName );
		
		if( !this.projectSourceJarFile.isValidJarFile() )
			throw new IOException(aProjectSourceJarFileName + " is no valid .jar file!");
	}
	
	public boolean exists()
	{
		return this.projectDirectory.exists() && this.projectJarFile.exists();
	}
	
	/**
	 * Check for conditions of the file system that could lead to failure of prepareRepository.
	 * 
	 * @throws IOException
	 */
	protected void testFilesystemAttributes() throws IOException
	{
		if(!this.projectSourceJarFile.exists())
			throw new IOException("The source .jar file " + this.projectSourceJarFile.getCanonicalPath() + " does not exist!");
		if(!this.projectSourceJarFile.canRead())
			throw new IOException("The source .jar file " + this.projectSourceJarFile.getCanonicalPath() + " can't be read!");
		if(!this.repositoryDirectory.exists())
			throw new IOException("The repository directory " + this.repositoryDirectory.getCanonicalPath() + " does not exist!");
		if(!this.repositoryDirectory.canWrite())
			throw new IOException("The repository directory " + this.repositoryDirectory.getCanonicalPath() + " can't be written!");
	}	
	
	
	public void createProjectJarFile() throws IOException
	{	
		// get all classes
		this.projectClasses = this.projectSourceJarFile.classesInJarFile();
		
		if(this.projectClasses.size() == 0)
			throw new IOException("The JAR File " + this.projectSourceJarFile.getName() + " contained no class files!");
		
		// copy jar file
		logger.debug("Project JAR file: " + this.projectJarFile.getName());
		
		projectSourceJarFile.copyJarFile(this.projectJarFile,true);
	}
	
	
	public static void main(String[] args)
	{	
		try
		{
			BasicConfigurator.configure();
			
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);
			
			JJavaSourceJARProject jjp = new JJavaSourceJARProject("bcel", "test-repository", "lib/bcel-5.3.jar","lib/junit-3.8.1.jar");
			jjp.prepareRepository(false);
			jjp.saveProjectInfo();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
