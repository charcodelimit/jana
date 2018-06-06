package jana.lang.java.soot.values.constants;

import jana.lang.java.soot.typesystem.JJavaSootNullType;
import jana.metamodel.values.JValue;
import soot.jimple.Constant;
import soot.jimple.NullConstant;

public class JJavaSootNullConstant extends JJavaSootConstantValue
{
	private static JJavaSootNullConstant instance = new JJavaSootNullConstant();
	
	public JJavaSootNullConstant()
	{
		this.valueType = "java-null-value";
		this.value = "null";
		
		try
		{
			this.type = new JJavaSootNullType();
		}
		catch(Exception e)
		{
		}
	}
	
	public String getValue()
	{
		return value;
	}
	
	/**
	 * the hash code has to be the same for all instances of the same subtype of JNullValue,
	 * but distinct from other objects
	 */
	public int hashCode()
	{
		return instance.hashCode();
	}
	
	public String toString()
	{
		return "null";
	}
	
	public String toSExpression()
	{
		return this.valueType;
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.toSExpression());
	}
		
	@Override
	protected boolean compareValues(JValue aValue) 
	{
		if(aValue instanceof JJavaSootStringConstant) 
		{
			JJavaSootStringConstant other = (JJavaSootStringConstant) aValue;
			
			return this.getValue().equals(other.getValue());
		}
		
		return false;
	}
	
	public static JJavaSootNullConstant getInstance() 
	{
		return instance;
	}
	
	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof NullConstant;
	}
}
