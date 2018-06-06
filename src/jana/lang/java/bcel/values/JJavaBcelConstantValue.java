package jana.lang.java.bcel.values;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantValue;

import jana.lang.java.values.JJavaConstantValue;

/*
 * TODO: pull up all the inheritance relationships and subclasses of JJavaBcelConstantValue
 *       into corresponding classes in jana.lang.java.values
 */

public abstract class JJavaBcelConstantValue extends JJavaConstantValue 
{
	/**
	 * used only to create constant values directly
	 */
	protected JJavaBcelConstantValue()
	{
	}
	
	protected JJavaBcelConstantValue(ConstantValue constantValue)
	{
		int index;
		Constant constant;
		ConstantPool pool;
		
		index = constantValue.getConstantValueIndex();
		pool = constantValue.getConstantPool();
		constant = pool.getConstant(index);
		
		this.initialize(constant, pool);
	}

	/*
	 * subclass-responsibility
	 */
	protected abstract void initialize(Constant aConstant, ConstantPool theConstantPool); 
	
	/*
	 * subclass responsibility
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();
	
	/*
	 * modelsConstantValue @returns true if the @argument represents 
	 * a constant value that is modeled by this class.
	 * because JJavaBcelConstantValue is an abstract class it returns false.
	 */
	protected static boolean modelsConstantValue(Constant aConstantValue)
	{
		return JJavaBcelUtf8Constant.modelsConstantValue(aConstantValue) || 
			   JJavaBcelIntegerConstant.modelsConstantValue(aConstantValue) ||
			   JJavaBcelFloatConstant.modelsConstantValue(aConstantValue) ||
			   JJavaBcelLongConstant.modelsConstantValue(aConstantValue) ||
			   JJavaBcelDoubleConstant.modelsConstantValue(aConstantValue) ||
			   JJavaBcelClassReferenceConstant.modelsConstantValue(aConstantValue) ||
			   JJavaBcelStringConstant.modelsConstantValue(aConstantValue);
	}
	
	public static JJavaBcelConstantValue produce(ConstantValue aConstantValue) throws Exception
	{
		Constant c;
		
		c = JJavaBcelConstantValue.getConstant(aConstantValue);
		
		if( JJavaBcelUtf8Constant.modelsConstantValue(c) )
			return new JJavaBcelUtf8Constant(aConstantValue);
		
		if( JJavaBcelIntegerConstant.modelsConstantValue(c) )
			return new JJavaBcelIntegerConstant(aConstantValue);
		
		if( JJavaBcelFloatConstant.modelsConstantValue(c) )
			return new JJavaBcelFloatConstant(aConstantValue);
		
		if( JJavaBcelLongConstant.modelsConstantValue(c) )
			return new JJavaBcelLongConstant(aConstantValue);
		
		if( JJavaBcelDoubleConstant.modelsConstantValue(c) )
			return new JJavaBcelDoubleConstant(aConstantValue);
		
		if( JJavaBcelClassReferenceConstant.modelsConstantValue(c) )
			return new JJavaBcelClassReferenceConstant(aConstantValue);
		
		if( JJavaBcelStringConstant.modelsConstantValue(c) )
			return new JJavaBcelStringConstant(aConstantValue);
		
		
		throw new Exception("Unsupported Java Constant Type: " + aConstantValue.toString());
	}
	
	protected static Constant getConstant(ConstantValue aConstantValue)
	{
		int index;
		ConstantPool pool;
		
		index = aConstantValue.getConstantValueIndex();
		pool = aConstantValue.getConstantPool();
		
		return pool.getConstant(index);
	}
}
