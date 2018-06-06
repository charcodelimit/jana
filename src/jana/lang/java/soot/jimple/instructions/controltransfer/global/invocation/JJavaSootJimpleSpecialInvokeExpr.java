package jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation;

import soot.jimple.InvokeExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;


public class JJavaSootJimpleSpecialInvokeExpr extends JJavaSootJimpleDynamicInvokeExpr
{	
	public JJavaSootJimpleSpecialInvokeExpr(InvokeExpr invokeStatement) throws Exception
	{
		this((JSpecialInvokeExpr) invokeStatement);
	}
	
    public JJavaSootJimpleSpecialInvokeExpr(JSpecialInvokeExpr invokeStatement) throws Exception
	{
    	super(invokeStatement);
    	
    	this.instructionType = "jimple-invoke-special-instruction";
    	this.localVariable = JJavaSootJimpleReferenceValueLocal.produce(invokeStatement.getBase());
	}
    
    public static boolean modelsJimpleInvokeExpression(InvokeExpr invokeExpression)
	{
    	return (invokeExpression instanceof JSpecialInvokeExpr);
	}
}
