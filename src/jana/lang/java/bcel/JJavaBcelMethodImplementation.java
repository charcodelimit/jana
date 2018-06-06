package jana.lang.java.bcel;

import java.util.ArrayList;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

import jana.lang.java.JJavaAnnotation;
import jana.lang.java.JJavaClassifier;
import jana.lang.java.JJavaMethodImplementation;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.typesystem.JJavaBcelObjectType;
import jana.lang.java.bcel.typesystem.JJavaBcelType;
import jana.lang.java.typesystem.JJavaReferenceType;
import jana.metamodel.JClosure;
import jana.metamodel.typesystem.JType;

public class JJavaBcelMethodImplementation extends JJavaMethodImplementation
{

	protected JJavaBcelMethodImplementation(Method aMethod, JJavaClassifier anOwner, int methodID) throws Exception
	{
		this.name = aMethod.getName();
		this.owner = anOwner;
		
		initialize(aMethod, methodID);
	}
	
	protected void initialize(Method aMethod, int methodID) throws Exception
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
		
		this.body = ((JJavaClassifier) this.owner).initClosureFor(this, methodID);
	}
	
	public JClosure getMethodBody()
	{
		return this.body;
	}
	
	protected void initializeAnnotations(AnnotationEntry[] annotationEntries) throws Exception
	{
		this.annotations = new ArrayList<JJavaAnnotation>(annotationEntries.length);
		
		for(int i=0; i < annotationEntries.length; i++)
			this.annotations.add(new JJavaBcelAnnotation(annotationEntries[i]));
	}
}
