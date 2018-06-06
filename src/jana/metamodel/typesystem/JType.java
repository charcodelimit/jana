package jana.metamodel.typesystem;

import jana.metamodel.JNamedElement;

public abstract class JType extends JNamedElement 
{
	public abstract String toSExpression();
	public abstract void toSExpression(StringBuffer aStringBuffer);
	
	public abstract boolean equals(Object anObject);
	public abstract int hashCode();
}
