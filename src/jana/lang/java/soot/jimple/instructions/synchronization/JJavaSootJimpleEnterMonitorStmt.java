package jana.lang.java.soot.jimple.instructions.synchronization;

import soot.Unit;
import soot.jimple.internal.AbstractStmt;
import soot.jimple.internal.JEnterMonitorStmt;
import soot.util.Chain;

public class JJavaSootJimpleEnterMonitorStmt extends JJavaSootJimpleCondCriticalSectionInstruction
{
	public JJavaSootJimpleEnterMonitorStmt(AbstractStmt aStatement, Chain<Unit> aUnitChain ) throws Exception
	{
		this((JEnterMonitorStmt) aStatement);
	}
	
	public JJavaSootJimpleEnterMonitorStmt(JEnterMonitorStmt enterMonitorStatement) throws Exception
	{
		super(enterMonitorStatement, enterMonitorStatement.getOp());
		this.instructionType = "jimple-ccsec-enter-instruction";
	}
	
	public static boolean modelsJimpleStatement(AbstractStmt aStatement)
	{
		return (aStatement instanceof JEnterMonitorStmt);
	}
}
