package jana.lang.java.soot.jimple.values;

import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValue;
import jana.lang.java.soot.values.JJavaSootValue;
import soot.Value;

/**
 * Update the meta-model!
 * This type hierarchy should be an instance of the Java meta-model as well!
 */
public abstract class JJavaSootJimpleValue extends JJavaSootValue
{	
	/**
	 * (value-type [values] type)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (value-type [values] type)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.valueType);
		
		if(this.value != null && this.value.length() > 0)
		{
			aStringBuffer.append(' ');
			aStringBuffer.append('\"');
			aStringBuffer.append(this.value);
			aStringBuffer.append('\"');
		}
		
		aStringBuffer.append(' ');		
		aStringBuffer.append('(');
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
	}
	
	public static JJavaSootJimpleValue produce(Value aValue) throws Exception
	{	
		if(JJavaSootJimpleReferenceValue.modelsValue(aValue))
			return JJavaSootJimpleReferenceValue.produce(aValue);
		
		throw new Exception("Unknown value type " + aValue.toString());
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return JJavaSootJimpleReferenceValue.modelsValue(aValue);
	}
}
