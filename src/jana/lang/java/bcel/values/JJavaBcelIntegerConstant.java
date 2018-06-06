package jana.lang.java.bcel.values;

import jana.metamodel.values.JValue;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;

public class JJavaBcelIntegerConstant extends JJavaBcelConstantValue 
{
	Integer constantValue; // INV constantValue != null
	
	protected JJavaBcelIntegerConstant(ConstantValue constantValue)
	{
		super(constantValue);
	}

	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ConstantInteger;
	}
	
	@Override
	protected void initialize(Constant aConstant, ConstantPool theConstantPool) 
	{
		ConstantInteger ci;
		
		ci = (ConstantInteger) aConstant;
		this.constantValue = new Integer( ci.getBytes() );
	}
	
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaBcelStringConstant) 
		{
			JJavaBcelIntegerConstant other = (JJavaBcelIntegerConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Integer getValue()
	{
		return this.constantValue;
	}
		
	public int hashCode() 
	{
		return this.constantValue.hashCode();
	}
	
	public String toString()
	{
		return getValue().toString();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}

	public void toSExpression(StringBuffer aStringBuffer) 
	{
		aStringBuffer.append("java-constant-integer-value");
		
		aStringBuffer.append(" ");
		aStringBuffer.append(getValue());
	}
}
