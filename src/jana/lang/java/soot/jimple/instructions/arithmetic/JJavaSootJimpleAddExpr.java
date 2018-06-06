package jana.lang.java.soot.jimple.instructions.arithmetic;

import soot.jimple.Expr;
import soot.jimple.internal.JAddExpr;

public class JJavaSootJimpleAddExpr extends JJavaSootJimpleArithmeticInstruction
{
	public JJavaSootJimpleAddExpr(Expr aJimpleExpression) throws Exception
	{
		this((JAddExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleAddExpr(JAddExpr anAddExpression) throws Exception
	{
		super(anAddExpression);
		this.instructionType = "jimple-arithmetic-instruction-add";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JAddExpr);
	}
}

