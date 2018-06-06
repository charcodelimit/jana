package jana.lang.java.soot.typesystem;

import jana.lang.java.JJavaBasicTypeSignature;

import java.io.IOException;

import soot.Type;
import soot.BooleanType;

/**
 * An integer value where any nonzero value stands for true.
 * 
 * @author chr
 *
 */
public class JJavaSootBooleanType extends JJavaSootType
{
	private static final long serialVersionUID = 1568457619323146921L;

	public JJavaSootBooleanType(Type aType) throws IOException
	{
		this((BooleanType) aType);
	}
	
	public JJavaSootBooleanType(BooleanType aBooleanType) throws IOException
	{
		this.signature =  JJavaBasicTypeSignature.signatureFor( aBooleanType.toString() );
		this.name = this.signature.unqualifiedName();
	}
	
	public String toSExpression()
	{
		return "java-basic-type 'boolean";
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
		return (aType instanceof BooleanType);
	}

}
