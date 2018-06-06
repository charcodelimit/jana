package jana.lang.java.soot.jimple.values.references;

import soot.Value;
import soot.jimple.ParameterRef;

public class JJavaSootJimpleParameterRef extends JJavaSootJimpleReferenceValue
{	
	private int index;
	
	public JJavaSootJimpleParameterRef(Value aValue) throws Exception
	{
		this((ParameterRef) aValue);
	}
	
	public JJavaSootJimpleParameterRef(ParameterRef aParameterReference) throws Exception
	{
		super(aParameterReference.getType());
		this.valueType = "jimple-reference-value-argument";
		this.value = "";
		this.index = aParameterReference.getIndex();
	}
	
	/**
	 * (jimple-reference-value-parameter parameter-index parameter-type)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-reference-value-parameter parameter-index parameter-type)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.valueType);
		
		aStringBuffer.append(" ");
		aStringBuffer.append(this.index);
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof ParameterRef);
	}
}
