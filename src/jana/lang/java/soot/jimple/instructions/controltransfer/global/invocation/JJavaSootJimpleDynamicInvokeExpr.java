package jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation;

import soot.jimple.InvokeExpr;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;

/**
 * Invocation of instance methods with the instance referenced by the localVariable as receiver.
 * 
 * @author chr
 *
 */
public abstract class JJavaSootJimpleDynamicInvokeExpr extends JJavaSootJimpleInvokeInstruction
{
	protected JJavaSootJimpleReferenceValueLocal localVariable;
	
	protected JJavaSootJimpleDynamicInvokeExpr(InvokeExpr invokeExpression)	throws Exception
	{
		super(invokeExpression);
	}
	
	/**
	 * `(,invoke-statement local-variable method-reference arguments)
	 * 
	 * @return
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * `(,invoke-statement local-variable method-reference arguments)
	 * 
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.instructionType);
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.localVariable.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		super.toSExpression(aStringBuffer);
	}
}
