package jana.lang.java.soot.jimple.instructions.object.creation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Value;
import soot.jimple.AnyNewExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.lang.java.soot.values.JJavaSootValue;
import jana.lang.java.typesystem.JJavaType;
import jana.metamodel.JNamedElement;

@SuppressWarnings("unchecked")
public class JJavaSootJimpleNewArrayExpr extends JJavaSootJimpleObjectInstantiationInstruction
{
	protected int dimension;
	protected List<JJavaSootValue> size;
	
	public JJavaSootJimpleNewArrayExpr(JJavaType aType, int arrayDimension)
	{
		this.instructionType = "jimple-new-array";
		this.type = aType;
		this.dimension = arrayDimension;
		this.size = new ArrayList<JJavaSootValue>(this.dimension); 
	}
	
	/**
	 * Don't use this constructor, as this type models two Soot types!
	 * The modeled type cannot be decided in a Java constructor 
	 * (chr: unlike in Smalltalk constructors do not return the newly created instances,
	 *       or unlike in C++ new cannot be overwritten ARRGGH!)
	 * 
	 * @param anyNewExpr
	 * @throws Exception
	 */
	public JJavaSootJimpleNewArrayExpr(AnyNewExpr anyNewExpr) throws Exception
	{
		throw new Exception("Use the right cast to JNewMultiArrayExpr or JNewArrayExpr!");
	}
	
	public JJavaSootJimpleNewArrayExpr(JNewMultiArrayExpr newMultiArrayExpression) throws Exception
	{
		this(JJavaSootType.produce(newMultiArrayExpression.getBaseType().baseType), newMultiArrayExpression.getSizeCount());
       
		for(Iterator iter = newMultiArrayExpression.getSizes().iterator(); iter.hasNext(); )
		{
			this.size.add( JJavaSootValue.produce( (Value) iter.next() ) );
		}
    }  
	
    public JJavaSootJimpleNewArrayExpr(JNewArrayExpr newArrayExpression) throws Exception
    {
    	this(JJavaSootType.produce(newArrayExpression.getBaseType()), 1);
        
        this.size.add( JJavaSootValue.produce(newArrayExpression.getSize()) );
    }
    
    public String toSExpression()
    {
    	StringBuffer sb = new StringBuffer();
    	
    	this.toSExpression(sb);
    	
    	return sb.toString();
    }

   /** 
    * (new-array type dimension sizes)
    * @return
    */
   public void toSExpression(StringBuffer aStringBuffer)
   {
	   aStringBuffer.append(this.instructionType);
	   
	   aStringBuffer.append(" (");
	   this.type.toSExpression(aStringBuffer);
	   aStringBuffer.append(")");
	   
	   aStringBuffer.append(" ");
	   aStringBuffer.append(this.dimension);
	   
	   JNamedElement.elementListToSExpression(this.size, aStringBuffer);
   }
   
   public static boolean modelsJimpleAnyNewExpression(AnyNewExpr newExpression)
   {
	   return (newExpression instanceof JNewArrayExpr) || 
	   		  (newExpression instanceof JNewMultiArrayExpr);
   }
}
