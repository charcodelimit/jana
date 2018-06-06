package jana.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * An extension of JJavaClasspath that searches the library JAR files installed in java.home
 * and adds them to the list of classpath elements
 * @author chr
 *
 */
public class JJavaDefaultClasspath extends JJavaSearchableClasspath
{
	public static final String JAVA_LIBRARY_DIRECTORY_NAME = "lib";
	
	public JJavaDefaultClasspath() throws IOException
	{
		super();
		this.maxCacheCapacity = 1 << 12; // 4096 elements
		addClasspathElements(this.defaultClasspathElements());
	}

	public JJavaDefaultClasspath(int maxCacheCapacity) throws IOException
	{
		super();
		this.maxCacheCapacity = maxCacheCapacity;
		addClasspathElements(this.defaultClasspathElements());
	}
	
	/**
	 * Tries to find all .JAR files in the JavaHome directory
	 * 
	 * @return the list of library .jar files found 
	 */
	private List<String> javaLibraryJars()
	{
		String libDirectoryName;
		File libDirectory;		
		
		libDirectoryName = System.getProperty("java.home")+File.separator+JAVA_LIBRARY_DIRECTORY_NAME;
		libDirectory = new File(libDirectoryName);
	
		return findJarFilesInDirectory(libDirectory);
	}

	/**
	 * Enumerates the files in a directory and 
	 * adds class-names of classes found in class-files 
	 * to the list of analyzed classes
	 * 
	 * @param aFile
	 * @throws IOException
	 */
	private List<String> findJarFilesInDirectory(File aFile)
	{
		String filename;
		File currentDirectory;
		File[] files;
		List<File> subDirectories;
		List<String> jarFilesFound;
		
		jarFilesFound = new ArrayList<String>();
		subDirectories = new ArrayList<File>();
		subDirectories.add(aFile);
		
		while(subDirectories.size() > 0)
		{
			currentDirectory = subDirectories.remove(0);
			
			if( currentDirectory.isDirectory() )
			{	
				files = currentDirectory.listFiles();
			
				for(File file : files)
				{
					if( file.isDirectory() )
						subDirectories.add(file);
					else
					{
						filename = file.getPath().toLowerCase();
						
						if( filename.endsWith(JAR_SUFFIX) || filename.endsWith(ZIP_SUFFIX) )
						{
							try
							{
								jarFilesFound.add(file.getCanonicalPath());
							}
							catch(IOException ioe)
							{
								logger.error("Error while adding the file " + file.getPath() + " to the classpath.");
							}
						}
					}
				}
			}
		}
		
		return jarFilesFound;
	}

	/**
	 * returns the current classpath used by the JVM, and all the libraries found in the JRE's
	 * lib directory  
	 * @return a list of classpath element filenames
	 */
	private List<String> defaultClasspathElements()
	{
		List<String> classPathElements;
		String classPath; 
		String pathSeparator; 
		
		// add libraries
		classPathElements = javaLibraryJars();
		
		// add the classpath of the currently running JVM
		classPath = System.getProperty("java.class.path");
		pathSeparator = System.getProperty("path.separator");
		StringTokenizer st = new StringTokenizer( classPath, pathSeparator );	
		
		while(st.hasMoreElements()) 
		{
			classPathElements.add( st.nextToken() );
		}
		
		
		return classPathElements;
	}
}
