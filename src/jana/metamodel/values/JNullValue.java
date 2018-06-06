package jana.metamodel.values;

public abstract class JNullValue extends JConstantValue 
{
	protected JNullValue()
	{	
	}
	
	/**
	 * Should implement the Singleton pattern!
	 * @return JNullValue
	 */
	public static JNullValue getInstance()
	{
		throw new RuntimeException("Subclass Responsibility! Can't create an instance of an abstract class.");
	}
		
	public Object value()
	{
		return null;
	}

	/*
	 * there is only one instance of type null value namely NULL (or NIL)
	 * @see jana.metamodel.values.JValue#compareValues(jana.metamodel.values.JValue)
	 */
	protected boolean compareValues(JValue aValue) 
	{
		if(aValue instanceof JNullValue)
			return true;
		else
			return false;
	}

	/**
	 * the hash code has to be the same for all instances of the same subtype of JNullValue,
	 * but distinct from other objects
	 * implement for example using instance.hashCode(); where instance is the singleton instance
	 */ 
	public abstract int hashCode();
}
