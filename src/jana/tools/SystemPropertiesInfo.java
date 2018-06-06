package jana.tools;

import java.util.Properties;
import java.util.TreeSet;

public class SystemPropertiesInfo
{
	public String systemPropertiesToString()
	{
		Properties properties;
		StringBuffer sb;
		
		sb = new StringBuffer();
		properties = System.getProperties();
		
		// sort keys alphabetically
		TreeSet<Object> sortedKeys = new TreeSet<Object>(properties.keySet());
		
		for(Object key : sortedKeys)
		{
			sb.append((String) key);
			sb.append('=');
			sb.append((String) properties.get(key));
			sb.append('\n');
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args)
	{
		SystemPropertiesInfo spi = new SystemPropertiesInfo();
		System.out.println(spi.systemPropertiesToString());
	}
}
