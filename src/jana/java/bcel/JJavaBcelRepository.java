package jana.java.bcel;

import java.io.IOException;
import java.net.URL;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import jana.java.JJavaProject;
import jana.java.JJavaRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.JJavaBcelClassifier;
import jana.lang.java.bcel.typesystem.JJavaBcelType;
import jana.lang.java.soot.JJavaSootClosureAbstractFactory;
import jana.lang.java.soot.JJavaSootJimpleCompiler;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.util.logging.JLogger;

public class JJavaBcelRepository extends JJavaRepository 
{	
	// the number of classes to analyze before a reset can occur (prevents cycles)
	private final static int RESET_CYCLE_COUNT = 16; 
	
	private final long thresholdHigh, thresholdLow;
	private ClassPath bcelClasspath;
	private int analyzedClasses;
	
	public JJavaBcelRepository(JJavaProject aProject) throws Exception
	{
		super(aProject);
		
		this.analyzedClasses = 0;
		
		// chr: resetting the soot caches saves a lot of memory, but is horribly expensive
		thresholdHigh = (Runtime.getRuntime().maxMemory() >> MEMORY_SAVING_THRESHOLD);
		thresholdLow = (Runtime.getRuntime().maxMemory() >> (MEMORY_SAVING_THRESHOLD + 1));
		JLogger.getLogger(VERBOSE_LOGGER).verbose("Threshold 0: " + (thresholdHigh >> 20) + " MBytes");
		JLogger.getLogger(VERBOSE_LOGGER).verbose("Threshold 1: " + (thresholdLow >> 20) + " MBytes");
		
		this.closureAbstractFactory = new JJavaSootClosureAbstractFactory(thresholdHigh, thresholdLow);
	}

	public JJavaBcelRepository(boolean analyzeMethods, JJavaProject aProject) throws Exception 
	{
		super(analyzeMethods, aProject);
		
		this.analyzedClasses = 0;
		
		// chr: resetting the soot caches saves a lot of memory, but is horribly expensive
		thresholdHigh = (Runtime.getRuntime().maxMemory() >> MEMORY_SAVING_THRESHOLD);
		thresholdLow = (Runtime.getRuntime().maxMemory() >> (MEMORY_SAVING_THRESHOLD + 1));
		JLogger.getLogger(VERBOSE_LOGGER).verbose("Threshold 0: " + (thresholdHigh >> 20) + " MBytes");
		JLogger.getLogger(VERBOSE_LOGGER).verbose("Threshold 1: " + (thresholdLow >> 20) + " MBytes");
		
		this.closureAbstractFactory = new JJavaSootClosureAbstractFactory(thresholdHigh, thresholdLow);
	}
	
	@Override
	protected void classpathChanged()
	{	
		super.classpathChanged();
		
		if(this.javaProject != null)
			this.bcelClasspath = new ClassPath(this.javaProject.getProjectClasspath().toString());
		else
			this.bcelClasspath = null;
		
		this.resetFrontend();
		this.resetBackend();
	}
	
	/**
	 * (re-)initialize the compiler by adding the transformation output directory 
	 * to the classpath of the compiler, 
	 * and set the compilation output directory of the compiler.
	 * 
	 * @throws IOException
	 */
	@Override
	protected void initializeCompiler() throws IOException
	{
		if(compiler == null)
			this.compiler = new JJavaSootJimpleCompiler();
		
		super.initializeCompiler();
	}
	
	/**
	 * Analyzes a class using the repository.
	 * (used to find aspects)
	 * 
	 * @param aFullyQualifiedClassName
	 * @return
	 * @throws Exception
	 */
	public JJavaBcelClassifier analyzeClass(String aFullyQualifiedClassName, Logger aLogger) throws Exception
	{
		long t1,t2, start;
		JavaClass cls;
		JJavaBcelClassifier jjbc;
		SyntheticRepository repository;
		
		// check if the classname is well-formed
		if(!JJavaSignature.isValidSignature(aFullyQualifiedClassName))
			throw new Exception(aFullyQualifiedClassName + " is no valid classname!");
		
		if(Level.DEBUG.isGreaterOrEqual(aLogger.getLevel())) // ... give the JITter a chance
			aLogger.debug("Analyzing: " + aFullyQualifiedClassName);
		
		this.initialize();
		
		t1 = System.currentTimeMillis();
		start = t1;
		repository = SyntheticRepository.getInstance( new ClassPath(this.javaProject.getProjectClasspath().toString()) );
		cls =  repository.loadClass(aFullyQualifiedClassName);
		t2 = System.currentTimeMillis();
		if(Level.DEBUG.isGreaterOrEqual(aLogger.getLevel())) // ... give the JITter a chance
			aLogger.debug("BCEL Syntactic Analysis of Classfile took: " + (t2 - t1) + " ms");

		t1 = System.currentTimeMillis();
		jjbc = JJavaBcelClassifier.produce(cls, this);
		t2 = System.currentTimeMillis();
		if(Level.DEBUG.isGreaterOrEqual(aLogger.getLevel())) // ... give the JITter a chance
			aLogger.debug("Semantic Analysis of Classfile took: " + (t2 - t1) + " ms");

		if(Level.DEBUG.isGreaterOrEqual(aLogger.getLevel())) // ... give the JITter a chance
			aLogger.debug("Overall analysis took: " + (t2 - start) + " ms");
		
		this.analyzedClasses++;
		
		return jjbc;
	}
	
	/**
	 * Analyzes a class using the repository.
	 * (used for the analysis of inner classes)
	 * 
	 * @param aFullyQualifiedClassName
	 * @return
	 * @throws Exception
	 */
	public JJavaBcelClassifier analyzeClass(String aFullyQualifiedClassName) throws Exception
	{
		JavaClass cls;
		JJavaBcelClassifier jjbc;
		
		
		// check if the classname is well-formed
		if(!JJavaSignature.isValidSignature(aFullyQualifiedClassName))
			throw new Exception(aFullyQualifiedClassName + " is no valid classname!");
		
		cls =  this.loadJavaClass(aFullyQualifiedClassName);
		
		jjbc = JJavaBcelClassifier.produce(cls, this);
			
		return jjbc;
	}
	
	public URL whichJavaClass(String aFullyQualifiedClassName) throws ClassNotFoundException
	{
		SyntheticRepository repository;
		URL classURL;
		String className;
		
		className = aFullyQualifiedClassName;
		
		className = className.replace('.', '/');
		className = className + ".class";
		
		repository = SyntheticRepository.getInstance( this.bcelClasspath );
		classURL = repository.getClassPath().getResource(className);
		
		return classURL;
	}
	
	/**
	 * Loads the BCEL representation of a class.
	 * (used for the analysis of classes with {@link fee.stardust.StarDust})
	 * 
	 * @param aFullyQualifiedClassName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public JavaClass loadJavaClass(String aFullyQualifiedClassName) throws ClassNotFoundException
	{
		SyntheticRepository repository;
		JavaClass cls;
		
		repository = SyntheticRepository.getInstance( this.bcelClasspath );
		cls = repository.loadClass(aFullyQualifiedClassName);
		
		this.analyzedClasses++;
		
		return cls;
	}
	
	/**
	 * Frees BCEL's repository cache if memory runs low.
	 */
	private void resetBCELCacheLazily()
	{
		long free;
		
		free = Runtime.getRuntime().freeMemory();
		
		if( free < this.thresholdHigh )
		{
			JLogger.getLogger(VERBOSE_LOGGER).verbose((free >> 20) + " MB [" + (thresholdHigh >> 20) + " MB]" );
			
			this.resetBCELCache();
		}
	}
	
	private void resetBCELCache()
	{
		SyntheticRepository bcelSyntheticRepository;
	
		this.analyzedClasses = 0;
		
		if(RESET_CACHES)
		{
			System.out.print("b");
		
			bcelSyntheticRepository = SyntheticRepository.getInstance( this.bcelClasspath );
			bcelSyntheticRepository.clear();			
		}
	}
	
	@Override
	public void resetMemoizationCaches()
	{
		JJavaSootType.initialize();
		JJavaBcelType.initialize();
		JJavaSignature.initialize();
	}
	
	@Override
	public void resetMemoizationCachesLazily()
	{	
		if(JJavaSootType.numTypes() > 2048)
			JJavaSootType.initialize();
		if(JJavaBcelType.numTypes() > 1024)
			JJavaBcelType.initialize();
		if(JJavaSignature.numSignatures() > 4096)
			JJavaSignature.initialize();
	}
	
	@Override
	public void resetFrontendCachesLazily()
	{
		if( this.analyzedClasses > RESET_CYCLE_COUNT)
		{		
			resetBCELCacheLazily();
			super.resetFrontendCachesLazily();
		}
	}
	
	@Override
	public void resetFrontendCaches()
	{
		this.analyzedClasses = 0;
		
		resetBCELCache();
		super.resetFrontendCaches();
	}
	
	@Override
	public void resetFrontend()
	{
		SyntheticRepository bcelSyntheticRepository;
		
		this.analyzedClasses = 0;
		
		bcelSyntheticRepository = SyntheticRepository.getInstance( this.bcelClasspath );
		bcelSyntheticRepository.clear();
		
		super.resetFrontend();
	}
}
