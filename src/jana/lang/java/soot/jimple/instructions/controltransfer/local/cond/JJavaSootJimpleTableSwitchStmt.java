package jana.lang.java.soot.jimple.instructions.controltransfer.local.cond;

import java.util.List;

import soot.Unit;
import soot.jimple.internal.JTableSwitchStmt;
import soot.util.Chain;

@SuppressWarnings("unchecked")
public class JJavaSootJimpleTableSwitchStmt extends JJavaSootJimpleSwitchStmt
{
	protected int lowIndex;
	protected int highIndex;
	
	public JJavaSootJimpleTableSwitchStmt(Unit aUnit, Chain aUnitChain) throws Exception
	{
		this((JTableSwitchStmt) aUnit, aUnitChain);
	}
	
	public JJavaSootJimpleTableSwitchStmt(JTableSwitchStmt switchStatement, Chain aUnitChain) throws Exception
	{
		super(switchStatement, switchStatement.getKey());
		
		this.instructionType = "jimple-table-switch-instruction";
		
		this.lowIndex = switchStatement.getLowIndex();
		this.highIndex = switchStatement.getHighIndex();
		
		initSwitchTargetIndices(switchStatement, aUnitChain);
	}
	
	protected void initSwitchTargetIndices(JTableSwitchStmt switchStatement, Chain aUnitChain) throws Exception
	{
		List<Unit> targetList;
		
		targetList = switchStatement.getTargets();
		
		for(Unit currentTargetUnit : targetList) // chr: should decide on a coding style
		{
			try
			{
				addTargetIndexForUnit(currentTargetUnit, aUnitChain);
			}
			catch(Exception e)
			{ 
				throw new Exception("Can't find target Unit" + currentTargetUnit.toString() + 
						            " for switch-statement " + switchStatement.toString(), e);
			}
		}
		
		try
		{
			this.defaultTargetIndex = indexForUnit(switchStatement.getDefaultTarget(), aUnitChain);
		}
		catch(Exception e)
		{ 
			throw new Exception("Can't find default target Unit" + switchStatement.getDefaultTarget().toString() + 
								" for switch-statement " + switchStatement.toString(), e);
		}
		
	}
	
	/**
	 * (jimple-table-switch-instruction local-variable low-index high-index target-indices default-target-index)
	 * 
	 * @return
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-table-switch-instruction local-variable low-index high-index target-labels default-target-label)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.instructionType);
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.switchArgument.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lowIndex);
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.highIndex);
		
		aStringBuffer.append(" (list");
		for(String currentTargetLabel : this.branchTargetLabels)
		{
			aStringBuffer.append(' ');
			aStringBuffer.append('\"');
			aStringBuffer.append(currentTargetLabel);
			aStringBuffer.append('\"');
		}
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.defaultTargetLabel);
		aStringBuffer.append('\"');
		
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lineNumber);
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JTableSwitchStmt);
	}
}
