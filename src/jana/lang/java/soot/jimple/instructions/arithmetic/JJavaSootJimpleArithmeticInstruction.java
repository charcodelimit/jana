package jana.lang.java.soot.jimple.instructions.arithmetic;

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import soot.jimple.Expr;
import soot.jimple.internal.AbstractBinopExpr;
import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleInstruction;
import jana.lang.java.soot.jimple.instructions.arithmetic.comparison.JJavaSootJimpleComparisonInstruction;
import jana.lang.java.soot.jimple.instructions.arithmetic.logic.JJavaSootJimpleArithmeticLogicInstruction;
import jana.lang.java.soot.jimple.instructions.arithmetic.predicate.JJavaSootJimplePredicateInstruction;
import jana.lang.java.soot.values.JJavaSootValue;
import jana.metamodel.JNamedElement;

public abstract class JJavaSootJimpleArithmeticInstruction extends JJavaSootJimpleInstruction
{
	List<JJavaSootValue> arguments;
	
	protected JJavaSootJimpleArithmeticInstruction(AbstractBinopExpr binopExpr) throws Exception
	{
		this( binopExpr.getOp1(), binopExpr.getOp2() );
	}
	
	public JJavaSootJimpleArithmeticInstruction(Value aValue) throws Exception
	{
		this.arguments = new ArrayList<JJavaSootValue>(1);
		this.arguments.add( JJavaSootValue.produce(aValue) );	
	}
	
	protected JJavaSootJimpleArithmeticInstruction( Value leftHandSideValue, Value rightHandSideValue ) throws Exception
	{
		this.arguments = new ArrayList<JJavaSootValue>(2);
		this.arguments.add( JJavaSootValue.produce(leftHandSideValue) );
		this.arguments.add( JJavaSootValue.produce(rightHandSideValue) );
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.instructionType);
		
		JNamedElement.elementListToSExpression(arguments, aStringBuffer);
	}
	
	public static JJavaSootJimpleArithmeticInstruction produce(Expr anExpression) throws Exception
	{
		if(JJavaSootJimpleAddExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleAddExpr(anExpression);
		
		if(JJavaSootJimpleSubExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleSubExpr(anExpression);
		
		if(JJavaSootJimpleMulExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleMulExpr(anExpression);
		
		if(JJavaSootJimpleDivExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleDivExpr(anExpression);
		
		if(JJavaSootJimpleRemExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleRemExpr(anExpression);
		
		if(JJavaSootJimpleShlExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleShlExpr(anExpression);
		
		if(JJavaSootJimpleShrExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleShrExpr(anExpression);
		
		if(JJavaSootJimpleNegExpr.modelsJimpleExpression(anExpression))
			return new JJavaSootJimpleNegExpr(anExpression);
		
		if( JJavaSootJimplePredicateInstruction.modelsJimpleExpression(anExpression))
			return JJavaSootJimplePredicateInstruction.produce(anExpression);
		
		if(JJavaSootJimpleArithmeticLogicInstruction.modelsJimpleExpression(anExpression))
			return JJavaSootJimpleArithmeticLogicInstruction.produce(anExpression);
		
		if(JJavaSootJimpleComparisonInstruction.modelsJimpleExpression(anExpression))
			return JJavaSootJimpleComparisonInstruction.produce(anExpression);
		
		throw new Exception("Unknown Arithmetic Instruction " + anExpression.toString() );
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return JJavaSootJimpleAddExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleSubExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleMulExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleDivExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleRemExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleShlExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleShrExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleNegExpr.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimplePredicateInstruction.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleArithmeticLogicInstruction.modelsJimpleExpression(anExpression) ||
		       JJavaSootJimpleComparisonInstruction.modelsJimpleExpression(anExpression);
	}
}
