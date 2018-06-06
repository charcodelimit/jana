package jana.tools;

import java.io.File;
import java.io.IOException;

import jana.lang.java.JJavaSignature;
import jana.util.JClasspath;
import jana.util.JClasspathDirectory;
import jana.util.jar.JJarFile;

public class BootClassesInfo
{
	final static String BOOT_CLASS_PATH_PROPERTY_NAME = "sun.boot.class.path";
	
	public void test() throws IOException
	{
		String bootClasspath;
		JClasspath classpath;
		JJarFile janaJarFile;
		JClasspathDirectory janaClasspathDirectory;
		
		bootClasspath = System.getProperty(BOOT_CLASS_PATH_PROPERTY_NAME);
		classpath = new JClasspath(bootClasspath);
		
		for(File jarFile : classpath.getClasspathJarFiles())
		{
			janaJarFile = new JJarFile(jarFile);
			
			System.out.println("----------------");
			System.out.println(jarFile);
			System.out.println("----------------");
			
			for(JJavaSignature classSignature : janaJarFile.classesInJarFile() )
			{
				System.out.println(classSignature.qualifiedName());
			}			
		}		
		
		for(File directory : classpath.getClasspathDirectories())
		{
			janaClasspathDirectory = new JClasspathDirectory(directory);
			
			System.out.println("----------------");
			System.out.println(directory);
			System.out.println("----------------");
			
			for(JJavaSignature classSignature : janaClasspathDirectory.classesInDirectory() )
			{
				System.out.println(classSignature.qualifiedName());
			}
		}
	}
	
	public static void main(String[] args)
	{
		BootClassesInfo bci = new BootClassesInfo();
		
		try
		{
			bci.test();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
