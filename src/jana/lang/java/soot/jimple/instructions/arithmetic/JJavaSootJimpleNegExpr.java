package jana.lang.java.soot.jimple.instructions.arithmetic;

import jana.lang.java.soot.values.JJavaSootValue;
import soot.jimple.Expr;
import soot.jimple.internal.JNegExpr;


public class JJavaSootJimpleNegExpr extends JJavaSootJimpleArithmeticInstruction
{
	protected JJavaSootValue value;
	
	JJavaSootJimpleNegExpr(Expr jimpleExpression) throws Exception
	{
		this((JNegExpr) jimpleExpression);
	}
	
	JJavaSootJimpleNegExpr(JNegExpr negExpression) throws Exception
	{
		super(negExpression.getOp());
		this.instructionType = "jimple-arithmetic-instruction-neg";
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return( anExpression instanceof JNegExpr);
	}
}
