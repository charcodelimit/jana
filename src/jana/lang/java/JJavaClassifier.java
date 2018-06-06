package jana.lang.java;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jana.java.JJavaRepository;
import jana.metamodel.JNamedElement;
import jana.metamodel.JReferenceTypeDefinition;

/*
 * A Class or an Interface
 */
public abstract class JJavaClassifier extends JReferenceTypeDefinition implements JNamedJavaElement
{	
	protected String classifierType;
	
	protected JJavaSignature signature;
	
	protected JJavaRepository repository;
	private JJavaPackage parentPackage; // use getters and setters!
	
	protected List<JJavaAnnotation> annotations;
	
	protected JJavaClassModifiers modifiers; 

	protected JJavaImplements implementsRelation;
	
	protected String sourceFile;
	
	private JJavaClassifier parentClassifier;  // use getters and setters!
	protected SortedSet<JJavaClassifier> nestedClassifiers;
	
	protected transient JJavaClosureFactory closureFactory; // only used during object initialization
	
	protected JJavaClassifier(JJavaSignature theSignature)
	{		
		this.nestedClassifiers = new TreeSet<JJavaClassifier>();
		this.signature = theSignature;
	}
	
	public JJavaSignature getSignature()
	{
		return signature;
	}
	
	public String qualifiedName()
	{
		return this.signature.qualifiedName();
	}
	
	public String getName()
	{
		if(name == null)
			name = this.signature.unqualifiedName();
		
		return name;
	}
	
	protected String packageName()
	{
		return this.signature.previousSignatureElement();
	}
	
	public JJavaPackage parentPackage() throws Exception
	{	
		return parentPackage;
	}
	
	/*
	 * Can be set only once, because there is only one parent for an element in the Java language!
	 */
	protected void initParentPackage() throws Exception
	{
		if(repository.getPackage(packageName()) == null)
		{
			JJavaSignature signature;
			
			signature =  JJavaSignature.signatureFor(packageName());
			this.parentPackage = new JJavaPackage(signature, this, repository);
		}
	}
	
	/*
	 * Can be set only once, because there is only one parent class for per nested class in the Java language!
	 */
	public void setParentClassifier(JJavaClassifier theParentClassifier)
	{
		if(this.parentClassifier == null)
			this.parentClassifier = theParentClassifier;
	}
	
	public JJavaClosure initClosureFor(JJavaMethodImplementation aMethodImplementation, int methodID) throws Exception
	{
		if( closureFactory == null)
		{
			closureFactory = repository.getClosureFactory().newInstance(this.signature.qualifiedName(), repository.getClasspath());
		}
			
		return closureFactory.produce(aMethodImplementation, methodID);
	}
	
	protected void discardClosureFactory()
	{
		if(closureFactory != null)
		{
			closureFactory = null;
		}
	}
		
	/*
	 * true if signatures are equal
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object anObject) 
	{
		assert( this.signature != null ); // PRE
		
		if(anObject instanceof JJavaClassifier)
		{
			return ((JJavaClassifier) anObject).signature.equals(this.signature);
		}
	
		return false;
	}
	
	@Override
	public int hashCode() 
	{
		assert( this.signature != null ); // PRE
		
		return this.signature.hashCode();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (classifier-type parent-package modifiers signature generalization nested-classifiers attributes routines)
	 * @return
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append(this.classifierType);
		
		aStringBuffer.append('\n');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		try
		{
			parentPackage().toSExpression(aStringBuffer);
		}
		catch(Exception e)
		{
			System.out.println("Warning! Error while converting parent package to SExpression " + e.toString());
			e.printStackTrace();
		}
		aStringBuffer.append(')');
		
		aStringBuffer.append('\n');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.sourceFile);
		aStringBuffer.append('\"');
		
		aStringBuffer.append('\n');
		
		JNamedElement.elementListToSExpression(this.annotations, aStringBuffer);
		
		aStringBuffer.append('\n');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.modifiers.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append('\n');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.signature.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append('\n');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.generalizationRelation.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
		
		aStringBuffer.append('\n');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.implementsRelation.toSExpression(aStringBuffer);
		aStringBuffer.append(')');

		aStringBuffer.append('\n');
		
		JNamedElement.elementSetToSExpression(this.nestedClassifiers, aStringBuffer);
		
		aStringBuffer.append('\n');
		
		JNamedElement.elementListToSExpression(this.attributes, aStringBuffer);
		
		aStringBuffer.append('\n');
		
		aStringBuffer.append(";; -------- Methods -------- \n");
		
		JNamedElement.shortElementListToSExpression(this.routines, aStringBuffer);
	}
}
