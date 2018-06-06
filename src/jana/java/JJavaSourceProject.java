package jana.java;

import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.JJavaBcelClassifier;
import jana.util.jar.JJarFile;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

/**
 * Repsonsibilities: create new projects
 *
 * 
 * @author chr
 *
 */
public class JJavaSourceProject extends JJavaProject
{	
	protected File projectSourceDirectory;
	protected JJavaClasspath projectSourceClasspath;
	protected File projectSourceBaseDirectory;
	
	protected JJavaSourceProject(String aProjectName, String aRepositoryDirectoryName, String aProjectSourceClasspath) throws IOException
	{
		super(aProjectName, aRepositoryDirectoryName, "");
		
		if(aProjectSourceClasspath == null)
			this.projectSourceClasspath = new JJavaClasspath();
		else	
			this.projectSourceClasspath = new JJavaClasspath(aProjectSourceClasspath);
		
		init();
	}
	
	
	protected JJavaSourceProject(String aProjectName, String aRepositoryDirectoryName, List<String> aProjectSourceClasspath) throws IOException
	{
		super(aProjectName, aRepositoryDirectoryName, "");

		if(aProjectName == null || aProjectName.isEmpty() )
			throw new IOException("No project name was provided!");	
		
		this.projectSourceClasspath = new JJavaClasspath();
		this.projectSourceClasspath.addClasspathElements(aProjectSourceClasspath);
		
		init();
	}
	
	public JJavaSourceProject(String aProjectName, String aRepositoryDirectoryName, String aProjectSourceDirectoryName, String aProjectSourceClasspath) throws IOException
	{
		this(aProjectName, aRepositoryDirectoryName, aProjectSourceClasspath);
		
		if(aProjectName == null || aProjectName.isEmpty() )
			throw new IOException("No project name was provided!");	
		
		this.projectSourceDirectory = new File(aProjectSourceDirectoryName);
		this.projectSourceBaseDirectory = new File(this.projectSourceClasspath.directoryNameRelativeToClasspath(this.projectSourceDirectory));
			
		init();
	}
	
	
	public JJavaSourceProject(String aProjectName, String aRepositoryDirectoryName, String aProjectSourceDirectoryName, List<String> aProjectSourceClasspath) throws IOException
	{
		this(aProjectName, aRepositoryDirectoryName, aProjectSourceClasspath);
		
		this.projectSourceDirectory = new File(aProjectSourceDirectoryName);
		this.projectSourceBaseDirectory = new File(this.projectSourceClasspath.directoryNameRelativeToClasspath(this.projectSourceDirectory));
			
		init();
	}
	
	@Override
	protected void init() throws IOException
	{
		super.init();
		
		this.projectJarFile = new JJarFile(this.projectDirectory.getRelativePath() + File.separator + this.projectName + JARFILE_FILENAME_EXTENSION, 
											  this.repositoryDirectory);
		this.projectFinalJarFile = new JJarFile(this.projectDirectory.getRelativePath() + File.separator + this.projectName +
												   FINAL_JAR_FILE_NAME_SUFFIX + JARFILE_FILENAME_EXTENSION, this.repositoryDirectory);
	}
	
	/**
	 * Check for conditions of the file system that could lead to failure of prepareRepository.
	 * 
	 * @throws IOException
	 */
	protected void testFilesystemAttributes() throws IOException
	{
		if(!this.projectSourceDirectory.exists())
			throw new IOException("The source directory " + this.projectSourceDirectory.getCanonicalPath() + " does not exist!");
		if(!this.projectSourceDirectory.canRead())
			throw new IOException("The source directory " + this.projectSourceDirectory.getCanonicalPath() + " can't be read!");
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
	
	protected void analyzeProject() throws Exception
	{	
		this.repository = new JJavaBcelRepository(false, this);

		this.projectClasspath.addElement(this.projectJarFile);
		
		findAspects();
	}	
	
	/**
	 * Analyzes all classes found in the project and adds classes
	 * that are annotated with the Aspect annotation class to the
	 * list of aspects.
	 */
	protected void findAspects() throws Exception
	{
		JJavaBcelClassifier jjbc;
				
		for(JJavaSignature signature : this.projectClasses )
		{
			if(logger != null)
				jjbc = this.repository.analyzeClass(signature.qualifiedName(), logger);
			else
				jjbc = this.repository.analyzeClass(signature.qualifiedName());
			
			if(jjbc.isAspect())
			{
				this.projectAspects.add(signature);
				logger.debug("Added aspect: " + signature.qualifiedName());
			}
		}
		
		this.projectClasses.removeAll(this.projectAspects);
	}
	
	public void copyLibraries() throws IOException
	{
		File libDir;
		
		libDir = new File(this.projectDirectory.getCanonicalPath() + File.separator + DEFAULT_LIBRARY_DIRECTORY_NAME);
	
		try
		{
			this.projectLibraryJarFiles = this.projectSourceClasspath.copyUserDefinedClasspathElements(libDir, true);
		}
		catch(IOException ioe)
		{
			throw new IOException("Error while copying libraries of project " + this.projectName + "!", ioe);
		}
	}
	
	public void createProjectJarFile() throws IOException
	{
		this.projectClasses = this.projectSourceClasspath.classesInDirectory(this.projectSourceDirectory); 
		this.projectJarFile.addClasses(this.projectSourceBaseDirectory,this.projectClasses);
	}

	/**
	 *  Make sure you call first prepare repository to create the project jar-file
	 *  in the repository directory and gather all the project information.
	 * 
	 * @throws IOException -- If this happens: check if prepareRepository() has been called, 
	 *                                         check if the source directory is correct and not empty,
	 *                                         check if the project source classpath has been set correctly.
	 */
	public void prepareRepository(boolean searchForAspects) throws IOException
	{
		testFilesystemAttributes();
		
		logger.info("Creating project directories");
		createDirectories();
		createProjectLockFile();
		
		logger.info("Copying libaries");
		copyLibraries();
		
		logger.info("Creating project .jar file");
		createProjectJarFile();
		
		if(searchForAspects)
		{
			logger.info("Searching for Aspect definitions");
			try
			{
				analyzeProject();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new IOException("Failed to analyze project!",e);
			}
		}
	}
	
	public void prepareRepository(List<String> qualifiedAspectClassNames) throws IOException
	{
		JJavaSignature signature;
		
		prepareRepository(false);
		
		for(String className : qualifiedAspectClassNames)
		{
			signature = JJavaSignature.signatureFor(className);
			
			if(!this.projectAspects.contains(signature))
				this.projectAspects.add(signature);
			
			if(this.projectClasses.contains(signature))
				this.projectClasses.remove(signature);
		}
	}
	
	/**
	 * Creates the project JAR-File in the repository directory, and
	 * collects project information.
	 * 
	 * @param qualifiedAspectClassNames - a colon separated list of fully qualfied classnames
	 * @throws IOException 
	 */
	public void prepareRepository(String qualifiedAspectClassNames) throws IOException
	{
		JJavaClasspathString cps;
		
		cps = new JJavaClasspathString(qualifiedAspectClassNames);
		this.prepareRepository(cps.asFilenameStringList());
	}

	
	public static void main(String[] args)
	{
		List<String> projectClasspath = new ArrayList<String>();
		
		try
		{
			JJavaSourceProject jjp;
			
			projectClasspath.add("bin");
			
			BasicConfigurator.configure();
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);
			
			jjp = new JJavaSourceProject("fee-examples", "test-repository", "bin/example/jana/classes", projectClasspath);
			jjp.prepareRepository(true);
			jjp.saveProjectInfo();
			
			jjp = new JJavaSourceProject("bcel-examples", "test-repository", "bin/example/org/apache/bcel", projectClasspath);
			jjp.prepareRepository(true);
			jjp.saveProjectInfo();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
