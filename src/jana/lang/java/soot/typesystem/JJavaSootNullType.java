package jana.lang.java.soot.typesystem;

import jana.lang.java.JJavaBasicTypeSignature;
import jana.lang.java.JJavaSignature;

import java.io.IOException;

import soot.NullType;
import soot.Type;

/**
 * The type of the expression null, which has no name. 
 * "The null reference is the only possible value of an expression of null type and can always be converted to any reference type. 
 *  In practice, the programmer can ignore the null type and just pretend that null is a special literal that can be of any reference type."
 *  JVM Specification 2nd Edition
 * 
 * @author chr
 *
 */
public class JJavaSootNullType extends JJavaSootType
{
	private static final long serialVersionUID = 7111255903186220916L;

	public JJavaSootNullType() throws IOException
	{
		this.signature = JJavaBasicTypeSignature.signatureFor("null_type");
	}
	
	public JJavaSootNullType(Type aType) throws IOException
	{
		this((NullType) aType);
	}
	
	public JJavaSootNullType(NullType aNullType) throws IOException
	{
		this.signature =  JJavaSignature.signatureFor( aNullType.toString() );
		this.name = this.signature.unqualifiedName();
	}
	
	public String toSExpression()
	{
		return "java-null-type";
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.toSExpression());
	}
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(Type aType)
	{
		return (aType instanceof NullType);
	}
}
