package jana.lang.java.bcel.values;

import jana.metamodel.values.JValue;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;

public class JJavaBcelLongConstant extends JJavaBcelConstantValue 
{
	Long constantValue; // INV constantValue != null
	
	protected JJavaBcelLongConstant(ConstantValue constantValue)
	{
		super(constantValue);
	}

	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ConstantLong;
	}
	
	@Override
	protected void initialize(Constant aConstant, ConstantPool theConstantPool) 
	{
		ConstantLong ci;
		
		ci = (ConstantLong) aConstant;
		this.constantValue = new Long( ci.getBytes() );
	}
	
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaBcelStringConstant) 
		{
			JJavaBcelLongConstant other = (JJavaBcelLongConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Long getValue()
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
		aStringBuffer.append("java-constant-long-value");
		
		aStringBuffer.append(" ");
		aStringBuffer.append(getValue());
	}
}
