package jana.lang.java.soot.jimple.instructions;

import soot.jimple.Expr;
import soot.jimple.internal.JLengthExpr;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;

/**
 * calculates the length of an array that is assigned to a local variable
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleLengthExpr extends JJavaSootJimpleInstruction
{
	protected JJavaSootJimpleReferenceValueLocal localVariable;
	
	public JJavaSootJimpleLengthExpr(Expr jimpleExpression) throws Exception
	{
		this((JLengthExpr) jimpleExpression);
	}
	
	public JJavaSootJimpleLengthExpr(JLengthExpr lengthExpression) throws Exception
	{
		this.instructionType = "jimple-length-instruction";
		this.localVariable = JJavaSootJimpleReferenceValueLocal.produce(lengthExpression.getOp());
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer) 
	{
		aStringBuffer.append(this.instructionType);
		
		aStringBuffer.append(" (");
		this.localVariable.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return( anExpression instanceof JLengthExpr);
	}
}
