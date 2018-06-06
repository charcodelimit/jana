package jana.lang.java.soot.jimple.instructions.arithmetic.comparison;

import soot.jimple.Expr;
import soot.jimple.internal.JCmpgExpr;

public class JJavaSootJimpleCmpgExpr extends JJavaSootJimpleComparisonInstruction
{
	public JJavaSootJimpleCmpgExpr(Expr aJimpleExpression) throws Exception
	{
		this((JCmpgExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleCmpgExpr(JCmpgExpr aCmpgExpression) throws Exception
	{
		super(aCmpgExpression);
		this.instructionType = "jimple-arithmetic-instruction-cmpg";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JCmpgExpr);
	}
}
