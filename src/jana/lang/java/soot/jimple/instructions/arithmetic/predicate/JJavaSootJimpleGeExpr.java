package jana.lang.java.soot.jimple.instructions.arithmetic.predicate;

import soot.jimple.ConditionExpr;
import soot.jimple.Expr;
import soot.jimple.GeExpr;

public class JJavaSootJimpleGeExpr extends JJavaSootJimplePredicateInstruction
{
	public JJavaSootJimpleGeExpr(ConditionExpr conditionExpression) throws Exception
	{
		this((GeExpr) conditionExpression);
	}
	
	public JJavaSootJimpleGeExpr(GeExpr aGeExpression) throws Exception
	{
		super("ge-p", aGeExpression);
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof GeExpr);
	}
}


