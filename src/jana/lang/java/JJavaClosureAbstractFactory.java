package jana.lang.java;

import jana.java.JJavaClasspath;

public abstract class JJavaClosureAbstractFactory extends Object 
{	
	public abstract JJavaClosureFactory newInstance(String qualifiedClassName, JJavaClasspath classpath);

	public abstract void resetCachesLazily();
	public abstract void resetCaches();
	
	/**
	 * Unconditional Reset
	 */
	public abstract void reset();
}
