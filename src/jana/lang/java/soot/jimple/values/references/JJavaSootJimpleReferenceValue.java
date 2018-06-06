package jana.lang.java.soot.jimple.values.references;

import jana.lang.java.soot.jimple.values.JJavaSootJimpleValue;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.lang.java.typesystem.JJavaType;
import soot.Type;
import soot.Value;

public abstract class JJavaSootJimpleReferenceValue extends JJavaSootJimpleValue
{
	protected JJavaSootJimpleReferenceValue(Type aType) throws Exception
	{
		this.type = JJavaSootType.produce(aType);
	}
	
	protected JJavaSootJimpleReferenceValue(JJavaType aJavaType) throws Exception
	{
		this.type = aJavaType;
	}
	
	public static JJavaSootJimpleReferenceValue produce(Value aValue) throws Exception
	{
		if(JJavaSootJimpleReferenceValueLocal.modelsValue(aValue))
			return JJavaSootJimpleReferenceValueLocal.produce(aValue);
		
		if(JJavaSootJimpleArrayRef.modelsValue(aValue))
			return new JJavaSootJimpleArrayRef(aValue);
		
		if(JJavaSootJimpleCaughtExceptionRef.modelsValue(aValue))
			return new JJavaSootJimpleCaughtExceptionRef(aValue);
		
		if(JJavaSootJimpleMethodRef.modelsValue(aValue))
			return new JJavaSootJimpleMethodRef(aValue);
		
		if(JJavaSootJimpleParameterRef.modelsValue(aValue))
			return new JJavaSootJimpleParameterRef(aValue);
			
		if(JJavaSootJimpleThisRef.modelsValue(aValue))
			return new JJavaSootJimpleThisRef(aValue);

		if(JJavaSootJimpleFieldReference.modelsValue(aValue))
			return JJavaSootJimpleFieldReference.produce(aValue);
		
		throw new Exception("Unknown value type " + aValue.toString());
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return JJavaSootJimpleReferenceValueLocal.modelsValue(aValue) || 
			   JJavaSootJimpleArrayRef.modelsValue(aValue) ||
		       JJavaSootJimpleCaughtExceptionRef.modelsValue(aValue) ||
		       JJavaSootJimpleMethodRef.modelsValue(aValue) ||
		       JJavaSootJimpleParameterRef.modelsValue(aValue) ||
		       JJavaSootJimpleThisRef.modelsValue(aValue) ||  
			   JJavaSootJimpleFieldReference.modelsValue(aValue);
	}
}
