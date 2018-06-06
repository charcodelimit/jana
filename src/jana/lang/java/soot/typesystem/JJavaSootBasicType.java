package jana.lang.java.soot.typesystem;

import java.io.IOException;

import soot.PrimType;
import soot.Type;

import jana.lang.java.JJavaBasicTypeSignature;
import jana.lang.java.typesystem.JJavaType;


public class JJavaSootBasicType extends JJavaType 
{
	private static final long serialVersionUID = 8401774773540743153L;

	public JJavaSootBasicType(Type aType) throws IOException
	{
		this.signature =  JJavaBasicTypeSignature.signatureFor( aType.toString() );
		this.name = this.signature.unqualifiedName();
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
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(Type aType)
	{
		return (aType instanceof PrimType);
	}
}
