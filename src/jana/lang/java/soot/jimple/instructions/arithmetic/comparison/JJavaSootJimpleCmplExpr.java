package jana.lang.java.soot.jimple.instructions.arithmetic.comparison;

import soot.jimple.Expr;
import soot.jimple.internal.JCmplExpr;

public class JJavaSootJimpleCmplExpr extends JJavaSootJimpleComparisonInstruction
{
	public JJavaSootJimpleCmplExpr(Expr aJimpleExpression) throws Exception
	{
		this((JCmplExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleCmplExpr(JCmplExpr aCmplExpression) throws Exception
	{
		super(aCmplExpression);
		this.instructionType = "jimple-arithmetic-instruction-cmpl";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JCmplExpr);
	}
}
