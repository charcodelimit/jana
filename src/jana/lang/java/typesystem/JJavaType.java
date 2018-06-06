package jana.lang.java.typesystem;

import java.io.Serializable;

import jana.lang.java.JJavaSignature;
import jana.metamodel.typesystem.JType;

/*
 * The top-level class for the metamodel of the Java type system.
 * Types have a signature. Based on it types can be compared. 
 */
public abstract class JJavaType extends JType implements Serializable
{	
	protected JJavaSignature signature;
	
	public JJavaSignature getSignature()
	{
		return signature;
	}
	
	/**
	 * Subclass responsibility;
	 * 
	 * @return the fully qualified signature, as used in Java Sourcecode
	 */
	public abstract String getJavaSignature();
	
	/*
	 * true if signatures are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object anObject) 
	{
		assert( this.signature != null ); // PRE
		
		if(anObject instanceof JJavaType)
		{	
			JJavaType javaType = (JJavaType) anObject;
			
			assert( javaType.signature != null ); // PRE
			
			return javaType.signature.equals(this.signature);
		}
	
		return false;
	}
	
	public int hashCode() 
	{
		assert( this.signature != null ); // PRE
		
		return this.signature.hashCode();
	}
}
