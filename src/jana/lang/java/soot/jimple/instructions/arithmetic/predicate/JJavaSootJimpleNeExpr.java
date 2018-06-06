package jana.lang.java.soot.jimple.instructions.arithmetic.predicate;

import soot.jimple.ConditionExpr;
import soot.jimple.Expr;
import soot.jimple.NeExpr;


public class JJavaSootJimpleNeExpr extends JJavaSootJimplePredicateInstruction
{
	public JJavaSootJimpleNeExpr(ConditionExpr conditionExpression) throws Exception
	{
		this((NeExpr) conditionExpression);
	}
	
	public JJavaSootJimpleNeExpr(NeExpr aNeExpression) throws Exception
	{
		super("ne-p", aNeExpression);
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof NeExpr);
	}
}
