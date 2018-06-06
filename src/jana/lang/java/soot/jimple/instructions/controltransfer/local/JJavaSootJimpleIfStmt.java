package jana.lang.java.soot.jimple.instructions.controltransfer.local;


import jana.lang.java.soot.jimple.instructions.arithmetic.predicate.JJavaSootJimplePredicateInstruction;
import soot.Unit;
import soot.jimple.Expr;
import soot.jimple.internal.JIfStmt;
import soot.util.Chain;


public class JJavaSootJimpleIfStmt extends JJavaSootJimpleBranchInstruction
{
	private JJavaSootJimplePredicateInstruction conditionStatement;
	
	public JJavaSootJimpleIfStmt(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		 this((JIfStmt) aUnit, aUnitChain);
	}	
	
    public JJavaSootJimpleIfStmt(JIfStmt ifStatement, Chain<Unit> aUnitChain) throws Exception
    {
    	super(ifStatement);
    	
    	this.instructionType = "jimple-if-instruction";
    	
    	this.conditionStatement = JJavaSootJimplePredicateInstruction.produce( (Expr) ifStatement.getCondition() );
    	
    	try
    	{
    		addTargetIndexForUnit(ifStatement.getTarget(), aUnitChain);
    	}
    	catch(Exception e)
    	{ 
			throw new Exception("Can't find target Unit" + ifStatement.getTarget().toString() + " for if-statement " + ifStatement.toString(), e);
    	}
    }
    
    /**
     * INV there is only one target index for an if-then statement
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
    	assert(this.branchTargetLabels.size() == 1);
    	
    	return this.branchTargetLabels.get(0);
    }
    
    /**
     * (jimple-if-instruction condition-statement target-index)
     */
    public String toSExpression()
    {
    	StringBuffer sb = new StringBuffer();
    	
    	this.toSExpression(sb);
    	
    	return sb.toString();
    }
    
    /**
     * (jimple-if-instruction condition-statement target-label)
     */
    public void toSExpression(StringBuffer aStringBuffer)
    {
    	aStringBuffer.append(this.instructionType);
    	
    	aStringBuffer.append(' ');
    	aStringBuffer.append('(');
    	this.conditionStatement.toSExpression(aStringBuffer);
    	aStringBuffer.append(')');
    	
    	aStringBuffer.append(' ');
    	aStringBuffer.append('\"');
    	aStringBuffer.append(getTargetLabel());
    	aStringBuffer.append('\"');
    	
    	aStringBuffer.append(' ');
    	aStringBuffer.append(this.lineNumber);
    }
    
    public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JIfStmt);
	}
}
