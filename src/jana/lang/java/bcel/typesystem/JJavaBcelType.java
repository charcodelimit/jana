package jana.lang.java.bcel.typesystem;

import java.util.HashMap;

import org.apache.bcel.generic.Type;

import jana.lang.java.typesystem.JJavaType;

/*
 * There can be many instances of a type, 
 * but only one implementation for it.
 * For example, several methods may have the same 
 * return type. When asked for their return type they will
 * return a new instance of type. These different instances, 
 * however, refer all to the same implementation.
 * Types are resolved during linking. The different instances are
 * then connected to one implementation.
 */
public abstract class JJavaBcelType extends JJavaType 
{
	static transient HashMap<String, JJavaType> types;
	
	public static void initialize()
	{
		if(types != null)
			System.out.print("t");
		
		types = null;
	}
	
	public static JJavaType produce(Type aType) throws Exception
	{
		JJavaType javaType;
		String typeName;
		
		if(types == null)
			types = new HashMap<String, JJavaType>();
		
		javaType = null;
		
		typeName = aType.toString();
		if(types.containsKey(typeName))
		{
			javaType = types.get(typeName);
		}
		else
		{		
			if(JJavaBcelObjectType.modelsType(aType))
				javaType = new JJavaBcelObjectType(aType);
		
			if(javaType == null && JJavaBcelBasicType.modelsType(aType))
				javaType = new JJavaBcelBasicType(aType);
		
			if(javaType == null && JJavaBcelArrayType.modelsType(aType))
				javaType = new JJavaBcelArrayType(aType);
		
			if(javaType == null)
				throw new Exception("Unknown Java Type: " + aType.getSignature());
			else
				types.put(typeName, javaType);
		}
		
		if(javaType == null)
			throw new Exception("Unknown Java Type: " + aType.toString());
		
		return javaType;
	}
	
	public static int numTypes()
	{
		if(types != null)
			return types.size();
		else
			return 0;
	}
}
