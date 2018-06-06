package jana.lang.java.soot.jimple.instructions.object.creation;

import soot.jimple.AnyNewExpr;
import soot.jimple.Expr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import jana.lang.java.soot.jimple.instructions.object.JJavaSootJimpleObjectInstruction;

public abstract class JJavaSootJimpleObjectInstantiationInstruction extends
		JJavaSootJimpleObjectInstruction
{

	public static JJavaSootJimpleObjectInstantiationInstruction produce(Expr anExpression) throws Exception
	{
		return produce((AnyNewExpr) anExpression);
	}
	
	/**
	 * @see JJavaSootJimpleNewArrayExpr
	 * 
	 * @param newExpression
	 * @return
	 * @throws Exception
	 */
	public static JJavaSootJimpleObjectInstantiationInstruction produce(AnyNewExpr newExpression) throws Exception
	{
		if(JJavaSootJimpleNewExpr.modelsJimpleAnyNewExpression(newExpression))
			return new JJavaSootJimpleNewExpr(newExpression);
		
		/** 
		 * chr: Here I break the pattern intendedly!
		 * 
		 * @see: JJavaSootJimpleNewArrayExpr
		 */
		if(newExpression instanceof JNewArrayExpr)
			return new JJavaSootJimpleNewArrayExpr((JNewArrayExpr) newExpression);
		
		if(newExpression instanceof JNewMultiArrayExpr)
			return new JJavaSootJimpleNewArrayExpr((JNewMultiArrayExpr) newExpression);
		
		throw new Exception("Unknown Object Instantiation Instruction " + newExpression.toString() );
	}
	
	/**
	 * Override this method! Otherwise cycles will occur.
	 * chr: unfortunately this cannot be enforced with Java interfaces.
	 * 
	 * @param newExpression
	 * @return
	 */
	public static boolean modelsJimpleAnyNewExpression(AnyNewExpr newExpression)
	{
		return JJavaSootJimpleNewExpr.modelsJimpleAnyNewExpression(newExpression) ||
			   JJavaSootJimpleNewArrayExpr.modelsJimpleAnyNewExpression(newExpression);
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		if( anExpression instanceof AnyNewExpr)
			return modelsJimpleAnyNewExpression((AnyNewExpr) anExpression);		
		
		return false;
	}
}
