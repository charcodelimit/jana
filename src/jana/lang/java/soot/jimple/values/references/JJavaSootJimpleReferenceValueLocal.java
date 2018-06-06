package jana.lang.java.soot.jimple.values.references;

import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.lang.java.typesystem.JJavaType;

import java.util.HashMap;

import soot.Value;
import soot.jimple.internal.JimpleLocal;

/***
 * (jimple-value-local-variable variable-name variable-type)
 * 
 * Uses memoization to reduce the number of created instances.
 * 
 * @author chr
 *
 */
public class JJavaSootJimpleReferenceValueLocal extends JJavaSootJimpleReferenceValue
{
	protected static HashMap<String, JJavaSootJimpleReferenceValueLocal> localVariables;
	
	public JJavaSootJimpleReferenceValueLocal(String aName, JJavaType aJavaType) throws Exception
	{
		super(aJavaType);
		
		this.valueType = "jimple-reference-value-local";
		this.value = aName;
	}
	
	/**
	 * Reset the memoization cache.
	 */
	public static void initialize()
	{
		localVariables = null;
	}
	
	public static int numLocalVariables()
	{
		if(localVariables != null)
			return localVariables.size();
		else
			return 0;
	}
	
	public static JJavaSootJimpleReferenceValueLocal produce(Value aValue) throws Exception
	{
		JJavaSootJimpleReferenceValueLocal instance;
		JJavaType javaType;
		JimpleLocal local;
		StringBuffer key;
		
		key = new StringBuffer();
		local = (JimpleLocal) aValue; 
		
		if(localVariables == null)
			localVariables = new HashMap<String, JJavaSootJimpleReferenceValueLocal>();
		
		javaType = JJavaSootType.produce(local.getType());
		key.append(local.getName());
		key.append('<');
		key.append(javaType.getJavaSignature());
		key.append('>'); 		
		
		instance = localVariables.get(key.toString());
		
		if(instance == null)
		{
			instance = new JJavaSootJimpleReferenceValueLocal(local.getName(),javaType);
			localVariables.put(key.toString(), instance);
		}
		
		return instance;
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof JimpleLocal);
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
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(this.value);
		aStringBuffer.append('\"');
		
		aStringBuffer.append(" (");
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
	}
}
