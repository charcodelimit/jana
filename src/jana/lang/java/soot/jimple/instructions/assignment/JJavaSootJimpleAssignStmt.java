package jana.lang.java.soot.jimple.instructions.assignment;

import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleInstruction;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValue;
import jana.lang.java.soot.values.JJavaSootValue;

import soot.Immediate;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.Expr;
import soot.jimple.Ref;
import soot.jimple.internal.JAssignStmt;
import soot.util.Chain;

/**
 * Assignment instructions are virtual instructions that assign to a targetVariable or targetField
 * a sourceValue, sourceVariable or the result of a sourceInstruction
 *
 * @author chr
 *
 */
public class JJavaSootJimpleAssignStmt extends JJavaSootJimpleImaginaryAssignmentInstruction
{
	// local variable 
	JJavaSootJimpleReferenceValueLocal targetLocalVariable;
	// or field reference
	JJavaSootJimpleReferenceValue targetReferenceValue;
	// local variable, field/array reference, or constant
	JJavaSootValue sourceValue;
	// any instruction
	JJavaSootJimpleInstruction sourceInstruction;
	
	@SuppressWarnings("unchecked") // chr: argument is ignored anyway
	public JJavaSootJimpleAssignStmt(Unit aUnit, Chain aUnitChain) throws Exception
	{
		this((JAssignStmt) aUnit);
	}
	
	public JJavaSootJimpleAssignStmt(JAssignStmt assignmentStatement) throws Exception
	{
		super(assignmentStatement);
		
		Value lvalue, rvalue;
		
		lvalue = assignmentStatement.leftBox.getValue();
		rvalue = assignmentStatement.rightBox.getValue();
		
		this.instructionType = "jimple-imaginary-instruction-assignment";
		
		if(lvalue instanceof Local)
			this.targetLocalVariable = JJavaSootJimpleReferenceValueLocal.produce(lvalue);
		if(lvalue instanceof Ref)
			this.targetReferenceValue = JJavaSootJimpleReferenceValue.produce(lvalue);
				
		if(rvalue instanceof Expr)
			this.sourceInstruction = JJavaSootJimpleInstruction.produce((Expr) rvalue);
		if( (rvalue instanceof Immediate) || (rvalue instanceof Ref) )
			this.sourceValue = JJavaSootValue.produce(rvalue);
	}
	
	
	/**
	 * 
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}

	public void toSExpression(StringBuffer aStringBuffer) 
	{
		aStringBuffer.append(this.instructionType);
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		if(this.targetLocalVariable != null)
			this.targetLocalVariable.toSExpression(aStringBuffer);
		if(this.targetReferenceValue != null)
			this.targetReferenceValue.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		if(this.sourceInstruction != null)
			this.sourceInstruction.toSExpression(aStringBuffer);
		if(this.sourceValue != null)
			this.sourceValue.toSExpression(aStringBuffer);
		aStringBuffer.append(')');	
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lineNumber);
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JAssignStmt);
	}
}




