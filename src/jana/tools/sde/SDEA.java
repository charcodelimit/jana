package jana.tools.sde;

import jana.util.logging.JLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.BasicConfigurator;

import soot.PackManager;
import soot.Scene;
import soot.options.Options;

/***
 * Soot-Based Java Decompiler
 * 
 * @author chr
 *
 */
public class SDEA
{
	protected static JLogger logger = JLogger.getLogger("SDE");
	protected List<String> classNames;
	protected SDEClasspath classpath;
	
	protected final static String CLASS_SUFFIX = ".class";
	protected final static String JAR_SUFFIX = ".jar";
	protected final static String ZIP_SUFFIX = ".zip";
	protected final static char CLASS_SEPARATOR_CHAR = '.';
	
	public SDEA() throws Exception
	{
		BasicConfigurator.configure();
		
		this.classNames = new ArrayList<String>(512);
		this.classpath = SDEClasspath.defaultClasspath();
	}
	
	/**
	 * Extracts a list of elements from a String, where elements are separated by a colon
	 * 
	 * @param aFilenameList - a ':' separated list of Strings
	 * @return - the Strings found between the colons
	 */
	private String[] filenames(String aFilenameList)
	{
		List<String> filenameList = new ArrayList<String>();
		
		String rest;
		String fileName;
		int index;
		
		rest = aFilenameList;
		index = rest.indexOf(File.pathSeparatorChar);
		
		
		if(index > 0)
		{
			fileName = rest.substring(0,index);
			rest = rest.substring(index + 1);
		}
		else fileName = rest;
		
		while(fileName.length() > 0)
		{
			filenameList.add(fileName);
			
			index = rest.indexOf(File.pathSeparatorChar);
			if(index > 0)
			{
				fileName = rest.substring(0,index);
				rest = rest.substring(index + 1);
			}
			else 
			{	
				fileName = rest;
				rest = "";
			}
		}
		
		String[] result = new String[filenameList.size()];
		result = filenameList.toArray(result);
		
		return result;
	}
	
	/**
	 * Adds files to the classpath
	 * 
	 * @param aString - a ':' separated list of file-names
	 * @throws IOException
	 */
	protected void classPath(String aString) throws IOException
	{
		File file;
		String[] filenames;
		
		filenames = filenames(aString);
		
		for( String fileName : filenames )
		{
			file = new File(fileName);
			
			if(file.exists())
				this.classpath.addElement(file);
		}	
	}
	
	/**
	 * Excludes classes from the analysis
	 * 
	 * @param aString - a ':' separated list of class names that should be excluded from the analysis
	 * @throws IOException
	 */
	protected void excludeClasses(String aString) throws IOException
	{
		String[] classnames;
		int index;
		
		classnames = filenames(aString);
		
		for(String className : classnames)
		{
			index = this.classNames.indexOf(className);
			if(index >= 0)
			{
				logger.debug("Removed Class: " + className);
				this.classNames.remove(index);
			}
		}	
	}
	
	/**
	 * Adds classes in jar-files that should be analyzed
	 * 
	 * @param aString
	 * @throws IOException
	 */
	protected void jarFiles(String aString) throws IOException
	{
		File jarFile;
		String[] filenames;
		
		filenames = filenames(aString);
		
		for( String fileName : filenames )
		{
			jarFile = new File(fileName);
			
			if(jarFile.exists())
				addClassesInJarFile(jarFile);
		}
	}
	
	/**
	 * Adds classes in directories that should be analyzed
	 * 
	 * @param aString
	 * @throws IOException
	 */
	protected void directories(String aString) throws IOException
	{
		File directory;
		String[] filenames;
		
		filenames = filenames(aString);
		
		for( String fileName : filenames )
		{
			directory = new File(fileName);
			
			if(directory.exists())
				addClassesInDirectory(directory);
		}
	}
	
	/**
	 * Decompiles a list of classes that are found in the classpath
	 * 
	 * @param theClassNames
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void analyzeClasses(String[] theClassNames) throws Exception
	{
		if(theClassNames != null)
		{
			for(String className : theClassNames)
				this.classNames.add(className);
		}
		
		Scene.v().setSootClassPath(this.classpath.toString());
		
		Options.v().set_output_format( Options.output_format_dava );
		
		logger.info("Adding Classes");
		
		for(String className : this.classNames)
		{
			logger.debug(className);
			Options.v().classes().add(className);
		}
		
		if(classNames.size() == 0)
			return; 
		
		logger.debug("Loading " + classNames.size() + " classes");
		Scene.v().loadNecessaryClasses();
		
		logger.info("Analyzing");
		PackManager.v().runPacks();
		logger.info("Writing Output");
		PackManager.v().writeOutput();
	}
	
	/**
	 * Converts a filename to a class-name
	 * 
	 * @param aFilename
	 */
	private void addFilename(String aFilename)
	{
		String className;
		
		if( aFilename.endsWith(".class") )
		{
			className = aFilename.substring(0, aFilename.lastIndexOf('.'));
			className = className.replace(File.separatorChar, CLASS_SEPARATOR_CHAR);
			logger.debug("adding Class: " + className);
			this.classNames.add( className );
		}
	}
	
	/**
	 * Enumerates the files in a .jar file and 
	 * adds class-names of classes found in class-files 
	 * to the list of analyzed classes
	 * 
	 * @param aFile
	 * @throws IOException
	 */
	private void addClassesInJarFile(File aFile) throws IOException
	{
		ZipFile zf;
		String fileName;
		
		Enumeration<? extends ZipEntry> entries;
		
		classpath.addElement(aFile);
		
		zf = new ZipFile(aFile);
		entries = zf.entries();
		
		for(ZipEntry ze = entries.nextElement(); entries.hasMoreElements(); ze = entries.nextElement() )
		{
			if(! ze.isDirectory() )
			{
				fileName = ze.getName();
				addFilename(fileName);
			}
		}
		
		zf.close();
	}
	
	/**
	 * Enumerates the files in a directory and 
	 * adds class-names of classes found in class-files 
	 * to the list of analyzed classes
	 * 
	 * @param aFile
	 * @throws IOException
	 */
	private void addClassesInDirectory(File aFile) throws IOException
	{
		String filename;
		File currentDirectory;
		File[] files;
		List<File> subDirectories;
		
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
						filename = file.getName();
						
						if( filename.endsWith(".zip") || filename.endsWith(".jar") )
							this.addClassesInJarFile(file);
						else
							addFilename(filename);
					}
				}
			}
		}
	}

}
