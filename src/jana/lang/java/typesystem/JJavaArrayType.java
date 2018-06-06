package jana.lang.java.typesystem;

/*
 * The Java ArrayType carries besides name and signature also information about
 * the dimensions of the array. The dimension of Object[][], for example, is 2.
 */
public abstract class JJavaArrayType extends JJavaReferenceType
{
	protected JJavaType type;
	protected int dimensions; 
	
	/*
	 * hashCode() is implemented by the superclass
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) 
	{
		if(arg0 instanceof JJavaArrayType)
		{
			JJavaArrayType that = (JJavaArrayType) arg0;
			
			return this.name.equals(that.name) && (this.dimensions == that.dimensions);
		}
		
		return false;
	}
	
	public int hashCode() 
	{
		return super.hashCode() ^ this.dimensions;
	}
}
