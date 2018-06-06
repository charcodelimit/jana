package jana.lang.java.bcel;

import org.apache.bcel.classfile.JavaClass;

import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;

public class JJavaBcelClass extends JJavaBcelClassifier 
{
	protected JJavaBcelClass(JJavaSignature theSignature, JavaClass aClass, JJavaBcelRepository aJJavaBcelRepository) throws Exception
	{
		super(theSignature, aClass, aJJavaBcelRepository);
		
		this.classifierType = "java-class-declaration";
	}

	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(JavaClass aJavaClass)
	{
		return aJavaClass.isClass() && !aJavaClass.isEnum() && !aJavaClass.isAnnotation();
	}
}
