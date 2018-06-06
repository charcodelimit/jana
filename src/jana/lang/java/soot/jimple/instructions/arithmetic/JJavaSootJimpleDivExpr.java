package jana.lang.java.soot.jimple.instructions.arithmetic;

import soot.jimple.Expr;
import soot.jimple.internal.JDivExpr;

public class JJavaSootJimpleDivExpr extends JJavaSootJimpleArithmeticInstruction
{
	public JJavaSootJimpleDivExpr(Expr aJimpleExpression) throws Exception
	{
		this((JDivExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleDivExpr(JDivExpr anDivExpression) throws Exception
	{
		super(anDivExpression);
		this.instructionType = "jimple-arithmetic-instruction-div";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JDivExpr);
	}
}
