/*
 * StringAtom.java
 *
 * Created on 9. Mai 2009, 14:45
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package jana.util.exps;

/**
 *
 * @author chr
 */
public class JSExpressionStringAtom extends JSExpressionAtom {
    
    public JSExpressionStringAtom(String aString) 
    {
        super(aString);
    }
    
	public static boolean isStringAtom(String aString)
	{	
		return aString.charAt(0) == '"' && aString.charAt(aString.length() - 1) == '"';
	}
    
    public String toString()
    {
    	return this.symbolName;
    }
    
    public void toSExpression(StringBuffer aStringBuffer)
    {   
        aStringBuffer.append("\"");
        aStringBuffer.append(this.symbolName);
        aStringBuffer.append("\"");
    }
}
