package jana.metamodel.values;

import jana.metamodel.SExpression;
import jana.metamodel.typesystem.JType;

public abstract class JValue implements SExpression
{
	protected JType type;
	
	public boolean equals(Object anObject)
	{
		if(anObject instanceof JValue)
		{
			JValue that;
			
			that = (JValue) anObject;
			
			return this.compareValues( that );
		}
		
		return false;
	}
	
	public abstract int hashCode();
	
	/*
	 * subclass-responsibility
	 */
	protected abstract boolean compareValues(JValue aValue);
}
