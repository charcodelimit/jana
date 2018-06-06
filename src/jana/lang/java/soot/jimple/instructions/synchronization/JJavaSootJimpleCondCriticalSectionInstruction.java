package jana.lang.java.soot.jimple.instructions.synchronization;

import soot.Unit;
import soot.Value;
import soot.jimple.internal.AbstractStmt;
import soot.util.Chain;
import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleInstruction;
import jana.lang.java.soot.values.JJavaSootValue;

public abstract class JJavaSootJimpleCondCriticalSectionInstruction extends
		JJavaSootJimpleInstruction
{
	protected JJavaSootValue value;
	
	protected JJavaSootJimpleCondCriticalSectionInstruction(Unit aUnit, Value aValue) throws Exception
	{
		super(aUnit);
		this.value = JJavaSootValue.produce(aValue);
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
		
		aStringBuffer.append(" (");
		this.value.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lineNumber);
	}
	
	/**
	 * Units are top-level statements of the Jimple Intermediate Representation
	 * 
	 * @param aUnit
	 * @param aUnitChain
	 * @return
	 * @throws Exception
	 */
	public static JJavaSootJimpleCondCriticalSectionInstruction produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		return produce((AbstractStmt) aUnit, aUnitChain);
	}
	
	public static JJavaSootJimpleCondCriticalSectionInstruction produce(AbstractStmt aStatement, Chain<Unit> aUnitChain) throws Exception
	{
		if(JJavaSootJimpleEnterMonitorStmt.modelsJimpleStatement(aStatement))
			return new JJavaSootJimpleEnterMonitorStmt(aStatement, aUnitChain);
		
		if(JJavaSootJimpleExitMonitorStmt.modelsJimpleStatement(aStatement))
			return new JJavaSootJimpleExitMonitorStmt(aStatement, aUnitChain);
				
		throw new Exception("Unknown Conditional Critical Section Statement " + aStatement.toString() );
	}
	
	public static boolean modelsJimpleStatement(AbstractStmt aStatement)
	{
		return JJavaSootJimpleEnterMonitorStmt.modelsJimpleStatement(aStatement) || 
			   JJavaSootJimpleExitMonitorStmt.modelsJimpleStatement(aStatement);
	}

	public static boolean modelsJimpleUnit(Unit unit)
	{
		if( unit instanceof AbstractStmt)
			return modelsJimpleStatement((AbstractStmt) unit);
			
		return false;
	}
}
