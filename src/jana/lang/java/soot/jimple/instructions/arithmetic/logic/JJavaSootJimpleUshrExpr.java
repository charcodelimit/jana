package jana.lang.java.soot.jimple.instructions.arithmetic.logic;

import soot.jimple.Expr;
import soot.jimple.internal.JUshrExpr;

public class JJavaSootJimpleUshrExpr extends JJavaSootJimpleArithmeticLogicInstruction
{
	public JJavaSootJimpleUshrExpr(Expr aJimpleExpression) throws Exception
	{
		this((JUshrExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleUshrExpr(JUshrExpr anUshrExpression) throws Exception
	{
		super(anUshrExpression);
		this.instructionType = "jimple-arithmetic-logic-instruction-ushr";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JUshrExpr);
	}
}
