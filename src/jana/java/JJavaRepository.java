package jana.java;

import jana.JanaRepository;
import jana.lang.java.JJavaClosureAbstractFactory;
import jana.lang.java.JJavaPackage;
import jana.lang.java.JJavaSignature;
import jana.lang.java.soot.typesystem.JJavaSootType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Responsibilities: class loading, logger initialization
 * Subclasses: Framework specific specializations 
 * 
 * @author chr
 *
 */

public abstract class JJavaRepository extends JanaRepository 
{	
	private final static String APPLICATION_NAME = "Fee";
	private final static String VERSION = "Beta 11";
	private final static String VERSION_STRING = APPLICATION_NAME + " " + VERSION;
	public final static String DEFAULT_LOGGER = "jana";
	public final static String VERBOSE_LOGGER = "jana.verbose";

	// start saving memory when less than 1/4th (1>>2) of the maximum Memory is available
	protected static final int MEMORY_SAVING_THRESHOLD = 2; 
	// set to true if caches should be reset to save memory
	protected static final boolean RESET_CACHES = true;
	
	protected Map<JJavaSignature,JJavaPackage> packages;
	//protected Map<String,JJavaType> types;
	//protected Map<String,JJavaSignature> signatures;
	
	protected boolean produceClosures;
	protected JJavaClosureAbstractFactory closureAbstractFactory;
	
	protected JJavaIntermediateRepresentationCompiler compiler = null;
	
	protected JJavaProject javaProject;
	protected JJavaProject lastUsedJavaProject;
	
	protected transient boolean recordSuperTypes = false;
	protected transient Set<String> supertypes; // actually belongs to javaProject, but this would cause a major refactoring
	
	protected JJavaRepository(JJavaProject aProject) throws Exception
	{			
		this.produceClosures = true;
		this.javaProject = aProject;
		
		initialize();
	}
	
	/**
	 * Use this constructor to turn semantic analysis on or off.
	 * If the semantic analysis is switched off, all classes are treated like
	 * interfaces, and only the method declaration metaobject is created for
	 * the methods in a class.
	 * Otherwise the methods are analyzed and if a method has an implementation,
	 * the corresponding method implementation object is created that contains
	 * a closure with information about locals and instructions of the method. 
	 * 
	 * @param analyzeMethods - true if methods should be analyzed
	 * @param aProject
	 * @throws Exception
	 */
	protected JJavaRepository(boolean analyzeMethods, JJavaProject aProject) throws Exception
	{	
		this.produceClosures = analyzeMethods;
		this.javaProject = aProject;
		
		initialize();
	}
	
	protected void initialize() throws IOException
	{
		this.packages = new HashMap<JJavaSignature, JJavaPackage>();
		this.supertypes = new HashSet<String>();
		//this.types = new HashMap<String, JJavaType>();
		//this.signatures = new HashMap<String, JJavaSignature>();
		
		classpathChanged();
	}
		
	public void setJavaProject(JJavaProject aProject) throws IOException
	{
		if(aProject != null)
		{
			this.javaProject = aProject;
			
			initialize();
		}
	}

	public JJavaClosureAbstractFactory getClosureFactory()
	{
		return closureAbstractFactory;
	}
	
	public void setClosureFactory(JJavaClosureAbstractFactory aClosureFactory)
	{
		if(aClosureFactory!=null)
			closureAbstractFactory = aClosureFactory;
	}
	
	public void addClasspathElements(String aClasspathString) throws IOException
	{
		this.javaProject.getProjectClasspath().addClasspathElements(aClasspathString);
		initialize();
	}
	
	public void addClasspathElement(String aFilename) throws IOException
	{
		this.javaProject.getProjectClasspath().addClasspathElements(aFilename);
		initialize();
	}
	
	public void removeClasspathElement(String aFilename) throws IOException
	{
		this.javaProject.getProjectClasspath().removeElement(new File(aFilename));
		initialize();
	}
	/**
	 * Subclass Responsibility!
	 * Called when the classpath changes
	 */
	protected void classpathChanged()
	{
		try
		{
			this.initializeCompiler();
		}
		catch(IOException ioException)
		{
			throw new RuntimeException("Could not initialize Compiler!",ioException);
		}
	}

	public JJavaClasspath getClasspath()
	{
		return this.javaProject.getProjectClasspath();
	}
	
	public JJavaPackage getPackage(String aPackageName)
	{
		return packages.get(aPackageName);
	}
	
	public void addPackage(JJavaPackage aJavaPackage)
	{
		JJavaSignature signature;
		
		signature = aJavaPackage.getSignature();
		
		if(!packages.containsKey(signature))
			packages.put(signature, aJavaPackage);
	}
	
	public boolean shouldAnalyzeMethodImplementations()
	{
		return produceClosures;
	}
	
	/**
	 * (re-)initialize the compiler by adding the transformation output directory 
	 * to the classpath of the compiler, 
	 * and set the compilation output directory of the compiler.
	 * 
	 * @throws IOException
	 */
	protected void initializeCompiler() throws IOException
	{
		File dir;
		JJavaClasspath javaClasspath;
		
		// nothing to do
		if(this.javaProject == null)
			return;
		
		dir = this.javaProject.getTransformationOutputDirectory();		
		if(!dir.exists())
			throw new IOException("Found no directory with input for the backend. The directory " + dir.getCanonicalPath() + " does not exist!");
		if(!dir.canRead())
			throw new IOException("Cannot read the directory with input for the backend. Please check permissions for directory " + 
								  dir.getCanonicalPath() + " !");
		
		dir = this.javaProject.getCompilationOutputDirectory();
		if(!dir.exists())
			throw new IOException("Found no directory for backend output. The directory " + dir.getCanonicalPath() + " does not exist!");
		if(!dir.exists())
			throw new IOException("Cannot read the directory for backend output. Please check permissions for directory " + 
								  dir.getCanonicalPath() + " !");
		
		javaClasspath = new JJavaClasspath();
		
		// first use those files that were transformed
		javaClasspath.addElement(this.javaProject.getTransformationOutputDirectory());
		
		// if this fails, use the library jar-files
		javaClasspath.addClasspathElementsInDirectory(this.javaProject.getProjectLibraryJarFiles(), this.javaProject.getRepositoryDirectory());
		
		// if everything else fails, use the project classpath, or system classpath, if no project classpath was defined
		if(this.javaProject.getProjectClasspath() != null)
			javaClasspath.merge(this.javaProject.getProjectClasspath());
		else
			javaClasspath.merge(this.javaProject.getDefaultClasspath());			
		
		this.compiler.initializeCompiler(javaClasspath, this.javaProject.getCompilationOutputDirectory(), this.javaProject.getDebugInformation());
	}
	
	public void compileIntermediateRepresentation(String aClassName) throws IOException
	{
		this.compiler.compileIntermediateRepresentation(aClassName);
	}
	
	public void setRecordReferencedObjectTypes(boolean aValue)
	{
		JJavaSootType.setRecordReferencedObjectTypes(aValue);
	}
	
	public void setRecordSuperTypes(boolean aValue)
	{
		this.recordSuperTypes = aValue;
	}
	
	public void addSupertypes(List<String> aSignatureList)
	{
		if(recordSuperTypes)
			this.supertypes.addAll(aSignatureList);
	}
	
	public void addSupertype(String aSignature)
	{
		if(recordSuperTypes)
			this.supertypes.add(aSignature);
	}
	
	/**
	 * resets the list of recorded super-types
	 * @return a copy of the list of recorded super-types 
	 */
	public List<String> getSupertypes()
	{
		List<String> returnValue;
		
		returnValue = new ArrayList<String>(this.supertypes);
		this.supertypes = new HashSet<String>();
		return returnValue; 
	}
	
	public boolean removeSupertype(JJavaSignature aSignature)
	{
		return this.supertypes.remove(aSignature);
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("(");
		this.getClasspath().toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		aStringBuffer.append("\n ");
	}
	
	public static String getVersion()
	{
		return VERSION_STRING;
	}
	
	/**
	 * Resets the memoization caches
	 */
	abstract public void resetMemoizationCaches();
	
	/**
	 * Resets the memoization caches
	 */
	abstract public void resetMemoizationCachesLazily();
	
	/**
	 * Resets the caches used by the Frontends,
	 * when memory runs low.
	 */
	public void resetFrontendCachesLazily()
	{
		if(RESET_CACHES)
			resetMemoizationCachesLazily();
		if(RESET_CACHES && this.closureAbstractFactory != null)
			this.closureAbstractFactory.resetCachesLazily();
	}
	
	/**
	 * Immediately resets the caches used by the Frontends.
	 */
	public void resetFrontendCaches()
	{
		if(RESET_CACHES)
			resetMemoizationCaches();
		if(RESET_CACHES && this.closureAbstractFactory != null)
			this.closureAbstractFactory.resetCaches();
	}
	
	public void resetFrontend()
	{
		if(this.closureAbstractFactory != null)
			this.closureAbstractFactory.reset();
	}
	
	/**
	 * Resets the caches used by the Backend,
	 * when memory runs low.
	 */
	public void resetBackendCachesLazily()
	{
		if(RESET_CACHES && this.closureAbstractFactory != null)
			this.closureAbstractFactory.resetCachesLazily();
	}
	
	/**
	 * Immediately resets the caches used by the Backend.
	 */
	public void resetBackendCaches()
	{
		if(RESET_CACHES && this.closureAbstractFactory != null)
			this.closureAbstractFactory.resetCaches();
	}
	
	public void resetBackend()
	{
		if(this.closureAbstractFactory != null)
			this.closureAbstractFactory.reset();
	}
}
