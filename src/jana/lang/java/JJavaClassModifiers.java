package jana.lang.java;

/*
 * private, public, protected, final, static, 
 * abstract, strictfp 
 * a special role takes the modifier enum, which is used to denote an Enum type
 * and the modifier annotation, that denotes a Java 1.5 Annotation interface
 */
public abstract class JJavaClassModifiers extends JJavaModifiers 
{
	protected boolean isAnnotation;
	
	protected boolean isEnum;
	
	protected boolean isAbstract;
	protected boolean isStrict;
	
	public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(super.toString());
        
        buffer.append(" ");
     
        if(this.isAbstract)
        	buffer.append("abstract ");
        if(this.isStrict)
        	buffer.append("strictfp ");
        if(this.isEnum)
        	buffer.append("enum ");
        if(this.isAnnotation)
        	buffer.append("annotation ");
        
        return buffer.toString().trim();
    }

	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		this.toSExpression(sb);
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("java-class-modifiers");
		
		aStringBuffer.append(" '(");
		aStringBuffer.append(this.toString());
		aStringBuffer.append(")");
	}
}
