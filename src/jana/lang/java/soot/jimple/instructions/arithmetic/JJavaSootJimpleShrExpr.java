package jana.lang.java.soot.jimple.instructions.arithmetic;

import soot.jimple.Expr;
import soot.jimple.internal.JShrExpr;

public class JJavaSootJimpleShrExpr extends JJavaSootJimpleArithmeticInstruction
{
	public JJavaSootJimpleShrExpr(Expr aJimpleExpression) throws Exception
	{
		this((JShrExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleShrExpr(JShrExpr anShrExpression) throws Exception
	{
		super(anShrExpression);
		this.instructionType = "jimple-arithmetic-instruction-shr";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JShrExpr);
	}
}