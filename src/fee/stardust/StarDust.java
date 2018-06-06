package fee.stardust;

import jana.java.JJavaExistingProject;
import jana.java.JJavaProject;
import jana.java.JJavaRepository;
import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.JJavaBcelAnnotationType;
import jana.lang.java.bcel.JJavaBcelClass;
import jana.lang.java.bcel.JJavaBcelClassifier;
import jana.lang.java.bcel.JJavaBcelEnum;
import jana.lang.java.bcel.JJavaBcelInterface;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.util.JGZIPOutputStream;
import jana.util.JRelativeFile;
import jana.util.exceptions.JAnalysisException;
import jana.util.exceptions.JOutOfFilesException;
import jana.util.jar.JJarFile;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileLock;
import java.rmi.RemoteException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.bcel.classfile.JavaClass;
import org.apache.log4j.Level;

import fee.server.FeeStarDustServer;
import fee.server.ServerStatusView;

/**
 * SynTactic AnalyzeR and DistribUted Sourcecode Translation
 * 
 * @author chr
 *
 */
public class StarDust extends StarDustRemoteServer implements StarDustRemoteInterface
{
	private static final long serialVersionUID = 8464719170429963388L;
	
	private final static String APPLICATION_NAME = "StarDust";
	private final static String VERSION = "0.8a";
	private final static String VERSION_STRING = APPLICATION_NAME + VERSION + " [" + JJavaRepository.getVersion() + "]";
	
	private final static String DEFAULT_REPOSITORY_DIRECTORY_NAME = ".";
	
	private final static boolean LOG_MEMORY_USAGE = false;
	private final static boolean LOG_FILE_LOCKING = false;
	
	// Minix seems to have the severest limitations of current systems that are able to run Java 
	private final static int MAX_FILENAME_LENGTH = 30;  
	private final static int MAX_FILES = 32768;
	// 256 KBytes Buffer size
	private final static int FILE_OUTPUT_BUFFER_SIZE = 1 << 18;

	// don't try to wait more than MAX_TRIES for the servers
	private final static int MAX_TIMES_WAIT_FOR_SERVERS = 512; 
	
	private final static Object OUTPUT_LISP_FILE_SEMAPHORE = new Object();
	
	// the view that shows the status of this StarDustRemoteServer instance
	protected ServerStatusView feeStatusView;
	
	protected JJavaBcelRepository repository;
	protected JJavaProject javaProject;
	
	protected long memoryUsageAtStart;
	protected long memoryUsageAtEnd; 

	public StarDust() throws RemoteException
	{	
		super();
		
		initialize();
	}
	
	public StarDust(FeeStarDustServer aFeeStarDustServer, boolean cliMode) throws RemoteException
	{
		super(aFeeStarDustServer);

		this.setCliMode(cliMode);
		
		initialize();
		
		aFeeStarDustServer.registerStarDust(this, this.registeredName);
		
		setStatus("Ready");
	}
	
	private void initialize()
	{
		JLogger janaLogger;
		Level logLevel;
		
		if(logger.getLevel() == null)
			logLevel = Level.OFF;
		else
			logLevel = logger.getLevel();
		
		janaLogger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER);
		janaLogger.setLevel(logLevel);
		janaLogger = JLogger.getLogger(JJavaRepository.VERBOSE_LOGGER);
		janaLogger.setLevel(logLevel);
		
		this.repositoryDirectory = null;
		this.repositoryDirectory = new File(DEFAULT_REPOSITORY_DIRECTORY_NAME);
		
		try
		{
			this.javaProject = null;
			this.repository = new JJavaBcelRepository(this.javaProject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	public String getProjectInfo()
	{
		return this.javaProject.toString();
	}
	
	public String getVersion() throws RemoteException
	{
		return VERSION_STRING;
	}
	
	public String getJavaVersion() throws RemoteException
	{
		return System.getProperty("java.version");
	}

	public boolean isAlive() throws RemoteException
	{
		return this.getStatus() != null; // chr: just do something that can't be removed through constant propagation
	}

	
	/***************** Local Interface - File System ***************/
	
	public void addClasspath(String aClasspathString) throws IOException
	{
		this.repository.addClasspathElements(aClasspathString);
	}
	
	public void setRepositoryDirectory(String aFilename) throws IOException, RemoteException
	{
		File file;

		file = new File(aFilename);

		if(! file.isDirectory())
			throw new IOException( aFilename + " is no directory!");
		
		if(! file.exists())
			throw new IOException( "The directory " + aFilename + " does not exist!");

		if(! file.canRead())
			throw new IOException( "The directory " + aFilename + " can't be read! Please check the permissions!");
		
		repositoryDirectory = file;
		
		updateViews();
	}

	/***************** Remote Interface - File System **************/
	
	public void setProjectFileName(String aProjectFileName) throws RemoteException, IOException
	{
		if(aProjectFileName == null)
			throw new IOException(aProjectFileName + " is no valid project filename!");
		
		if(this.repositoryDirectory != null && this.repositoryDirectory.exists())
		{
			JRelativeFile rf = new JRelativeFile(aProjectFileName, this.repositoryDirectory);
			
			this.javaProject = new JJavaExistingProject(rf, this.repositoryDirectory, this.repository.getClasspath());
			
			if(!this.javaProject.filesystemSupportsLocking())
			{
				logger.warn("The filesystem of the StarDustServer at " + this.serverAddress.getHostName() + " [" + 
							this.serverAddress.getHostAddress() + "] does not support locking in the project repository directory" + 
							this.repositoryDirectory.getCanonicalPath());
			}
			
			this.repository.setJavaProject(this.javaProject);
			logger.debug("Using Java-Project File " + rf.getCanonicalPath());
		}
		else
		{
			logger.error("Please, provide first a valid project directory!");
			throw new IOException("Please, provide first a valid project directory!");
		}
	}
	
	public void setProjectName(String aProjectName) throws RemoteException, IOException
	{	
		if(aProjectName == null)
			throw new IOException(aProjectName + " is no valid project name!");
		
		if(this.repositoryDirectory != null && this.repositoryDirectory.exists())
		{
			this.javaProject = new JJavaExistingProject(aProjectName, this.repositoryDirectory.getCanonicalPath());
			
			this.repository.setJavaProject(this.javaProject);
			logger.debug("Using Java-Project File " + aProjectName);
		}
		else
		{
			logger.error("Please, provide first a valid project directory!");
			throw new IOException("Please, provide first a valid project directory!");
		}		
	}

	public JJavaProject getProject() throws RemoteException
	{
		return this.javaProject;
	}
	
	public void setCompressionLevel(int compressionLevel) throws RemoteException
	{	
		super.setCompressionLevel(compressionLevel);
	}
	
	/***************** Remote-Interface Analysis *************************/
	
	public void setRecordSupertypes(boolean aValue) throws RemoteException
	{
		this.repository.setRecordSuperTypes(aValue);
	}
	
	public void setRecordReferencedObjectTypes(boolean aValue) throws RemoteException
	{
		this.repository.setRecordReferencedObjectTypes(aValue);
	}
	
	public List<String> getReferencedObjectTypes() throws RemoteException
	{
		return JJavaSootType.getObjectTypes();
	}
	
	public List<String> getSuperTypes() throws RemoteException
	{
		return this.repository.getSupertypes();
	}
	
	/***
	 *  
	 * @param aClassName
	 */
	public void analyze(String aClassName) throws RemoteException
	{	
		String[] classesToAnalyze = new String[1];
		
		classesToAnalyze[0] = aClassName;
		this.analyze(classesToAnalyze);
	}

	
	public void analyze(String[] aClassNameArray) throws RemoteException
	{
		long start,end;
		String classname;
		
		classname = "";
		
		try
		{
			resetHadErrors();
			setBusy();
			
			start = System.currentTimeMillis();
			
			for(int c = 0; c < aClassNameArray.length; c++)
			{
				classname = aClassNameArray[c];
				analyzeSingleClass(classname);
			}
			
			end = System.currentTimeMillis();
			
			resetBusy();
			setStatus("Ready [Analysis Time: " + (end - start) + " ms]");
			
			if(isCliMode())
				verboseLogger.verbose("Total Analysis Time: " + (end - start) + " ms");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			setHadErrors();
			setStatus("Failed while analyzing " + classname + " because of: " + ex); 
			logger.error(getStatus());
		}
	}
	
	/**
	 * A File count is used to avoid overwriting existing analysis results in the repository.
	 * 
	 * This does not guarantee mutual exclusion!
	 * 
	 * It is very possible that if a name clash occurs (@see filenameForClass), 
	 * and the file is checked for existence at the same time by two independent processes,
	 * that analysis results are corrupted.
	 * 
	 * @param aClassName
	 * @throws Exception
	 */
	protected void analyzeSingleClass(String aClassName) throws Exception, IOException
	{
		JJavaBcelClassifier classifier;
		File outputFile;
		
		if(aClassName == null || aClassName.length() == 0)
			return;
		
		classifier = analyzeClass(aClassName);
		
		this.repository.resetFrontendCachesLazily();

		synchronized(OUTPUT_LISP_FILE_SEMAPHORE)
		{
			try
			{
				outputFile = createOutputFileExclusively(classifier);
				
				saveLispFile(outputFile, classifier);
			}
			catch(JOutOfFilesException oofe)
			{
				logger.warn("Please clean up the repository directory!");
				logger.warn("Found more than " + MAX_FILES + " files with the same name!");
			}
		}
	}
	
	/**
	 * Creates the output file for aClassifier.
	 * 
	 * @param aClassifier
	 * @return
	 * @throws IOException
	 * @throws JOutOfFilesException
	 */
	private File createOutputFileExclusively(JJavaBcelClassifier aClassifier) throws IOException, JOutOfFilesException
	{
		JRelativeFile outputFile;
		String outputFileName;
		FileLock outputFileLock;
		RandomAccessFile outputFileRaf;
		boolean fileExists;
		int count;
		
		count = 0;
		do
		{	
			// Path + Classifier Name
			outputFileName = this.filenameForClass(aClassifier, count);
			
			outputFile = new JRelativeFile(outputFileName, this.javaProject.getAnalysisOutputDirectory());
			fileExists = true; // we don't know yet, so we have to be pessimistic
			outputFileRaf = null;
			outputFileLock = null;
			
			try
			{
				outputFileRaf = new RandomAccessFile(outputFile, "rw");
				outputFileLock = outputFileRaf.getChannel().lock();
				
				if(outputFileLock != null)
				{
					if(LOG_FILE_LOCKING && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
						logger.debug("Acquired lock for output file.");
					
					if(!outputFile.exists())
						outputFile.createNewFile();
					
					// chr: when the lock has been acquired, an outputFile has been created OR existed
					fileExists = outputFile.length() > 0;
				}
			}
			finally
			{
				if( outputFileLock != null )
				{
					outputFileLock.release();
					if(LOG_FILE_LOCKING && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
						logger.debug("Released lock for output file.");
				}
				
				if(outputFileRaf != null)
					outputFileRaf.close();
			}
			
			count++;
		}
		while(fileExists && count < MAX_FILES); // chr: MAX_FILES guarantees termination
		
		if(count > MAX_FILES)
			throw new JOutOfFilesException("Found more than " + MAX_FILES + " files with name: " + outputFileName);
		
		return outputFile;
	}
	
	/**
	 * This method tries to create a unique filename for a unique fully qualified class-name.
	 * 
	 * Be aware that the result name may not be unique !
	 * Though, the probability for collisions is very low.  
	 */
	private String filenameForClass(JJavaBcelClassifier aClassifier, int aFileCount)
	{
		StringBuffer outputFileName;
		String unqualifiedClassName, hashCode, shortName;
		int length, maxLength, index;
		
		outputFileName = new StringBuffer();
		
		unqualifiedClassName = aClassifier.getName();
		hashCode = Integer.toHexString(aClassifier.qualifiedName().hashCode());
		
		// characters required for name
		length = unqualifiedClassName.length();
		// characters required for hashCode 
		maxLength = MAX_FILENAME_LENGTH - (hashCode.length() + 1);
		// max. characters required to print the file count  
		if(aFileCount > 0)
			maxLength = maxLength - (Integer.toString(aFileCount).length() + 1);
		// characters required for filetype designator
		if(this.compressionLevel > 0)
			maxLength = maxLength - javaProject.getCompressedLispFilenameExtension().length();
		else
			maxLength = maxLength - javaProject.getLispFilenameExtension().length();  
		
		shortName = unqualifiedClassName.substring(0, Math.min(length, maxLength));
		
		index = 0;
		
		if(length > maxLength)
		{
			for(int i = shortName.length() - 1; i > 0; i--)
			{
				if( Character.isUpperCase(shortName.charAt(i)))
				{
					index = i; 
					break;
				}	
			}
		}
		
		if(index > 0)
			outputFileName.append(shortName.substring(0, index));
		else
			outputFileName.append(shortName);
		
		outputFileName.append('-');
		outputFileName.append(hashCode);
		
		// evtl. count
		if(aFileCount > 0)
		{
			outputFileName.append('-');
			outputFileName.append(aFileCount);	
		}
		
		// filetype designator
		if(this.compressionLevel > 0)
			outputFileName.append(javaProject.getCompressedLispFilenameExtension());
		else
			outputFileName.append(javaProject.getLispFilenameExtension());
			
		
		return outputFileName.toString();
	}

	/**
	 * Analyze the class with aClassName
	 * This method is only used instead of JJavaBcelRepository.analyzeClass() 
	 * to make the status of the analysis visible in the StarDust user interface StarDustStatusView. 
	 * 
	 * @param aClassName
	 * @return
	 * @throws Exception
	 */
	private JJavaBcelClassifier analyzeClass(String aClassName) throws Exception
	{
		long t1,t2, start;
		JavaClass cls;
		JJavaBcelClassifier jjbc;

		// check if the classname is well-formed
		if(!JJavaSignature.isValidSignature(aClassName))
			throw new JAnalysisException(aClassName + " is no valid classname!");
		
		setStatus("Analyzing: " + aClassName); 
	
		if(isCliMode())
			verboseLogger.verbose(getStatus());
		
		if(LOG_MEMORY_USAGE)
		{
			System.gc();
			memoryUsageAtStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		}

		t1 = System.currentTimeMillis();
		start = t1;
		
		try
		{
			cls =  this.repository.loadJavaClass(aClassName);
		}
		catch(ClassNotFoundException cnfe)
		{
			cnfe.printStackTrace();
			throw new JAnalysisException("Class " + aClassName + " could not be found in classpath!", cnfe);
		}
		
		t2 = System.currentTimeMillis();
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("BCEL Syntactic Analysis of Classfile took: " + (t2 - t1) + " ms");

		t1 = System.currentTimeMillis();
		jjbc = JJavaBcelClassifier.produce(cls, this.repository);
		t2 = System.currentTimeMillis();
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Semantic Analysis of Classfile took: " + (t2 - t1) + " ms");
		
		if(LOG_MEMORY_USAGE)
		{
			memoryUsageAtEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			logger.info("Used " + (memoryUsageAtEnd - memoryUsageAtStart) / 1024 + " MBytes Memory.");
		}

		setStatus("Finished Analysis in " + (t2 - start) + " ms");
		
		if(isCliMode() && !JLogLevel.VERBOSE.isGreaterOrEqual(verboseLogger.getLevel()))
		{
			if(jjbc instanceof JJavaBcelClass)
				System.out.print("C");
			else if(jjbc instanceof JJavaBcelInterface)
			{
				if(jjbc instanceof JJavaBcelAnnotationType)
					System.out.print("A");
				else
					System.out.print("I");
			}
			else if(jjbc instanceof JJavaBcelEnum)
				System.out.print("E");
		}
			
		if(isCliMode())
			verboseLogger.verbose("Analysis took: " + (t2 - start) + " ms");
		
		return jjbc;
	}
	
	/**
	 * don't forget to close the file.
	 * 
	 * @param outputStream
	 * @param aClassifier
	 * @throws IOException
	 */
	private void write(OutputStream outputStream, JJavaBcelClassifier aClassifier) throws IOException
	{
		PrintWriter pw = new PrintWriter(outputStream);
		String expression;
		long start,end,t1,t2;

		start = System.currentTimeMillis();
		
		try
		{
			pw.println(";;; -*- Mode: LISP; PACKAGE: JANA.METAMODEL -*- ");
			pw.print("(");
			t1 = System.currentTimeMillis();
			expression = aClassifier.toSExpression();
			aClassifier = null;
			t2 = System.currentTimeMillis();
			pw.println(expression);
			pw.println(")");
		}
		finally
		{
			pw.flush();
		}
		
		end = System.currentTimeMillis();
		
		verboseLogger.verbose("Conversion to SExpression took: " + (t2 - t1) + " ms");
		verboseLogger.verbose("Writing to disc took: " + ((end - start) - (t2 - t1)) + " ms");
	}
	
	/**
	 * Output aClassifier to aFile and add file- and classname information
	 * to the classname map
	 * @param aFile
	 * @param aClassifier
	 */
	private void saveLispFile(File aFile, JJavaBcelClassifier aClassifier) throws IOException
	{
		JJavaSignature signature;
		
		setStatus("Saving file " + aFile.getPath());
		
		if(this.compressionLevel > 0)
			outputToGZIPCompressedLispFile(aFile, aClassifier);
		else
			outputToLispFile(aFile, aClassifier);
			

		signature = aClassifier.getSignature();
		aClassifier = null;
		
		JRelativeFile rf = new JRelativeFile(aFile, this.repositoryDirectory);
		feeStarDustServerInterface.addClassnameEntry(signature, rf.getRelativePath());
	}

	private void outputToLispFile(File aFile, JJavaBcelClassifier aClassifier) throws IOException
	{	
		FileOutputStream fos = new FileOutputStream(aFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, FILE_OUTPUT_BUFFER_SIZE);

		try
		{
			write(bos,aClassifier);
		}
		finally
		{
			bos.close();
			fos.close();
			aFile = null;
		}
	}
	
	@SuppressWarnings("unused")
	private void outputToDeflateCompressedLispFile(File aFile, JJavaBcelClassifier aClassifier) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(aFile);
		Deflater def = new Deflater(this.compressionLevel);
		DataOutputStream daos = new DataOutputStream(fos);
		DeflaterOutputStream dos = new DeflaterOutputStream(daos, def, FILE_OUTPUT_BUFFER_SIZE);
		
		try
		{
			write(dos,aClassifier);
			dos.finish();
			daos.writeInt(def.getTotalIn());
		}
		finally
		{
			dos.close();
			daos.close();
			fos.close();
			aFile = null;
		}
	}
	
	private void outputToGZIPCompressedLispFile(File aFile, JJavaBcelClassifier aClassifier) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(aFile);
		DeflaterOutputStream dos = new JGZIPOutputStream(fos, this.compressionLevel,FILE_OUTPUT_BUFFER_SIZE);
		
		try
		{
			write(dos,aClassifier);
		}
		finally
		{
			dos.close();
			fos.close();
			aFile = null;
		}
	}
		
/***************** Remote-Interface Compilation *************************/
	
	/***
	 *  
	 * @param aClassName
	 */
	public void compile(String aClassName) throws RemoteException
	{	
		String[] classesToCompile = new String[1];
		
		classesToCompile[0] = aClassName;
		this.compile(classesToCompile);
	}

	
	public void compile(String[] aClassNameArray) throws RemoteException
	{
		long start,end;
		String classname;
		
		classname = "";
		
		try
		{
			resetHadErrors();
			setBusy();
			
			start = System.currentTimeMillis();
			
			for(int c = 0; c < aClassNameArray.length; c++)
			{
				classname = aClassNameArray[c];
				compileSingleClass(classname);
			}
			
			end = System.currentTimeMillis();
			
			resetBusy();
			setStatus("Ready [Compilation Time: " + (end - start) + " ms]");
			
			if(isCliMode())
				verboseLogger.info("Total Compilation Time: " + (end - start) + " ms");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			setHadErrors();
			setStatus("Failed while compiling " + classname + " because of: " + ex);
			ex.printStackTrace();
			logger.error(getStatus());
		}
	}
	
	/**
	 * 
	 * @param aClassName
	 * @throws Exception
	 */
	protected void compileSingleClass(String aClassName) throws Exception, IOException
	{
		long start,end;
				
		start = System.currentTimeMillis();
		
		this.repository.resetBackendCachesLazily();
		
		setStatus("Compiling: " + aClassName); 

		if(isCliMode())
			verboseLogger.info(getStatus());
		
		if(isCliMode() && !JLogLevel.VERBOSE.isGreaterOrEqual(verboseLogger.getLevel()))
			System.out.print("c");
		
		this.repository.compileIntermediateRepresentation(aClassName);
		
		end = System.currentTimeMillis();
		
		setStatus("Compiled class " + aClassName + " in: " + ((end-start)/1000.0) + " sec");
		
		if(isCliMode())
			verboseLogger.info(getStatus());
	}

	/**
	 * Checks if all .class files are available, and produces a .jar file
	 */
	public void createFinalProjectJarFile(int serversToWaitFor) throws IOException
	{
		File classFile;
		JJarFile projectJarFile;
		JJarFile finalJarFile;
		File compilationOutputDirectory;
		List<String> resourceFiles;
		List<JJavaSignature> transformedClasses;
		int compiledClasses;
		int count;
		
		if(this.javaProject == null || !this.javaProject.transformationOutputIsAvailable())
			return;
		
		
		count = 0;
		compiledClasses = 0;
		transformedClasses = this.javaProject.getProjectTransformedClasses();
		compilationOutputDirectory = this.javaProject.getCompilationOutputDirectory();
		
		while( ( compiledClasses < transformedClasses.size() ) && ( serversToWaitFor > 0 ) && ( count < MAX_TIMES_WAIT_FOR_SERVERS ) )
		{
			for(JJavaSignature signature : transformedClasses )
			{
				classFile = new JRelativeFile(signature.asClassfileName(), compilationOutputDirectory);
				
				if(!classFile.exists())
				{
					if(serversToWaitFor > 0)
						break;
					else
						throw new IOException("Class File" + classFile.getCanonicalPath() + " could not be found!");
				}
				else
					compiledClasses++;
			}
			
			if(serversToWaitFor > 0 && compiledClasses < transformedClasses.size())
			{
				try
				{
					Thread.sleep(2500); // 2.5 sec
					compiledClasses = 0;
				}
				catch(InterruptedException ie)
				{
					return;
				}
			}
			
			count++;
		}
		
		projectJarFile = this.javaProject.getProjectJarFile(); 
		resourceFiles = projectJarFile.extractResources(this.javaProject.getCompilationOutputDirectory());
		
		finalJarFile = this.javaProject.getProjectFinalJarFile();
		finalJarFile.addClassesAndResources(compilationOutputDirectory, transformedClasses, resourceFiles);
	}
	
	public void which(String aClassName)
	{
		try
		{
			whichClass(aClassName);
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace(); 
			logger.error("Failed while searching class " + aClassName + " because of: " + ex);
		}
	}
	
	private void whichClass(String aClassName) throws Exception
	{
		URL url;

		// check if the classname is well-formed
		if(!JJavaSignature.isValidSignature(aClassName))
			throw new JAnalysisException(aClassName + " is no valid classname!");
		
		try
		{
			url =  this.repository.whichJavaClass(aClassName);
			
			if(url != null)
			{
				System.out.println("\nClass " + aClassName + " found in \n'" + url.getFile() + "'");
			}
			else
			{
				System.out.println("\nClass " + aClassName + " not found in \n'" + this.repository.getClasspath().toString() + "'");
			}
		}
		catch(ClassNotFoundException cnfe)
		{
			cnfe.printStackTrace();
			throw new JAnalysisException("Class " + aClassName + " could not be found in classpath!", cnfe);
		}		
	}
			
	public static String version()
	{
		return VERSION_STRING;
	}
}
