package jana.lang.java.soot.jimple.instructions.object;

import soot.jimple.Expr;
import soot.jimple.internal.JCastExpr;
import jana.lang.java.soot.values.JJavaSootValue;
import jana.lang.java.soot.typesystem.JJavaSootType;

public class JJavaSootJimpleCastExpr extends JJavaSootJimpleObjectInstruction
{   
	protected JJavaSootValue value;
	
	JJavaSootJimpleCastExpr(Expr jimpleExpression) throws Exception
	{
		this((JCastExpr) jimpleExpression);
	}
	
	JJavaSootJimpleCastExpr(JCastExpr castExpression) throws Exception
	{
		this.instructionType = "jimple-cast";
		this.value = JJavaSootValue.produce(castExpression.getOp());
		this.type = JJavaSootType.produce(castExpression.getType());
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
		return( anExpression instanceof JCastExpr);
	}	
}
