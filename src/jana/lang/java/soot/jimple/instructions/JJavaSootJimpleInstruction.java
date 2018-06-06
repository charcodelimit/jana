package jana.lang.java.soot.jimple.instructions;

import soot.Unit;
import soot.jimple.Expr;
import soot.tagkit.LineNumberTag;
import soot.util.Chain;
import jana.lang.java.soot.jimple.instructions.arithmetic.JJavaSootJimpleArithmeticInstruction;
import jana.lang.java.soot.jimple.instructions.assignment.JJavaSootJimpleImaginaryAssignmentInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.JJavaSootJimpleControlTransferInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation.JJavaSootJimpleInvokeInstruction;
import jana.lang.java.soot.jimple.instructions.object.JJavaSootJimpleObjectInstruction;
import jana.lang.java.soot.jimple.instructions.synchronization.JJavaSootJimpleCondCriticalSectionInstruction;
import jana.metamodel.JInstruction;
import jana.metamodel.SExpression;

/**
 * Factory for Jimple instructions
 * @author chr
 *
 */
public abstract class JJavaSootJimpleInstruction extends JInstruction implements SExpression
{	
	protected JJavaSootJimpleInstruction()
	{
		this.lineNumber = -1;
	}
	
	protected JJavaSootJimpleInstruction(Unit aUnit)
	{
		if (aUnit.hasTag("LineNumberTag"))
		{
           LineNumberTag tag = (LineNumberTag) aUnit.getTag("LineNumberTag");
           this.lineNumber = tag.getLineNumber();
        }
	}
	
	public abstract String toSExpression();
	
	/**
	 * Expressions are parts of the right hand sides of units. 
	 * Position-wise they are the same as values, but they correspond to Java Bytecode instructions.
	 * 
	 * @param anExpression
	 * @return
	 * @throws Exception
	 */
	public static JJavaSootJimpleInstruction produce(Expr anExpression) throws Exception
	{
		if( JJavaSootJimpleObjectInstruction.modelsJimpleExpression(anExpression) )
			return JJavaSootJimpleObjectInstruction.produce(anExpression);
		
		if( JJavaSootJimpleInvokeInstruction.modelsJimpleExpression(anExpression))
			return JJavaSootJimpleInvokeInstruction.produce(anExpression);
		
		if( JJavaSootJimpleArithmeticInstruction.modelsJimpleExpression(anExpression))
			return JJavaSootJimpleArithmeticInstruction.produce(anExpression);
		
		if( JJavaSootJimpleLengthExpr.modelsJimpleExpression(anExpression) )
			return new JJavaSootJimpleLengthExpr(anExpression);
		
		throw new Exception("Unknown Jimple Expression " + anExpression.toString());
	}
	
	/**
	 * Units are top-level statements of the Jimple Intermediate Representation
	 * 
	 * @param aUnit
	 * @param aUnitChain
	 * @return
	 * @throws Exception
	 */
	public static JJavaSootJimpleInstruction produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{	
		if( JJavaSootJimpleControlTransferInstruction.modelsJimpleUnit(aUnit) )
			return JJavaSootJimpleControlTransferInstruction.produce(aUnit, aUnitChain);
		
		if( JJavaSootJimpleImaginaryAssignmentInstruction.modelsJimpleUnit(aUnit))
			return JJavaSootJimpleImaginaryAssignmentInstruction.produce(aUnit, aUnitChain);
		
		if( JJavaSootJimpleCondCriticalSectionInstruction.modelsJimpleUnit(aUnit) )
			return JJavaSootJimpleCondCriticalSectionInstruction.produce(aUnit, aUnitChain);
		
		if( JJavaSootJimpleNopInstruction.modelsJimpleUnit(aUnit) )
			return new JJavaSootJimpleNopInstruction(aUnit, aUnitChain);
		
		if( JJavaSootJimpleBreakpointStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleBreakpointStmt(aUnit, aUnitChain);
		
		throw new Exception("Unknown Jimple Unit " + aUnit.toString() );
	}
}
