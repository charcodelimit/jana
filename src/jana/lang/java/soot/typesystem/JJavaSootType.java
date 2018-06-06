package jana.lang.java.soot.typesystem;


import jana.lang.java.typesystem.JJavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import soot.Type;

/**
 * There can be many instances of a type, 
 * but only one implementation for it.
 * For example, several methods may have the same 
 * return type. When asked for their return type they will
 * return a new instance of type. These different instances, 
 * however, refer all to the same implementation.
 * Types are resolved during linking. The different instances are
 * then connected to one implementation.
 * 
 * using the hash map reduces the number of created objects significantly (factor 100)
 */
public abstract class JJavaSootType extends JJavaType 
{
	protected static transient HashMap<String, JJavaType> types;
	
	protected static transient boolean recordObjectTypes = false;
	protected static transient ArrayList<String> objectTypes;
	
	public static void initialize()
	{
		if(types != null)
			System.out.print("t");
		
		types = null;
		objectTypes = null;
	}
	
	/**
	 * ASSUMES THAT TYPE NAMES ARE UNIQUE!
	 * 
	 * @param aType
	 * @return
	 * @throws Exception
	 */
	public static JJavaType produce(Type aType) throws Exception
	{
		JJavaType javaType;
		String typeName;
		boolean isObjectType;
		
		if(types == null)
		{
			types = new HashMap<String, JJavaType>();
			objectTypes = new ArrayList<String>();
		}
		
		javaType = null;
		isObjectType = false;
		
		typeName = aType.toString();
				
		if(types.containsKey(typeName))
		{
			javaType = types.get(typeName);
		}
		else
		{
			if(JJavaSootObjectType.modelsType(aType))
			{
				javaType = new JJavaSootObjectType(aType);
				isObjectType = true;
			}
		
			if(javaType == null && JJavaSootBasicType.modelsType(aType))
				javaType = new JJavaSootBasicType(aType);
		
			if(javaType == null && JJavaSootArrayType.modelsType(aType))
				javaType = new JJavaSootArrayType(aType);
		
			if(javaType == null && JJavaSootBooleanType.modelsType(aType))
				javaType = new JJavaSootBooleanType(aType);
			
			if(javaType == null && JJavaSootVoidType.modelsType(aType))
				javaType = new JJavaSootVoidType(aType);
			
			if(javaType == null && JJavaSootNullType.modelsType(aType))
				javaType = new JJavaSootNullType(aType);
			
			if(javaType == null)
				throw new Exception("Unknown Java Type: " + aType.toString());
			else
			{
				types.put(typeName, javaType);
				
				if(recordObjectTypes && isObjectType)
					objectTypes.add(typeName);
			}
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
	
	public static void setRecordReferencedObjectTypes(boolean aValue)
	{
		recordObjectTypes = aValue;
	}
	
	/**
	 * resets the list of recorded object types
	 * @return a copy of the list of recorded object types
	 */
	public static List<String> getObjectTypes()
	{
		List<String> returnValue;
		
		if(objectTypes != null)
			returnValue = new ArrayList<String>(objectTypes);
		else
			returnValue = new ArrayList<String>();
		
		objectTypes = new ArrayList<String>();
		
		return returnValue;
	}
	
	public String getJavaSignature()
	{
		return this.signature.qualifiedName();
	}
	
	public String toString()
	{
		return this.getJavaSignature();
	}
}
