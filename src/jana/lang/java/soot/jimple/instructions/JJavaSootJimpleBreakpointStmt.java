package jana.lang.java.soot.jimple.instructions;

import soot.Unit;
import soot.jimple.internal.JBreakpointStmt;
import soot.util.Chain;

public class JJavaSootJimpleBreakpointStmt extends JJavaSootJimpleInstruction implements JJavaSootJimpleImaginaryInstruction
{  
	public JJavaSootJimpleBreakpointStmt(Unit aJimpleUnit, Chain<Unit> aUnitChain) throws Exception
	{
		this(aJimpleUnit);
	}
	
	public JJavaSootJimpleBreakpointStmt(Unit aJimpleUnit) throws Exception
	{
		this((JBreakpointStmt) aJimpleUnit);
	}
	
	public JJavaSootJimpleBreakpointStmt(JBreakpointStmt nopStatement) throws Exception
	{
		super(nopStatement);
		
		this.instructionType = "jimple-breakpoint-instruction";
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
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lineNumber);
	}
	
	public static boolean modelsJimpleUnit(Unit aJimpleUnit)
	{
		return( aJimpleUnit instanceof JBreakpointStmt);
	}
}
