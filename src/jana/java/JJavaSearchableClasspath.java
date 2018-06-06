package jana.java;

import jana.lang.java.JJavaSignature;
import jana.util.jar.JJarFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JJavaSearchableClasspath extends JJavaClasspath
{
	protected final static int CLASS_CACHE_SIZE = 1 << 18; // 256k entries
	protected int maxCacheCapacity;
	protected int cacheLevel;
	protected Map<File,List<JJavaSignature>> classCache;
	
	protected void init()
	{
		this.maxCacheCapacity = CLASS_CACHE_SIZE;
		this.cacheLevel = 0;
		this.classCache = new HashMap<File,List<JJavaSignature>>();
	}
	
	public JJavaSearchableClasspath(List<File> classpathElementFilenames)
			throws IOException
	{
		super(classpathElementFilenames);
		
		init();
	}

	public JJavaSearchableClasspath(String classpathString) throws IOException
	{
		super(classpathString);
		
		init();
	}

	/**
	 * Creates an empty classpath
	 */
	public JJavaSearchableClasspath() throws IOException
	{
		super();
		
		init();
	}
	
	private void addSignaturesToCache(File aFile, List<JJavaSignature> aSignatureList)
	{
		if( this.cacheLevel + aSignatureList.size() < this.maxCacheCapacity )
		{
			this.classCache.put(aFile, aSignatureList);
			this.cacheLevel += aSignatureList.size();
		}
	}
	
	/**
	 * 
	 * @return a list of signatures corresponding to the classes in the classpath
	 */
	public List<JJavaSignature> classesInClasspath() throws IOException
	{
		List<JJavaSignature> signatures = new ArrayList<JJavaSignature>();
		List<JJavaSignature> signaturesFromFile;
		JJarFile jjf;
		
		for(File file : this.classpathElements)
		{
			if(this.classCache.containsKey(file))
			{
				signatures.addAll(this.classCache.get(file));
			}
			else
			{
				if(file.isDirectory())
				{
					signaturesFromFile = classesInDirectory(file);
					signatures.addAll(signaturesFromFile);
					
					addSignaturesToCache(file, signaturesFromFile);
				}
				else
				{
					jjf = new JJarFile(file);
					if( jjf.isValidJarFile() )
					{
						signaturesFromFile = jjf.classesInJarFile();
						signatures.addAll(signaturesFromFile);
				
						addSignaturesToCache(file, signaturesFromFile);
					}
				}
			}
		}
		
		return signatures;
	}
	
	/**
	 * Collects the signatures of all classes in a directory.
	 * This method tries to determine a possible base-directory, if the base-directory is part of the classpath.
	 * 
	 * @see directoryNameRelativeToClasspath
	 * 
	 * @param aBaseDirectory
	 * @param muffleExceptions if true -- ignore and continue in-spite of errors
	 * @return a list of signatures
	 * @throws IOException
	 */
	public List<JJavaSignature> classesInDirectory(File aDirectory, boolean muffleExceptions) throws IOException
	{
		List<JJavaSignature> signaturesFromFile;
		
		if(this.classCache.containsKey(aDirectory))
		{
			return this.classCache.get(aDirectory);
		}
		else
		{
			signaturesFromFile = super.classesInDirectory(aDirectory, muffleExceptions);
			addSignaturesToCache(aDirectory, signaturesFromFile);
			
			return signaturesFromFile;
		}
	}
	
	/**
	 * Searches all classpath elements and returns the classpath element that contains a class
	 * with signature aSignature.
	 *  
	 * @param aSignature
	 * @return File or null - the classpath element that contains the signature
	 * @throws IOException
	 */
	public File findClasspathElementForClass(JJavaSignature aSignature) throws IOException
	{
		File srcFile;
		List<JJavaSignature> signaturesFromFile;
		JJarFile jjf;
		
		srcFile = null;
		signaturesFromFile = new ArrayList<JJavaSignature>();
		
		for(File file : this.classpathElements)
		{
			if(this.classCache.containsKey(file))
			{
				signaturesFromFile = this.classCache.get(file);
			}
			else
			{
				if(file.isDirectory())
				{
					signaturesFromFile = classesInDirectory(file);
					addSignaturesToCache(file, signaturesFromFile);
				}
				else
				{
					jjf = new JJarFile(file);
					if( jjf.isValidJarFile() )
					{
						signaturesFromFile = jjf.classesInJarFile();
						addSignaturesToCache(file, signaturesFromFile);
					}
				}
			}
			
			if( signaturesFromFile.contains(aSignature) )
			{
				srcFile = file;
				break;
			}
		}
		
		return srcFile;
	}
}
