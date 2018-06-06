package jana.lang.java.soot.jimple.instructions.arithmetic.logic;

import soot.Value;
import soot.jimple.Expr;
import soot.jimple.internal.AbstractBinopExpr;
import jana.lang.java.soot.jimple.instructions.arithmetic.JJavaSootJimpleArithmeticInstruction;

public abstract class JJavaSootJimpleArithmeticLogicInstruction extends
		JJavaSootJimpleArithmeticInstruction
{
	protected JJavaSootJimpleArithmeticLogicInstruction(AbstractBinopExpr binopExpr) throws Exception
	{
		super(binopExpr);
	}
	
	protected JJavaSootJimpleArithmeticLogicInstruction( Value firstValue, Value secondValue ) throws Exception
	{
		super(firstValue,secondValue);
	}
		
	public static JJavaSootJimpleArithmeticInstruction produce(Expr anExpression) throws Exception
	{
		if(JJavaSootJimpleAndExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleAndExpr(anExpression);
		
		if(JJavaSootJimpleOrExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleOrExpr(anExpression);
		
		if(JJavaSootJimpleXorExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleXorExpr(anExpression);
		
		if(JJavaSootJimpleUshrExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleUshrExpr(anExpression);
			
		throw new Exception("Unknown Multiple Argument Arithmetic Logic Instruction " + anExpression.toString() );
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return JJavaSootJimpleAndExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleOrExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleXorExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleUshrExpr.modelsJimpleExpression(anExpression);
	}
}
