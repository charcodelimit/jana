package jana.lang.java.soot.jimple.instructions.controltransfer;

import soot.Unit;
import soot.util.Chain;
import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.global.JJavaSootJimpleGlobalControlTransferInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.local.JJavaSootJimpleLocalControlTransferInstruction;

public abstract class JJavaSootJimpleControlTransferInstruction extends JJavaSootJimpleInstruction
{
	protected JJavaSootJimpleControlTransferInstruction()
	{
		super();
	}
	
	protected JJavaSootJimpleControlTransferInstruction(Unit aUnit)
	{
		super(aUnit);
	}
	
	public static JJavaSootJimpleControlTransferInstruction produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{	
		if(JJavaSootJimpleLocalControlTransferInstruction.modelsJimpleUnit(aUnit))
			return JJavaSootJimpleLocalControlTransferInstruction.produce(aUnit, aUnitChain);
		
		if(JJavaSootJimpleGlobalControlTransferInstruction.modelsJimpleUnit(aUnit))
			return JJavaSootJimpleGlobalControlTransferInstruction.produce(aUnit, aUnitChain);
		
		throw new Exception("Unknown Control Transfer Instruction" + aUnit.toString() );
	}

	public static boolean modelsJimpleUnit(Unit aUnit)
	{	
		return  JJavaSootJimpleLocalControlTransferInstruction.modelsJimpleUnit(aUnit) ||
				JJavaSootJimpleGlobalControlTransferInstruction.modelsJimpleUnit(aUnit);
	}
}
