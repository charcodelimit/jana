package jana.lang.java.soot;

import java.util.List;

import soot.Type;
import soot.SootClass;
import soot.SootMethod;

import jana.lang.java.JJavaMethodImplementation;
import jana.lang.java.typesystem.JJavaType;

public class JJavaSootMethodImplementation extends JJavaMethodImplementation
{

	protected JJavaSootMethodImplementation()
	{	
	}
	
	public static JJavaSootMethodImplementation fromJJavaMethodImplementation(JJavaMethodImplementation aMethodImplementation)
	{
		JJavaSootMethodImplementation instance;
		
		instance = new JJavaSootMethodImplementation();
		instance.initializeInstance(aMethodImplementation);
		
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasCompatibleParameterTypes(SootMethod aSootMethod)
	{
		List<Type> methodParameterTypes;
		JJavaType myArgType;
		Type argType;
		
		if(aSootMethod.getParameterCount() == this.parameterTypes.size())
		{
			methodParameterTypes = aSootMethod.getParameterTypes();
		
			for(int index = 0; index < this.parameterTypes.size(); index++)
			{
				myArgType = (JJavaType) this.parameterTypes.get(index);
				argType = methodParameterTypes.get(index);
				
				if( !(argType.toString().equals(myArgType.getJavaSignature())))
					return false;
			}
		
			return true;
		}
		
		return false;
	}
	
	public boolean correspondsTo(SootMethod aSootMethod)
	{
		SootClass declaringClass;
		String returnTypeName;
		JJavaType myReturnType;
		
		declaringClass = aSootMethod.getDeclaringClass();
		
		if(declaringClass.getName().equals(this.owner.qualifiedName())) // check declaring class
		{
			if(aSootMethod.getName().equals(this.name)) // method name
			{
				returnTypeName = aSootMethod.getReturnType().toString();
				myReturnType = (JJavaType) this.returnType;
				
				if(returnTypeName.equals(myReturnType.getJavaSignature())) // return type compatibility
					return hasCompatibleParameterTypes(aSootMethod);
			}
		}
		
		return false;
	}
}
