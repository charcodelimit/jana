package jana.lang.java.soot.jimple.instructions.controltransfer.local.cond;

import jana.lang.java.soot.values.constants.JJavaSootConstantValue;
import jana.metamodel.JNamedElement;

import java.util.ArrayList;
import java.util.List;

import soot.Unit;
import soot.jimple.Constant;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.util.Chain;

@SuppressWarnings("unchecked")
public class JJavaSootJimpleLookupSwitchStmt extends JJavaSootJimpleSwitchStmt
{
	protected List<JJavaSootConstantValue> lookupValues;
	
	JJavaSootJimpleLookupSwitchStmt(Unit aUnit, Chain aUnitChain) throws Exception
	{
		this((JLookupSwitchStmt) aUnit, aUnitChain);
	}
	
	JJavaSootJimpleLookupSwitchStmt(JLookupSwitchStmt switchStatement, Chain aUnitChain) throws Exception
	{
		super(switchStatement, switchStatement.getKey());
		
		this.instructionType = "jimple-lookup-switch-instruction";
		
		initLookupValues(switchStatement.getLookupValues());
		
		initSwitchTargetIndices(switchStatement, aUnitChain);
	}
	
	protected void initLookupValues(List<Constant> lookupValues) throws Exception
	{
		this.lookupValues = new ArrayList<JJavaSootConstantValue>(lookupValues.size());
		
		for(Constant currentLookupValue : lookupValues)
		{
			this.lookupValues.add( JJavaSootConstantValue.produce(currentLookupValue) );
		}
	}
	
	protected void initSwitchTargetIndices(JLookupSwitchStmt switchStatement, Chain aUnitChain) throws Exception
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
			throw new Exception("Can't default find target Unit" + switchStatement.getDefaultTarget().toString() + 
								" for switch-statement " + switchStatement.toString(), e);
		}
		
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-lookup-switch-instruction local-variable lookup-values target-labels default-target-label)
	 * 
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append(this.instructionType);
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.switchArgument.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		JNamedElement.elementListToSExpression(this.lookupValues, aStringBuffer);
		
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
		return (aUnit instanceof JLookupSwitchStmt);
	}
}

