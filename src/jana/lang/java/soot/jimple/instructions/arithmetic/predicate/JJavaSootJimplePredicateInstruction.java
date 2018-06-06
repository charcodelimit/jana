package jana.lang.java.soot.jimple.instructions.arithmetic.predicate;

import soot.jimple.ConditionExpr;
import soot.jimple.Expr;
import jana.lang.java.soot.jimple.instructions.arithmetic.JJavaSootJimpleArithmeticInstruction;

/**
 * 
 * @author chr
 *
 */
public abstract class JJavaSootJimplePredicateInstruction extends
		JJavaSootJimpleArithmeticInstruction
{	
	public JJavaSootJimplePredicateInstruction(String theInstructionType, ConditionExpr aConditionExpression) throws Exception
	{
		super( aConditionExpression.getOp1(), aConditionExpression.getOp2() );
		this.instructionType = "jimple-arithmetic-predicate-" + theInstructionType;
	}
	
	public static JJavaSootJimplePredicateInstruction produce(Expr anExpression) throws Exception
	{
		return produce((ConditionExpr) anExpression);
	}
	
	public static JJavaSootJimplePredicateInstruction produce(ConditionExpr conditionExpression) throws Exception
	{
		if(JJavaSootJimpleEqExpr.modelsJimpleExpression(conditionExpression))
			return new JJavaSootJimpleEqExpr(conditionExpression);
		
		if(JJavaSootJimpleGeExpr.modelsJimpleExpression(conditionExpression))
			return new JJavaSootJimpleGeExpr(conditionExpression);
		
		if(JJavaSootJimpleGtExpr.modelsJimpleExpression(conditionExpression))
			return new JJavaSootJimpleGtExpr(conditionExpression);
		
		if(JJavaSootJimpleLeExpr.modelsJimpleExpression(conditionExpression))
			return new JJavaSootJimpleLeExpr(conditionExpression);
		
		if(JJavaSootJimpleLtExpr.modelsJimpleExpression(conditionExpression))
			return new JJavaSootJimpleLtExpr(conditionExpression);
		
		if(JJavaSootJimpleNeExpr.modelsJimpleExpression(conditionExpression))
			return new JJavaSootJimpleNeExpr(conditionExpression);
		
		throw new Exception("Unknown Predicate Instruction " + conditionExpression.toString() );
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		if( !(anExpression instanceof ConditionExpr))
			return false;
		
		return JJavaSootJimpleEqExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleGeExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleGtExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleLeExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleLtExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleNeExpr.modelsJimpleExpression(anExpression);	
	}
}
