package jana.lang.java.soot.jimple.instructions.controltransfer.global;

import soot.Unit;
import soot.jimple.internal.JReturnStmt;
import soot.util.Chain;
import jana.lang.java.soot.values.JJavaSootValue;

/**
 * Return of control to the caller after a method call.
 * This instruction returns a value.
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleReturnStmt extends JJavaSootJimpleAbstractReturnStmt
{
    JJavaSootValue returnValue;

    public JJavaSootJimpleReturnStmt(Unit aUnit, Chain<Unit> aUnitChain) throws Exception
	{
		this((JReturnStmt) aUnit);
	}
    
    public JJavaSootJimpleReturnStmt(JReturnStmt returnStatement) throws Exception
	{
    	super(returnStatement);
    	
    	this.instructionType = "jimple-return-instruction";
    	this.returnValue = JJavaSootValue.produce(returnStatement.getOpBox().getValue());
	}

	public String toString()
    {
    	return "return " + this.returnValue.toString();
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
    	
    	aStringBuffer.append(" (");
    	this.returnValue.toSExpression(aStringBuffer);
    	aStringBuffer.append(")");
    	
    	aStringBuffer.append(' ');
    	aStringBuffer.append(this.lineNumber);
	}
    
    public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JReturnStmt);
	}
}

