package jana.lang.java.soot.jimple.instructions.arithmetic;

import soot.jimple.Expr;
import soot.jimple.internal.JRemExpr;

public class JJavaSootJimpleRemExpr extends JJavaSootJimpleArithmeticInstruction
{
	public JJavaSootJimpleRemExpr(Expr aJimpleExpression) throws Exception
	{
		this((JRemExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleRemExpr(JRemExpr anRemExpression) throws Exception
	{
		super(anRemExpression);
		this.instructionType = "jimple-arithmetic-instruction-rem";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JRemExpr);
	}
}
