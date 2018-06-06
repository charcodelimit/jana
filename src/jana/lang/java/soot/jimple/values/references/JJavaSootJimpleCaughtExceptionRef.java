package jana.lang.java.soot.jimple.values.references;

import soot.Value;
import soot.jimple.internal.JCaughtExceptionRef;

/***
 * (jimple-reference-value-caught-exception type) The empty String in field this.value is handled correctly by toSExpression!
 * 
 * @author chr
 *
 */

public class JJavaSootJimpleCaughtExceptionRef extends JJavaSootJimpleReferenceValue
{

	public JJavaSootJimpleCaughtExceptionRef(Value aValue) throws Exception
	{
		this((JCaughtExceptionRef) aValue);
	}
	
	public JJavaSootJimpleCaughtExceptionRef(JCaughtExceptionRef aCaughtExceptionReference) throws Exception
	{
		super(aCaughtExceptionReference.getType());
		
		this.valueType = "jimple-reference-value-caught-exception";
		this.value = "";
	}

	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof JCaughtExceptionRef);
	}
}
