package jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation;

import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;


public class JJavaSootJimpleVirtualInvokeExpr extends JJavaSootJimpleDynamicInvokeExpr
{       
	public JJavaSootJimpleVirtualInvokeExpr(InvokeExpr invokeExpression) throws Exception
	{
		this((JVirtualInvokeExpr) invokeExpression);
	}
	
    public JJavaSootJimpleVirtualInvokeExpr(JVirtualInvokeExpr invokeStatement) throws Exception
	{
    	super(invokeStatement);
    	
    	this.instructionType = "jimple-invoke-virtual-instruction";
    	this.localVariable = JJavaSootJimpleReferenceValueLocal.produce(invokeStatement.getBase());
	}
    
    public static boolean modelsJimpleInvokeExpression(InvokeExpr invokeExpression)
    {
    	return (invokeExpression instanceof JVirtualInvokeExpr);
    }
}
