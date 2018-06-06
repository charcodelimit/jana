package jana.lang.java.soot.values.constants;

import soot.jimple.Constant;
import soot.jimple.IntConstant;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.values.JValue;

public class JJavaSootIntegerConstant extends JJavaSootConstantValue 
{	
	public JJavaSootIntegerConstant(Constant constantValue) throws Exception
	{
		this((IntConstant) constantValue);
	}
	
	protected JJavaSootIntegerConstant(IntConstant constantValue) throws Exception
	{
		this.valueType = "java-constant-int-value";
		this.value = constantValue.toString();
		this.type = JJavaSootType.produce(constantValue.getType());
	}

	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof IntConstant;
	}
	
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaSootStringConstant) 
		{
			JJavaSootIntegerConstant other = (JJavaSootIntegerConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Integer getValue()
	{
		return Integer.getInteger(this.value);
	}
		
	public int hashCode() 
	{
		return this.value.hashCode();
	}
	
	public String toString()
	{
		return this.value;
	}
}
