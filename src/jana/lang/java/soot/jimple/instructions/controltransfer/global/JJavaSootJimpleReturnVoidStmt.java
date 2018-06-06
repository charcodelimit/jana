package jana.lang.java.soot.jimple.instructions.controltransfer.global;

import soot.Unit;
import soot.jimple.internal.JReturnVoidStmt;
import soot.util.Chain;

public class JJavaSootJimpleReturnVoidStmt extends JJavaSootJimpleAbstractReturnStmt
{
	public JJavaSootJimpleReturnVoidStmt(Unit unit, Chain<Unit> unitChain)
	{
		this(unit);
	}
	
    public JJavaSootJimpleReturnVoidStmt(Unit unit)
    {
    	super(unit);
    	
    	this.instructionType = "jimple-return-void-instruction";
    }

	public String toString()
    {
    	return "return";
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
		aStringBuffer.append(this.lineNumber);
	}
    
    public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JReturnVoidStmt);
	}
}

