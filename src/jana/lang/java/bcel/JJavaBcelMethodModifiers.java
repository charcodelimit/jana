package jana.lang.java.bcel;

import jana.lang.java.JJavaMethodModifiers;

import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.Method;

public class JJavaBcelMethodModifiers extends JJavaMethodModifiers 
{
	public JJavaBcelMethodModifiers(Method aJavaMethod)
	{
		setFlags(aJavaMethod);
	}
	
	protected void setFlags(Method aJavaMethod)
	{
		AccessFlags af;
		
		af = aJavaMethod;
		
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
		
		// method modifiers
		
		if(af.isAbstract())
			isAbstract = true;
		else
			isAbstract = false;
		
		if(af.isStrictfp())
			isStrict = true;
		else
			isStrict = false;
		
		if(af.isSynchronized())
			isSynchronized = true;
		else
			isSynchronized = false;
		
		if(af.isNative())
			isNative = true;
		else
			isNative = false;
		
		if(af.isSynthetic())
			isSynthetic = true;
		else
			isSynthetic = false;
			
	}
}
