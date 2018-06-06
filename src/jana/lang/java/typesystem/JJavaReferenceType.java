package jana.lang.java.typesystem;

/*
 * Reference types have also a name. 
 * It can be used to refer to the type in the lexical scope 
 * defined by a package <b>without</b> referring to the package.
 * 
 * All reference types are instantiated in Jana with 
 * their full signature.
 */
public abstract class JJavaReferenceType extends JJavaType 
{
	public String getName()
	{
		if(name == null)
			name = signature.unqualifiedName();
		
		return name;
	}
	
	protected String packageName()
	{
		return this.signature.previousSignatureElement();
	}
}
