package jana.lang.java.soot.jimple.instructions.controltransfer.local;

import jana.lang.java.soot.jimple.instructions.controltransfer.JJavaSootJimpleControlTransferInstruction;

import java.util.Iterator;
import java.util.Map;

import soot.Unit;
import soot.util.Chain;

public abstract class JJavaSootJimpleLocalControlTransferInstruction extends
		JJavaSootJimpleControlTransferInstruction
{
	protected JJavaSootJimpleLocalControlTransferInstruction()
	{
		super();
	}
	
	protected JJavaSootJimpleLocalControlTransferInstruction(Unit aUnit)
	{
		super(aUnit);
	}
	
	/**
	 * returns the index for a given unit in a chain of units
	 * Units and Jimple instructions have a one to one correspondence, therefore no
	 * offset calculations are required.
	 * 
	 * @param aTargetUnit
	 * @param aUnitChain
	 * @return
	 * @throws Exception
	 */
	protected int indexForUnit(Unit aTargetUnit, Chain<Unit> aUnitChain) throws Exception
	{
		int count;
		int branchTargetIndex;
		
		count = 0;
		branchTargetIndex = -1;
		
		for(Iterator<Unit> iter = aUnitChain.iterator(); iter.hasNext();)
		{
			if( iter.next() == aTargetUnit)
			{
				branchTargetIndex = count;
				return branchTargetIndex;
			}
						
			count++;
		}
		
		throw new Exception("No index found for branch target: " + aTargetUnit.toString());
	}
	
	public static JJavaSootJimpleBranchInstruction produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{	
		if(JJavaSootJimpleBranchInstruction.modelsJimpleUnit(aUnit))
			return JJavaSootJimpleBranchInstruction.produce(aUnit, aUnitChain);
		
		throw new Exception("Unknown Local Control Transfer Instruction" + aUnit.toString());
	}
	
	/**
	 * Initialize the branch-target labels using a labelMap that
	 * maps branch-target indices to labels.
	 * @param labelMap
	 */
	public abstract void initTargetLabels(Map<Integer,String> labelMap);

	public static boolean modelsJimpleUnit(Unit aUnit)
	{	
		return JJavaSootJimpleBranchInstruction.modelsJimpleUnit(aUnit);
	}
}
