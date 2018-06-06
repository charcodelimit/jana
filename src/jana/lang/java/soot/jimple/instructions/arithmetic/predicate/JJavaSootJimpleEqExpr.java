package jana.lang.java.soot.jimple.instructions.arithmetic.predicate;

import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.Expr;

public class JJavaSootJimpleEqExpr extends JJavaSootJimplePredicateInstruction
{
	public JJavaSootJimpleEqExpr(ConditionExpr conditionExpression) throws Exception
	{
		this((EqExpr) conditionExpression);
	}
	
	public JJavaSootJimpleEqExpr(EqExpr anEqExpression) throws Exception
	{
		super("eq-p", anEqExpression);
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof EqExpr);
	}
}
