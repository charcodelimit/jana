package jana.lang.java.soot.values.constants;

import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.values.JValue;


public class JJavaSootDoubleConstant extends JJavaSootConstantValue 
{	
	public JJavaSootDoubleConstant(Constant constantValue) throws Exception
	{
		this((DoubleConstant) constantValue);
	}
	
	/**
	 * As the maximum supported length for floating point values is implementation dependent,
	 * we have to make the conservative estimate that all Common Lisp implementations
	 * at least support java.lang.Float's maximum and minimum values
	 * 
	 * @param constantValue
	 * @throws Exception
	 */
	protected JJavaSootDoubleConstant(DoubleConstant constantValue) throws Exception
	{	
		this.type = JJavaSootType.produce(constantValue.getType());
		
		this.valueType = "java-constant-double-value";
		
		if( JJavaSootFloatConstant.isOutOfRange(constantValue.value) || JJavaSootFloatConstant.isInfinite(constantValue.value) )
		{
			this.value = "\"" + constantValue.toString() + "\"";
		}
		else
		{
			this.value = constantValue.toString();
			
			if(this.value.indexOf('E') >= 0)
				this.value = this.value.replace('E', 'D');
			else
			{
				this.valueType = "java-constant-exponent-free-double-value";
				this.value = this.value + "D0";
			}
		}
	}

	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof DoubleConstant;
	}
	
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaSootDoubleConstant) 
		{
			JJavaSootDoubleConstant other = (JJavaSootDoubleConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Double getValue()
	{
		return Double.valueOf(this.value);
	}
		
	public int hashCode() 
	{
		return this.value.hashCode();
	}
	
	public String toString()
	{
		return this.value.toString();
	}
}
