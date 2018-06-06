package jana.lang.java.soot.values.constants;

import soot.Value;
import soot.jimple.Constant;
import jana.lang.java.soot.values.JJavaSootValue;

/*
 * TODO: pull up the fields into JJavaConstantValue.
 *       add interfaces for toString() and toSExpression().
 */

public abstract class JJavaSootConstantValue extends JJavaSootValue 
{	
	protected JJavaSootConstantValue()
	{
	}
	
	/*
	 * subclass responsibility
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
	
	public static JJavaSootConstantValue produce(Value aValue) throws Exception
	{
		if(aValue instanceof Constant)
			return JJavaSootConstantValue.produce((Constant) aValue);
		
		return null;
	}
	
	public static JJavaSootConstantValue produce(Constant aConstantValue) throws Exception
	{	
		if( JJavaSootIntegerConstant.modelsConstantValue(aConstantValue) )
			return new JJavaSootIntegerConstant(aConstantValue);
		
		if( JJavaSootFloatConstant.modelsConstantValue(aConstantValue) )
			return new JJavaSootFloatConstant(aConstantValue);
		
		if( JJavaSootLongConstant.modelsConstantValue(aConstantValue) )
			return new JJavaSootLongConstant(aConstantValue);
		
		if( JJavaSootDoubleConstant.modelsConstantValue(aConstantValue) )
			return new JJavaSootDoubleConstant(aConstantValue);
		
		if( JJavaSootClassReferenceConstant.modelsConstantValue(aConstantValue) )
			return new JJavaSootClassReferenceConstant(aConstantValue);
		
		if( JJavaSootStringConstant.modelsConstantValue(aConstantValue) )
			return new JJavaSootStringConstant(aConstantValue);
		
		if( JJavaSootNullConstant.modelsConstantValue(aConstantValue) )
			return JJavaSootNullConstant.getInstance();
		
		throw new Exception("Unsupported Java Constant Type: " + aConstantValue.toString());
	}
	
	public static boolean modelsValue(Value aValue)
	{
		if(aValue instanceof Constant)
			return modelsConstantValue((Constant) aValue);
		
		return false;
	}
	
	protected static boolean modelsConstantValue(Constant aConstantValue)
	{
		return JJavaSootIntegerConstant.modelsConstantValue(aConstantValue) ||
			   JJavaSootFloatConstant.modelsConstantValue(aConstantValue) ||
			   JJavaSootLongConstant.modelsConstantValue(aConstantValue) ||
			   JJavaSootDoubleConstant.modelsConstantValue(aConstantValue) ||
			   JJavaSootClassReferenceConstant.modelsConstantValue(aConstantValue) ||
			   JJavaSootStringConstant.modelsConstantValue(aConstantValue) || 
			   JJavaSootNullConstant.modelsConstantValue(aConstantValue); 
	}
	
	/**
	 * (... value)
	 * @return
	 */
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
		aStringBuffer.append(this.valueType);
		
		aStringBuffer.append(" ");
		
		aStringBuffer.append(this.value);
	}
}
