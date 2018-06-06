package jana.lang.java.soot.jimple.instructions.arithmetic;

import soot.jimple.Expr;
import soot.jimple.internal.JSubExpr;

public class JJavaSootJimpleSubExpr extends JJavaSootJimpleArithmeticInstruction
{
	public JJavaSootJimpleSubExpr(Expr aJimpleExpression) throws Exception
	{
		this((JSubExpr) aJimpleExpression);
	}
	
	public JJavaSootJimpleSubExpr(JSubExpr anSubExpression) throws Exception
	{
		super(anSubExpression);
		this.instructionType = "jimple-arithmetic-instruction-sub";
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof JSubExpr);
	}
}
