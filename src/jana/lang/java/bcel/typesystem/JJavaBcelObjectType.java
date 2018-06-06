package jana.lang.java.bcel.typesystem;

import java.io.IOException;

import jana.lang.java.JJavaSignature;
import jana.lang.java.typesystem.JJavaReferenceType;

import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

/*
 * An object type corresponds to a concrete type 
 * in the Java type system and can therefore be instantiated.
 */
public class JJavaBcelObjectType extends JJavaReferenceType 
{
	private static final long serialVersionUID = 2953920007137201738L;

	protected JJavaBcelObjectType(Type type) throws IOException 
	{
		ObjectType objectType;
		
		objectType = (ObjectType) type;
		
		this.signature = JJavaSignature.signatureFor( objectType.getClassName() );
	}
		
	public JJavaBcelObjectType(JJavaSignature theSignature)
	{
		this.signature = theSignature;
	}
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(Type aType)
	{
		return (aType instanceof ObjectType);
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
		aStringBuffer.append("java-object-reference-type");
		
		aStringBuffer.append(" (");
		this.signature.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
}
