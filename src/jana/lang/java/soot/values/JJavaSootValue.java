package jana.lang.java.soot.values;

import soot.Value;
import jana.lang.java.soot.jimple.values.JJavaSootJimpleValue;
import jana.lang.java.soot.values.constants.JJavaSootConstantValue;
import jana.metamodel.values.JValue;

public abstract class JJavaSootValue extends JValue
{
	protected String valueType;
	protected String value;
	// protected JType type;
	
	
	public static JJavaSootValue produce(Value aValue) throws Exception
	{
		if(JJavaSootConstantValue.modelsValue(aValue))
			return JJavaSootConstantValue.produce(aValue);
		
		if(JJavaSootJimpleValue.modelsValue(aValue))
			return JJavaSootJimpleValue.produce(aValue);
		
		assert(aValue != null);
		
		throw new Exception("Unknown Value type " + aValue);
	}
	
	
	@Override
	protected boolean compareValues(JValue value)
	{
		if(value instanceof JJavaSootValue)
		{
			JJavaSootValue sootValue;
			
			sootValue = (JJavaSootValue) value;
			
			return sootValue.valueType.equals(this.valueType) && 
				   sootValue.type.equals(this.type) && 
				   sootValue.value.equals(this.value);
		}
			
		return false;
	}

	@Override
	public int hashCode()
	{
		return this.valueType.hashCode() ^ this.type.hashCode() ^ this.type.hashCode();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (... value type)
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.valueType);
		
		aStringBuffer.append(" ");
		
		aStringBuffer.append(this.value);
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
}
