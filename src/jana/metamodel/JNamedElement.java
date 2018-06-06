package jana.metamodel;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * "Language elements that can be explicitly referred to by that name."
 */
public abstract class JNamedElement implements SExpression
{
	protected String name;
	protected final static int LINEBREAK_LIST_SIZE_LIMIT = 25;
	// The call-arguments-limit is in almost all modern common-lisp implementations 4096.
	// LispWorks: 2047, CLISP 2.48: 4096, Clozure CL Version 1.4: 4096, Allegro CL 8.1: 16384, SBCL 1.0.32: 1152921504606846975
	// A CLTL2 compatible implementation must guarantee a minimum size of 50.
	protected final static int CL_CALL_ARGUMENTS_LIMIT = 1768; 
	
	public String getName()
	{
		return name;
	}
	
	public abstract String toSExpression();
	

	@SuppressWarnings("unchecked")
	public static String elementSetToSExpression(Set aSetOfElements)
	{
		StringBuffer sb = new StringBuffer();
		elementSetToSExpression(aSetOfElements,sb);
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static void elementSetToSExpression(Set aSetOfElements, StringBuffer aStringBuffer)
	{
		int setLength = aSetOfElements.size();
		
		aStringBuffer.append(' ');
		
		if(setLength > 0)
		{	
			elementsToSExpression(aSetOfElements.iterator(), aStringBuffer, setLength);
		}
		else
		{
			aStringBuffer.append("()");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void elementListToSExpression(List aListOfElements, StringBuffer aStringBuffer)
	{
		int listLength = aListOfElements.size();
		
		aStringBuffer.append(' ');
		
		if(listLength > 0)
		{	
			elementsToSExpression(aListOfElements.iterator(), aStringBuffer, listLength);
		}
		else
		{
			aStringBuffer.append("()");
		}
	}

	@SuppressWarnings("unchecked")
	private static void elementsToSExpression(Iterator anIterator, StringBuffer aStringBuffer, int elements)
	{	
		StringBuffer sb;
		StringBuffer head;
		int elementsLeft = elements;
		
		List<StringBuffer> stack1 = new ArrayList<StringBuffer>();
		List<StringBuffer> stack2 = new ArrayList<StringBuffer>();
		
		while(elementsLeft > 0)
		{
			sb = new StringBuffer();
		
			sb.append("(list ");
			elementsLeft -= elementsToSExpression(anIterator, sb);
			sb.append(')');
			
			stack1.add(sb);
		}
		
		while(stack1.size() > 1)
		{
			while(stack1.size() > 0)
			{
				sb = new StringBuffer();
		
				sb.append("(nconc");
				
				for( int i = 0; i < CL_CALL_ARGUMENTS_LIMIT && stack1.size() > 0; i++)
				{
					head = (StringBuffer) stack1.remove(0);
					sb.append("\n  ");
					sb.append( head );
				}
			
				sb.append(')');
			
				stack2.add(sb);
			}

			stack1 = stack2;
			stack2 = new ArrayList<StringBuffer>();
		}

		if(stack1.size() == 1)
		{
			head = (StringBuffer) stack1.remove(0);
			aStringBuffer.append(head);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private static int elementsToSExpression(Iterator anIterator, StringBuffer aStringBuffer)
	{
		SExpression currentSExpressionElement;
		
		int count;
		
		count = 0;
		
		while( anIterator.hasNext() && count < CL_CALL_ARGUMENTS_LIMIT)
		{
			currentSExpressionElement = (SExpression) anIterator.next();
			count++;
			
			if( count == 1)
				aStringBuffer.append('(');
			else
			{
				aStringBuffer.append(' ');
				aStringBuffer.append('(');
			}
			
			aStringBuffer.append(currentSExpressionElement.toSExpression());
			aStringBuffer.append(')');
			
			if(count % LINEBREAK_LIST_SIZE_LIMIT == 0)
				aStringBuffer.append('\n');
		}
		
		return count;
	}
		
	@SuppressWarnings("unchecked")
	public static void shortElementListToSExpression(List aListOfElements,StringBuffer aStringBuffer)
	{
		if(aListOfElements.size() > 0)
		{	
			aStringBuffer.append("(list");
			shortElementListToSExpression(aListOfElements.iterator(),aStringBuffer);
			aStringBuffer.append(")");
		}
		else
		{
			aStringBuffer.append("()");
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void shortElementListToSExpression(Iterator anIterator, StringBuffer aStringBuffer)
	{
		SExpression currentSExpressionElement;
		
		while( anIterator.hasNext() )
		{
			currentSExpressionElement = (SExpression) anIterator.next();
			
			aStringBuffer.append("\n;;----\n");
			
			aStringBuffer.append('(');
			aStringBuffer.append(currentSExpressionElement.toSExpression());
			aStringBuffer.append(')');
		}
	}
}
