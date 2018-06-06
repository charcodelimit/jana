/*
 * SymbolicExpressionLexer.java
 *
 * Created on 9. Mai 2009, 14:27
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
public class SymbolicExpressionString 
{
    String string;
            
    /** Creates a new instance of SymbolicExpressionLexer */
    public SymbolicExpressionString(String aString) 
    {
        string = aString;
    }
    
    public boolean charIsNumber(int pos)
    {
    	if(this.string.length() < pos)
    		return false;
    	
        char ch = this.string.charAt(pos);
        return ch >= '0' && ch <= '9';
    }
    
    public boolean charIsWhitespace(int pos)
    {
    	if(this.string.length() < pos)
    		return false;
    	
        char ch = this.string.charAt(pos);
        
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r';
    }
    
    public boolean charIsDelimiter(int pos)
    {
    	if(this.string.length() < pos)
    		return false;
    	
        char ch = this.string.charAt(pos);
        
        return ch == '(' || ch == ')' || charIsWhitespace(pos);
    }
    
    public boolean charIsQuote(int pos)
    {
    	if(this.string.length() < pos)
    		return false;
    	
        return this.string.charAt(pos) == '"';
    }
    
    public boolean firstCharIsQuote()
    {
        return this.charIsQuote(0);
    }
    
    public boolean lastCharIsQuote()
    {
        return this.charIsQuote(this.string.length() - 1);
    }
    
    public int firstDelimiterPosition()
    {
        for( int i = 0; i < this.string.length(); i++ )
        {
            if(charIsDelimiter(i))
             return i;
        }    
        
        return -1;
    }

	public String substring(int start, int end)
	{
		return this.string.substring(start,end);
	}
}
