package jana.lang.java.soot.jimple.instructions.assignment;

import soot.Unit;
import soot.jimple.internal.JIdentityStmt;
import soot.util.Chain;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValueLocal;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleReferenceValue;

/**
 * This is a virtual instructions, as Jimple's local variable declaration and initialization statements 
 * have no direct Java Bytecode counterpart
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleIdentityStmt extends JJavaSootJimpleImaginaryAssignmentInstruction
{
	protected JJavaSootJimpleReferenceValueLocal targetVariable;
	protected JJavaSootJimpleReferenceValue sourceReference;
	
	public JJavaSootJimpleIdentityStmt(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		this((JIdentityStmt) aUnit);
	}
	
	public JJavaSootJimpleIdentityStmt(JIdentityStmt identityStatement) throws Exception
	{
		super(identityStatement);
		
		this.instructionType = "jimple-imaginary-instruction-variable-initialization";
		this.targetVariable = JJavaSootJimpleReferenceValueLocal.produce(identityStatement.leftBox.getValue());
		this.sourceReference = JJavaSootJimpleReferenceValue.produce(identityStatement.rightBox.getValue());
	}	

	/**
	 * (jimple-virtual-variable-initialization local-variable reference-value)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
	   
		this.toSExpression(sb);
	   
		return sb.toString();
	}
	
	/**
	 * (jimple-virtual-variable-initialization local-variable reference-value)
	 */
	public void toSExpression(StringBuffer aStringBuffer) 
	{
		aStringBuffer.append(this.instructionType);
		   
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.targetVariable.toSExpression(aStringBuffer);
		aStringBuffer.append(')');

		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.sourceReference.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lineNumber);
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JIdentityStmt);
	}
}





