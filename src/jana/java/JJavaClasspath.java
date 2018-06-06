package jana.java;

import jana.lang.java.JJavaPackage;
import jana.lang.java.JJavaSignature;
import jana.metamodel.SExpression;
import jana.util.JClassFile;
import jana.util.JRelativeFile;
import jana.util.jar.JJarFile;
import jana.util.jar.OverwritingFileException;
import jana.util.logging.JLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

/**
 * 
 * @see JJavaPackage
 **/
@RunWith(Enclosed.class)
public class JJavaClasspath implements SExpression
{
	protected static final JLogger logger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER);
	private final static boolean LOG_CLASS_FILE_SEARCH = false;
	
	protected Collection<JJavaPackage> childModules;
	
	protected List<File> classpathElements;
	protected HashMap<String, File> classpathElementMap;
	
	protected final static String ZIP_SUFFIX = ".zip";
	protected final static String JAR_SUFFIX = ".jar";
	
	/**
	 * Creates an empty classpath
	 */
	public JJavaClasspath() throws IOException
	{
		this.classpathElements = new ArrayList<File>();
		this.classpathElementMap = new HashMap<String, File>();
	}
	
	public JJavaClasspath(String aClasspathString) throws IOException
	{
		JJavaClasspathString cps;
		
		this.classpathElements = new ArrayList<File>();
		this.classpathElementMap = new HashMap<String, File>();
	
		cps = new JJavaClasspathString(aClasspathString);
		this.addClasspathElements(cps.asFilenameStringList());
	}
	
	public JJavaClasspath(List<File> classpathElementFilenames) throws IOException
	{
		this.classpathElements = classpathElementFilenames;
		addElementsToMap(classpathElementFilenames);
	}
	
	public void addElementToMap(File aFile)
	{
		try
		{
			this.classpathElementMap.put(aFile.getCanonicalPath(), aFile);
		}
		catch(IOException ioe)
		{
			this.classpathElementMap.put(aFile.getAbsolutePath(), aFile);
		}
	}
	
	public void addElementsToMap(List<File> classpathElementFilenames)
	{
		for(File classpathElement : classpathElementFilenames)
			addElementToMap(classpathElement);
	}
	
	public void removeElementsFromMap(List<File> aFileList)
	{
		String fileName;
	
		for(File fileToRemove : aFileList)
		{		
			try
			{
				fileName = fileToRemove.getCanonicalPath();
			}
			catch(IOException ioe)
			{
				fileName = fileToRemove.getAbsolutePath();
			}
			
			this.classpathElementMap.remove(fileName);
		}
	}
	
	/**
	 * Only adds valid classpath elements.
	 * Valid elements exist in the filesystem, and are either directories, .ZIP, or .JAR files.
	 * 
	 * @param aFile
	 */
	public void addElement(File aFile) throws IOException
	{
		String path;
		
		if( aFile.exists() )
		{
			if(!aFile.canRead())
				logger.warn("The classpath element " + aFile.getCanonicalPath() + " exists, but can't be read!");
			
			if( aFile.isDirectory() )
			{
				this.classpathElements.add(aFile);
				addElementToMap(aFile);
			}
			else
			{
				path = aFile.getPath().toLowerCase();
				// only add classpath elements that are relevant
				if(path.endsWith(ZIP_SUFFIX) || path.endsWith(JAR_SUFFIX))
				{
					this.classpathElements.add(aFile);
					addElementToMap(aFile);
				}
			}
		}
		else
		{
			logger.warn("The classpath element " + aFile.getCanonicalPath() + " cannot be found in your file-system!");
		}
	}
	

	public void addClasspathElements(List<String> classpathElementFilenames, File baseDirectory) throws IOException
	{
		if( classpathElementFilenames != null ) 
		{
			for(String element : classpathElementFilenames)
				this.addElement(new JRelativeFile(element, baseDirectory));
		}
	}
	
	/**
	 * Adds files to the classpath
	 * 
	 * @param classpathElementFilenames
	 * @throws IOException
	 */
	public void addClasspathElementsInDirectory(List<String> classpathElementFilenames, File aBaseDirectory) throws IOException
	{	
		if( classpathElementFilenames != null ) 
		{
			for(String element : classpathElementFilenames)
				this.addElement(new JRelativeFile(element, aBaseDirectory));
		}
	}
	
	/**
	 * Adds files to the classpath
	 * 
	 * @param classpathElementFilenames
	 * @throws IOException
	 */
	public void addClasspathElements(List<String> classpathElementFilenames) throws IOException
	{	
		if( classpathElementFilenames != null ) 
		{
			for(String element : classpathElementFilenames)
				this.addElement(new File(element));
		}
	}
	
	/**
	 * Adds files to the classpath
	 * 
	 * @param aString - a ':' separated list of file-names
	 * @throws IOException
	 */
	public void addClasspathElements(String aString) throws IOException
	{
		JJavaClasspathString cps;
		List<String> filenames;
		
		cps = new JJavaClasspathString(aString);
		filenames = cps.asFilenameStringList();
		addClasspathElements( filenames );	
	}
	
	public void removeElement(File aFile)
	{
		ArrayList<File> elementsToRemove = new ArrayList<File>();
		
		// collect files to remove
		for(File file : this.classpathElements)
		{
			if(file.getAbsolutePath().equals(aFile.getAbsolutePath()))
				elementsToRemove.add(file);
		}
		
		// remove
		this.classpathElements.removeAll(elementsToRemove);
		this.removeElementsFromMap(elementsToRemove);
	}
	
	public void merge(JJavaClasspath classpathObject)
	{
		for(File currentFile : classpathObject.classpathElements) 
		{
			if(!this.classpathElements.contains(currentFile))
			{
				this.classpathElements.add(currentFile);
				addElementToMap(currentFile);
			}
		}
	}
	
	/**
	 * collects all elements in the classpath that are relative to a directory
	 * 
	 * @return
	 * @throws IOException
	 */
	public JJavaClasspath getUserDefinedClasspath(File aDirectory) throws IOException
	{
		JRelativeFile rf;
		
		JJavaDefaultClasspath dcp = new JJavaDefaultClasspath();		
		JJavaClasspath cp = new JJavaClasspath();
		
		for( File element : this.classpathElements )
		{
			if( ! dcp.classpathElements.contains(element) )
			{
				rf = new JRelativeFile(element, aDirectory);
				if(!rf.getRelativePath().equals(element.getCanonicalPath()))
				{
					cp.addElement(element);
				}
			}
		}
		
		return cp;
	}
	
	/**
	 * creates jar files from all classpath elements that are directories, 
	 * and copies them together with the classpath elements that are .jar files
	 * to <emph>dstDirectory</emph>.
	 * 
	 * @param dstDirectory - the target directory where the .jar files should be placed
	 * @param overwrite - if true overwrites existing files, otherwise an IOException is raised
	 * @return the filenames of the created .jar files 
	 */
	public List<String> copyClasspathElements(File dstDirectory, boolean overwrite) throws IOException
	{
		JJarFile jarFile;
		JRelativeFile dstFile;
		String jarFileName;
		String fileName;
		List<JJavaSignature> classes;
		List<String> jarFileNames;
		StringBuffer error = new StringBuffer();
		
		jarFileNames = new ArrayList<String>(this.classpathElements.size());
		
		for( File element : this.classpathElements )
		{
			if( element.isDirectory() )
			{
				classes = classesInDirectory(element);
				
				fileName = element.getPath();
				jarFileName = fileName.substring(fileName.lastIndexOf(File.separatorChar)+1);
				jarFileName = jarFileName + JAR_SUFFIX;
				jarFile = new JJarFile(jarFileName,dstDirectory);
				jarFile.addClasses(element, classes);
				jarFileNames.add(jarFile.getRelativePath());
			}
			else
			{
				jarFile = new JJarFile(element);
				dstFile = new JRelativeFile(jarFile.getRelativePath(), dstDirectory);
				
				jarFileNames.add(dstFile.getRelativePath());
				
				if( jarFile.isValidJarFile() )
				{
					try
					{
						jarFile.copyJarFile(dstFile, overwrite);
					}
					catch(OverwritingFileException ofe)
					{
						error.append(ofe.toString());
						error.append("\n");
					}
				}
			}
		}
		
		if(error.length() > 0)
		{
			throw new IOException(error.toString());
		}
		
		return jarFileNames;
	}

	/**
	 * creates jar files from all directories that are user defined classpath elements,
	 * and copies those together with the user-defined .jar files to a destination directory
	 * @param baseDirectory - the base directory of the classpath
	 * @param libDirectory - the target directory where the library .jar files should be placed
	 * @throws IOException 
	 */
	public void copyUserDefinedClasspathElements(File baseDirectory, File libDirectory, boolean overwrite) throws IOException
	{
		JJavaClasspath userClassPath;
		
		userClassPath = getUserDefinedClasspath(baseDirectory);
		userClassPath.copyClasspathElements(libDirectory, overwrite);
	}
	
	/**
	 * collects all directories and .jar files that are part of the user-defined classpath
	 * 
	 * @return
	 * @throws IOException
	 */
	public JJavaClasspath getUserDefinedClasspath() throws IOException
	{
		JJavaDefaultClasspath dcp = new JJavaDefaultClasspath();		
		JJavaClasspath cp = new JJavaClasspath();
		
		for( File element : this.classpathElements )
		{
			if( ! dcp.classpathElements.contains(element) )
			{
				cp.addElement(element);
			}
		}
		
		return cp;
	}
	
	/**
	 * creates jar files from all directories that are user defined classpath elements,
	 * and copies those together with the user-defined .jar files to a destination directory
	 * @param libDirectory - the target directory where the library .jar files should be placed
	 * @throws IOException
	 * @return the filenames of the created .jar files 
	 */
	public List<String> copyUserDefinedClasspathElements(File libDirectory,boolean overwrite) throws IOException
	{
		JJavaClasspath userClassPath;
		
		userClassPath = getUserDefinedClasspath();
		return userClassPath.copyClasspathElements(libDirectory, overwrite);
	}
	
	
	/**
	 * 
	 * @return a list of signatures corresponding to the classes in the classpath
	 */
	public List<JJavaSignature> classesInClasspath() throws IOException
	{
		List<JJavaSignature> signatures = new ArrayList<JJavaSignature>();
		JJarFile jjf;
		
		for(File file : this.classpathElements)
		{
			if(file.isDirectory())
			{
				signatures.addAll(this.classesInDirectory(file));
			}
			else
			{
				jjf = new JJarFile(file);
				if( jjf.isValidJarFile() )
					signatures.addAll(jjf.classesInJarFile());
			}
		}
		
		return signatures;
	}
	
	public List<JJavaSignature> classesInDirectory(File aDirectory)
	{
		try
		{
			return this.classesInDirectory(aDirectory,true);
		}
		catch(IOException ioe)
		{
			logger.warn(ioe.toString());
			ioe.printStackTrace();
			
			return new ArrayList<JJavaSignature>();
		}
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
		JClassFile classFile;
		JJavaSignature signature;
		File currentDirectory;
		File[] files;
		List<File> subDirectories;
		File baseDirectory;
		
		List<JJavaSignature> signatures = new ArrayList<JJavaSignature>();
		
		// try to determine the base directory name
		baseDirectory = new File(this.directoryNameRelativeToClasspath(aDirectory));
		
		subDirectories = new ArrayList<File>();
		subDirectories.add(aDirectory);
		
		while(!subDirectories.isEmpty())
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
						try
						{
							classFile = new JClassFile(file,baseDirectory);
						
							if( classFile.isValidClassfile() )
							{
								signature = classFile.getSignature();
								if(LOG_CLASS_FILE_SEARCH && Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
									logger.debug("adding class name: " + signature.qualifiedName());
								signatures.add(signature);
							}
						}
						catch(IOException ioe)
						{
							if(!muffleExceptions)
								throw ioe;
						}
					}
				}
			}
		}
		
		return signatures;
	}
	
	public String toString()
	{
		StringBuffer classpath = new StringBuffer();
		File directoryOrFile;
		
		for(Iterator<File> i = classpathElements.iterator(); i.hasNext(); )
		{
			directoryOrFile = (File) i.next();
			classpath.append(directoryOrFile.getAbsolutePath());
			
			if(i.hasNext())
				classpath.append(File.pathSeparatorChar);
		}
		
		return classpath.toString();
	}
	
	/**
	 * Calculates the longest matching prefix between a directory name and the directories in the classpath 
	 * This is necessary, for example, if you only want to know the classes of your own projects, 
	 * even though classes from libraries are in the classpath as well.
	 * 
	 * Example: classpath: -cp /home/usr/java-sources:/usr/share/java/junit.jar
	 *          aDirectory: /home/usr/java-sources/net/sf/myproject
	 *          => resulting BaseDirectory
	 *           /home/usr/java-sources
	 *          => collects class-files in
	 *          /home/usr/java-sources/net/sf/myproject/*
	 * 
	 * @return the longest prefix matching aDirectory
	 */
	public String directoryNameRelativeToClasspath(File aDirectory) throws IOException
	{
		int maximumMatchLength;
		int currentLength;
		int maxLength;
		int index;
		String maximumMatch;
		String directoryName;
		String currentDirName;
		File currentDir;

		if(!aDirectory.isDirectory())
			throw new IOException(aDirectory + " is no directory!");

		directoryName = aDirectory.getCanonicalPath();//this.projectSourceDirectory.getCanonicalPath();
		maximumMatchLength = 0;
		maximumMatch = "";

		for(File classpathElement : this.classpathElements)
		{
			if(classpathElement.isDirectory())
			{
				currentDir = classpathElement;
				currentDirName = currentDir.getCanonicalPath();
				currentLength = 0;
				
				maxLength = Math.min(directoryName.length(), currentDirName.length());

				for(index = 0; index < maxLength; index++ )
				{
					if( currentDirName.charAt(index) != directoryName.charAt(index) )
					{
						currentLength = index - 1;

						if( currentLength > maximumMatchLength )
						{
							maximumMatchLength = currentLength;
							maximumMatch = currentDirName;
						}

						break;
					}
				}
				
				if(index == maxLength)
				{
					maximumMatch = currentDirName;
					maximumMatchLength = maximumMatch.length();	
				}
			}
		}
		
		if(maximumMatchLength == 0)
			return directoryName;
		else	
			return maximumMatch;
	}

	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("java-classpath");
		
		aStringBuffer.append(" (list");
		for(File currentFile : this.classpathElements)
		{
			try
			{
				aStringBuffer.append(" \"");
				aStringBuffer.append(currentFile.getCanonicalPath());
				aStringBuffer.append("\"");
			}
			catch(IOException e)
			{
				logger.error("Invalid classpath element " + currentFile.toString());
			}
		}
		aStringBuffer.append(")");
	}
	
	public static class JJavaClasspathTest
	{	
		@Test
		public void classpathPrintingTest() throws IOException
		{
			JJavaClasspath cp;
			String[] pathname;
			File currentDir = new File("."); // get current directory
			
			// empty classpath
			cp = new JJavaClasspath("");
			Assert.assertTrue( cp.toString().isEmpty() );
			Assert.assertEquals("(java-classpath (list))", "(" + cp.toSExpression() + ")" );
			
			// single element classpath
			cp = new JJavaClasspath("bin");			
			pathname = new String[1];
			pathname[0] = currentDir.getCanonicalPath() + File.separator + "bin";
			Assert.assertEquals( pathname[0],cp.toString() );
			Assert.assertEquals("(java-classpath (list \"" + pathname[0] + "\"))", "(" + cp.toSExpression() + ")" );
			
			// multiple element classpath
			cp = new JJavaClasspath("bin:src");
			pathname = new String[2];
			pathname[0] = currentDir.getCanonicalPath() + File.separator + "bin";
			pathname[1] = currentDir.getCanonicalPath() + File.separator + "src";
			Assert.assertEquals( pathname[0] + File.pathSeparator + pathname[1], cp.toString() );
			Assert.assertEquals("(java-classpath (list \"" + pathname[0] + "\" \"" + pathname[1] + "\"))", "(" + cp.toSExpression() + ")" );
		}
	}

	/**
	 * 
	 * @param aPathnameStringList
	 * @return true if aPathnameStringList contains all classpath elements in this classpath
	 */
	public boolean classpathElementsAreInList(List<String> aPathnameStringList)
	{
		if(aPathnameStringList == null)
			return this.classpathElements.size() == 0;
		
		if(aPathnameStringList.size() < this.classpathElements.size())
			return false;
		
		int found = 0;
		for(String pathnameString : aPathnameStringList)
		{
			if( this.classpathElementMap.get(pathnameString) != null )
				found++;
		}
		
		return found == this.classpathElements.size();
	}
}
