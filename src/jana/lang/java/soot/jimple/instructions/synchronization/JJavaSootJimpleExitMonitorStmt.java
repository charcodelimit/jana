package jana.lang.java.soot.jimple.instructions.synchronization;

import soot.Unit;
import soot.jimple.internal.AbstractStmt;
import soot.jimple.internal.JExitMonitorStmt;
import soot.util.Chain;


public class JJavaSootJimpleExitMonitorStmt extends JJavaSootJimpleCondCriticalSectionInstruction
{
	public JJavaSootJimpleExitMonitorStmt(AbstractStmt aStatement, Chain<Unit> aUnitChain ) throws Exception
	{
		this((JExitMonitorStmt) aStatement);
	}
	
	public JJavaSootJimpleExitMonitorStmt(JExitMonitorStmt enterMonitorStatement) throws Exception
	{
		super(enterMonitorStatement, enterMonitorStatement.getOp());
		this.instructionType = "jimple-ccsec-exit-instruction";
	}
	
	public static boolean modelsJimpleStatement(AbstractStmt aStatement)
	{
		return(aStatement instanceof JExitMonitorStmt);
	}
}
