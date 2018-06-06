package jana.lang.java;

import jana.metamodel.JGeneralization;
import jana.metamodel.JNamedElement;
import jana.metamodel.typesystem.JReferenceType;

public class JJavaInherits extends JGeneralization 
{	
	
	public JJavaInherits()
	{
		super();
	}
	
	/**
	 * The standard case in Java is single inheritance.
	 * 
	 * @param aReferenceType
	 */
	public JJavaInherits(JReferenceType aReferenceType)
	{
		this();
		this.addSuperType(aReferenceType);
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("java-extends");
		
		if(this.superTypes.size() > 0)
		{
			aStringBuffer.append(" (list");
			JNamedElement.elementListToSExpression(this.superTypes, aStringBuffer);
			aStringBuffer.append(")");
		}
		else
		{
			aStringBuffer.append(" NIL");
		}
		
		JNamedElement.elementListToSExpression(this.subTypes, aStringBuffer);
	}
}
