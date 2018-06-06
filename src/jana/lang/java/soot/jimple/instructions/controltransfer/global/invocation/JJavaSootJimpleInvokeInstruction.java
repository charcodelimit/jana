package jana.lang.java.soot.jimple.instructions.controltransfer.global.invocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Unit;
import soot.Value;
import soot.jimple.Expr;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.util.Chain;

import jana.lang.java.soot.jimple.instructions.controltransfer.global.JJavaSootJimpleGlobalControlTransferInstruction;
import jana.lang.java.soot.jimple.values.references.JJavaSootJimpleMethodRef;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.lang.java.soot.values.JJavaSootValue;
import jana.lang.java.typesystem.JJavaReferenceType;
import jana.metamodel.JNamedElement;

/**
 * This is a Kludge that works around a wart in Soot's Jimple grammar.
 * There is actually no invoke statement present in the textual form of a Jimple Body.
 * Instead the invoke expression that is part of an JInvokeStmt is printed directly.
 * 
 * Therefore we implement a produce method for (Unit aUnit, Chain aUnitChain) and
 * (Expr anExpression).
 * The method produce(Unit aUnit, Chain aUnitChain) extracts the invoke expression
 * from the invoke statement and forwards the construction of the appropriate object to
 * produce(Expr anExpression)
 * 
 * We don't need the wrapper to the different types of invoke statements, because
 * it is clear from the inheritance hierarchy that they are invocation statements.
 * 
 * @author chr
 *
 */
@SuppressWarnings("unchecked")
public abstract class JJavaSootJimpleInvokeInstruction extends JJavaSootJimpleGlobalControlTransferInstruction
{	
	protected JJavaReferenceType receiverClass;
	protected JJavaSootJimpleMethodRef methodReference;
	protected List<JJavaSootValue> arguments;
	
	protected JJavaSootJimpleInvokeInstruction(InvokeExpr invokeExpression) throws Exception
	{	
		this.methodReference = new JJavaSootJimpleMethodRef(invokeExpression.getMethodRef());
		this.receiverClass = (JJavaReferenceType) JJavaSootType.produce( invokeExpression.getMethodRef().declaringClass().getType() );
   
		initArguments(invokeExpression.getArgs());
	}
	
	protected void initArguments(List anArgumentList) throws Exception
	{
		Value value;
		
		if(anArgumentList == null)
			this.arguments = new ArrayList<JJavaSootValue>();
		else
			this.arguments = new ArrayList<JJavaSootValue>(anArgumentList.size());
		
		for(Iterator iter=anArgumentList.iterator(); iter.hasNext(); )
		{
			value = (Value) iter.next();
			this.arguments.add(JJavaSootValue.produce(value));
		}
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (... reference-type method-reference arguments)
	 * 
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.receiverClass.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.methodReference.toSExpression(aStringBuffer);
		aStringBuffer.append(')');

		JNamedElement.elementListToSExpression(this.arguments, aStringBuffer);
	}
	
	
	public static JJavaSootJimpleInvokeInstruction invocationStatement(Unit aUnit) throws Exception
	{
		JInvokeStmt invocationStatement;
		
		invocationStatement = (JInvokeStmt) aUnit;
		return produce( invocationStatement.getInvokeExpr() );
	}
	
	public static JJavaSootJimpleGlobalControlTransferInstruction produce(Unit aUnit, Chain aUnitChain) throws Exception
	{
		if(modelsJimpleUnit(aUnit))
			return invocationStatement(aUnit);
		
		throw new Exception("Unknown Invocation Instruction");
	}
	
	
	public static JJavaSootJimpleInvokeInstruction produce(Expr aJimpleExpression) throws Exception
	{
		return produce((InvokeExpr) aJimpleExpression);
	}
	
	public static JJavaSootJimpleInvokeInstruction produce(InvokeExpr invokeExpression) throws Exception
	{	
		if(JJavaSootJimpleSpecialInvokeExpr.modelsJimpleInvokeExpression(invokeExpression))
			return new JJavaSootJimpleSpecialInvokeExpr(invokeExpression);
		
		if(JJavaSootJimpleInterfaceInvokeExpr.modelsJimpleInvokeExpression(invokeExpression))
			return new JJavaSootJimpleInterfaceInvokeExpr(invokeExpression);
		
		if(JJavaSootJimpleVirtualInvokeExpr.modelsJimpleInvokeExpression(invokeExpression))
			return new JJavaSootJimpleVirtualInvokeExpr(invokeExpression);
		
		if(JJavaSootJimpleStaticInvokeExpr.modelsJimpleInvokeExpression(invokeExpression))
			return new JJavaSootJimpleStaticInvokeExpr(invokeExpression);
		
		throw new Exception("Unknown Invocation Statement " + invokeExpression.toString() );
	}
	
	/**
	 * Override this method! Otherwise cycles will occur.
	 * chr: unfortunately this cannot be enforced with Java interfaces.
	 * 
	 * @param invokeExpression
	 * @return
	 */
	public static boolean modelsJimpleInvokeExpression(InvokeExpr invokeExpression)
	{
		return JJavaSootJimpleSpecialInvokeExpr.modelsJimpleInvokeExpression(invokeExpression) ||
			   JJavaSootJimpleInterfaceInvokeExpr.modelsJimpleInvokeExpression(invokeExpression) ||
			   JJavaSootJimpleVirtualInvokeExpr.modelsJimpleInvokeExpression(invokeExpression) ||
			   JJavaSootJimpleStaticInvokeExpr.modelsJimpleInvokeExpression(invokeExpression);
	}

	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		if( anExpression instanceof InvokeExpr)
			return modelsJimpleInvokeExpression((InvokeExpr) anExpression);
			
		return false;
	}
	
	public static boolean modelsJimpleUnit(Unit aUnit)
	{
		return (aUnit instanceof JInvokeStmt);
	}
}
