package example.tests;
import jana.java.JJavaExistingProject;
import jana.java.JJavaProject;
import jana.java.JJavaRepository;
import jana.lang.java.JJavaSignature;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

/**
 * Starting
Total Memory: 4MB
 Free Memory: 1MB
 Used Memory: 2MB
0 [main] DEBUG jana  - Loading Project File
26 [main] DEBUG jana  - Loading Project File took 24ms
33 [main] DEBUG jana  - Parsing Project File
282 [main] DEBUG jana  - Parsing Project File took 249ms
282 [main] DEBUG jana  - Parsing Project Data
669 [main] DEBUG jana  - Parsing Project Data took 387ms
Parsing java took 769ms
Total Memory: 19MB
 Free Memory: 5MB
 Used Memory: 14MB
Copying Classnames took 1ms
Total Memory: 19MB
 Free Memory: 4MB
 Used Memory: 14MB
Loading 18312 classnames took 775ms
Total Memory: 19MB
 Free Memory: 14MB
 Used Memory: 5MB
 * @author chr
 *
 */


public class TestJavaClassesMemoryConsumption extends Object
{
	private static final String JAVA_PROJECT_NAME = "java";
	private long startTotal, startFree, projectTotal, projectFree, classnameTotal, classnameFree, endTotal, endFree;
	private Map<String,Object> classnames;
	
	public TestJavaClassesMemoryConsumption()
	{
		startTotal = Runtime.getRuntime().totalMemory();
		startFree = Runtime.getRuntime().freeMemory();
		
		System.out.println("Starting");
		
		System.out.println("Total Memory: " + (startTotal >> 20) + "MB");
		System.out.println(" Free Memory: " + (startFree >> 20) + "MB");
		System.out.println(" Used Memory: " + ((startTotal - startFree) >> 20) + "MB");
	}
	
	public JJavaProject loadJavaProject(String aRepositoryName) throws IOException
	{
		long start,end;		
		JJavaExistingProject ep;
				
		start = System.currentTimeMillis();
		ep = new JJavaExistingProject(JAVA_PROJECT_NAME,aRepositoryName);
		end = System.currentTimeMillis();
				
		System.out.println("Parsing " + ep.getProjectName() + " took " + (end - start) + "ms");
		
		projectTotal = Runtime.getRuntime().totalMemory();
		projectFree = Runtime.getRuntime().freeMemory();
		
		System.out.println("Total Memory: " + (projectTotal >> 20) + "MB");
		System.out.println(" Free Memory: " + (projectFree >> 20) + "MB");
		System.out.println(" Used Memory: " + ((projectTotal - projectFree) >> 20) + "MB");
		
		return ep;
	}
	
	public void loadJavaSystemLibraryClassnames(String aRepositoryName) throws IOException
	{
		long start, end;
		JJavaProject project;
		
		project = this.loadJavaProject(aRepositoryName);

		start = System.currentTimeMillis();
		
		this.classnames = new HashMap<String,Object>(project.getProjectClasses().size());
		
		for(JJavaSignature signature : project.getProjectClasses())
		{
			this.classnames.put(signature.qualifiedName(), null);
		}
		//this.classnames = project.getProjectClasses().toArray(this.classnames);
		
		end = System.currentTimeMillis();
		
		System.out.println("Copying Classnames took " + (end - start) + "ms");
		
		classnameTotal = Runtime.getRuntime().totalMemory();
		classnameFree = Runtime.getRuntime().freeMemory();
		
		System.out.println("Total Memory: " + (classnameTotal >> 20) + "MB");
		System.out.println(" Free Memory: " + (classnameFree >> 20) + "MB");
		System.out.println(" Used Memory: " + ((classnameTotal - classnameFree) >> 20) + "MB");		
	}
	
	public Map<String,Object> getJavaSystemLibraryClassnames()
	{
		return this.classnames;
	}
	
	public static void main(String[] args)
	{
		long start, end;
		
		BasicConfigurator.configure();
		JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(JLogLevel.DEBUG);

		start = System.currentTimeMillis();
		
		TestJavaClassesMemoryConsumption tjcmc = new TestJavaClassesMemoryConsumption();
		try
		{
			tjcmc.loadJavaSystemLibraryClassnames("test-repository");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		end = System.currentTimeMillis();
		
		System.out.println("Loading " + tjcmc.getJavaSystemLibraryClassnames().keySet().size() + " classnames took " + (end - start) + "ms");

		JJavaSignature.initialize();
		
		System.gc();
		
		try
		{
			Thread.sleep(5000);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		tjcmc.endTotal = Runtime.getRuntime().totalMemory();
		tjcmc.endFree = Runtime.getRuntime().freeMemory();
		

		System.out.println("Total Memory: " + (tjcmc.endTotal >> 20) + "MB");
		System.out.println(" Free Memory: " + (tjcmc.endFree >> 20) + "MB");
		System.out.println(" Used Memory: " + ((tjcmc.endTotal - tjcmc.endFree) >> 20) + "MB");		
	}
}
