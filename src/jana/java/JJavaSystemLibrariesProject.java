package jana.java;

import jana.util.JClasspath;
import jana.util.JClasspathDirectory;
import jana.util.jar.JJarFile;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

public class JJavaSystemLibrariesProject extends JJavaProject
{
	private final static String SYSTEM_LIBRARIES_PROJECT_NAME = "java";
	private final static String BOOT_CLASS_PATH_PROPERTY_NAME = "sun.boot.class.path";
	
	protected JClasspath javaBootClasspath;
	
	public JJavaSystemLibrariesProject(String aRepositoryDirectoryName) throws IOException
	{
		super(SYSTEM_LIBRARIES_PROJECT_NAME, aRepositoryDirectoryName, "");
		
		String bootClasspath = System.getProperty(BOOT_CLASS_PATH_PROPERTY_NAME);
		
		this.projectClasspath.addClasspathElements(bootClasspath);		
		this.javaBootClasspath = new JClasspath(System.getProperty(BOOT_CLASS_PATH_PROPERTY_NAME));
		
		init();
	}	
	
	@Override
	protected void init() throws IOException
	{
		super.init();

		this.projectJarFile = new JJarFile("", this.repositoryDirectory);
		this.projectFinalJarFile = new JJarFile("", this.repositoryDirectory);
	}
	
	/**
	 * Check for conditions of the file system that could lead to failure of prepareRepository.
	 * 
	 * @throws IOException
	 */
	protected void testFilesystemAttributes() throws IOException
	{
		for(File directory : this.javaBootClasspath.getClasspathDirectories())
		{
			if(!directory.exists())
				throw new IOException("The boot classpath directory " + directory.getCanonicalPath() + " does not exist!");
		}
		
		for(File jarFile : this.javaBootClasspath.getClasspathJarFiles())
		{
			if(!jarFile.exists())
				throw new IOException("The boot classpath JAR file " + jarFile.getCanonicalPath() + " does not exist!");
		}
		
		if(!this.repositoryDirectory.exists())
			throw new IOException("The repository directory " + this.repositoryDirectory.getCanonicalPath() + " does not exist!");
		if(!this.repositoryDirectory.canWrite())
			throw new IOException("The source directory " + this.repositoryDirectory.getCanonicalPath() + " can't be written!");
	}
	
	protected void createDirectories() throws IOException
	{
		if(!this.projectDirectory.exists())
			this.projectDirectory.mkdir();	
		
		File libDir = new File(this.projectDirectory.getCanonicalPath() + File.separator + DEFAULT_LIBRARY_DIRECTORY_NAME);
		
		if(!libDir.exists())
			libDir.mkdir();
		
		if(!this.analysisOutputDirectory.exists())
			this.analysisOutputDirectory.mkdir();	
		
		if(!this.compilationOutputDirectory.exists())
			this.compilationOutputDirectory.mkdir();
		
		if(!this.transformationOutputDirectory.exists())
			this.transformationOutputDirectory.mkdir();
	}

	/**
	 * Collects all the classes in the java boot classpath
	 * 
	 * @throws IOException
	 */
	public void collectClasses() throws IOException
	{
		JJarFile janaJarFile;
		JClasspathDirectory janaClasspathDirectory;
		
		for(File jarFile : this.javaBootClasspath.getClasspathJarFiles())
		{
			janaJarFile = new JJarFile(jarFile);
			
			logger.debug("Processing: " + janaJarFile);
			
			this.projectClasses.addAll(janaJarFile.classesInJarFile());
		}		
		
		for(File directory : this.javaBootClasspath.getClasspathDirectories())
		{
			janaClasspathDirectory = new JClasspathDirectory(directory);
			
			logger.debug("Processing: " + janaClasspathDirectory);
			
			this.projectClasses.addAll(janaClasspathDirectory.classesInDirectory());
		}		
	}
	
	protected void projectToSExpression(StringBuffer aStringBuffer)
	{
		super.projectToSExpression(aStringBuffer);
		
		aStringBuffer.append("\n (project-boot-classpath-directories");		
		for(File directory : this.javaBootClasspath.getClasspathDirectories())
		{
			aStringBuffer.append(' ');
			aStringBuffer.append('\"');
			try {
				aStringBuffer.append(directory.getCanonicalPath());
			} catch(IOException ioe) {}
			aStringBuffer.append('\"');
		}		
		aStringBuffer.append(")\n");
		
		aStringBuffer.append(" (project-boot-classpath-jar-files");		
		for(File directory : this.javaBootClasspath.getClasspathJarFiles())
		{
			aStringBuffer.append(' ');
			aStringBuffer.append('\"');
			try {
				aStringBuffer.append(directory.getCanonicalPath());
			} catch(IOException ioe) {}
			aStringBuffer.append('\"');
		}		
		aStringBuffer.append(")\n");
	}
	
	/**
	 *  Make sure you call first prepare repository to create the project directory structure
	 *  in the repository directory.
	 * 
	 * @throws IOException -- If this happens: check if prepareRepository() has been called.
	 */
	public void prepareRepository(boolean searchForAspects) throws IOException
	{
		testFilesystemAttributes();
		
		logger.info("Creating project directories");
		createDirectories();
		createProjectLockFile();
		
		logger.info("Searching for classes in " + BOOT_CLASS_PATH_PROPERTY_NAME);
		collectClasses();
	}
	
	public String getProjectName()
	{
		return SYSTEM_LIBRARIES_PROJECT_NAME;
	}
	
	public static String getSystemLibrariesProjectName()
	{
		return SYSTEM_LIBRARIES_PROJECT_NAME;
	}
	
	public static void main(String[] args)
	{
		try
		{
			JJavaSystemLibrariesProject jbtlp;
			
			BasicConfigurator.configure();
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);
			
			jbtlp = new JJavaSystemLibrariesProject("java-repository");
			jbtlp.prepareRepository(true);
			jbtlp.saveProjectInfo();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
