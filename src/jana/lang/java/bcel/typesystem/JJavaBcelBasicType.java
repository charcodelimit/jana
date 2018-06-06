package jana.lang.java.bcel.typesystem;

import java.io.IOException;

import jana.lang.java.JJavaBasicTypeSignature;
import jana.lang.java.typesystem.JJavaType;

import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.Type;

public class JJavaBcelBasicType extends JJavaType 
{
	private static final long serialVersionUID = 1748015634526931003L;

	public JJavaBcelBasicType(Type aType) throws IOException
	{
		this.signature =  JJavaBasicTypeSignature.signatureFor( aType.toString() );
		this.name = this.signature.unqualifiedName();
	}
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(Type aType)
	{
		return (aType instanceof BasicType);
	}
	
	public String getJavaSignature()
	{
		return this.signature.qualifiedName();
	}
	
	public String toString()
	{
		return this.getJavaSignature();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("java-basic-type");
		
		aStringBuffer.append(" '");
		aStringBuffer.append(this.toString());
	}
}
