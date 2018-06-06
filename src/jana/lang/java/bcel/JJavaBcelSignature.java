package jana.lang.java.bcel;

import org.apache.bcel.classfile.JavaClass;

import jana.lang.java.JJavaSignature;

public abstract class JJavaBcelSignature extends JJavaSignature
{	
	public static JJavaSignature signatureFor(JavaClass aJavaClass) throws Exception
	{
		return JJavaSignature.signatureFor( aJavaClass.getClassName() );
	}
}
