package jana.lang.java;

import jana.metamodel.typesystem.JVirtualType;

import java.util.TreeMap;

/**
 * Supports only simple annotation types.
 * Full support of all annotation types would require an implementation of
 * the full ElementValue hierarchy. This hierarchy is now collapsed to one node,
 * namely SimpleElementValue, which are represented as String.
 * 
 * @author chr
 *
 */
public class JJavaAnnotation extends JVirtualType implements Comparable<JJavaAnnotation>
{
	protected JJavaSignature signature;
	protected TreeMap<String, String> elementValuePairs;
	
	protected JJavaAnnotation(JJavaSignature theSignature) 
	{
		this.signature = theSignature;
	}

	public String getName()
	{
		return this.signature.qualifiedName();
	}
	
	public JJavaSignature getSignature() 
	{
		return this.signature;
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("java-annotation");
		
		aStringBuffer.append(" (");
		this.signature.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		if(elementValuePairs.size() > 0 )
		{
			String value;
			
			aStringBuffer.append("(list ");
			for(String element : elementValuePairs.keySet()) //navigableKeySet()
			{
				aStringBuffer.append("(cons \"");
				aStringBuffer.append(element);
				aStringBuffer.append("\" \"");
				value = elementValuePairs.get(element);
				aStringBuffer.append( value );
				aStringBuffer.append("\")");
			}
			aStringBuffer.append(")");
		}
		else
			aStringBuffer.append(" ()");
	}

	/**
	 * Sort Annotations by Annotation Class Name
	 */
	public int compareTo(JJavaAnnotation anAnnotation)
	{
		return this.signature.qualifiedName().compareTo(anAnnotation.signature.qualifiedName());
	}
	
	/*
	 * true if signatures are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object anObject) 
	{
		assert( this.signature != null ); // PRE
		
		if(anObject instanceof JJavaAnnotation)
		{	
			JJavaAnnotation javaAnnotation = (JJavaAnnotation) anObject;
			
			assert( javaAnnotation.signature != null ); // PRE
			
			return javaAnnotation.signature.equals(this.signature);
		}
	
		return false;
	}
	
	public int hashCode() 
	{
		assert( this.signature != null ); // PRE
		
		return this.signature.hashCode();
	}
}
