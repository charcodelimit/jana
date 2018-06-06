package jana.lang.java;

/*
 * 
 * volatile, transient
 */
public abstract class JJavaFieldModifiers extends JJavaModifiers 
{
	protected boolean isVolatile;
	protected boolean isTransient;
	protected boolean isEnum;
	protected boolean isSynthetic;
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
	        
		buffer.append(super.toString());
	    
		buffer.append(" ");
		
		if(this.isVolatile)
			buffer.append("volatile ");
		if(this.isTransient)
			buffer.append("transient ");
		if(this.isEnum)
			buffer.append("enum ");
		if(this.isSynthetic)
			buffer.append("synthetic ");
		
		return buffer.toString().trim();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("java-field-modifiers");
		
		sb.append(" '(");
		sb.append(this.toString());
		sb.append(")");
		
		return sb.toString();
	}
}
