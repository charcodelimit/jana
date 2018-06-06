package jana.lang.java.bcel.values;

import java.io.IOException;

import jana.metamodel.values.JValue;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantValue;

/*
 * Utf8 encoded string constants are a special kind of string constants
 * 
 * They have no meaning outside of the ClassPool, therefore they appear only in BCEL.
 */
public class JJavaBcelUtf8Constant extends JJavaBcelStringConstant 
{
	public JJavaBcelUtf8Constant(ConstantValue constantValue) throws IOException
	{
		super(constantValue);
	}
	
	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ConstantUtf8;
	}
	
	protected void initialize(Constant aConstant, ConstantPool theConstantPool) 
	{
		ConstantUtf8 cs;
		
		cs = (ConstantUtf8) aConstant;
		this.constantValue = cs.getBytes();
	}

	protected boolean compareValues(JValue aValue) 
	{
		if(aValue instanceof JJavaBcelUtf8Constant) 
		{
			return super.compareValues(aValue);
		}
		
		return false;
	}
}
