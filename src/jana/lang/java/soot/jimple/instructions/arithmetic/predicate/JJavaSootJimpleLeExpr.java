package jana.lang.java.soot.jimple.instructions.arithmetic.predicate;

import soot.jimple.ConditionExpr;
import soot.jimple.Expr;
import soot.jimple.LeExpr;

public class JJavaSootJimpleLeExpr extends JJavaSootJimplePredicateInstruction
{
	public JJavaSootJimpleLeExpr(ConditionExpr conditionExpression) throws Exception
	{
		this((LeExpr) conditionExpression);
	}
	
	public JJavaSootJimpleLeExpr(LeExpr aLeExpression) throws Exception
	{
		super("le-p", aLeExpression);
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return (anExpression instanceof LeExpr);
	}
}


