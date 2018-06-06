package jana.lang.java.soot.jimple.instructions.arithmetic.comparison;

import soot.jimple.Expr;
import soot.jimple.internal.JCmpExpr;

public class JJavaSootJimpleCmpExpr extends JJavaSootJimpleComparisonInstruction
{
	public JJavaSootJimpleCmpExpr(Expr aJimpleExpression) throws Exception
	{
		this((JCmpExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleCmpExpr(JCmpExpr aCmpExpression) throws Exception
	{
		super(aCmpExpression);
		this.instructionType = "jimple-arithmetic-instruction-cmp";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JCmpExpr);
	}
}
