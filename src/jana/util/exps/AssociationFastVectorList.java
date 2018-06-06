package jana.util.exps;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import jana.metamodel.SExpression;

/**
 * O(1)
 * remove if hit - O(n)
 * remove, no hit - O(1)
 * 
 * @author chr
 *
 */
public class AssociationFastVectorList<T1,T2> extends AssociationList<T1,T2>
{
	protected Map<T1,T2> valueMap;
	protected ArrayList<T1> keyList;
	
	AssociationFastVectorList()
	{
		valueMap = new HashMap<T1,T2>();
		keyList = new ArrayList<T1>();
	}

	@Override
	public void addPair(T1 aKey, T2 aValue)
	{	
		if(!valueMap.containsKey(aKey))
			keyList.add(aKey);
		
		valueMap.put(aKey, aValue);
	}

	@Override
	public boolean containsKey(T1 aKey)
	{
		return valueMap.containsKey(aKey);
	}

	@Override
	public T2 getValue(T1 aKey)
	{
		return valueMap.get(aKey);
	}

	@Override
	public boolean remove(T1 aKey)
	{		
		T2 value;
		
		value = valueMap.remove(aKey);
		
		if(value == null)
			return false;
		
		return keyList.remove(aKey);
	}

	@Override
	public int length()
	{
		return  keyList.size();
	}

	@Override
	public SExpression nth(int index)
	{
		T1 key; 
		T2 value; 
		
		key = keyList.get(index);
		
		if(key == null)
			return null;
		
		value = valueMap.get(key);
		return new Pair<T1,T2>(key,value);
	}

	public String toSExpression()
	{
            StringBuffer sb = new StringBuffer(keyList.size());
		
            this.toSExpression(sb);
                
            return sb.toString();
	}

	public void toSExpression(StringBuffer aStringBuffer)
	{
		T1 key;
		
		aStringBuffer.append('(');

		for( int index = 0; index < this.keyList.size(); index++ )
		{
			key = this.keyList.get(index);
			
			if(index > 0)
				aStringBuffer.append('\n');
			
			aStringBuffer.append('(');
			aStringBuffer.append('\"');
			aStringBuffer.append(key.toString());
			aStringBuffer.append('\"');
			aStringBuffer.append(" . ");
			aStringBuffer.append("\"");
			aStringBuffer.append(this.valueMap.get(key).toString());
			aStringBuffer.append('\"');
			aStringBuffer.append(')');
		}

		aStringBuffer.append(')');
	}

	public static AssociationList<String,String> fromString(String aString) throws IOException
	{

		AssociationList<String,String> instance;
		
		instance = new AssociationFastVectorList<String, String>();
		instance.initialize(aString);
		
		return instance;	
	}

	public static AssociationList<String,String> fromFile(File aFile) throws IOException
	{
		FileInputStream fis = new FileInputStream(aFile);
		BufferedInputStream bis = new BufferedInputStream(fis,READ_BUFFER);
		
		try
		{
			return read(bis);
		}
		finally
		{
			bis.close();
			fis.close();
		}
	}
	
	/**
	 * This method may not be safe for concurrent file access.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static AssociationList<String,String> read(InputStream in) throws IOException
	{
		AssociationList<String,String> instance;
		
		instance = new AssociationFastVectorList<String, String>();
		instance.initializeFromStream(in);
		
		return instance;
	}
	
	public static class AssociationFastVectorListTest
	{
		@Test
		public void testGenerateAndParse() throws Exception
		{
			AssociationFastVectorList<String,String> testMap;
			AssociationList<String, String> map2;
			
			testMap = new AssociationFastVectorList<String, String>();
			Assert.assertEquals("()", testMap.toSExpression());
			
			map2 = AssociationFastVectorList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(), map2.toSExpression());
			
			testMap = new AssociationFastVectorList<String, String>();
			testMap.addPair("a","b");
			Assert.assertEquals("((\"a\" . \"b\"))",testMap.toSExpression());
			
			map2 = AssociationFastVectorList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(),map2.toSExpression());
			
			testMap = new AssociationFastVectorList<String, String>();
			testMap.addPair("a","b");
			testMap.addPair("c","d");
			testMap.addPair("e","f");
			Assert.assertEquals("((\"a\" . \"b\")\n(\"c\" . \"d\")\n(\"e\" . \"f\"))",testMap.toSExpression());
			
			map2 = AssociationFastVectorList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(),map2.toSExpression());
		}
	}

}
