package jana.lang.java.soot.jimple.instructions.controltransfer.global;

import soot.Unit;
import soot.util.Chain;

public abstract class JJavaSootJimpleAbstractReturnStmt extends
		JJavaSootJimpleGlobalControlTransferInstruction
{
	protected JJavaSootJimpleAbstractReturnStmt(Unit aUnit)
	{
		super(aUnit);
	}
	
	public static JJavaSootJimpleAbstractReturnStmt produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{	
		if(JJavaSootJimpleReturnStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleReturnStmt(aUnit, aUnitChain);
		
		if(JJavaSootJimpleReturnVoidStmt.modelsJimpleUnit(aUnit))
			return new JJavaSootJimpleReturnVoidStmt(aUnit, aUnitChain);
		
		throw new Exception("Unknown Global Control Transfer Instruction" + aUnit.toString() );
	}

	public static boolean modelsJimpleUnit(Unit aUnit)
	{	
		return JJavaSootJimpleReturnStmt.modelsJimpleUnit(aUnit) ||
			   JJavaSootJimpleReturnVoidStmt.modelsJimpleUnit(aUnit);
	}
}
