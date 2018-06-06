package jana.lang.java.soot.jimple.instructions.arithmetic;

import soot.jimple.Expr;
import soot.jimple.internal.JMulExpr;

public class JJavaSootJimpleMulExpr extends JJavaSootJimpleArithmeticInstruction
{
	public JJavaSootJimpleMulExpr(Expr aJimpleExpression) throws Exception
	{
		this((JMulExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleMulExpr(JMulExpr anMulExpression) throws Exception
	{
		super(anMulExpression);
		this.instructionType = "jimple-arithmetic-instruction-mul";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JMulExpr);
	}
}