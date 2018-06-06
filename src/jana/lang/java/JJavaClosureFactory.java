package jana.lang.java;

public abstract class JJavaClosureFactory {
	/**
	 * Given a method implementation a closure factory produces a closure with instructions that belong to a specific intermediate language.
	 * Intermediate languages are, for example, Java ByteCode, Soot's Jimple, ...
	 */
	public abstract JJavaClosure produce(JJavaMethodImplementation aMethodImplementation) throws Exception;
	
	/**
	 * Given a method implementation a closure factory produces a closure with instructions that belong to a specific intermediate language.
	 * Intermediate languages are, for example, Java ByteCode, Soot's Jimple, ...
	 * @param the method implementation object for the closure
	 * @param the method ID indicating in which place in the class-file the method is declared
	 */
	public abstract JJavaClosure produce(JJavaMethodImplementation aMethodImplementation, int methodID) throws Exception;
}
