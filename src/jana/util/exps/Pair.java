package jana.util.exps;

import jana.metamodel.SExpression;
import jana.util.exceptions.JParseException;

import java.io.IOException;

public class Pair<PT1,PT2> extends JSExpressionList
{
	private PT1 key;
	private PT2 value;
	
	Pair(PT1 aKey, PT2 aValue)
	{
		this.key = aKey;
		this.value = aValue;
	}
	
	public PT1 key()
	{
		return key;
	}
	
	public PT2 value()
	{
		return value;
	}
	
	@Override
	public SExpression nth(int index)
	{
		SExpression expression;
		
		expression = (SExpression) JSExpressionList.NIL;
		
		if(index == 0)
			if(key instanceof SExpression)
				expression = (SExpression) key;
			else
				expression = (SExpression) new JSExpressionAtom(key.toString());
		
		if(index == 1)
			if(value instanceof SExpression)
				expression = (SExpression) value;
			else
				expression = (SExpression) new JSExpressionAtom(value.toString());
		
		return expression;
	}
	
	@Override
	public int length()
	{
		return 2;
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("(");
		aStringBuffer.append("\"");
		aStringBuffer.append(key.toString());
		aStringBuffer.append("\"");
		aStringBuffer.append(" . ");
		aStringBuffer.append("\"");
		aStringBuffer.append(value.toString());
		aStringBuffer.append("\"");
		aStringBuffer.append(")");
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		this.toSExpression(sb);
		return sb.toString();
	}
	
	public static Pair<String,String> parsePair(String aString) throws IOException
	{	
		String tmp;
		String keyString, valueString;
		int first, last;
		
		first = aString.indexOf("(");
		last = aString.lastIndexOf(")");
		if(first < 0 || last < 0)
			throw new JParseException("Error while parsing the Pair " + aString);
		tmp = aString.substring(first + 1, last);
		
		first = tmp.indexOf("\"");
		if(first < 0 )
			throw new JParseException("Error while parsing the Pair " + aString);
		tmp = tmp.substring( first + 1);
		
		first = tmp.indexOf("\"");
		if(first < 0 )
			throw new JParseException("Error while parsing the Pair " + aString);
		keyString = new String(tmp.substring(0, first).getBytes());
		tmp = tmp.substring(first + 1);
		
		first = tmp.indexOf("\"");
		if(first < 0 )
			throw new JParseException("Error while parsing the Pair " + aString);
		tmp = tmp.substring( first + 1);
		
		first = tmp.indexOf("\"");
		if(first < 0 )
			throw new JParseException("Error while parsing the Pair " + aString);
		valueString = tmp.substring(0, first);
		tmp = tmp.substring(first + 1);
		
		if(tmp.length() > 0)
			throw new JParseException("Error while parsing the Pair " + aString);
		
		return new Pair<String,String>(new String(keyString.getBytes()),new String(valueString.getBytes()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Pair)
		{
			Pair otherPair = (Pair) o;
			
			return otherPair.key.equals(this.key) && otherPair.value.equals(this.value);
		}
			
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return (key.hashCode() + value.hashCode()) % Integer.MAX_VALUE;
	}
}
