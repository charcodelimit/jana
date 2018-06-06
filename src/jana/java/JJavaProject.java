package jana.java;

import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;
import jana.metamodel.SExpression;
import jana.util.JRelativeFile;
import jana.util.exceptions.JParseException;
import jana.util.exps.JClassnameMap;
import jana.util.jar.JJarFile;
import jana.util.logging.JLogger;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

/**
 * TODO: Eventually add required class-path elements to the repository-JAR file.
 * 
 * The current policy of the repository is, that the user takes care of the class-path.
 * That is when adding a project to the repository, and when analyzing a project with Fee, 
 * the user has to provide the necessary class-path.
 * 
 * Semi-automatic solution: add all .class files found in directories or .jar/.zip files in the
 * class-path to the repository .jar file (pollutes the repository .jar file)
 * create a library .jar file (more complex, need to add library .jar file name as information 
 * to the project-file
 * 
 * Fully-automatic solution: calculate the dependencies, and add only those .class files to the 
 * repository .jar file or a library .jar file (makes the project preparation more time consuming)
 * 
 * @author chr
 *
 */
public abstract class JJavaProject implements SExpression 
{
	protected final static JLogger logger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER);
	private final static boolean LOG_FILE_LOCKING = false;
	private final static boolean LOG_PROJECT_INFO = false;
	private final static Object CLASSNAME_MAP_SEMAPHORE = new Object();
	
	protected transient JJavaBcelRepository repository;
	protected transient JClassnameMap classnameMap;
	protected transient File repositoryDirectory;
	protected transient JJavaDefaultClasspath defaultClasspath;
	protected transient JRelativeFile projectLockFile;
	protected transient JRelativeFile classnameMapTempFile;
	protected transient JJavaDebugInformation debugInformation;
	
	protected String projectName;
	protected String javaVersion;
	protected JRelativeFile projectDirectory;
	protected JJarFile projectJarFile;
	protected JJarFile projectFinalJarFile;
	protected JRelativeFile classnameMapFile;
	protected JRelativeFile debugInformationFile;
	protected JJavaClasspath projectClasspath;
	protected JRelativeFile libraryDirectory;
	protected JRelativeFile analysisOutputDirectory;
	protected JRelativeFile compilationOutputDirectory;
	protected JRelativeFile transformationOutputDirectory;
	
	protected List<String> projectLibraryJarFiles;
	protected List<JJavaSignature> projectClasses;
	protected List<JJavaSignature> projectAspects;
	protected List<JJavaSignature> projectTransformedClasses;
	
	private final static String DEFAULT_ANALYSIS_OUTPUT_DIRECTORY_NAME = "lsp";
	private final static String TRANSFORMATION_OUTPUT_DIRECTORY_NAME = "trn";
	private final static String DEFAULT_COMPILATION_OUTPUT_DIRECTORY_NAME = "bin";
	protected final static String DEFAULT_LIBRARY_DIRECTORY_NAME = "lib";
	
	protected final static String PROJECT_DIRECTORY_NAME_PREFIX = "project-";
	protected final static String FINAL_JAR_FILE_NAME_SUFFIX = "-final";
	
	protected final static String JIMPLE_FILENAME_EXTENSION = ".jimple";
	protected final static String CLASSFILE_FILENAME_EXTENSION = ".class";
	protected final static String JARFILE_FILENAME_EXTENSION = ".jar";
	protected final static String LISP_FILENAME_EXTENSION = ".lisp";
	protected final static String COMPRESSED_LISP_FILENAME_EXTENSION = ".lz";
	protected final static String CLASSNAMEDICTIONARY_FILENAME_EXTENSION = ".cnd";
	protected final static String DEBUGINFORMATION_FILENAME_EXTENSION = ".dbg";
	protected final static String CLASSNAMEDICTIONARY_TEMPFILE_EXTENSION = ".tmp";
	protected final static String PROJECT_LOCKFILE_EXTENSION = ".lck";
	
	protected final static int MAX_WRITE_RETRIES = 16;
	protected final static int FILE_BUFFER_SIZE = 1 << 12; // 4k
	
	
	protected JJavaProject(String aProjectName, File aRepositoryDirectory)
	{
		if(logger.getLevel() == null)
			logger.setLevel(Level.WARN);
		
		this.projectName = aProjectName;
		this.javaVersion = "Java Version: " + System.getProperty("java.version") + " from " + System.getProperty("java.vendor"); 
		this.repositoryDirectory = aRepositoryDirectory;
	}
	
	protected JJavaProject(String aProjectName, String aRepositoryDirectoryName)
	{
		this(aProjectName, new File(aRepositoryDirectoryName));
	}
	
	protected JJavaProject(String aProjectName, File aRepositoryDirectory, JJavaClasspath aProjectClasspath) throws IOException
	{		
		this(aProjectName, aRepositoryDirectory);
		this.projectClasspath = aProjectClasspath;
		
		init();
	}
	
	protected JJavaProject(String aProjectName, String aRepositoryDirectoryName, JJavaClasspath aProjectClasspath) throws IOException
	{	
		this(aProjectName, aRepositoryDirectoryName);
		this.projectClasspath = aProjectClasspath;
		
		init();
	}
	
	protected JJavaProject(String aProjectName, String aRepositoryDirectoryName, String aProjectClasspath) throws IOException
	{		
		this(aProjectName, aRepositoryDirectoryName, new JJavaClasspath(aProjectClasspath));
		
		init();
	}
	
	protected void init() throws IOException
	{	
		String projectDirectoryName;
		
		this.defaultClasspath = new JJavaDefaultClasspath();
		
		this.projectClasses = new ArrayList<JJavaSignature>();
		this.projectAspects = new ArrayList<JJavaSignature>();
		this.projectTransformedClasses = new ArrayList<JJavaSignature>();
		
		this.projectLibraryJarFiles = new ArrayList<String>();
		
		projectDirectoryName = PROJECT_DIRECTORY_NAME_PREFIX + this.projectName;
		this.projectDirectory = new JRelativeFile(projectDirectoryName, this.repositoryDirectory);
		
		this.libraryDirectory = new JRelativeFile(projectDirectoryName + File.separator + DEFAULT_LIBRARY_DIRECTORY_NAME, 
													 this.repositoryDirectory);
		this.analysisOutputDirectory = new JRelativeFile(projectDirectoryName + File.separator + DEFAULT_ANALYSIS_OUTPUT_DIRECTORY_NAME, 
															this.repositoryDirectory);
		this.compilationOutputDirectory = new JRelativeFile(projectDirectoryName + File.separator + DEFAULT_COMPILATION_OUTPUT_DIRECTORY_NAME, 
															   this.repositoryDirectory);
		this.transformationOutputDirectory = new JRelativeFile(projectDirectoryName + File.separator + TRANSFORMATION_OUTPUT_DIRECTORY_NAME, 
																  this.repositoryDirectory);
		this.debugInformationFile = new JRelativeFile(projectDirectoryName + File.separator + TRANSFORMATION_OUTPUT_DIRECTORY_NAME + File.separator + projectName + DEBUGINFORMATION_FILENAME_EXTENSION, 
				                                         this.repositoryDirectory);
		this.classnameMapFile = new JRelativeFile(projectDirectoryName + File.separator + projectName + CLASSNAMEDICTIONARY_FILENAME_EXTENSION, 
                                                     this.repositoryDirectory);
		this.projectLockFile = new JRelativeFile(projectDirectoryName + File.separator + projectName + PROJECT_LOCKFILE_EXTENSION, 
														 this.repositoryDirectory);
		this.classnameMapTempFile = new JRelativeFile(projectDirectoryName + File.separator + projectName + CLASSNAMEDICTIONARY_FILENAME_EXTENSION + CLASSNAMEDICTIONARY_TEMPFILE_EXTENSION, 
														 this.repositoryDirectory);
		this.classnameMap = new JClassnameMap(this.classnameMapTempFile);
	}	

	public JJavaClasspath getDefaultClasspath()
	{
		return this.defaultClasspath;
	}
	
	public JJavaDebugInformation getDebugInformation()
	{
		return this.debugInformation;
	}
	
	public JJarFile getProjectJarFile()
	{
		return this.projectJarFile;
	}
	
	public JJarFile getProjectFinalJarFile()
	{
		return this.projectFinalJarFile;
	}
	
	public List<String> getProjectLibraryJarFiles()
	{
		return this.projectLibraryJarFiles;
	}
	
	public List<JJavaSignature> getProjectClasses()
	{
		return this.projectClasses;
	}
	
	public List<JJavaSignature> getProjectAspects()
	{
		return this.projectAspects;
	}
	
	public List<JJavaSignature> getProjectTransformedClasses()
	{
		return this.projectTransformedClasses;
	}
	
	public File getRepositoryDirectory()
	{
		return this.repositoryDirectory;
	}
	
	public JRelativeFile getAnalysisOutputDirectory()
	{
		return this.analysisOutputDirectory;
	}
	
	public JRelativeFile getCompilationOutputDirectory()
	{
		return this.compilationOutputDirectory;
	}
	
	public JRelativeFile getTransformationOutputDirectory()
	{
		return this.transformationOutputDirectory;
	}
		
	public JJavaClasspath getProjectClasspath()
	{
		return this.projectClasspath;
	}
	
	public String getProjectName()
	{
		return this.projectName;
	}
		
	public void addAspect(JJavaSignature aSignature)
	{
		if(!this.projectAspects.contains(aSignature))
			this.projectAspects.add(aSignature);
	}
	
	public void addClass(JJavaSignature aSignature)
	{
		if(!this.projectClasses.contains(aSignature))
			this.projectClasses.add(aSignature);
	}
	
	/**
	 * Returns true if at least as many .jimple files as transformed classes
	 * were found in the transformation-output directory.
	 * @return
	 */
	public boolean transformationOutputIsAvailable()
	{
		String[] files;
		int transformedClasses;
		int foundJimpleFiles;
		
		foundJimpleFiles = 0;
		transformedClasses = this.projectTransformedClasses.size();
		
		if( transformedClasses == 0)
			return false;
		
		if(!this.transformationOutputDirectory.exists())
			return false;
		
		files = this.transformationOutputDirectory.list();
		
		for(String filename : files)
		{
			if( filename.indexOf(JIMPLE_FILENAME_EXTENSION) > 0 )
				foundJimpleFiles++;
		}
		
		return transformedClasses <= foundJimpleFiles;
	}
	
	/**
	 * This method is synchronized, and allows only one thread to add an entry.
	 * 
	 * @param aSignature
	 * @param aRelativeFilename
	 * @throws IOException
	 */
	public void addClassnameEntry(JJavaSignature aSignature, String aRelativeFilename) throws IOException
	{
		String filename, qualifiedName;
		int filenameExtensionIndex;
		JRelativeFile rf = new JRelativeFile(aRelativeFilename,this.repositoryDirectory);

		filename = rf.getRelativePath();
		filenameExtensionIndex = filename.lastIndexOf(".");
		
		if(filenameExtensionIndex > 0)
			filename = filename.substring(0, filenameExtensionIndex);
		
		qualifiedName = aSignature.qualifiedName();
		
		synchronized(CLASSNAME_MAP_SEMAPHORE)
		{
			this.classnameMap.addPair(qualifiedName, filename);
		}
	}
	
	public List<String> getClassnameEntries() throws IOException
	{	
		if(this.classnameMap == null)
			loadClassnameMap();
		
		return this.classnameMap.getEntries(); 
	}
	
	/**
	 * Loads a classname map.
	 * 
	 * @see jana.java.JJavaProject#saveClassnameMap
	 */
	public void loadClassnameMap() throws IOException
	{
		synchronized(CLASSNAME_MAP_SEMAPHORE) // please, don't do this concurrently
		{
			if(this.classnameMapFile.exists())
				this.classnameMap = JClassnameMap.fromFile(this.classnameMapFile);
		}
	}
	
	/**
	 * Saves a classname map
	 * 
	 * Roles of Locks:
	 *  - CLASSNAME_MAP_SEMAPHORE prevents servers from updating the classname map while it is saved
	 *  
	 * @see jana.java.JJavaProject#saveClassnameMapFile
	 */
	public void saveClassnameMap() throws IOException
	{
		int retries;
		boolean retry;
		
		retries = 0;
		
		do // retry if the classname map could not be written successfully
		{	
			retry = false;
			
			synchronized(CLASSNAME_MAP_SEMAPHORE) // please, don't do this concurrently
			{	
				try
				{
					saveClassnameMapFile();
				}
				catch(JParseException jpe)
				{
					retry = true;
					retries++;
				}
			}
		}
		while(retry && retries < MAX_WRITE_RETRIES); 
	}
	
	
	/**
	 * Follows a pessimistic locking approach:
	 *   
	 * Role of Renaming:
	 *   - renaming is used, because it allows to implement writing the classname-map more safely.
	 *     - firstly it is implemented by most file-systems as an atomic operation that almost takes no time
	 *     - secondly even if writing to the temp-file fails, still a valid classname-map remains in the file-system
	 * 
	 * Use this method only serially ! 
	 * This is ensured when called from {@link jana.java.JJavaProject#saveClassnameMap} by {@link jana.java.JJavaProject#CLASSNAME_MAP_SEMAPHORE}. 
	 * 
	 * @throws IOException
	 */
	private void saveClassnameMapFile() throws IOException
	{
		boolean success;
	
		this.classnameMap.saveClassnameMap(this.classnameMapTempFile);

		if(this.classnameMapFile.exists())
		{
			success = this.classnameMapFile.delete();

			if(!success)
				throw new IOException("The old classname map file " + this.classnameMapFile.getCanonicalPath() + " cannot be deleted!");
		}

		success = this.classnameMapTempFile.renameTo(this.classnameMapFile);

		if(!success)
			throw new IOException("The classname map temp file " + this.classnameMapTempFile.getCanonicalPath() + 
					              " cannot be renamed to " + this.classnameMapFile.getCanonicalPath() + " !");
	}
	
	/**
	 * Determine if the filesystem supports locking.
	 * 
	 * @return true if locking is supported
	 * @throws IOException
	 */
	public boolean filesystemSupportsLocking() throws IOException
	{
		return this.projectLockFile.lockingSupported();
	}
	
	public void createProjectLockFile() throws IOException
	{
		this.projectLockFile.createNewFile();
	}
	
	public synchronized boolean lockProject() throws IOException
	{
		boolean result;
		
		RandomAccessFile file = null;
		FileLock lock = null;

		result = false;
		
		try
		{
			file = new RandomAccessFile(this.projectLockFile,"rw");
			FileChannel fileChannel = file.getChannel();

			lock = fileChannel.lock();

			if(lock != null)
			{
				if(LOG_FILE_LOCKING && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
					logger.debug("Acquired lock for classname-map lock-file.");

				if(!this.projectLockFile.exists())
					createProjectLockFile();
				
				result = checkInstanceCount();
			}
		}
		finally
		{
			if(lock != null)
			{
				if(LOG_FILE_LOCKING && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
					logger.debug("Releasing lock for classname-map lock-file.");
				lock.release();
			}

			if(file != null)
				file.close();
		}
		
		return result;
	}
	
	public synchronized boolean unlockProject() throws IOException
	{
		boolean result;
		
		RandomAccessFile file = null;
		FileLock lock = null;

		result = false;
		
		try
		{
			file = new RandomAccessFile(this.projectLockFile,"rw");
			FileChannel fileChannel = file.getChannel();

			lock = fileChannel.lock();

			if(lock != null)
			{
				if(LOG_FILE_LOCKING && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
					logger.debug("Acquired lock for classname-map lock-file.");

				if(!this.projectLockFile.exists())
					throw new IOException("Trying to unlock a projet that has not been locked beforehand!");
				
				resetInstanceCount();
			}
		}
		finally
		{
			if(lock != null)
			{
				if(LOG_FILE_LOCKING && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
					logger.debug("Releasing lock for classname-map lock-file.");
				lock.release();
			}

			if(file != null)
				file.close();
		}
		
		return result;
	}
	
	/**
	 * Use this method only serially ! 
	 * This is ensured when called from {@link jana.java.JJavaProject#unlockProject} by the filesystem lock. 
	 * 
	 * @throws IOException
	 */
	private void resetInstanceCount() throws IOException
	{		
		FileOutputStream fos = new FileOutputStream(this.projectLockFile);
		DataOutputStream dos = new DataOutputStream(fos);
		
		try
		{
			dos.writeInt(0);
		}
		finally
		{
			dos.close();
			fos.close();
		}		
	}
		
	/**
	 * Use this method only serially ! 
	 * This is ensured when called from {@link jana.java.JJavaProject#lockProject} by the filesystem lock. 
	 * 
	 * @throws IOException
	 * @return
	 */
	private boolean checkInstanceCount() throws IOException
	{
		int instanceCount;
		boolean result;
		
		FileInputStream fis = new FileInputStream(this.projectLockFile);
		DataInputStream dis = new DataInputStream(fis);
		
		instanceCount = 0;
		
		try
		{
			instanceCount = dis.readInt();
		}
		catch(EOFException eoe)
		{
			result = true; // fresh lock-file
		}
		finally
		{
			dis.close();
			fis.close();
			dis = null;
			fis = null;
		}
		
		instanceCount++;
		
		if(instanceCount > 1)
			result = false;
		else
			result = true;
		
		FileOutputStream fos = new FileOutputStream(this.projectLockFile);
		DataOutputStream dos = new DataOutputStream(fos);
		
		try
		{
			dos.writeInt(instanceCount);
		}
		finally
		{
			dos.close();
			fos.close();
			dos = null;
			fos = null;
		}		
		
		return result;
	}
	
	/**
	 * Writes the project file, which contains all information about the project: 
	 * its name, aspects, classes, and the project jar-file.
	 */
	public void saveProjectInfo() throws IOException
	{
		File projectFile;
		BufferedOutputStream bos;
		FileOutputStream fos;
		
		if(projectClasses.size() == 0 && projectAspects.size() == 0)
			throw new IOException("The Project " + this.projectName + " contains no classes!");
		
		projectFile = projectFile();
		
		if( projectFile.exists() )
			logger.warn("The project file " + projectFile.getCanonicalPath() + " already exists!");
		else
			projectFile.createNewFile();
		
		if( ! projectFile.canWrite() )
		{
			logger.error("Can't write the project file " + projectFile.getCanonicalPath());
			throw new IOException("Can't write the project file " + projectFile.getCanonicalPath() + " please check the directorie's write permissions!");
		}
		
		fos = new FileOutputStream(projectFile);
		bos = new BufferedOutputStream( fos, FILE_BUFFER_SIZE );
		
		try
		{
			String sexpr = this.toSExpression();
			if(LOG_PROJECT_INFO && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance 
				logger.debug(sexpr);
			bos.write(sexpr.getBytes());
		}
		finally
		{		
			bos.close();
			fos.close();
		}
	}
	
	protected File projectFile() throws IOException
	{
		return new File( this.repositoryDirectory.getCanonicalPath() + File.separator + PROJECT_DIRECTORY_NAME_PREFIX + this.projectName + LISP_FILENAME_EXTENSION );
	}

	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public String getCompressedLispFilenameExtension()
	{
		return COMPRESSED_LISP_FILENAME_EXTENSION;
	}
	
	public String getLispFilenameExtension()
	{
		return LISP_FILENAME_EXTENSION;
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("(project\n");

		projectToSExpression(aStringBuffer);
		
		aStringBuffer.append(')');		
	}
	
	protected void projectToSExpression(StringBuffer aStringBuffer)
	{
		int entries;
		
		if(this.projectName != null && this.projectName.length() > 0)
		{
			aStringBuffer.append(" (project-name \"");
			aStringBuffer.append(this.projectName);
			aStringBuffer.append("\")\n");
		}
		else
		{
			throw new RuntimeException("Project has no name!");
		}
		
		aStringBuffer.append(" (java-version \"");
		aStringBuffer.append(this.javaVersion);
		aStringBuffer.append("\")\n");
		
		try
		{
			aStringBuffer.append(" (project-directory \"");
			aStringBuffer.append(this.projectDirectory.getRelativePath());
			aStringBuffer.append("\")\n");
			
			aStringBuffer.append(" (project-library-directory \"");
			aStringBuffer.append(this.libraryDirectory.getRelativePath());
			aStringBuffer.append("\")\n");
			
			aStringBuffer.append(" (project-analysis-directory \"");
			aStringBuffer.append(this.analysisOutputDirectory.getRelativePath());
			aStringBuffer.append("\")\n");
			
			aStringBuffer.append(" (project-compilation-directory \"");
			aStringBuffer.append(this.compilationOutputDirectory.getRelativePath());
			aStringBuffer.append("\")\n");
			
			aStringBuffer.append(" (project-transformation-directory \"");
			aStringBuffer.append(this.transformationOutputDirectory.getRelativePath());
			aStringBuffer.append("\")\n");
			
			aStringBuffer.append(" (project-jar-file \"");
			aStringBuffer.append(this.projectJarFile.getRelativePath());
			aStringBuffer.append("\")\n");
			
			aStringBuffer.append(" (project-final-jar-file \"");
			aStringBuffer.append(this.projectFinalJarFile.getRelativePath());
			aStringBuffer.append("\")\n");
		
			aStringBuffer.append(" (project-classname-dictionary-file \"");
			aStringBuffer.append(this.classnameMapFile.getRelativePath());
			aStringBuffer.append("\")\n");
			
			aStringBuffer.append(" (project-debug-information-file \"");
			aStringBuffer.append(this.debugInformationFile.getRelativePath());
			aStringBuffer.append("\")\n");
			
			entries = 0;
			aStringBuffer.append(" (project-library-jar-files");
			for(String filename : this.projectLibraryJarFiles)
			{
				if(entries > 0)
					aStringBuffer.append("  ");
				
				aStringBuffer.append(' ');
				aStringBuffer.append('\"');
				aStringBuffer.append(this.libraryDirectory.getRelativePath() + File.separator + filename);
				aStringBuffer.append('\"');
				
				if(entries > 0)
					aStringBuffer.append('\n');
				
				entries++;
			}
			aStringBuffer.append(')');
			aStringBuffer.append('\n');
		}
		catch(IOException ioe)
		{
			throw new RuntimeException("Error while converting the project " + this.projectName + " into a Symbolic Expression!", ioe);
		}
		
		aStringBuffer.append(" (project-analysis-filename-extensions (");
		aStringBuffer.append("(uncompressed ");
		aStringBuffer.append('\"');
		aStringBuffer.append(getLispFilenameExtension().substring(1)); // remove the "." !
		aStringBuffer.append('\"');
		aStringBuffer.append(')');
		aStringBuffer.append(" (compressed ");
		aStringBuffer.append('\"');
		aStringBuffer.append(getCompressedLispFilenameExtension().substring(1)); // remove the "." !
		aStringBuffer.append('\"');
		aStringBuffer.append(')');
		aStringBuffer.append(')');
		aStringBuffer.append(')');
		aStringBuffer.append('\n');
		
		this.signatureListToSExpression(aStringBuffer, "project-aspects", this.projectAspects);
		aStringBuffer.append('\n');

		this.signatureListToSExpression(aStringBuffer, "project-classes", this.projectClasses);
		aStringBuffer.append('\n');
		
		this.signatureListToSExpression(aStringBuffer, "project-transformed-classes", this.projectTransformedClasses);
	}
	
	protected void signatureListToSExpression(StringBuffer aStringBuffer, String aKey, List<JJavaSignature> aListOfSignatures)
	{
		int entries;
		
		entries = 0;
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		aStringBuffer.append(aKey);
		for(JJavaSignature signature : aListOfSignatures)
		{
			if(entries > 0)
				aStringBuffer.append("  ");
			
			aStringBuffer.append(' ');
			aStringBuffer.append('\"');
			aStringBuffer.append(signature.qualifiedName());
			aStringBuffer.append('\"');
			
			if(entries < aListOfSignatures.size() - 1)
				aStringBuffer.append('\n');
			
			entries++;
		}
		aStringBuffer.append(')');
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(this.projectName);
		sb.append(" [classes: ");
		sb.append(this.projectClasses.size());
		sb.append(" aspects: ");
		sb.append(this.projectAspects.size());
		sb.append(" analyzed: ");
		sb.append(this.classnameMap.length());
		sb.append(" ]");
		
		return sb.toString();
	}
}
