package jana.lang.java.soot.typesystem;

import jana.lang.java.JJavaBasicTypeSignature;

import java.io.IOException;

import soot.Type;
import soot.VoidType;

public class JJavaSootVoidType extends JJavaSootType
{	
	private static final long serialVersionUID = 6887263277542409194L;

	public JJavaSootVoidType(Type aType) throws IOException
	{
		this((VoidType) aType);
	}
	
	public JJavaSootVoidType(VoidType aVoidType) throws IOException
	{
		this.signature =  JJavaBasicTypeSignature.signatureFor( aVoidType.toString() );
		this.name = this.signature.unqualifiedName();
	}
	
	public String toSExpression()
	{
		return "java-basic-type 'void";
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
		return (aType instanceof VoidType);
	}
}