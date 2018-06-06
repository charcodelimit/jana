package jana.lang.java.bcel;

import java.util.ArrayList;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import jana.lang.java.JJavaAnnotation;
import jana.lang.java.JJavaClassifier;
import jana.lang.java.JJavaMethodDeclaration;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.typesystem.JJavaBcelObjectType;
import jana.lang.java.bcel.typesystem.JJavaBcelType;
import jana.lang.java.typesystem.JJavaReferenceType;
import jana.metamodel.JNamedElement;
import jana.metamodel.typesystem.JType;

public class JJavaBcelMethodDeclaration extends JJavaMethodDeclaration 
{
	protected JJavaBcelMethodDeclaration(Method aMethod, JJavaClassifier anOwner) throws Exception
	{
		this.name = aMethod.getName();
		this.owner = anOwner;
		
		initialize(aMethod);
	}
	
	protected void initialize(Method aMethod) throws Exception
	{
		this.returnType = JJavaBcelType.produce( aMethod.getReturnType() );
	
		Type[] argumentTypes;
		argumentTypes = aMethod.getArgumentTypes();
		
		this.parameterTypes = new ArrayList<JType>(argumentTypes.length);
			
		for(int i=0; i < argumentTypes.length; i++)
			this.parameterTypes.add(JJavaBcelType.produce( argumentTypes[i] ));
		
		initializeAnnotations(aMethod.getAnnotationEntries());
		
		this.modifiers = new JJavaBcelMethodModifiers(aMethod);
		
		String[] exceptionNames;
		if( aMethod.getExceptionTable() != null)
			exceptionNames =  aMethod.getExceptionTable().getExceptionNames();
		else
			exceptionNames = new String[0];
		
		this.thrownExceptions = new ArrayList<JJavaReferenceType>(exceptionNames.length);
		
		for(int i = 0; i < exceptionNames.length; i++)
		{
			this.thrownExceptions.add( new JJavaBcelObjectType(JJavaSignature.signatureFor(exceptionNames[i])));
		}
	}

	protected void initializeAnnotations(AnnotationEntry[] annotationEntries) throws Exception
	{
		this.annotations = new ArrayList<JJavaAnnotation>(annotationEntries.length);
		
		for(int i=0; i < annotationEntries.length; i++)
			this.annotations.add(new JJavaBcelAnnotation(annotationEntries[i]));
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (java-method-declaration annotations modifiers owner-class name return-type parameter-types) 
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("java-method-declaration");
		
		JNamedElement.elementListToSExpression(this.annotations, aStringBuffer);
		
		aStringBuffer.append(" (");
		modifiers.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		JJavaClassifier ownerClassifier = (JJavaClassifier) this.owner;
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(ownerClassifier.qualifiedName());
		aStringBuffer.append("\"");
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(this.name);
		aStringBuffer.append("\"");
		
		aStringBuffer.append(" (");
		aStringBuffer.append(this.returnType.toSExpression());
		aStringBuffer.append(")");
		
		JNamedElement.elementListToSExpression(this.parameterTypes, aStringBuffer);
		
		JNamedElement.elementListToSExpression(this.thrownExceptions, aStringBuffer);
	}
	
	
}
