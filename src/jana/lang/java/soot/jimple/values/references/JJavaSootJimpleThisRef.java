package jana.lang.java.soot.jimple.values.references;

import soot.Value;
import soot.jimple.ThisRef;

/**
 * Virtual - This is implicit in the Bytecode instruction
 * 
 * (jimple-reference-value-this type) The empty String in field this.value is handled correctly by toSExpression!
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleThisRef extends JJavaSootJimpleReferenceValue
{
	public JJavaSootJimpleThisRef(Value aValue) throws Exception
	{
		this((ThisRef) aValue);
	}
	
	public JJavaSootJimpleThisRef(ThisRef aThisRef) throws Exception
	{
		super( aThisRef.getType() );
		
		this.valueType = "jimple-reference-value-this";
		this.value = "";
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof ThisRef);
	}
}
