package jana.util.exps;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import jana.metamodel.SExpression;

/**
 * O(n)
 * 
 * @author chr
 *
 * @param <T1>
 * @param <T2>
 */
public class AssociationVectorList<T1,T2> extends AssociationList<T1,T2>
{
	protected List<T1> keyList;
	protected List<T2> valueList;
	
	AssociationVectorList()
	{
		keyList = new ArrayList<T1>();
		valueList = new ArrayList<T2>();
	}

	@Override
	public void addPair(T1 key, T2 value)
	{	
		remove(key);
		
		keyList.add(key);
		valueList.add(value);

	}

	@Override
	public boolean containsKey(T1 key)
	{
		return keyList.contains(key);
	}

	@Override
	public T2 getValue(T1 key)
	{
		int index;
		
		index = keyList.indexOf(key);
		
		if(index < 0)
			return null;
		else
			return valueList.get(index);
	}

	@Override
	public boolean remove(T1 key)
	{
		int index;
		index = keyList.indexOf(key);
		
		if(index < 0)
			return false;
		
		keyList.remove(index);
		valueList.remove(index);
			
		return true;
	}

	@Override
	public int length()
	{
		return keyList.size();
	}

	@Override
	public SExpression nth(int index)
	{
		T1 key; 
		T2 value; 
		
		key = keyList.get(index);
		
		if(key == null)
			return null;
		
		value = valueList.get(index);
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
		aStringBuffer.append("(");

		for( int index = 0; index < this.keyList.size(); index++ )
		{
			aStringBuffer.append("(");
			aStringBuffer.append("\"");
			aStringBuffer.append(this.keyList.get(index).toString());
			aStringBuffer.append("\"");
			aStringBuffer.append(" . ");
			aStringBuffer.append("\"");
			aStringBuffer.append(this.valueList.get(index).toString());
			aStringBuffer.append("\"");
			aStringBuffer.append(")");
		}

		aStringBuffer.append(")");
	}

	public static AssociationList<String,String> fromString(String aString) throws IOException
	{

		AssociationList<String,String> instance;
		
		instance = new AssociationVectorList<String, String>();
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
		
		instance = new AssociationVectorList<String, String>();
		instance.initializeFromStream(in);
		
		return instance;
	}
	
	public static class AssociationVectorListTest
	{
		@Test
		public void testGenerateAndParse() throws Exception
		{
			AssociationVectorList<String,String> testMap;
			AssociationList<String, String> map2;
			
			testMap = new AssociationVectorList<String, String>();
			Assert.assertEquals("()", testMap.toSExpression());
			
			map2 = AssociationVectorList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(), map2.toSExpression());
			
			testMap = new AssociationVectorList<String, String>();
			testMap.addPair("a","b");
			Assert.assertEquals("((\"a\" . \"b\"))",testMap.toSExpression());
			
			map2 = AssociationVectorList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(),map2.toSExpression());
			
			testMap = new AssociationVectorList<String, String>();
			testMap.addPair("a","b");
			testMap.addPair("c","d");
			testMap.addPair("e","f");
			Assert.assertEquals("((\"a\" . \"b\")(\"c\" . \"d\")(\"e\" . \"f\"))",testMap.toSExpression());
			
			map2 = AssociationVectorList.fromString(testMap.toSExpression());
			Assert.assertEquals(testMap.toSExpression(),map2.toSExpression());
		}
	}
}
