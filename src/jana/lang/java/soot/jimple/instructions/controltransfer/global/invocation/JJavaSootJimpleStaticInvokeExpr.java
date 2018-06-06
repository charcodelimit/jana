package jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation;

import soot.jimple.InvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;

public class JJavaSootJimpleStaticInvokeExpr extends JJavaSootJimpleInvokeInstruction
{	
	public JJavaSootJimpleStaticInvokeExpr(InvokeExpr invokeExpression) throws Exception
	{
		this((JStaticInvokeExpr) invokeExpression);
	}
	
	public JJavaSootJimpleStaticInvokeExpr(JStaticInvokeExpr invokeExpression) throws Exception
	{
		super( invokeExpression );
		
		this.instructionType = "jimple-invoke-static-instruction";
	}
	
	public static boolean modelsJimpleInvokeExpression(InvokeExpr invokeExpression)
	{
		return (invokeExpression instanceof JStaticInvokeExpr);
	}
	
	/**
	 * (jimple-invoke-static-instruction reference-type method-reference arguments)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
	
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-invoke-static-instruction reference-type method-reference arguments)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.instructionType);
		super.toSExpression(aStringBuffer);
	}
}


