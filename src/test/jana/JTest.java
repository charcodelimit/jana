package test.jana;

import jana.java.JJavaRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.JJavaBcelClassifier;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;

public abstract class JTest implements JTestInterface
{
	public abstract List<JJavaBcelClassifier> testAnalysis() throws Exception;
	
	public abstract void unitTest() throws Exception;
	
	public List<JJavaBcelClassifier> test() throws Exception
	{
		List<JJavaBcelClassifier> classifiers;
		
		System.out.println(this.getClass().toString());
		
		classifiers = this.testAnalysis();
		System.out.println("Signatures: " + JJavaSignature.numSignatures());
		System.out.println("Types: " + JJavaSootType.numTypes());
		
		return classifiers;
	}
	
	@Before public void setUp() throws Exception
	{
		BasicConfigurator.configure();
		JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.WARN);
		
		JJavaSignature.initialize();
		JJavaSootType.initialize();
	}
	
	public void runTest(String[] args)
	{
		int timesRepeat = 0;
		long now, then, avg;
		boolean result = false;
		List<JJavaBcelClassifier> classifiers;
		
		if(args.length == 1)
		{
			timesRepeat = Integer.parseInt(args[0]);
		}
		if(args.length > 1)
		{
			System.out.println("Usage: " + this.getClass().getName() + " <(Integer) number-of-repetitions>");
			return;
		}
		
		avg = 0L;
		
		try
		{
			for(int i = 0; i < timesRepeat + 1; i++)
			{	
				then = System.currentTimeMillis();
				classifiers = this.test();
				now = System.currentTimeMillis();
				
				System.out.println("-------");
				
				for(JJavaBcelClassifier currentClassifier : classifiers)
				{
					System.out.println(currentClassifier.toString());
					System.out.println("(" + currentClassifier.toSExpression() + ")");
				}
				
				avg += ((long) (now - then) / 100);
			}
			
			result = true;
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			
			result = false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		
			result = false;
		}
		
		System.out.println();
		System.out.println("-------------------------");
		
		if(result)
		{
			System.err.println("Test Passed Successfully!");
		}
		else
		{
			System.err.println("Test Failed!");
		}
		
		System.err.println("Test took: " +  ((float) avg / (10 * (timesRepeat + 1))) + " sec");
	}
}
