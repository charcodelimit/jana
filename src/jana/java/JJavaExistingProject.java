package jana.java;

import jana.lang.java.JJavaSignature;
import jana.metamodel.SExpression;
import jana.util.JRelativeFile;
import jana.util.exceptions.JParseException;
import jana.util.exps.JClassnameMap;
import jana.util.exps.JSExpressionList;
import jana.util.exps.JSExpressionStringAtom;
import jana.util.exps.JSExpressionVectorList;
import jana.util.jar.JJarFile;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Enclosed;

@RunWith(Enclosed.class)
public class JJavaExistingProject extends JJavaProject
{
	private final int READ_BUFFER_SIZE = 1 << 12;
	
	public JJavaExistingProject(String aProjectName, String aRepositoryDirectoryName) throws IOException
	{
		super(aProjectName, aRepositoryDirectoryName);
		
		init();
		
		loadProjectData(this.projectFile());
	}
	
	public JJavaExistingProject(String aProjectName, File aRepositoryDirectory, JJavaClasspath aProjectClasspath) throws IOException
	{
		super(aProjectName, aRepositoryDirectory);
		this.projectClasspath = aProjectClasspath;
		
		init();
		
		loadProjectData(this.projectFile());
	}
	
	public JJavaExistingProject(File aProjectFile, File aRepositoryDirectory) throws IOException
	{
		super("", aRepositoryDirectory);
		
		init();
		
		loadProjectData(aProjectFile);
	}
	
	public JJavaExistingProject(File aProjectFile, File aRepositoryDirectory, JJavaClasspath aProjectClasspath) throws IOException
	{
		super("", aRepositoryDirectory);
		this.projectClasspath = aProjectClasspath;
		
		init();
		
		loadProjectData(aProjectFile);
	}

	/**
	 * Makes sure that first the project classpath,
	 * then the project libraries,
	 * and finally the system classpath is searched
	 * in that order.
	 * 
	 * @throws IOException
	 */
	private void initClasspath() throws IOException
	{
		if(this.projectClasspath == null)
		{
			this.projectClasspath = new JJavaClasspath();
		}

		// first search project jar-file
		if(this.projectJarFile != null)
			this.projectClasspath.addElement(this.projectJarFile);
		
		// then the library jar-files
		this.projectClasspath.addClasspathElements(this.projectLibraryJarFiles, this.repositoryDirectory);
		
		// finally the system's classpath
		this.projectClasspath.merge(this.defaultClasspath);
	}
	
	private void loadProjectData(File aFile) throws IOException
	{	
		FileInputStream fis;
		
		if(!aFile.exists())
			throw new FileNotFoundException("Can't find project file " + aFile.getCanonicalPath() + " !");
		if(!aFile.canRead())
			throw new FileNotFoundException("The project file " + aFile.getCanonicalPath() + " is not accessible !");
		
		fis = new FileInputStream(aFile);
		
		try
		{
			this.read(fis);
		}
		finally
		{
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
	private void read(InputStream in) throws IOException
	{
		DataInputStream dis = new DataInputStream(in);
		byte[] byteBuffer = new byte[READ_BUFFER_SIZE];
		StringBuffer sb = new StringBuffer();
		String s;
		int bytesRead = 0;
		long start, end;
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Loading Project File");
		start = System.currentTimeMillis();
		
		try
		{
			while(bytesRead != -1)
			{
				if(bytesRead > 0)
					sb.append( new String(byteBuffer,0, bytesRead) );
				
				bytesRead = dis.read(byteBuffer);
			}
		}
		finally
		{
			dis.close();
		}
		
		end = System.currentTimeMillis();	
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Loading Project File took " + (end - start) + "ms");
		
    	s = sb.toString();
    	sb = null;
    	this.parse(s);
	}
	
	private void parse(String aString) throws JParseException, IOException
	{
		JSExpressionList list;
		long start,end;
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Parsing Project File");
		start = System.currentTimeMillis();	  
		
		list = JSExpressionVectorList.parse(aString);
		aString = null;
		
		end = System.currentTimeMillis();	
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Parsing Project File took " + (end - start) + "ms");
	    	
		this.parse(list);
	}
	
	@SuppressWarnings("unchecked")
	private void parse(JSExpressionList aList) throws JParseException, IOException
	{
		JSExpressionList exprList;
		SExpression listElement;
		String key, value;
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> list;
		long start, end;
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Parsing Project Data");
		
		start = System.currentTimeMillis();
		
		value = aList.first().toString();
		
		if( value.toLowerCase().equals("project") )
		{	
			for(int i = 1; i < aList.length(); i++)
			{
				exprList = (JSExpressionList) aList.nth(i);
				key = exprList.first().toString();
				
				list = new ArrayList<String>(exprList.length() - 1);
					
				for(int j = 1; j < exprList.length(); j++)
				{
					listElement = exprList.nth(j);
					
					if(listElement instanceof JSExpressionStringAtom)
					{
						value = listElement.toString();
						list.add(value);
					}
				}
				
				map.put(key.toLowerCase(), list);
			}
		}
		else
			throw new JParseException(aList.toString() + " is no proper project record.");
		
		key = "project-name";
		if(map.containsKey(key))
		{
			list = map.get(key);
			
			if(list.size() > 0)
				value = list.get(0);
			else
				value = null;
			
			if(value == null || value.length() == 0)
				throw new JParseException("The Project File contains no valid project-name!");
			
			this.projectName = value; 
		}
		else
			throw new JParseException(aList.toString() + " is no proper project record. Missing " + key + " entry.");
		
		key = "java-version";
		if(map.containsKey(key))
		{
			list = map.get(key);
			
			if(list.size() > 0)
				value = list.get(0);
			else
				value = null;
			
			this.javaVersion = value; 
		}
		else
			throw new JParseException(aList.toString() + " is no proper project record. Missing " + key + " entry.");
		
		key = "project-directory";
		try {
			this.projectDirectory = getDirectoryEntry(map, key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-analysis-directory";
		try {
			this.analysisOutputDirectory = getDirectoryEntry(map, key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-compilation-directory";
		try {
			this.compilationOutputDirectory = getDirectoryEntry(map, key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-transformation-directory";
		try {
			this.transformationOutputDirectory = getDirectoryEntry(map, key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-jar-file";
		try {
			this.projectJarFile = this.getJarFileEntry(map, key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-final-jar-file";
		try {
			this.projectFinalJarFile = this.getJarFileEntry(map, key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
	
		key = "project-classname-dictionary-file";
		try { 
			this.classnameMap = getClassnameMapEntry(map, key);
		}
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-debug-information-file";
		try { 
			this.debugInformation = getDebugInformationEntry(map, key);
		}
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-library-jar-files";
		if(map.containsKey(key))
		{
			this.projectLibraryJarFiles = map.get(key);
		}
		else
			throw new JParseException(aList.toString() + " is no proper project record. Missing " + key + " entry.");
	
		key = "project-aspects";
		try { 
			this.projectAspects = getSignatureListEntry(map,key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
	
		key = "project-classes";
		try { 
			this.projectClasses = getSignatureListEntry(map,key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		key = "project-transformed-classes";
		try { 
			this.projectTransformedClasses = getSignatureListEntry(map,key); }
		catch(JParseException jpe) {
			throw new JParseException(aList.toString() + " is no proper project record." + jpe.toString());	}
		
		aList = null;
		map = null;
		
		initClasspath();
		
		end = System.currentTimeMillis();
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Parsing Project Data took " + (end - start) + "ms");
	}
	
	/**
	 * @param aMap
	 * @param key the name of the directory entry.
	 * @return the relative file for the directory entry stored in aMap.
	 * @throws IOException
	 */
	private JRelativeFile getDirectoryEntry(Map<String, List<String>> aMap, String key) throws IOException
	{
		JRelativeFile value;
		List<String> list;
		String entry;
		
		value = null;
		
		if(aMap.containsKey(key))
		{
			list = aMap.get(key);
			
			if(list.size() > 0)
				entry = list.get(0);
			else
				entry = null;
			
			if(entry == null || entry.length() == 0)
				throw new JParseException("The Project File contains no valid " + key + " name!"); 
			
			value = new JRelativeFile(entry,this.repositoryDirectory);
		}
		else
			throw new JParseException("Missing " + key + " entry.");
		
		return value;
	}
	
	/**
	 * @param aMap
	 * @param key the name of the jar-file entry.
	 * @return the {@link JJarFile} relative to {@link this.repositoryDirectory} for the jar-file entry stored in aMap.
	 * @throws IOException
	 */
	private JJarFile getJarFileEntry(Map<String, List<String>> aMap, String key) throws IOException
	{
		JJarFile value;
		List<String> list;
		String entry;
		
		value = null;
		
		if(aMap.containsKey(key))
		{
			list = aMap.get(key);
			
			if(list.size() > 0)
			{
				entry = list.get(0);
				value = new JJarFile(entry,this.repositoryDirectory);
			}
			else
				value = null;
		}
		else
			throw new JParseException("Missing " + key + " entry.");
		
		return value;
	}
	
	/**
	 * Returns the classname map-file to the file stored in aMap and creates a new {@link JClassnameMap} entry.
	 * The {@link JRelativeFile} instances for lock and temp-files are created accordingly.
	 * 
	 * @param aMap
	 * @param key the name of the project-classname-dictionary-file entry.
	 * @throws IOException
	 */
	private JClassnameMap getClassnameMapEntry(Map<String, List<String>> aMap, String key) throws IOException
	{
		JClassnameMap value;
		List<String> list;
		String entry;
		
		value = null;
				
		if(aMap.containsKey(key))
		{
			list = aMap.get(key);
			
			if(list.size() > 0)
				entry = list.get(0);
			else
				entry = null;

			if(entry == null || entry.length() == 0)
				throw new JParseException("The Project File contains no valid name for the project-classname-dictionary-file!");
			
			this.classnameMapFile = new JRelativeFile(entry,this.repositoryDirectory);
			this.projectLockFile = new JRelativeFile(this.projectDirectory.getRelativePath() + File.separator + projectName +  PROJECT_LOCKFILE_EXTENSION, 
					 this.repositoryDirectory);
			this.classnameMapTempFile = new JRelativeFile(this.projectDirectory.getRelativePath() + File.separator + projectName + CLASSNAMEDICTIONARY_FILENAME_EXTENSION + CLASSNAMEDICTIONARY_TEMPFILE_EXTENSION, 
					 this.repositoryDirectory);
			value = new JClassnameMap(this.classnameMapTempFile);
		}
		else
			throw new JParseException("Missing " + key + " entry.");
		
		return value;
	}
	
	/**
	 * Returns the debug-information file to the file stored in aMap and creates a new {@link JJavaDebugInformation} entry.
	 * 
	 * @param aMap
	 * @param key the name of the project-debug-information-file entry.
	 * @throws IOException
	 */
	private JJavaDebugInformation getDebugInformationEntry(Map<String, List<String>> aMap, String key) throws IOException
	{
		JJavaDebugInformation value;
		List<String> list;
		String entry;
		
		value = null;
				
		if(aMap.containsKey(key))
		{
			list = aMap.get(key);
			
			if(list.size() > 0)
				entry = list.get(0);
			else
				entry = null;

			if(entry == null || entry.length() == 0)
				throw new JParseException("The Project File contains no valid name for the project-debug-information-file!");
			
			this.debugInformationFile = new JRelativeFile(entry,this.repositoryDirectory);
			value = JJavaDebugInformation.fromFile(this.debugInformationFile);
		}
		else
			throw new JParseException("Missing " + key + " entry.");
		
		return value;
	}
	
	/**
	 * @param aMap
	 * @param key the name of the signature list entry.
	 * @return a list of signatures stored in aMap.
	 * @throws IOException
	 */
	private List<JJavaSignature> getSignatureListEntry(Map<String, List<String>> aMap, String key) throws IOException
	{
		List<JJavaSignature> value;
		
		value = null;
		
		if(aMap.containsKey(key))
		{
			List<JJavaSignature> sigList = new ArrayList<JJavaSignature>();
			
			for(String qualifiedName : aMap.get(key))
			{
				if(!qualifiedName.toUpperCase().equals("NIL"))
					sigList.add(JJavaSignature.signatureFor(qualifiedName));
			}
				
			value = sigList;
		}
		else
			throw new JParseException("Missing " + key + " entry.");
		
		return value;
	}
	
	public static class ExistingProjectTest
	{
		@Before
		public void setUp() throws IOException
		{
			BasicConfigurator.configure();
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);
			
			File f = new File("test-repository" + File.separator + "project-fee-examples.lisp");
			
			if(!f.exists())
				JJavaSourceProject.main(new String[0]);
		}
		
		@Test
		public void test() throws IOException
		{
			JJavaProject ep = new JJavaExistingProject("fee-examples","test-repository");
			Assert.assertEquals("fee-examples",ep.projectName);
			Assert.assertTrue(ep.projectJarFile.exists());
		}
	}
	
	public static void main(String[] args)
	{
		JJavaProject ep;
		try
		{
			long start,end;
			BasicConfigurator.configure();
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);
			
			start = System.currentTimeMillis();
			ep = new JJavaExistingProject("java","test-repository");
			end = System.currentTimeMillis();
			
			Assert.assertEquals("java",ep.projectName);
			Assert.assertTrue(ep.projectJarFile.exists());
			
			System.out.println("Parsing " + ep.projectName + " took " + (end - start) + "ms");
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
