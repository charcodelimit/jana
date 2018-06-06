package jana.lang.java.soot.jimple.instructions.arithmetic.logic;

import soot.jimple.Expr;
import soot.jimple.internal.JAndExpr;

public class JJavaSootJimpleAndExpr extends JJavaSootJimpleArithmeticLogicInstruction
{
	public JJavaSootJimpleAndExpr(Expr aJimpleExpression) throws Exception
	{
		this((JAndExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleAndExpr(JAndExpr anAndExpression) throws Exception
	{
		super(anAndExpression);
		this.instructionType = "jimple-arithmetic-logic-instruction-and";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JAndExpr);
	}
}
