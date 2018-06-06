package jana.lang.java.soot.jimple.instructions.arithmetic;

import soot.jimple.Expr;
import soot.jimple.internal.JShlExpr;

public class JJavaSootJimpleShlExpr extends JJavaSootJimpleArithmeticInstruction
{
	public JJavaSootJimpleShlExpr(Expr aJimpleExpression) throws Exception
	{
		this((JShlExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleShlExpr(JShlExpr anShlExpression) throws Exception
	{
		super(anShlExpression);
		this.instructionType = "jimple-arithmetic-instruction-shl";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JShlExpr);
	}
}
