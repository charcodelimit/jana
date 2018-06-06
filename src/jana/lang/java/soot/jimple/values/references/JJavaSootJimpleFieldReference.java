package jana.lang.java.soot.jimple.values.references;

import jana.lang.java.JJavaSignature;
import soot.SootFieldRef;
import soot.Value;

public abstract class JJavaSootJimpleFieldReference extends JJavaSootJimpleReferenceValue
{
	// the class to which the field belongs
	JJavaSignature ownerClassSignature;
	
	protected JJavaSootJimpleFieldReference(SootFieldRef aFieldReference) throws Exception
	{
		super(aFieldReference.type());
		this.ownerClassSignature = JJavaSignature.signatureFor(aFieldReference.declaringClass().getName());
		this.value = aFieldReference.name();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (... declaring-class-signature type field-name)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.ownerClassSignature.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.value);
		aStringBuffer.append('\"');
	}
	
	public static JJavaSootJimpleFieldReference produce(Value aValue) throws Exception
	{
		if(JJavaSootJimpleInstanceFieldRef.modelsValue(aValue))
			return new JJavaSootJimpleInstanceFieldRef(aValue);
		
		if(JJavaSootJimpleStaticFieldRef.modelsValue(aValue))
			return new JJavaSootJimpleStaticFieldRef(aValue);
		
		throw new Exception("Unknown field reference value " + aValue.toString());
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return JJavaSootJimpleInstanceFieldRef.modelsValue(aValue) ||
			   JJavaSootJimpleStaticFieldRef.modelsValue(aValue);
	}

}
