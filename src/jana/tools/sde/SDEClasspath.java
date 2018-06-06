package jana.tools.sde;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The Java classpath is an imaginary module, because it contains a set of root modules that contain all other packages that are accesible.
 * It is thus an imaginary root node for the package tree
 * 
 * Responsibilities: Loading of Files and Directories in the Classpath and creation of the top level Java packages
 * Collaborators: JJavaRepository
 * 
 * @see JJavaPackage
 **/
public class SDEClasspath
{	
	protected List<File> classpathElements;
	
	protected final static String ZIP_SUFFIX = ".zip";
	protected final static String JAR_SUFFIX = ".jar";
	
	public SDEClasspath(String[] theFilenames) throws IOException
	{
		init(theFilenames);
	}
	
	public void merge(SDEClasspath classpathObject)
	{
		for(File currentFile : classpathObject.classpathElements) 
		{
			if(!this.classpathElements.contains(currentFile))
				this.classpathElements.add(currentFile);
		}
	}
	
	/* classpath related functionality */
	protected void init(String[] theFilenames) throws IOException
	{
		this.classpathElements = new ArrayList<File>();
		
		if( theFilenames != null ) 
		{
			for(int i=0; i<theFilenames.length; i++)
				init( new File( theFilenames[i] ));
		}
	}
	
	protected void init(File aFile) throws IOException
	{
		if(aFile.exists())
		{
	     classpathElements.add(aFile);
		}	
	}
	
	public void addElement(File aFile)
	{
		if(aFile.exists())
			classpathElements.add(aFile);
	}
	
	public String toString()
	{
		StringBuffer classpath = new StringBuffer();
		File directoryOrFile;
		String pathSeparator;
		
		pathSeparator = System.getProperty("path.separator");
		
		for(Iterator<File> i = classpathElements.iterator(); i.hasNext(); )
		{
			directoryOrFile = (File) i.next();
			classpath.append(directoryOrFile.getAbsolutePath());
			
			if(i.hasNext())
				classpath.append(pathSeparator);
		}
		
		return classpath.toString();
	}

	public static void main(String[] args)
	{
		String[] filenames = { "bin" };
		
		try
		{
			SDEClasspath cp = new SDEClasspath(filenames);
			
			System.out.println( cp.toString() );	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void traverseSubDirectories(File aDirectory, List<File> aFileList)
	{
		File[] files;
		File file;
		List<File> discoveredSubDirectories;
		
		files = aDirectory.listFiles();
		discoveredSubDirectories = new ArrayList<File>(8);
		
		for(int i = 0; i < files.length; i++)
		{
			file = files[i];
			
			if(file.isDirectory())
				discoveredSubDirectories.add(file);
			
			if(file.isFile())
				aFileList.add(file);
		}
		
		for(File directory : discoveredSubDirectories)
			traverseSubDirectories(directory, aFileList);
	}
	
	private static List<String> libraryJars()
	{
		String libDirectoryName;
		String currentFileName;
		File libDirectory;
		List<File> libDirectoryFiles;
		List<String> libraryJars;
		
		
		libraryJars = new ArrayList<String>();
		
		libDirectoryName = System.getProperty("java.home")+File.separator+"lib";
		libDirectory = new File(libDirectoryName);
		
		libDirectoryFiles = new ArrayList<File>(libDirectory.list().length);

		traverseSubDirectories(libDirectory, libDirectoryFiles);
		
		for( File currentFile : libDirectoryFiles )
		{
			currentFileName = currentFile.getName();
				
			if(currentFileName.endsWith(JAR_SUFFIX) || currentFileName.endsWith(ZIP_SUFFIX))
				libraryJars.add(currentFile.getAbsolutePath());
		}	
		
		return libraryJars;
	}
	
	private static String[] defaultClasspathElements() throws Exception
	{
		List<String> classPathElements;
		String classPath; 
		String pathSeparator; 
		
		classPath = System.getProperty("java.class.path");
		pathSeparator = System.getProperty("path.separator");
		StringTokenizer st = new StringTokenizer( classPath, pathSeparator );	
		
		classPathElements = libraryJars();
		
		while(st.hasMoreElements()) 
		{
			classPathElements.add( st.nextToken() );
		}
		
		String[] elements = new String[classPathElements.size()];
		
		elements = classPathElements.toArray(elements);
		
		classPathElements = null;
		
		return elements;
	}
	
	public static SDEClasspath defaultClasspath() throws Exception
	{
		return new SDEClasspath( defaultClasspathElements());
	}
	
	/**
	 * For debugging purposes
	 */
	public static String defaultClasspathString() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		String[] cpElements = defaultClasspathElements();
		
		for(int i = 0; i < cpElements.length; i++)
		{
			if(i > 0)
				sb.append(File.pathSeparatorChar);
			
			sb.append(cpElements[i]);
		}
		
		return sb.toString();
	}
}
