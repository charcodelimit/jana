package test.jana;

import java.io.IOException;	

import org.apache.log4j.BasicConfigurator;

import jana.java.JJavaRepository;
import jana.java.JJavaSourceJARProject;
import jana.lang.java.JJavaSignature;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

public class BetaThreeDotFiveTest
{
	public static void setUp()
	{
		BasicConfigurator.configure();
		JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.WARN);
		
		JJavaSignature.initialize();
		JJavaSootType.initialize();
	}
	
	public static void main(String[] args)
	{
		try
		{
			setUp();
			
			String jarFile;
			String url = Object.class.getResource("Runnable.class").toString();
			int start = url.lastIndexOf(":");
			int end = url.indexOf("!");
		
			if( start != -1 && end != -1 )
			{
				jarFile = url.substring(start+1,end);
			
				JJavaSourceJARProject jjp = new JJavaSourceJARProject("java", "test-repository", jarFile, "");
				jjp.prepareRepository(true);
				jjp.saveProjectInfo();
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
