package jana.lang.java.soot.jimple.instructions.controltransfer.global;

import soot.Unit;
import soot.util.Chain;

import jana.lang.java.soot.jimple.instructions.controltransfer.JJavaSootJimpleControlTransferInstruction;
import jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation.JJavaSootJimpleInvokeInstruction;

public abstract class JJavaSootJimpleGlobalControlTransferInstruction extends
		JJavaSootJimpleControlTransferInstruction
{
	protected JJavaSootJimpleGlobalControlTransferInstruction()
	{
		super();
	}
	
	protected JJavaSootJimpleGlobalControlTransferInstruction(Unit aUnit)
	{
		super(aUnit);
	}
	
	public static JJavaSootJimpleGlobalControlTransferInstruction produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{	
		if(JJavaSootJimpleAbstractReturnStmt.modelsJimpleUnit(aUnit))
			return JJavaSootJimpleAbstractReturnStmt.produce(aUnit, aUnitChain);
		
		if(JJavaSootJimpleThrowStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleThrowStmt(aUnit, aUnitChain);
		
		if(JJavaSootJimpleInvokeInstruction.modelsJimpleUnit(aUnit))
			return JJavaSootJimpleInvokeInstruction.produce(aUnit, aUnitChain);
		
		throw new Exception("Unknown Global Control Transfer Instruction " + aUnit.toString() );
	}

	public static boolean modelsJimpleUnit(Unit aUnit)
	{	
		return  JJavaSootJimpleInvokeInstruction.modelsJimpleUnit(aUnit) || 
				JJavaSootJimpleThrowStmt.modelsJimpleUnit(aUnit) ||
				JJavaSootJimpleAbstractReturnStmt.modelsJimpleUnit(aUnit);
	}
}
