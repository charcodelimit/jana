package jana.java;

import jana.metamodel.SExpression;
import jana.util.exceptions.JParseException;
import jana.util.exps.JSExpressionStringAtom;
import jana.util.exps.JSExpressionVectorList;
import jana.util.logging.JLogger;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

public class JJavaDebugInformation
{
	protected final static JLogger logger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER);
	private final static int READ_BUFFER_SIZE = 1 << 12;
	
	private Map<String, Map<String, int[]>> sourcePositions;
	private Map<String, String> filenames;
	
	public JJavaDebugInformation()
	{
		sourcePositions = new TreeMap<String, Map<String, int[]>>();
		filenames = new TreeMap<String, String>();
	}
	
	
	public Map<String, Map<String, int[]>> getSourcePositions()
	{
		return sourcePositions;
	}
	
	public Map<String, String> getFilenames()
	{
		return filenames;
	}
	
	public static JJavaDebugInformation fromFile(File aDebugInformationFile) throws IOException
	{
		JJavaDebugInformation instance = new JJavaDebugInformation();
		
		if(aDebugInformationFile.exists())
		{
			FileInputStream fis = new FileInputStream(aDebugInformationFile);
			BufferedInputStream bis = new BufferedInputStream(fis, READ_BUFFER_SIZE);

			try
			{
				instance.read(bis);
			}
			finally
			{
				bis.close();
				fis.close();
			}
		}
		
		return instance;
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
		long mstart, mend;
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Loading Project File");
		
		mstart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
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
		mend = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Loading Debug Information File took " + (end - start) + " ms [" + (mend - mstart) / 1024 + " KBytes of memory]");
		
    	s = sb.toString();
    	sb = null;
    	byteBuffer = null;
    	System.gc();
    	this.parse(s);
    
	}
	
	private void parse(String aString) throws JParseException, IOException
	{
		JSExpressionVectorList list;
		long start,end;
		long mstart, mend;
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Parsing Debug Information File");
		mstart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		start = System.currentTimeMillis();	  
		
		list = JSExpressionVectorList.parse(aString);
		aString = null;
		
		end = System.currentTimeMillis();	
		mend = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Parsing Debug Information File took " + (end - start) + " ms [" + (mend - mstart) / 1024 + " KBytes of memory]");
	    	
		this.parse(list);
	}
	
	private void parse(JSExpressionVectorList aList) throws JParseException
	{
		long start,end;
		long mstart,mend;
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Parsing Debug Information");
		
		if( aList.length() != 2 )
			throw new JParseException("The Debug Information List must Contain 2 Lists -- The Source-Positions List, and the Sourcefile List!");
		
		System.gc();
		mstart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		start = System.currentTimeMillis();	  
		this.parseSourcePositionList((JSExpressionVectorList) aList.first());
		end = System.currentTimeMillis();
		mend = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Processing the Source Positions List took " + (end - start) + " ms [" + (mend - mstart) / 1024 + " KBytes of memory]");
		
		System.gc();
		mstart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		start = System.currentTimeMillis();	  
		this.parseFilenameList((JSExpressionVectorList) aList.second());
		end = System.currentTimeMillis();
		mend = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel())) // ... give the JITter a chance
			logger.debug("Processing the Filename List took " + (end - start) + " ms [" + (mend - mstart) / 1024 + " KBytes of memory]");
	}
	
	private void parseSourcePositionList(JSExpressionVectorList aList) throws JParseException
	{
		JSExpressionVectorList list;
		
		if(!aList.first().toString().equals("source-positions"))
			throw new JParseException(aList.toSExpression() + " is no proper source-positions list!");
		
		this.sourcePositions = new TreeMap<String, Map<String, int[]>>();
		
		for( SExpression element : aList.getElements() )
		{
			if(element instanceof JSExpressionVectorList)
			{
				list = (JSExpressionVectorList) element;
				
				this.sourcePositions.put(list.first().toString(), parseClassSourcePositionList(list));
			}
		}
	}
	
	private Map<String,int[]> parseClassSourcePositionList(JSExpressionVectorList aList) throws JParseException
	{
		Map<String,int[]> methodSourcePositions;
		JSExpressionVectorList list, methodList;
		
		if(aList.length() != 2 || !(aList.first() instanceof JSExpressionStringAtom) || !(aList.second() instanceof JSExpressionVectorList))
			throw new JParseException(aList.toSExpression() + " must contain two elements: a class name, and a list.");
		
		methodList = (JSExpressionVectorList) aList.second();
		methodSourcePositions = new TreeMap<String, int[]>();
		
		for( SExpression element : methodList.getElements() )
		{
			if(element instanceof JSExpressionVectorList)
			{
				list = (JSExpressionVectorList) element;
				
				methodSourcePositions.put(list.first().toString(), parseMethodSourcePositionList(list));
			}
		}
		
		return methodSourcePositions;
	}
	
	private int[] parseMethodSourcePositionList(JSExpressionVectorList aList) throws JParseException
	{
		int[] sourcePositions;
		JSExpressionVectorList positionsList;
		
		if(aList.length() != 2 || !(aList.first() instanceof JSExpressionStringAtom) || !(aList.second() instanceof JSExpressionVectorList))
			throw new JParseException(aList.toSExpression() + " must contain two elements: a method name, and a list.");
		
		positionsList = (JSExpressionVectorList) aList.second();
		sourcePositions = new int[positionsList.length()];
		
		int i = 0;
		for( SExpression element : positionsList.getElements() )
		{
			sourcePositions[i] = Integer.parseInt(element.toString());
			i++;
		}
		
		return sourcePositions;
	}
	
	private void parseFilenameList(JSExpressionVectorList aList) throws JParseException
	{
		Object classname, filename; 
		JSExpressionVectorList list;
		
		if(!aList.first().toString().equals("source-files"))
			throw new JParseException(aList.toSExpression() + " is no proper source-files list!");
		
		this.filenames = new TreeMap<String, String>();
		
		for( SExpression element : aList.getElements() )
		{
			if(element instanceof JSExpressionVectorList)
			{
				list = (JSExpressionVectorList) element;
				
				if(list.length() != 2)
					throw new JParseException(list.toSExpression() + " is no proper <classname,filename> pair!");
				
				classname = list.first();
				filename = list.second();
				
				if(!((classname instanceof JSExpressionStringAtom) && 
						(filename instanceof JSExpressionStringAtom)))
					throw new JParseException(list.toSExpression() + " must contain a classname String and a filename String!");
				
				this.filenames.put(classname.toString(), filename.toString());
			}
		}
	}
	
	public static void main(String[] args)
	{
		JJavaDebugInformation jdi;
		JJavaDebugInformation.logger.setLevel(Level.DEBUG);
		
		BasicConfigurator.configure();
		
		try
		{
			jdi = JJavaDebugInformation.fromFile(new File("test-repository/project-bcel/trn/bcel.dbg"));
			System.out.println(jdi.toString());
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
			System.exit(1);
		}
	}
}
