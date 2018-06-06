package jana.lang.java.soot.jimple.instructions.arithmetic.predicate;

import soot.jimple.ConditionExpr;
import soot.jimple.Expr;
import soot.jimple.GtExpr;

public class JJavaSootJimpleGtExpr extends JJavaSootJimplePredicateInstruction
{
	public JJavaSootJimpleGtExpr(ConditionExpr conditionExpression) throws Exception
	{
		this((GtExpr) conditionExpression);
	}
	
	public JJavaSootJimpleGtExpr(GtExpr aGtExpression) throws Exception
	{
		super("gt-p", aGtExpression);
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof GtExpr);
	}
}

