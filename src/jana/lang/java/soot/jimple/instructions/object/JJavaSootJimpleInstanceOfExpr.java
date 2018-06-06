package jana.lang.java.soot.jimple.instructions.object;


import soot.jimple.Expr;
import soot.jimple.internal.JInstanceOfExpr;
import jana.lang.java.soot.values.JJavaSootValue;
import jana.lang.java.soot.typesystem.JJavaSootType;

public class JJavaSootJimpleInstanceOfExpr extends JJavaSootJimpleObjectInstruction
{   
	protected JJavaSootValue value;
	
	JJavaSootJimpleInstanceOfExpr(Expr jimpleExpression) throws Exception
	{
		this((JInstanceOfExpr) jimpleExpression);
	}
	
	JJavaSootJimpleInstanceOfExpr(JInstanceOfExpr instanceOfExpression) throws Exception
	{
		this.instructionType = "jimple-instanceof";
		this.value = JJavaSootValue.produce(instanceOfExpression.getOp());
		this.type = JJavaSootType.produce(instanceOfExpression.getCheckType());
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
		this.value.toSExpression(aStringBuffer); 
		aStringBuffer.append(")");
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer); 
		aStringBuffer.append(")");
	}
	
	public static boolean modelsJimpleExpression(Expr anExpression)
	{
		return( anExpression instanceof JInstanceOfExpr);
	}
}
