package jana.lang.java.soot.jimple.values.references;

import soot.Value;
import soot.jimple.StaticFieldRef;

public class JJavaSootJimpleStaticFieldRef extends
		JJavaSootJimpleFieldReference
{
	public JJavaSootJimpleStaticFieldRef(Value aValue) throws Exception
	{
		this((StaticFieldRef) aValue);
	}
	
	public JJavaSootJimpleStaticFieldRef(StaticFieldRef aStaticFieldReference) throws Exception
	{
		super(aStaticFieldReference.getFieldRef());
		this.valueType = "jimple-reference-value-class-variable";
	}
	
	/**
	 * (value-reference-field-static declaring-class-signature type field-name)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (value-reference-field-static declaring-class-signature type field-name)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append(this.valueType);
		
		super.toSExpression(aStringBuffer);
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof StaticFieldRef);
	}
}
