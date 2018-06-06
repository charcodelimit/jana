package jana.lang.java.soot.jimple.instructions.controltransfer.local.cond;

import jana.lang.java.soot.jimple.instructions.controltransfer.local.JJavaSootJimpleBranchInstruction;
import jana.lang.java.soot.values.JJavaSootValue;

import java.util.Map;

import soot.Unit;
import soot.Value;
import soot.util.Chain;

@SuppressWarnings("unchecked")
public abstract class JJavaSootJimpleSwitchStmt extends
		JJavaSootJimpleBranchInstruction
{
	protected int defaultTargetIndex;
	protected String defaultTargetLabel;
	protected JJavaSootValue switchArgument;
	
	protected JJavaSootJimpleSwitchStmt(Unit unit, Value switchArgument) throws Exception
	{ 
		super(unit);
		
		this.switchArgument = JJavaSootValue.produce(switchArgument);
	}
	
	public Integer getDefaultTargetIndex()
	{
		return new Integer(defaultTargetIndex);
	}
	
	/**
	 * @see {@link JJavaSootJimpleBranchInstruction}
	 * @override
	 */
	public void initTargetLabels(Map<Integer,String> labelMap)
	{
		super.initTargetLabels(labelMap);
				
		this.defaultTargetLabel = labelMap.get(this.getDefaultTargetIndex());
	}
	
	public static JJavaSootJimpleBranchInstruction produce(Unit aUnit, Chain aUnitChain) throws Exception
	{
		if(JJavaSootJimpleLookupSwitchStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleLookupSwitchStmt(aUnit,aUnitChain);
		
		if(JJavaSootJimpleTableSwitchStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleTableSwitchStmt(aUnit,aUnitChain);
		
		throw new Exception("Unknown Switch Instruction " + aUnit.toString() );
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return JJavaSootJimpleLookupSwitchStmt.modelsJimpleUnit(aUnit) || 
			   JJavaSootJimpleTableSwitchStmt.modelsJimpleUnit(aUnit);
	}
}
