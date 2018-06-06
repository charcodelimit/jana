package jana.lang.java.soot.jimple.instructions.arithmetic.logic;

import soot.jimple.Expr;
import soot.jimple.internal.JOrExpr;

public class JJavaSootJimpleOrExpr extends JJavaSootJimpleArithmeticLogicInstruction
{
	public JJavaSootJimpleOrExpr(Expr aJimpleExpression) throws Exception
	{
		this((JOrExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleOrExpr(JOrExpr anOrExpression) throws Exception
	{
		super(anOrExpression);
		this.instructionType = "jimple-arithmetic-logic-instruction-or";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JOrExpr);
	}
}
