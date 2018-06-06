package jana.lang.java;

import java.util.ArrayList;
import java.util.List;

import jana.lang.java.bcel.JJavaBcelMethodImplementation;
import jana.lang.java.typesystem.JJavaReferenceType;
import jana.metamodel.JNamedElement;
import jana.metamodel.JRoutineImplementation;
import jana.metamodel.typesystem.JType;

public abstract class JJavaMethodImplementation extends JRoutineImplementation 
{
	protected JJavaClassifier owner;
	protected JJavaMethodModifiers modifiers;
	protected List<JJavaAnnotation> annotations;
	protected List<JJavaReferenceType> thrownExceptions;
	
	public JJavaClassifier getOwnerType()
	{
		return this.owner;
	}
	
	public List<JType> getParameterTypes()
	{
		return parameterTypes;
	}
	
	public JType getReturnType()
	{
		return returnType;
	}
	
	public JJavaMethodModifiers getModifiers()
	{
		return modifiers;
	}

	public List<JJavaAnnotation> getAnnotations()
	{
		return annotations;
	}

	public List<JJavaReferenceType> getThrownExceptions()
	{
		return thrownExceptions;
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public boolean hasCompatibleParameterTypes(List<JType> parameterTypes)
	{
		int numArgs;
		JType myArgType, argType;
		
		numArgs = this.parameterTypes.size();
		
		if( numArgs == parameterTypes.size() )
		{
			for(int i = 0; i < numArgs; i++)
			{
				myArgType = this.parameterTypes.get(i);
				argType = parameterTypes.get(i);
				
				if(!myArgType.equals(argType))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param aMethodImplementation
	 * @return
	 */
	protected void initializeInstance(JJavaMethodImplementation aMethodImplementation)
	{
		List<JType> argTypes;
		List<JJavaAnnotation> annotations;
		List<JJavaReferenceType> exceptions;
 		
		argTypes = aMethodImplementation.getParameterTypes();
		annotations = aMethodImplementation.getAnnotations();
		exceptions = aMethodImplementation.getThrownExceptions();
		
		this.returnType = aMethodImplementation.getReturnType();
		this.name = aMethodImplementation.getName();
		
		this.parameterTypes = new ArrayList<JType>(argTypes.size());
		for(int i = 0; i < argTypes.size(); i++)
			this.parameterTypes.add(argTypes.get(i));
		
		this.owner = aMethodImplementation.getOwnerType();
	
		this.annotations = new ArrayList<JJavaAnnotation>();
		this.thrownExceptions = new ArrayList<JJavaReferenceType>();
				
		this.modifiers = aMethodImplementation.getModifiers();
		
		this.annotations = new ArrayList<JJavaAnnotation>(annotations.size());
		for(int i = 0; i < annotations.size(); i++)
			this.annotations.add(annotations.get(i));
		
		this.thrownExceptions = new ArrayList<JJavaReferenceType>(exceptions.size());
		
		for(int i = 0; i < exceptions.size(); i++)
			this.thrownExceptions.add(exceptions.get(i));
	}
	
	/*
	 * true if signatures are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object anObject)
	{
		if(anObject instanceof JJavaBcelMethodImplementation)
		{
			JJavaBcelMethodImplementation anotherMethodImplementation;
			
			anotherMethodImplementation = (JJavaBcelMethodImplementation) anObject;
			
			return this.name.equals(anotherMethodImplementation.name) && 
				   this.returnType.equals(anotherMethodImplementation.returnType) &&
				   this.hasCompatibleParameterTypes(anotherMethodImplementation.parameterTypes);
		
		}
		
		return false;
	}
	
	/**
	 * @return	the receiver's hash.
	 *
	 * @see	java.lang.Object#equals
	 */
	@Override
	public int hashCode()
	{
		return this.parameterTypes.hashCode() ^ this.name.hashCode() ^ this.returnType.hashCode(); 
	}
	
	/**
	 * (java-method-implementation annotations modifiers owner-class name return-type parameter-types body) 
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("java-method-implementation");
		
		JNamedElement.elementListToSExpression(this.annotations, aStringBuffer);
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		modifiers.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.owner.qualifiedName());
		aStringBuffer.append('\"');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.name);
		aStringBuffer.append('\"');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.returnType.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		JNamedElement.elementListToSExpression(this.parameterTypes, aStringBuffer);
		
		JNamedElement.elementListToSExpression(this.thrownExceptions, aStringBuffer);
		
		aStringBuffer.append('\n');
		aStringBuffer.append('(');
		this.body.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
	}
}
