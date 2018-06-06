package jana.lang.java.soot.jimple.instructions.controltransfer.global;

import soot.Unit;
import soot.jimple.internal.JThrowStmt;
import soot.util.Chain;
import jana.lang.java.soot.values.JJavaSootValue;

/**
 * Interprocedural control transfer
 */
public class JJavaSootJimpleThrowStmt extends JJavaSootJimpleGlobalControlTransferInstruction
{
	protected JJavaSootValue value;
	
	JJavaSootJimpleThrowStmt(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		this((JThrowStmt) aUnit);
	}
	
	JJavaSootJimpleThrowStmt(JThrowStmt throwStatement) throws Exception
	{
		super(throwStatement);
		
		this.instructionType = "jimple-throw-instruction";
		this.value = JJavaSootValue.produce(throwStatement.getOp());
	}
	
	/**
	 * (jimple-throw-instruction value)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		 
		this.toSExpression(sb);
	    	
		return sb.toString();
	}
	
	/**
	 * (jimple-throw-instruction value)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.instructionType);
		 
		aStringBuffer.append(" (");
		this.value.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lineNumber);
	}
	    
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JThrowStmt);
	}
}
