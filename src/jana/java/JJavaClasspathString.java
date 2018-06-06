package jana.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

import jana.util.JString;

@RunWith(Enclosed.class)
public class JJavaClasspathString extends JString
{

	public JJavaClasspathString(String stringValue)
	{
		super(stringValue);
	}
	
	/**
	 * Extracts a list of elements from a String, where elements are separated by a colon
	 * 
	 * @param classpathString - a ':' separated list of Strings
	 * @return - the Strings found between the colons
	 */
	public List<String> asFilenameStringList()
	{
		List<String> filenameList = new ArrayList<String>();
		
		String rest;
		String fileName;
		int index;
		
		fileName = "";
		rest = this.stringValue;
		
		while(rest.length() > 0)
		{	
			// chr: is pathSeparatorChar really O.K.? What happens if one uses ":" on a system where ";" is the default separator?
			index = rest.indexOf(File.pathSeparatorChar);
			if(index > 0)
			{
				fileName = rest.substring(0,index);
				rest = rest.substring(index + 1);
			}
			else 
			{	
				if(!(rest.charAt(0)==File.pathSeparatorChar))
					fileName = rest;
				else
					fileName = "";
				if(index<0)
					rest = "";
				else
					rest = rest.substring(index + 1);
			}
			
			if(fileName.length()!=0)
				filenameList.add(fileName);
		}
		
		return filenameList;
	}
	
	public static class ClasspathStringTest
	{	
		@Test 
		public void classpathElementsTest() throws Exception
		{
			JJavaClasspathString cps;
			List<String> result;
			
			// well formed
			cps = new JJavaClasspathString("test:test/lib:test.jar");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 3);
			Assert.assertEquals("test",result.get(0));
			Assert.assertEquals("test/lib",result.get(1));
			Assert.assertEquals("test.jar",result.get(2));
			// no separators
			cps = new JJavaClasspathString("test.jar");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 1);
			Assert.assertEquals("test.jar",result.get(0));
			// last element empty
			cps = new JJavaClasspathString("test.jar:");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 1);
			Assert.assertEquals("test.jar",result.get(0));
			// several empty elements
			cps = new JJavaClasspathString("test.jar::");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 1);
			Assert.assertEquals("test.jar",result.get(0));
			// first element empty
			cps = new JJavaClasspathString(":test.jar");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 1);
			Assert.assertEquals("test.jar",result.get(0));
			// several empty elements
			cps = new JJavaClasspathString("::test.jar");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 1);
			Assert.assertEquals("test.jar",result.get(0));
			//empty classpath
			cps = new JJavaClasspathString("");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 0);
			//empty classpath, single separator
			cps = new JJavaClasspathString(":");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 0);
			//empty classpath, several separators
			cps = new JJavaClasspathString("::");
			result = cps.asFilenameStringList();
			Assert.assertTrue(result.size() == 0);
		}
	}
}
