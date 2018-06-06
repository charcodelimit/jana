package jana.lang.java.soot.jimple.instructions.arithmetic.logic;

import soot.jimple.Expr;
import soot.jimple.internal.JXorExpr;

public class JJavaSootJimpleXorExpr extends JJavaSootJimpleArithmeticLogicInstruction
{
	public JJavaSootJimpleXorExpr(Expr aJimpleExpression) throws Exception
	{
		this((JXorExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleXorExpr(JXorExpr anXorExpression) throws Exception
	{
		super(anXorExpression);
		this.instructionType = "jimple-arithmetic-logic-instruction-xor";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JXorExpr);
	}
}
