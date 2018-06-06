package jana.lang.java.soot.jimple.instructions.object.creation;

import soot.jimple.AnyNewExpr;
import soot.jimple.internal.JNewExpr;
import jana.lang.java.soot.typesystem.JJavaSootType;

public class JJavaSootJimpleNewExpr extends JJavaSootJimpleObjectInstantiationInstruction
{	
   public JJavaSootJimpleNewExpr(JNewExpr newExpression) throws Exception
   {
	 this.instructionType = "jimple-new"; 
	 this.type = JJavaSootType.produce(newExpression.getType()); 
   }
   
   public JJavaSootJimpleNewExpr(AnyNewExpr newExpression) throws Exception
   {
	this((JNewExpr) newExpression);
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
	   this.type.toSExpression(aStringBuffer);
	   aStringBuffer.append(")");
   }
   
   public static boolean modelsJimpleAnyNewExpression(AnyNewExpr newExpression)
   {
	   return (newExpression instanceof JNewExpr);
   }
}
