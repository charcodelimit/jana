package jana.lang.java.values;

import jana.lang.java.soot.values.constants.JJavaSootStringConstant;
import jana.metamodel.values.JValue;

public class JJavaNullValue extends JJavaConstantValue
{
	private static JJavaNullValue instance = new JJavaNullValue();
		
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
		return "java-null-value";
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
			
			return this.toString().equals(other.toString());
		}
		
		return false;
	}
	
	public static JJavaNullValue getInstance() 
	{
		return instance;
	}
}
