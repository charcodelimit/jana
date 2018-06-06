package jana.lang.java.soot.jimple.instructions;

import soot.Unit;
import soot.jimple.internal.JNopStmt;
import soot.util.Chain;

public class JJavaSootJimpleNopInstruction extends JJavaSootJimpleInstruction
{
	public JJavaSootJimpleNopInstruction(Unit aJimpleUnit, Chain<Unit> aUnitChain) throws Exception
	{
		this(aJimpleUnit);
	}
	
	public JJavaSootJimpleNopInstruction(Unit aJimpleUnit) throws Exception
	{
		this((JNopStmt) aJimpleUnit);
	}
	
	public JJavaSootJimpleNopInstruction(JNopStmt nopStatement) throws Exception
	{
		super(nopStatement);
		
		this.instructionType = "jimple-nop-instruction";
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
	
	public static boolean modelsJimpleUnit(Unit aJimpleUnit)
	{
		return( aJimpleUnit instanceof JNopStmt);
	}
}




