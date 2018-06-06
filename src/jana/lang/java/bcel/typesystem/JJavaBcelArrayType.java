package jana.lang.java.bcel.typesystem;

import jana.lang.java.JJavaSignature;
import jana.lang.java.typesystem.JJavaArrayType;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.Type;

public class JJavaBcelArrayType extends JJavaArrayType 
{ 	
	private static final long serialVersionUID = 8481134189972885701L;

	protected JJavaBcelArrayType(Type type) throws Exception 
	{
		ArrayType arrayType;
		
		arrayType = (ArrayType) type;
		this.type = JJavaBcelType.produce(arrayType.getBasicType());
		this.name = this.type.getName();
		this.signature = JJavaSignature.signatureFor( arrayType.getBasicType().toString() );
		this.dimensions = arrayType.getDimensions(); 
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
		StringBuffer buffer = new StringBuffer(signature.qualifiedName());
		
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
