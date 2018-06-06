package jana.lang.java;

import jana.java.JJavaRepository;
import jana.metamodel.JModule;

/**
 * The package stores no references to the classes.
 * Classes are not managed. 
 * If classes should be managed, the repository would be the place to store them. 
 **/
public class JJavaPackage extends JModule implements JNamedJavaElement
{
	//	 INV assert( this.signature != null );
	protected JJavaSignature signature;
	
	public JJavaPackage(JJavaSignature theSignature, JJavaRepository aRepository) 
	{
		this.signature = theSignature;
		aRepository.addPackage(this);
	}
		
	protected JJavaPackage(JJavaSignature theSignature, JJavaClassifier aChildClassifier, JJavaRepository aRepository) 
	{
		this(theSignature, aRepository);
		
		// this.addChildClassifier(aChildClassifier);
	}
	
	/********* Testing **********************/
	
	
	/*
	 * true if signatures are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object anObject) 
	{
		assert( this.signature != null ); // PRE
		
		if(anObject instanceof JJavaPackage)
		{
			return ((JJavaPackage) anObject).signature.equals(this.signature);
		}
	
		return false;
	}
	
	public int hashCode() 
	{
		assert( this.signature != null ); // PRE
		
		return this.signature.hashCode();
	}

	
	/*********** Accessing ****************/
	
	public String relativeFilename()
	{
		return signature.asPackageDirectoryName();
	}
	
	public String getName() 
	{
		if(this.name == null)
		{
			name = this.getSignature().unqualifiedName();
		}
		
		return name;
	}

	public JJavaSignature getSignature() 
	{
		return this.signature;
	}		
	

	/************** Printing *****************/
	
	public String toString()
	{
		return this.signature.qualifiedName();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		this.toSExpression(sb);
		return sb.toString();
	}
	
	/**
	 * (package-java qualified-name)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("java-package");
		
		aStringBuffer.append(" \"");
		aStringBuffer.append(this.signature.qualifiedName());
		aStringBuffer.append("\"");
	}
}
