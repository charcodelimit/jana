package jana.lang.java.soot;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.options.Options;

import jana.java.JJavaClasspath;
import jana.java.JJavaRepository;
import jana.lang.java.JJavaClosure;
import jana.lang.java.JJavaClosureFactory;
import jana.lang.java.JJavaMethodImplementation;
import jana.lang.java.soot.jimple.JJavaSootJimpleClosure;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;
import jana.util.logging.JLogger;

public class JJavaSootClosureFactory extends JJavaClosureFactory
{
	private final static boolean DEBUG_SOOT = false;
	
	private final static JLogger logger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER);
	private final static JLogger verboseLogger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER + ".verbose");
	
	private JJavaClasspath classpath;
	private SootClass sootClass;
	
	public JJavaSootClosureFactory(String qualifiedClassName, JJavaClasspath aClassPath)
	{
		if(logger.getLevel() == null)
			verboseLogger.setLevel(Level.OFF);
		else
			verboseLogger.setLevel(logger.getLevel());
		
		JJavaSootJimpleReferenceValueLocal.initialize();
		
		this.classpath = aClassPath;
		
		initSootClasspath(this.classpath.toString());
		loadSootClass(qualifiedClassName);
	}

	/**
	 * Set Soot Classpath
	 * 
	 * @param classPath
	 * @param qualifiedClassName
	 */
	private void initSootClasspath(String classPath)
	{
		Scene sc;
		String cp;
		
		if (classPath == null || classPath.trim().length() == 0)
		{
			cp = Scene.v().defaultClassPath();
		}
		else
		{
			cp = classPath;
		}
		
		sc = Scene.v();
		
		if(!this.classpath.classpathElementsAreInList(soot.SourceLocator.v().classPath()))
		{
			logger.debug("Setting Classpath!");
			sc.setSootClassPath(cp);
		}
	}
		
	/**
	 * Load Soot Class
	 * 
	 * Unregisters already analyzed classes to make sure that it is re-analyzed.
	 * This is necessary, as some caches are scrapped {@link soot.FastHierarchy} 
	 * and {@link soot.Scene#getOrMakeFastHierarchy}
	 *
	 * An alternative implementation is to completely remove the class.
	 * Using the following code after the closure has been produced:
	 * 
	 * if(this.sootClass != null)
	 *		sc.removeClass(this.sootClass);
	 * this.sootClass = null;		
	 *
	 * @param qualifiedClassName
	 */
	private void loadSootClass(String qualifiedClassName)
	{
		Scene sc;
		long start,end;
		
		sc = Scene.v();
		
		if(sc.containsClass(qualifiedClassName))
		{
			this.sootClass = sc.getSootClass(qualifiedClassName);
			
			sc.removeClass(this.sootClass);
			sc.addClass(this.sootClass);
			
			System.out.print(".");
		}
		
		// Required for Soot Version dev-3307
		//Options.v().set_whole_program(true);
		//sc.loadNecessaryClasses();
		
		start = System.currentTimeMillis();
		
		this.sootClass = sc.loadClassAndSupport(qualifiedClassName);
		
		end = System.currentTimeMillis();
		
		verboseLogger.verbose("Loading & Analyzing " + qualifiedClassName + " took: " + (end-start) + " ms");
	}
	
	@Override
	/**
	 * Produce the Closure
	 * 
	 * @return the Closure for the instructions in the Method Body
	 */
	public JJavaClosure produce(JJavaMethodImplementation aMethodImplementation, int methodID)throws Exception
	{
		SootMethod sootMethod;
		JJavaClosure closure;
		JJavaSootMethodImplementation methodImplementation;
		
		if(methodID < this.sootClass.getMethodCount())
		{
			methodImplementation = JJavaSootMethodImplementation.fromJJavaMethodImplementation(aMethodImplementation);
			sootMethod = this.sootClass.getMethods().get(methodID);
			
			if(methodImplementation.correspondsTo(sootMethod))
				return produce(aMethodImplementation, sootMethod);		
		}
		
		closure = this.produce(aMethodImplementation);

		return closure;
	}
	
	@Override
	public JJavaClosure produce(JJavaMethodImplementation aMethodImplementation) throws Exception
	{
		SootMethod method = this.findMethodInSootClass(this.sootClass, aMethodImplementation);
		
		return produce(aMethodImplementation, method);
	}

	
	private JJavaClosure produce(JJavaMethodImplementation aMethodImplementation, SootMethod aSootMethod) throws Exception
	{
		long start,end;
		
		if(aSootMethod == null)
		{
			logger.error("Soot: No method " + aMethodImplementation.getName() + " in class " + this.sootClass.getName());
			return null;
		}

		if(DEBUG_SOOT && verboseLogger.getLevel().isGreaterOrEqual(Level.DEBUG))
			Options.v().set_verbose(true);
		
		start = System.currentTimeMillis();
		verboseLogger.debug("Retrieving Body for: " + aMethodImplementation.getName());
		Body b = aSootMethod.retrieveActiveBody();
		end = System.currentTimeMillis();
		
		verboseLogger.debug("Analyzing " + aMethodImplementation.getName() + " took: " + (end - start) + " ms" );
		
		if(DEBUG_SOOT && verboseLogger.getLevel().isGreaterOrEqual(Level.DEBUG))
			Options.v().set_verbose(false);					
		
		/* there is no guarantee that future versions of soot will also return a jimple body, so first check.*/
		if(! (b instanceof JimpleBody) )
		{
			throw new Exception("Whoops, didn't get a Jimple Body");
		}
		
		JimpleBody jb = (JimpleBody) b;
		
		if(DEBUG_SOOT && verboseLogger.getLevel().isGreaterOrEqual(Level.DEBUG))
			verboseLogger.debug(jb.toString());
		
		return new JJavaSootJimpleClosure(jb, aMethodImplementation);
	}
	
	/**
	 * Tries to find a SootMethod in aSootClass that matches aMethodImplementation.
	 * 
	 * chr: this may be slow, however each method is expected to be queried exactly once
	 */
	protected SootMethod findMethodInSootClass(SootClass aSootClass, JJavaMethodImplementation aMethodImplementation)
	{
		List<SootMethod> methods;
		SootMethod currentMethod;
		JJavaSootMethodImplementation methodImplementation;
		
		methods = aSootClass.getMethods();
		methodImplementation = JJavaSootMethodImplementation.fromJJavaMethodImplementation(aMethodImplementation);
		
		for( Iterator<SootMethod> i = methods.iterator(); i.hasNext(); )
		{
			currentMethod = (SootMethod) i.next();
			
			if(methodImplementation.correspondsTo(currentMethod))
				return currentMethod;
		}
		
		logger.error("No method with matching parameter types found!" + aMethodImplementation.getParameterTypes().toString() );
		
		return null;
	}
}
