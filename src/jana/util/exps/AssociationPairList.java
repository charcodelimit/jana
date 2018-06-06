package jana.util.exps;

import jana.metamodel.SExpression;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

/**
 * Association Lists are used for a rather small number of pairs, 
 * therefore they trade-off speed [O(1) for all operations] 
 * for storage (1 Hashmap and 1 ArrayList)
 * 
 * @author chr
 *
 * @param <T1>
 * @param <T2>
 */

@RunWith(Enclosed.class)
public class AssociationPairList<T1,T2> extends AssociationList<T1,T2>
{
	protected Map<T1,Integer> indices;
	protected ArrayList<Pair<T1,T2>> list;
	
	public AssociationPairList()
	{
		//map = new HashMap<T1,T2>();
		list = new ArrayList<Pair<T1,T2>>();
		indices = new HashMap<T1,Integer>();
	}
	
	public AssociationPairList(HashMap<T1,T2> map)
	{
		this();
		
		for( T1 key : map.keySet() )
		{
			this.addPair(key, map.get(key));
		}
	}
	
	public boolean containsKey(T1 aKey)
	{	
		return indices.containsKey(aKey);
	}
	
	public void addPair(Pair<T1,T2> aPair)
	{	
		remove(aPair.key());
		
		list.add(aPair);
		indices.put(aPair.key(), new Integer(list.size() - 1));
	}
	
	public void addPair(T1 aKey, T2 aValue)
	{
		remove(aKey);
		
		list.add(new Pair<T1,T2>(aKey, aValue));
		indices.put(aKey, new Integer(list.size() - 1));
	}
	
	/**
	 * Remove the Pair with aKey from the Association List
	 * 
	 * @param aKey
	 * @return true if the pair has been successfully removed
	 */
	public boolean remove(T1 aKey)
	{
		Integer index;
		
		index = indices.get(aKey);
		
		if( index != null )
		{
			list.remove(index.intValue());
			index = null;
			indices.remove(aKey);
			
			return true;
		}
		else
			return false;
	}
	
	public T2 getValue(T1 aKey)
	{
		Integer index;

		index = indices.get(aKey);

		if(index != null)
			return list.get(index.intValue()).value();
		else
			return null;
	}
		
	public SExpression nth(int index)
	{
		return (SExpression) list.get(index);
	}

	@Override
	public int length()
	{
		return list.size();
	}
	
	public String toSExpression()
	{
            StringBuffer sb = new StringBuffer(list.size() << 4);
		
            this.toSExpression(sb);
                
            return sb.toString();
	}
        
	public void toSExpression(StringBuffer aStringBuffer)
	{
		int pairs;
		
		pairs = 0;
			
		aStringBuffer.append('(');

		for( Pair<T1,T2> pair : list )
		{
			if(pairs > 0)
				aStringBuffer.append('\n');
			
			aStringBuffer.append(pair.toSExpression());
			
			pairs++;
		}

		aStringBuffer.append(')');
	}
	
	public static AssociationList<String,String> fromString(String aString) throws IOException
	{

		AssociationList<String,String> instance;
		
		instance = new AssociationPairList<String, String>();
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
		
		instance = new AssociationPairList<String, String>();
		instance.initializeFromStream(in);
		
		return instance;
	}
	
	public static class AssociationPairListTest
	{
		@Test
		public void testGenerateAndParse() throws Exception
		{
			AssociationPairList<String,String> testMap;
			AssociationList<String, String> map2;
			
			testMap = new AssociationPairList<String, String>();
			Assert.assertEquals("()", testMap.toSExpression());
			
			map2 = AssociationPairList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(), map2.toSExpression());
			
			testMap = new AssociationPairList<String, String>();
			testMap.addPair("a","b");
			Assert.assertEquals("((\"a\" . \"b\"))",testMap.toSExpression());
			
			map2 = AssociationPairList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(),map2.toSExpression());
			
			testMap = new AssociationPairList<String, String>();
			testMap.addPair("a","b");
			testMap.addPair("c","d");
			testMap.addPair("e","f");
			Assert.assertEquals("((\"a\" . \"b\")\n(\"c\" . \"d\")\n(\"e\" . \"f\"))",testMap.toSExpression());
			
			map2 = AssociationPairList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(),map2.toSExpression());
		}
	}
}
