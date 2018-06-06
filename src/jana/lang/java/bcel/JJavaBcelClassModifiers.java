package jana.lang.java.bcel;

import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.JavaClass;

import jana.lang.java.JJavaClassModifiers;

public class JJavaBcelClassModifiers extends JJavaClassModifiers
{	
	JJavaBcelClassModifiers(JavaClass aJavaClass)
	{
		setFlags(aJavaClass);
	}
	
	protected void setFlags(JavaClass aJavaClass)
	{
		AccessFlags af;
		
		af = aJavaClass;
		
		if(af.isPublic())
			isPublic = true;
		else
			isPublic = false;
		
		if(af.isPrivate())
			isPrivate = true;
		else
			isPrivate = false;
			
		if(af.isProtected())
			isProtected = true;
		else
			isProtected = false;
		
		if(af.isStatic())
			isStatic = true;
		else
			isStatic = false;
		
		if(af.isFinal())
			isFinal = true;
		else
			isFinal = false;
		
		// class modifiers
		
		if(af.isAbstract() && !aJavaClass.isInterface()) // interface are per definition abstract
			isAbstract = true;
		else
			isAbstract = false;
		
		if(af.isStrictfp())
			isStrict = true;
		else
			isStrict = false;
		
		if(af.isEnum())
			isEnum = true;
		else
			isEnum = false;
		
		if(af.isAnnotation())
			isAnnotation = true;
		else
			isAnnotation = false;
	}
}
