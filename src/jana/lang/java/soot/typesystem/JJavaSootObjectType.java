package jana.lang.java.soot.typesystem;

import jana.lang.java.JJavaSignature;
import jana.lang.java.typesystem.JJavaReferenceType;

import java.io.IOException;

import soot.RefType;
import soot.Type;


/*
 * An object type corresponds to a concrete type 
 * in the Java type system and can therefore be instantiated.
 */
public class JJavaSootObjectType extends JJavaReferenceType
{
	private static final long serialVersionUID = 2724297378250371596L;

	public JJavaSootObjectType(Type type) throws IOException 
	{
		RefType objectType;
		
		objectType = (RefType) type;
		this.signature = JJavaSignature.signatureFor( objectType.getClassName() );
	}
		
	public JJavaSootObjectType(JJavaSignature theSignature)
	{
		this.signature = theSignature;
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
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(Type aType)
	{
		return (aType instanceof RefType);
	}
}
