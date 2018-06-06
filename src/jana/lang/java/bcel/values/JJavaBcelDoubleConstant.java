package jana.lang.java.bcel.values;

import jana.metamodel.values.JValue;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;

public class JJavaBcelDoubleConstant extends JJavaBcelConstantValue 
{
	Double constantValue; // INV constantValue != null
	
	protected JJavaBcelDoubleConstant(ConstantValue constantValue)
	{
		super(constantValue);
	}

	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ConstantDouble;
	}
	
	@Override
	protected void initialize(Constant aConstant, ConstantPool theConstantPool) 
	{
		ConstantDouble ci;
		
		ci = (ConstantDouble) aConstant;
		this.constantValue = new Double( ci.getBytes() );
	}
	
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaBcelStringConstant) 
		{
			JJavaBcelDoubleConstant other = (JJavaBcelDoubleConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Double getValue()
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

	/**
	 * (... value)
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		String doubleValueString;
		
		doubleValueString = getValue().toString();
		
		if(doubleValueString.indexOf('E') >= 0)
		{
			aStringBuffer.append("java-constant-double-value");
			doubleValueString.replace('E', 'D');
		}
		else
		{
			aStringBuffer.append("java-constant-exponent-free-double-value");
			doubleValueString = doubleValueString + "D0";
		}
			
		aStringBuffer.append(" ");
		
		aStringBuffer.append(doubleValueString);
	}
}
