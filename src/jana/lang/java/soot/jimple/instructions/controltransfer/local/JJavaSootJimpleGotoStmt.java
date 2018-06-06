package jana.lang.java.soot.jimple.instructions.controltransfer.local;

import soot.Unit;
import soot.jimple.internal.JGotoStmt;
import soot.util.Chain;

/**
 * In the Jimple intermediate language of Soot, goto is a branch instruction 
 *  @see soot.jimple.internal.JGotoStmt
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleGotoStmt extends JJavaSootJimpleBranchInstruction
{	
	public JJavaSootJimpleGotoStmt(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		this((JGotoStmt) aUnit, aUnitChain);
	}
	
	/**
	 * @param gotoStatement
	 */
	public JJavaSootJimpleGotoStmt(JGotoStmt gotoStatement, Chain<Unit> aUnitChain) throws Exception
	{
		super(gotoStatement);
		
		this.instructionType = "jimple-goto-instruction";
		
		try
    	{
    		addTargetIndexForUnit(gotoStatement.getTarget(), aUnitChain);
    	}
    	catch(Exception e)
    	{ 
			throw new Exception("Can't find target Unit" + gotoStatement.getTarget().toString() + " for goto-statement " + gotoStatement.toString(), e);
    	}
	}
	
	 /**
     * INV there is only one target index for a goto statement
     * 
     * @return
     */
    public int getTargetIndex()
    {
    	assert(this.branchTargetIndices.size() == 1);
    	
    	return this.branchTargetIndices.get(0).intValue();
    }
    
    /**
     * INV there is only one target label for a goto statement
     * 
     * @return
     */
    public String getTargetLabel()
    {
    	assert(this.branchTargetIndices.size() == 1);
    	
    	return this.branchTargetLabels.get(0);
    }
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer) 
	{
		aStringBuffer.append(this.instructionType);
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(getTargetLabel());
		aStringBuffer.append('\"');
		aStringBuffer.append(' ');
		aStringBuffer.append(this.lineNumber);
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JGotoStmt);
	}
}



