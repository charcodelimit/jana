package jana.lang.java.bcel;

import java.util.ArrayList;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Field;

import jana.lang.java.JJavaAnnotation;
import jana.lang.java.JJavaFieldDeclaration;
import jana.lang.java.bcel.typesystem.JJavaBcelType;
import jana.lang.java.bcel.values.JJavaBcelConstantValue;
import jana.metamodel.JNamedElement;

public class JJavaBcelFieldDeclaration extends JJavaFieldDeclaration
{

	public JJavaBcelFieldDeclaration(Field field) throws Exception
	{
		ConstantValue value;
		
		value = field.getConstantValue();
		
		if(value != null)
			this.constantValue = JJavaBcelConstantValue.produce( value );
		else
			this.constantValue = null;
		
		initAnnotations(field.getAnnotationEntries());
		
		this.modifiers = new JJavaBcelFieldModifiers( field );
		
		this.name = field.getName();
		this.type = JJavaBcelType.produce( field.getType() );
	}
	
	protected void initAnnotations(AnnotationEntry[] annotationEntries) throws Exception
	{
		this.annotations = new ArrayList<JJavaAnnotation>(annotationEntries.length);
		
		for(int i=0; i < annotationEntries.length; i++)
			this.annotations.add(new JJavaBcelAnnotation(annotationEntries[i]));
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.modifiers.toString());
		
		sb.append(" ");
		sb.append(this.type.toString());
		
		sb.append(" ");
		sb.append(this.name);
		
		if(this.constantValue != null)
		{
			sb.append(" = ");
			sb.append(this.constantValue.toString());
		}
			
		return sb.toString();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (java-field-declaration field-name annotations modifiers type)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("java-field-declaration");
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(this.name);
		aStringBuffer.append("\"");
		
		JNamedElement.elementListToSExpression(this.annotations, aStringBuffer);
		
		aStringBuffer.append(" (");
		aStringBuffer.append(this.modifiers.toSExpression());
		aStringBuffer.append(")");
		
		aStringBuffer.append(" (");
		aStringBuffer.append(this.type.toSExpression());
		aStringBuffer.append(")");
	}
}
