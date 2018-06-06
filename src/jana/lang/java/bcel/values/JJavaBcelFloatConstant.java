package jana.lang.java.bcel.values;

import jana.metamodel.values.JValue;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;

public class JJavaBcelFloatConstant extends JJavaBcelConstantValue
{
	Float constantValue; // INV constantValue != null
	
	protected JJavaBcelFloatConstant(ConstantValue constantValue)
	{
		super(constantValue);
	}

	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ConstantFloat;
	}
	
	@Override
	protected void initialize(Constant aConstant, ConstantPool theConstantPool) 
	{
		ConstantFloat ci;
		
		ci = (ConstantFloat) aConstant;
		this.constantValue = new Float( ci.getBytes() );
	}
	
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaBcelStringConstant) 
		{
			JJavaBcelFloatConstant other = (JJavaBcelFloatConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Float getValue()
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
		aStringBuffer.append("java-constant-float-value");
		
		aStringBuffer.append(" ");
		aStringBuffer.append(getValue());
	}
}
