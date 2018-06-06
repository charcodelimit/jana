package jana.lang.java.soot.typesystem;

import soot.ArrayType;
import soot.Type;

import jana.lang.java.JJavaSignature;
import jana.lang.java.typesystem.JJavaArrayType;


public class JJavaSootArrayType extends JJavaArrayType 
{ 	
	private static final long serialVersionUID = 3878727718726108903L;

	/**
	 * This constructor depends on direct access to the fields
	 * of soot.ArrayType as no accessors exist for them.
	 * 
	 * @param type
	 * @throws Exception
	 */
 	public JJavaSootArrayType(Type type) throws Exception 
	{
		ArrayType arrayType;
		
		arrayType = (ArrayType) type;
		// chr: kludge
		this.type = JJavaSootType.produce(arrayType.baseType);
		this.name = this.type.getName();
		this.signature = JJavaSignature.signatureFor( arrayType.toString() );
		this.dimensions = arrayType.numDimensions; 
	}
	
	/*
	 * modelsType @returns true if the @argument represents a type that is modeled
	 * by this class.
	 */
	protected static boolean modelsType(Type aType)
	{
		return (aType instanceof ArrayType);
	}
	
	public String getJavaSignature()
	{
		StringBuffer buffer = new StringBuffer(this.signature.qualifiedName());
		
		for(int i = 0; i < this.dimensions; i++)
			buffer.append("[]");
		
		return buffer.toString();
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
		aStringBuffer.append("java-array-type");
		
		aStringBuffer.append(" ");
		aStringBuffer.append(this.dimensions);
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
}
