package jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation;

import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JInterfaceInvokeExpr;


public class JJavaSootJimpleInterfaceInvokeExpr extends JJavaSootJimpleDynamicInvokeExpr
{
	public JJavaSootJimpleInterfaceInvokeExpr(InvokeExpr invokeExpression) throws Exception
	{
		this((JInterfaceInvokeExpr) invokeExpression);
	}
	
    public JJavaSootJimpleInterfaceInvokeExpr(JInterfaceInvokeExpr invokeStatement) throws Exception
	{
    	super(invokeStatement);
    	
    	this.instructionType = "jimple-invoke-interface-instruction";
    	this.localVariable = JJavaSootJimpleReferenceValueLocal.produce(invokeStatement.getBase());
	}
    
    public static boolean modelsJimpleInvokeExpression(InvokeExpr invokeExpression)
	{
    	return (invokeExpression instanceof JInterfaceInvokeExpr);
	}
}


