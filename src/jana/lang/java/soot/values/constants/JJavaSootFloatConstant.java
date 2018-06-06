package jana.lang.java.soot.values.constants;


import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;

import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.metamodel.values.JValue;

/**
 * These LEAST_POSITIVE_SINGLE_FLOAT and MOST_POSITIVE_SINGLE_FLOAT values depend on the Common-Lisp implementation used!
 * Check it out with least-positive-single-float and most-positive-single-float respectively in your own CL-Implementation.
 * Successfully tested with: CLISP, SBCL, LISPWORKS
 */
public class JJavaSootFloatConstant extends JJavaSootConstantValue
{	
	static final double LEAST_POSITIVE_SINGLE_FLOAT = 1.1754944E-38;
	static final double MOST_POSITIVE_SINGLE_FLOAT = 3.4028235E38;
	
	public JJavaSootFloatConstant(Constant constantValue) throws Exception
	{
		this((FloatConstant) constantValue);
	}
	
	protected JJavaSootFloatConstant(FloatConstant constantValue) throws Exception
	{
		Float floatValue;
		DoubleConstant doubleConstantValue;
		
		this.valueType = "java-constant-float-value";
		this.type = JJavaSootType.produce(constantValue.getType());
		
		floatValue = new Float(constantValue.value);
		doubleConstantValue = DoubleConstant.v(constantValue.value);

		if( JJavaSootFloatConstant.isOutOfRange(doubleConstantValue.value) || JJavaSootFloatConstant.isInfinite(doubleConstantValue.value) )
		{
			if( JJavaSootFloatConstant.isInfinite(doubleConstantValue.value) )
				this.value = "\"" + doubleConstantValue.toString() + "\""; // it is really infinite
			else
				this.value = "\"" + floatValue.toString() + "\""; // float is just out of range
		}
		else
			this.value = floatValue.toString();
	}

	public static boolean isOutOfRange(double aDoubleValue)
	{
		Double abs = new Double( Math.abs(aDoubleValue) );
		
		return (abs.doubleValue() != 0.0) &&  
			   ((abs.doubleValue() < LEAST_POSITIVE_SINGLE_FLOAT) ||
				(abs.doubleValue() > MOST_POSITIVE_SINGLE_FLOAT));			
	}
	
	public static boolean isInfinite(double aDoubleValue)
	{
		Double doubleValue = new Double(aDoubleValue);
		
		return doubleValue.isInfinite() || doubleValue.isNaN();		
	}
	
	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof FloatConstant;
	}
		
	@Override
	protected boolean compareValues(JValue aValue)
	{
		if(aValue instanceof JJavaSootFloatConstant) 
		{
			JJavaSootFloatConstant other = (JJavaSootFloatConstant) aValue;
		
			return this.getValue().equals(other.getValue());
		}
	
		return false;
	}
	
	public Float getValue()
	{
		return Float.valueOf(this.value);
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
