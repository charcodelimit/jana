package jana.lang.java.soot.values.constants;

import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.values.JValue;
import jana.util.JString;

import soot.jimple.Constant;
import soot.jimple.StringConstant;

/**
 * A String literal representing the value of type java.lang.String.
 * 
 * @author chr
 *
 */
public class JJavaSootStringConstant extends JJavaSootConstantValue 
{
	protected JJavaSootStringConstant()
	{
	}
	
	public JJavaSootStringConstant(Constant constantValue) throws Exception
	{
		this((StringConstant) constantValue);
	}
	
	public JJavaSootStringConstant(StringConstant constantValue) throws Exception
	{
		this.valueType = "java-constant-string-value";
		//converts the value to a String with certain characters printed as
	    //if they were in a Java string literal. 
		this.value = JString.toQuotedString(constantValue.value);
		this.type = JJavaSootType.produce(constantValue.getType());
	}
	
	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof StringConstant;
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

	public String getValue()
	{
		return value;
	}
	
	@Override
	public int hashCode() 
	{
		return value.hashCode();
	}
	
	public String toString()
	{
		return getValue().toString();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append(this.valueType);
		
		aStringBuffer.append(" ");
		aStringBuffer.append("\"");
		aStringBuffer.append(this.value);
		aStringBuffer.append("\"");
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
	}
}
