package jana.lang.java.soot.jimple.instructions.arithmetic.predicate;

import soot.jimple.ConditionExpr;
import soot.jimple.Expr;
import soot.jimple.LtExpr;


public class JJavaSootJimpleLtExpr extends JJavaSootJimplePredicateInstruction
{
	public JJavaSootJimpleLtExpr(ConditionExpr conditionExpression) throws Exception
	{
		this((LtExpr) conditionExpression);
	}
	
	public JJavaSootJimpleLtExpr(LtExpr aLtExpression) throws Exception
	{
		super("lt-p", aLtExpression);
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof LtExpr);
	}
}


