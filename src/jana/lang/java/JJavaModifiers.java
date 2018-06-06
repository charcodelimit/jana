package jana.lang.java;

/*
 * the visibility modifiers are applicable to classes, methods and fields
 * Similarly static and final is applicable to each of them.
 * They are therefore part of this abstract superclass.
 * 
 * Furthermore, static and final apply to classes, methods or fields and are therefore 
 * part of this superclass as well.
 */
public abstract class JJavaModifiers 
{
	protected boolean isPublic;
	protected boolean isProtected;
	protected boolean isPrivate;
	
	protected boolean isStatic;
	protected boolean isFinal;
	
	public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        if(this.isPublic)
        	buffer.append("public ");
        else if(this.isProtected)
        	buffer.append("protected ");
        else if(this.isPrivate)
        	buffer.append("private ");
        
        if(this.isStatic)
        	buffer.append("static ");
        if(this.isFinal)
        	buffer.append("final ");
        
        return buffer.toString().trim();
    }
	
	public String toSExpression()
	{
		return this.toString();
	}
}
