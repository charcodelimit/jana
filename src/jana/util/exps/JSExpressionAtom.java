/*
 * Atom.java
 *
 * Created on 9. Mai 2009, 14:18
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package jana.util.exps;

import jana.metamodel.SExpression;


/**
 *
 * @author chr
 */
public class JSExpressionAtom implements SExpression
{
    protected String symbolName;
    
    public JSExpressionAtom(String aName) 
    {
        this.symbolName = aName;
    }
    
    public String toSExpression()
    {
    	StringBuffer sb = new StringBuffer();
    	this.toSExpression(sb);
    	return sb.toString();
    }
    
    public void toSExpression(StringBuffer aStringBuffer)
    {
    	aStringBuffer.append(this.symbolName);
   }
    
   public String toString()
   {
	   return symbolName;
   }
   
   public static JSExpressionAtom parse(SymbolicExpressionString aSymbolicExpressionString)
   {  
      int pos = aSymbolicExpressionString.firstDelimiterPosition();
      
      if(pos < 0)
          return null;
     
      String atomString = aSymbolicExpressionString.substring(0,pos);
      if(JSExpressionStringAtom.isStringAtom(atomString))
        return new JSExpressionStringAtom(atomString.substring(1,atomString.length() - 1));
      
      return new JSExpressionAtom(atomString);
   }
   
   public static JSExpressionAtom parse(String aString)
   {
     SymbolicExpressionString estring = new SymbolicExpressionString(aString);
      
     return parse(estring);
   }
}
