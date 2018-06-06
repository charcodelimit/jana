package jana.lang.java.soot.jimple.instructions.arithmetic.comparison;

import soot.jimple.Expr;
import soot.jimple.internal.AbstractBinopExpr;

import jana.lang.java.soot.jimple.instructions.arithmetic.JJavaSootJimpleArithmeticInstruction;

/**
 * "Each floating-point type has two comparison instructions: fcmpl and fcmpg for type float, and dcmpl and dcmpg for type double. 
 *  The variants differ only in their treatment of NaN. NaN is unordered, so all floating-point comparisons fail if either of their operands is NaN. 
 *  The compiler chooses the variant of the comparison instruction for the appropriate type that produces the same result whether the comparison 
 *  fails on non-NaN values or encounters a NaN."
 *  
 *  " 9 dload_1"
 *  "10 ldc2_w #4 // Push double constant 100.1"
 *  "13 dcmpg	  // To do the compare and branch we have to use..."
 *  "14 iflt 5	  // ...two instructions"
 *  
 *  lcmp: "Both value1 and value2 must be of type long. They are both popped from the operand stack, and a signed integer comparison is performed. 
 *         If value1 is greater than value2, the int value 1 is pushed onto the operand stack. 
 *         If value1 is equal to value2, the int value 0 is pushed onto the operand stack. 
 *         If value1 is less than value2, the int value -1 is pushed onto the operand stack."
 *
 *  All citations from:  The Java(TM) Virtual Machine Specification
 *
 * @author chr
 *
 */
public abstract class JJavaSootJimpleComparisonInstruction extends
		JJavaSootJimpleArithmeticInstruction
{
	protected JJavaSootJimpleComparisonInstruction(AbstractBinopExpr binopExpr) throws Exception
	{
		super(binopExpr);
	}
	
	public static JJavaSootJimpleComparisonInstruction produce(Expr anExpression) throws Exception
	{
		if(JJavaSootJimpleCmpExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleCmpExpr(anExpression);
		
		if(JJavaSootJimpleCmplExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleCmplExpr(anExpression);
		
		if(JJavaSootJimpleCmpgExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleCmpgExpr(anExpression);
		
		throw new Exception("Unknown Comparison Instruction " + anExpression.toString() );
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{	
		return JJavaSootJimpleCmpExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleCmplExpr.modelsJimpleExpression(anExpression) ||
			   JJavaSootJimpleCmpgExpr.modelsJimpleExpression(anExpression);	
	}
}
