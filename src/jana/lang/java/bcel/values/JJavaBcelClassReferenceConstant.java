package jana.lang.java.bcel.values;

import java.io.IOException;

import jana.metamodel.values.JValue;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantValue;

/*
 * symbolic references to classes (CONSTANT_Class) are represented as Strings
 * of their (fully qualified) names 
 */
public class JJavaBcelClassReferenceConstant extends JJavaBcelStringConstant
{
	protected final String typeString = "java.lang.Class";
	
	public JJavaBcelClassReferenceConstant(ConstantValue constantValue) throws IOException
	{
		super(constantValue);
	}
	
	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ConstantClass;
	}
	
	protected boolean compareValues(JValue aValue) 
	{
		if(aValue instanceof JJavaBcelClassReferenceConstant) 
		{
			return super.compareValues(aValue);
		}
		
		return false;
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("java-class-reference-constant-value");
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(getValue());
		aStringBuffer.append("\"");		
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
}
