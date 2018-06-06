package jana.lang.java.soot.jimple.instructions.controltransfer.local;

import jana.lang.java.soot.jimple.instructions.controltransfer.local.cond.JJavaSootJimpleSwitchStmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import soot.Unit;
import soot.util.Chain;

public abstract class JJavaSootJimpleBranchInstruction extends
		JJavaSootJimpleLocalControlTransferInstruction
{
	protected List<Integer> branchTargetIndices;
	protected List<String> branchTargetLabels;
	
	protected JJavaSootJimpleBranchInstruction(Unit aUnit)
	{
		super(aUnit);
		
		branchTargetIndices = new ArrayList<Integer>();
		branchTargetLabels = new ArrayList<String>();
	}
	
	protected void addTargetIndexForUnit(Unit aTargetUnit, Chain<Unit> aUnitChain) throws Exception
	{
		int branchTargetIndex;
		
		branchTargetIndex = indexForUnit(aTargetUnit, aUnitChain);
		this.branchTargetIndices.add(new Integer(branchTargetIndex));
	}
	
	public void initTargetLabels(Map<Integer,String> labelMap)
	{
		String label;
		
		for( Integer branchTargetIndex : this.branchTargetIndices )
		{
			label = labelMap.get(branchTargetIndex);
			this.branchTargetLabels.add(label);
		}
	}
	
	public List<Integer> getBranchTargetIndices()
	{
		return this.branchTargetIndices;
	}
	
	/**
	 * Units are top-level statements of the Jimple Intermediate Representation
	 * 
	 * @param aUnit
	 * @param aUnitChain
	 * @return
	 * @throws Exception
	 */
	public static JJavaSootJimpleBranchInstruction produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		if(JJavaSootJimpleGotoStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleGotoStmt(aUnit, aUnitChain);
		
		if(JJavaSootJimpleIfStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleIfStmt(aUnit, aUnitChain);
		
		if(JJavaSootJimpleSwitchStmt.modelsJimpleUnit(aUnit))
			return JJavaSootJimpleSwitchStmt.produce(aUnit, aUnitChain);
		
		throw new Exception("Unknown Branch Instruction " + aUnit.toString() );
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return JJavaSootJimpleGotoStmt.modelsJimpleUnit(aUnit) || 
			   JJavaSootJimpleIfStmt.modelsJimpleUnit(aUnit) ||
			   JJavaSootJimpleSwitchStmt.modelsJimpleUnit(aUnit);
	}
}
