package jana.lang.java.bcel;

import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;

import org.apache.bcel.classfile.JavaClass;

public class JJavaBcelInterface extends JJavaBcelClassifier 
{
	protected JJavaBcelInterface(JJavaSignature theSignature, JavaClass aJavaClass, JJavaBcelRepository aJJavaBcelRepository) throws Exception
	{
		super(theSignature, aJavaClass, aJJavaBcelRepository);
		
		this.classifierType  = "java-interface-declaration";
	}
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(JavaClass aJavaClass)
	{
		return aJavaClass.isInterface() && !aJavaClass.isAnnotation();
	}
}