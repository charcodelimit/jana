/*
 * List.java
 *
 * Created on 9. Mai 2009, 14:58
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package jana.util.exps;


import jana.metamodel.SExpression;
import jana.util.exceptions.JParseException;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

/**
 * This is actually a vector. 
 * We don't need the full generic CONS and LIST implementations here.
 *
 * @author chr
 */
@RunWith(Enclosed.class)
public class JSExpressionVectorList extends JSExpressionList implements SExpression
{
    protected ArrayList<SExpression> list;
    
    /** Creates a new instance of List */
    public JSExpressionVectorList() 
    {
        list = new ArrayList<SExpression>();
    }
    
    /**
     * Adjust Capacity to the elements contained.
     */
    public void trimToSize()
    {
    	this.list.trimToSize();
    }
    
    @Override
    public int length()
    {
    	return list.size();
    }
    
	@Override
	public SExpression nth(int index)
	{
		if(index < list.size())
			return list.get(index);
		else
			return JSExpressionList.NIL;
	}	
    
	public List<SExpression> getElements()
	{
		return this.list;
	}
	
    public void toSExpression(StringBuffer aStringBuffer)
    {
        int count;
    	
    	aStringBuffer.append("(");
        
    	count = 0;
        for(SExpression listElement : list)
        {
            listElement.toSExpression(aStringBuffer);
            if(count < list.size() - 1)
            	aStringBuffer.append(" ");
            count++;
        } 
        
        aStringBuffer.append(")");
    }
    
    public String toSExpression()
    {
        StringBuffer sb = new StringBuffer();
        
        this.toSExpression(sb);
        
        return sb.toString();
    }
    
    public String toString()
    {
    	return toSExpression();
    }
    
    public static JSExpressionVectorList parse(String aString) throws IOException
    {
    	return parseFast(aString);
    }
    
    
    /**
     * A fast but less modular implementation of the parser.
     * 
     * @param aString
     * @return
     * @throws IOException
     */
	public static JSExpressionVectorList parseFast(String aString) throws IOException
    {
		char c;
		int index,found,end, pos1, pos2;
		int lastStackElement;
        String currentString, atomString;
        List<JSExpressionVectorList> listStack;
        JSExpressionVectorList list,currentList;
        
        currentString = aString.trim();
        end = currentString.length();
        
        if(currentString.length() < 2)
        	throw new JParseException(aString + " is no valid List.");
        if(currentString.charAt(0) != '(')
        	throw new JParseException(aString + " is no valid List.");
        
        index = 1;
        currentList = new JSExpressionVectorList();
        listStack = new ArrayList<JSExpressionVectorList>();
        
        while(index < end)
        {
        	if( currentString.charAt(index) == ';' )
        	{
        		pos1 = currentString.indexOf('\n', index);
        		pos2 = currentString.indexOf('\r', index);
        		
        		if(pos1 > pos2 && pos2 > 0)
        			index = pos2;
        		
        		if(pos2 > pos1 && pos1 > 0)
        			index = pos1;
        		
        		if(pos1 < 0)
        			index = pos2;
        		
        		if(pos2 < 0)
        			index = pos1;
        		        		
        		if(index < 0)
        			break;
        	}
        	
        	while( index < end && Character.isWhitespace(currentString.charAt(index)))
        		index++;
        	
        	if( currentString.charAt(index) == ')' )
        	{
        		if(listStack.size() > 0)
        		{
        			list = currentList;
        			lastStackElement = listStack.size() - 1;
        			currentList = listStack.get(lastStackElement);
        			listStack.remove(lastStackElement);
        			list.trimToSize();
        			currentList.list.add(list);
        			list = null;
        		}
        		
        		index++;
        	}
        	else
        	{	
        		if( currentString.charAt(index) == '(' )
        		{
        	        listStack.add(currentList);
        	        currentList = new JSExpressionVectorList();
        	        
        	        index++;
        		}
        		else
        		{
        			found = index;
        			for(int i = index; i < end; i++)
        			{
        				c = currentString.charAt(i);
        				if(Character.isWhitespace(c) || c == '(' || c == ')' || c == '"' || c == ';')
        				{
        					found = i;
        					break;
        				}
        			}
        			
        			if(currentString.charAt(index) == '"')
        			{
        				// find next quote
        				found = currentString.indexOf('"', index+1);
        				
        				if(found < 0)
        					throw new JParseException("Found unbalanced quotes! " + currentString);
        					
            			atomString = new String(currentString.substring(index + 1, found).getBytes());
        				currentList.list.add((SExpression) new JSExpressionStringAtom(atomString));
        				
        				found++;
        			}
        			else if(currentString.charAt(index) != ';')
        			{
        				atomString = new String(currentString.substring(index,found).getBytes());
        				
        				if(!atomString.equals(".")) // dotted list
        				{
        					if(atomString.equals("NIL"))
        						currentList.list.add(JSExpressionList.NIL);
        					else
        						currentList.list.add(new JSExpressionAtom(atomString));
        				}
        			}
        			
        			index = found;
        		}
        	}
        }
        
        currentList.trimToSize();
        
        return currentList;
    }
    
    public static class JanaSExpressionVectorListTest
	{
    	@Test
    	public void testSimpleVectorList() throws IOException
    	{
    		String input;
    		JSExpressionVectorList vl;
    		
			input = "()";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "(test)";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "(test (test-1))";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "((test-1))";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "(test (test-1 \"test-string\"))";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "(test (test-1 \"test-string\" \"test-string\" \"test-string\"))";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "(test (test-1 \"test-string\" \"test-string\" \"test-string\"))";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "(test (test-1 \"test-string\") (\"test-string\" \"test-string\"))";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals(input, vl.toSExpression());
			
			input = "(test (test-1 \"test-string\") () (test-3 () ()) (test-4 \"t\" . \"d\")";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals("(test (test-1 \"test-string\") () (test-3 () ()) (test-4 \"t\" \"d\"))", vl.toSExpression());
			
			input = "(test (test-1 \"test-string\") () (test-3;test\n () ()) ;more-tests\n(test-4 \"t\";test-me-more\r . \"d\") ;final-comment";
			vl = JSExpressionVectorList.parse(input);
			Assert.assertEquals("(test (test-1 \"test-string\") () (test-3 () ()) (test-4 \"t\" \"d\"))", vl.toSExpression());
    	}
	}
}
