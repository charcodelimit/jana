package jana.lang.java.soot.jimple.instructions.controltransfer.local;

import jana.lang.java.soot.jimple.instructions.JJavaSootJimpleImaginaryInstruction;
import jana.lang.java.soot.typesystem.JJavaSootObjectType;

import java.util.Map;

import soot.Trap;
import soot.Unit;
import soot.jimple.internal.JTrap;
import soot.util.Chain;

/**
 * Exception handling information is stored in the exception table of a class.
 * Therefore, Java Bytecode has no corresponding instruction.
 * 
 * The trap instruction is a separate part of the intermediate representation created from a method.
 * Therefore it has to be instantiated as part of the process that creates a method body.
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleTrap extends JJavaSootJimpleLocalControlTransferInstruction implements JJavaSootJimpleImaginaryInstruction
{
	private int startIndex;
	private int endIndex;
	private int handlerIndex;
	private String startLabel;
	private String endLabel;
	private String handlerLabel;
	private JJavaSootObjectType exceptionType;
	
	public JJavaSootJimpleTrap(Trap aTrap, Chain<Unit> aChain) throws Exception
	{
		this((JTrap) aTrap, aChain);
	}
	
	public JJavaSootJimpleTrap(JTrap aTrap, Chain<Unit> aChain) throws Exception
	{
		this.instructionType = "jimple-imaginary-trap-instruction";
		this.startIndex = indexForUnit(aTrap.getBeginUnit(), aChain);
		this.endIndex = indexForUnit(aTrap.getEndUnit(), aChain);
		this.handlerIndex = indexForUnit(aTrap.getHandlerUnit(), aChain);
		this.exceptionType = new JJavaSootObjectType(aTrap.getException().getType());
	}
	
	public void initTargetLabels(Map<Integer,String> labelMap)
	{
		this.startLabel = labelMap.get(this.startIndex);
		this.endLabel = labelMap.get(this.endIndex);
		this.handlerLabel = labelMap.get(this.handlerIndex);
	}
	
	/**
	 * (jimple-imaginary-trap-instruction start-index end-index handler-index exception-type)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-imaginary-trap-instruction start-label end-label handler-label exception-type)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.instructionType);
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.startLabel);
		aStringBuffer.append('\"');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.endLabel);
		aStringBuffer.append('\"');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.handlerLabel);
		aStringBuffer.append('\"');
		
		aStringBuffer.append(" (");
		this.exceptionType.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
	
	public Integer getStartIndex()
	{
		return new Integer(this.startIndex);
	}
	
	public Integer getEndIndex()
	{
		return new Integer(this.endIndex);
	}
	
	public Integer getHandlerIndex()
	{
		return new Integer(this.handlerIndex);
	}
}
