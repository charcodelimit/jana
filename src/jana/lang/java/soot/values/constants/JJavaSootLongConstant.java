package jana.lang.java.soot.values.constants;

import soot.jimple.Constant;
import soot.jimple.LongConstant;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.values.JValue;


public class JJavaSootLongConstant extends JJavaSootConstantValue 
{
	public JJavaSootLongConstant(Constant constantValue) throws Exception
	{
		this((LongConstant) constantValue);
	}
	
	protected JJavaSootLongConstant(LongConstant constantValue) throws Exception
	{
		this.valueType = "java-constant-long-value";
		this.value = new Long(constantValue.value).toString();
		this.type = JJavaSootType.produce(constantValue.getType()); 
	}

	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof LongConstant;
	}
	
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaSootLongConstant) 
		{
			JJavaSootLongConstant other = (JJavaSootLongConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Long getValue()
	{
		return Long.valueOf(this.value);
	}
		
	public int hashCode() 
	{
		return this.value.hashCode();
	}
	
	public String toString()
	{
		return value;
	}
}
