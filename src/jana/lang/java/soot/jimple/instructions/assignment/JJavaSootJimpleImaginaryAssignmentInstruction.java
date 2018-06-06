package jana.lang.java.soot.jimple.instructions.assignment;

import soot.Unit;
import soot.util.Chain;
import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleImaginaryInstruction;
import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleInstruction;

public abstract class JJavaSootJimpleImaginaryAssignmentInstruction extends JJavaSootJimpleInstruction implements JJavaSootJimpleImaginaryInstruction
{
	protected JJavaSootJimpleImaginaryAssignmentInstruction(Unit aUnit)
	{
		super(aUnit);
	}
	
	/**
	 * Units are top-level statements of the Jimple Intermediate Representation
	 * 
	 * @param aUnit
	 * @param aUnitChain
	 * @return
	 * @throws Exception
	 */
	public static JJavaSootJimpleImaginaryAssignmentInstruction produce(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		if( JJavaSootJimpleIdentityStmt.modelsJimpleUnit(aUnit) )
			return new JJavaSootJimpleIdentityStmt(aUnit, aUnitChain);
		
		if( JJavaSootJimpleAssignStmt.modelsJimpleUnit(aUnit) )
			return new JJavaSootJimpleAssignStmt(aUnit, aUnitChain);
		
		throw new Exception("Unknown Assignment Statement " + aUnit.toString());
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return JJavaSootJimpleIdentityStmt.modelsJimpleUnit(aUnit) ||
			   JJavaSootJimpleAssignStmt.modelsJimpleUnit(aUnit);	
	}
}
