package jana.lang.java.soot.jimple.instructions.object;

import soot.jimple.Expr;
import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleInstruction;
import jana.lang.java.soot.jimple.instructions.object.creation.JJavaSootJimpleObjectInstantiationInstruction;
import jana.lang.java.typesystem.JJavaType;

public abstract class JJavaSootJimpleObjectInstruction extends
		JJavaSootJimpleInstruction
{
	protected JJavaType type;

	public static JJavaSootJimpleObjectInstruction produce(Expr anExpression) throws Exception
	{
		if(JJavaSootJimpleObjectInstantiationInstruction.modelsJimpleExpression(anExpression))
			return JJavaSootJimpleObjectInstantiationInstruction.produce(anExpression);
		
		if(JJavaSootJimpleCastExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleCastExpr(anExpression);
		
		if(JJavaSootJimpleInstanceOfExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleInstanceOfExpr(anExpression);
			
		throw new Exception("Unknown Object Related Instruction " + anExpression.toString() );
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return JJavaSootJimpleObjectInstantiationInstruction.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleCastExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleInstanceOfExpr.modelsJimpleExpression(anExpression);
	}
}
