package test.jana;

import jana.java.JJavaExistingProject;
import jana.java.JJavaProject;
import jana.java.JJavaRepository;
import jana.java.JJavaSourceJARProject;
import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.JJavaBcelClassifier;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.StringReader;
import java.util.List;
import java.util.zip.CRC32;

import org.apache.log4j.BasicConfigurator;

import org.junit.Before;
import org.junit.Test;

public class BetaFourTest implements JTestInterface
{
	public List<Long> testAnalysis(double targetCoverage) throws Exception
	{
		java.util.List<Long> crcList;
		JJavaBcelClassifier jjbc;
		JJavaBcelRepository jjbr;
		JJavaProject prj;
		int count, size;
		double covered;
		CRC32 crc;
		
		prj = new JJavaExistingProject("java", "test-repository");
		
		crcList = new java.util.ArrayList<Long>();
		jjbr = new JJavaBcelRepository(prj);
		crc = new CRC32();
		
		size = prj.getProjectClasses().size();
		count = 0;
		
		System.out.println("-----------------");
		for( JJavaSignature signature : prj.getProjectClasses() )
		{
			covered = count * 100.0 / size;
			System.out.println( Math.round(covered) + "% [" + signature.qualifiedName() + "]");
				
			if(covered >= targetCoverage)
				break;
		
			jjbc = jjbr.analyzeClass(signature.qualifiedName());
			
			StringReader sr = new StringReader(jjbc.toSExpression());
			int v;
			
			crc.reset();
			do
			{
				v = sr.read();
				crc.update(v);
			}
			while(v!= -1);
			
			sr.close();
			sr = null;
			jjbc = null;
			crcList.add(new Long(crc.getValue()));
			System.out.println("CRC: " + crc.getValue());
			System.gc();
			System.out.println("Free Memory: " + ((Runtime.getRuntime().freeMemory()) >> 20) + " MiB  " + "[" + ((Runtime.getRuntime().totalMemory()) >> 20) + " MiB" + "]");
			System.out.println("---------------------");
			
			count++;
		}
		
		return crcList;
	}

	@Test
	public void unitTest() throws Exception
	{
		this.testAnalysis(2.5); // default 2.5% coverage
	}	

	/**
	 * Runs only if the project does not exist, as the functionality is tested in the Beta3.5 test
	 */
	@Before public void setUp() throws Exception
	{
		String jarFile;
		String url = Object.class.getResource("Runnable.class").toString();
		int start = url.lastIndexOf(":");
		int end = url.indexOf("!");
		
		BasicConfigurator.configure();
		JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.WARN);
		
		if( start != -1 && end != -1 )
		{
			jarFile = url.substring(start+1,end);
			
			JJavaSourceJARProject jjp = new JJavaSourceJARProject("java", "test-repository", jarFile, "");
			
			if(!jjp.exists())
			{
				jjp.prepareRepository(true);
				jjp.saveProjectInfo();
			}
		}
	}
	
	public void runTest(String[] args)
	{
		try
		{
			if(args.length == 2)
			{
				if(args[0].equals("-coverage") || args[0].equals("--coverage"))
				{
					this.testAnalysis(Double.parseDouble(args[1]));
				}
			}
			else
			{

				this.testAnalysis(25.0);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.WARN);
		
		try
		{
			BetaFourTest bft = new BetaFourTest();
			bft.setUp();
			bft.runTest(args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
