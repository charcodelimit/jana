package jana.lang.java.soot.jimple.values.references;

import jana.lang.java.soot.values.JJavaSootValue;
import soot.Value;
import soot.jimple.internal.JArrayRef;

public class JJavaSootJimpleArrayRef extends JJavaSootJimpleReferenceValue
{
	JJavaSootJimpleReferenceValueLocal localVariable;
	JJavaSootValue indexValue;
	
	public JJavaSootJimpleArrayRef(Value aValue) throws Exception
	{
		this((JArrayRef) aValue);
	}
	
	public JJavaSootJimpleArrayRef(JArrayRef anArrayReference) throws Exception
	{
		super(anArrayReference.getType());
		
		this.valueType = "jimple-reference-value-array";
		this.localVariable = JJavaSootJimpleReferenceValueLocal.produce(anArrayReference.getBase());
		this.indexValue = JJavaSootValue.produce(anArrayReference.getIndex());
		this.value = "";
	}

	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof JArrayRef);
	}
	
	/**
	 * (jimple-reference-value-array local-variable type index-value)
	 * 
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-reference-value-array local-variable type index-value)
	 * 
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append(this.valueType);
		
		aStringBuffer.append(" (");
		this.localVariable.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		aStringBuffer.append(" (");
		this.indexValue.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
}
