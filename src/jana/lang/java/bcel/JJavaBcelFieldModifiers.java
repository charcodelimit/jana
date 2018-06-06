package jana.lang.java.bcel;

import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.Field;

import jana.lang.java.JJavaFieldModifiers;

public class JJavaBcelFieldModifiers extends JJavaFieldModifiers 
{
	JJavaBcelFieldModifiers(Field aField)
	{
		setFlags(aField);
	}
	
	/*
	 * the if..then..else coding style is more talkative,
	 * but it makes immediately clear that af.isPublic() is
	 * a predicate.
	 * 
	 * This could be optimized to a single assignment if 
	 * lack of speed would require it.
	 */
	protected void setFlags(Field aField)
	{
		AccessFlags af;
		
		af = aField;
		
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
		
		// field modifiers
		
		if(af.isVolatile())
			isVolatile = true;
		else
			isVolatile = false;
		
		if( af.isTransient() )
			isTransient = true;
		else
			isTransient = false;
		
		if( af.isEnum() )
			isEnum = true;
		else
			isEnum = false;
		
		if( af.isSynthetic() )
			isSynthetic = true;
		else
			isSynthetic = false;
	}
}
