package jana.lang.java;

/*
 * native, abstract, strictfp, synchronized, synthetic, bridge, var-args
 */
public abstract class JJavaMethodModifiers extends JJavaModifiers 
{
	// shared with class
	protected boolean isAbstract;
	protected boolean isStrict;
	
	protected boolean isSynchronized;
	protected boolean isNative;
	
	protected boolean isSynthetic;
	protected boolean isBridge;
	protected boolean isVarArgs;
	
	public String toString()
	{
		 StringBuffer buffer = new StringBuffer();
	        
		 buffer.append(super.toString());
	        
		 buffer.append(" ");
	        
		 if(this.isAbstract)
			 buffer.append("abstract ");
		 if(this.isStrict)
			 buffer.append("strictfp ");
		 if(this.isSynchronized)
			 buffer.append("synchronized ");
		 if(this.isNative)
			 buffer.append("native ");
		 if(this.isSynthetic)
			 buffer.append("synthetic ");
		 if(this.isBridge)
			 buffer.append("bridge ");
		 if(this.isVarArgs)
			 buffer.append("var-args ");
		 
		 return buffer.toString().trim();
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * returns a list of symbols that denote Java modifiers
	 * 
	 * (modifier-method method-modifiers)
	 * 
	 * 
	 * @see jana.lang.java.JJavaMethodModifiers#toSExpression()
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("java-method-modifiers");
		
		aStringBuffer.append(" '(");
		aStringBuffer.append(this.toString());
		aStringBuffer.append(")");
	}
}
