package jana.util.exps;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JClassnameMap extends AssociationFastVectorList<String,String>
{
	private File mapFile;
	
	public JClassnameMap(File aClassnameMapFile)
	{
		this.mapFile = aClassnameMapFile;
	}

	public static JClassnameMap fromFile(File aClassnameMapFile) throws IOException
	{
		JClassnameMap instance = new JClassnameMap(aClassnameMapFile);
		
		if(instance.mapFile.exists())
			instance.loadClassnameMap();	
		
		return instance;
	}
	
	public void merge(JClassnameMap classnameMapFromFile)
	{
		String key, value;
		
		for( int index = 0; index < classnameMapFromFile.length(); index++ )
		{
			key = classnameMapFromFile.keyList.get(index);
			value  = classnameMapFromFile.valueMap.get(key);
			this.addPair(key, value);
		}
	}
	
	private void loadClassnameMap() throws IOException
	{
		FileInputStream fis;
		
		if(!this.mapFile.canRead())
			throw new IOException("Can't read classname map file " + mapFile.getAbsolutePath() );

		fis = new FileInputStream(this.mapFile);
		
		try
		{
			this.keyList = new ArrayList<String>();
			this.valueMap = new HashMap<String,String>();
			
			this.initializeFromStream(fis);
		}
		finally
		{
			fis.close();
		}
	}

	public void saveClassnameMap() throws IOException
	{
		saveClassnameMap(this.mapFile);
	}
	
	public void saveClassnameMap(File aClassnameMapFile) throws IOException
	{
		FileOutputStream fos;
		BufferedOutputStream bos;

		if(!aClassnameMapFile.canWrite() && !aClassnameMapFile.createNewFile())
			throw new IOException("Can't write classname map file " + aClassnameMapFile.getAbsolutePath() );

		fos = new FileOutputStream(aClassnameMapFile);
		// classname map files are usually small, therefore allocate only 4KBytes of Buffer space
		bos = new BufferedOutputStream(fos, READ_BUFFER); 
		
		try
		{
			this.write( bos );
		}
		finally
		{
			bos.close();
			fos.close();
		}
	}
	
	public List<String> getEntries()
	{
		if(this.keyList == null)
			return new ArrayList<String>();
		
		List<String> entries = new ArrayList<String>(this.keyList.size());
		
		for( String key : this.keyList )
		{
			entries.add(this.valueMap.get(key));
		}
		
		return entries;
	}
}
