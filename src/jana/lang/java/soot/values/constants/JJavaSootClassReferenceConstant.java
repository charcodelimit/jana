package jana.lang.java.soot.values.constants;

import soot.jimple.Constant;
import soot.jimple.ClassConstant;
import jana.lang.java.soot.typesystem.JJavaSootType;

/*
 * symbolic references to classes are represented as Strings
 * of their fully qualified names 
 */
public class JJavaSootClassReferenceConstant extends JJavaSootStringConstant
{
	public JJavaSootClassReferenceConstant(Constant constantValue) throws Exception
	{
		this((ClassConstant) constantValue);
	}
	
	public JJavaSootClassReferenceConstant(ClassConstant constantValue) throws Exception
	{
		super();
		this.valueType = "java-constant-class-reference";
		this.value = constantValue.getValue();
		this.type = JJavaSootType.produce(constantValue.getType());
	}
	
	protected static boolean modelsConstantValue(Constant aConstant)
	{
		return aConstant instanceof ClassConstant;
	}
}
