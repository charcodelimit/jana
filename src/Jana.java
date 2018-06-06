

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import jana.java.JJavaDefaultClasspath;
import jana.java.JJavaImaginaryProject;
import jana.java.JJavaProject;
import jana.java.bcel.JJavaBcelRepository;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.JJavaBcelClassifier;
import jana.util.exps.AssociationList;
import jana.util.exps.AssociationPairList;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.apache.log4j.BasicConfigurator;

import fee.server.Server;
import fee.StarDustCLI;

@Deprecated
public class Jana
{
	private static JLogger logger = JLogger.getLogger("jana");
	
	private JJavaBcelRepository repository;
	private long memoryUsageAtStart, memoryUsageAtEnd;
	private String outputFile;
	
	private File repositoryDirectory;
	private String classnameMapFile;
	private AssociationList<String, String> classnameMap;
	
	private static int MAX_FILES = 1 << 15; // 32k - maximum number of files with the same name in the repository
	
	public Jana() throws Exception
	{
		this.memoryUsageAtStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		this.outputFile = null;
		
		this.repositoryDirectory = null;
		this.classnameMapFile = "dictionary.cnd";
	}
	
	public void setRepositoryDirectory(String aFilename) throws IOException
	{
		File file;
		
		file = new File(aFilename);
		
		if(! file.isDirectory())
			throw new IOException( aFilename + " is no directory!");
		
		repositoryDirectory = file;
	}
	
	private void analyze(JJavaSignature aClassifierSignature) throws Exception
	{
		JJavaBcelClassifier classifier;
		File file;
		
		classifier = analyze(aClassifierSignature.qualifiedName());
		
		// assume default Directory
		if(this.outputFile == null && this.repositoryDirectory == null )
			setRepositoryDirectory(".");
		
		if(this.repositoryDirectory != null)
		{
			loadClassnameMap();
		}
		
		if(this.outputFile != null)
		{
			file = new File(this.outputFile);
			
			outputToLispFile(file, classifier);
		}
		else
		{
			int count = 0;
			
			do
			{
				if(count != 0)
					file = new File(this.repositoryDirectory + File.separator + aClassifierSignature.unqualifiedName()+"-" + count + ".lisp");	
				else
					file = new File(this.repositoryDirectory + File.separator + aClassifierSignature.unqualifiedName()+".lisp");
					
				count++;
			}
			while(file.exists() && count < MAX_FILES);
				
				
			outputToLispFile(file, classifier);
		}
		
		if(this.classnameMap != null)
		{
			this.classnameMap.addPair(classifier.qualifiedName(), file.getAbsolutePath() );
			
			saveClassnameMap();
		}
	}
	
	public JJavaBcelClassifier analyze(String aClassname) throws Exception
	{
		long t1,t2, start;
		JavaClass cls;
		JJavaBcelClassifier jjbc;
		SyntheticRepository repository;
		
		logger.info("Analyzing: " + aClassname);
		
		t1 = System.currentTimeMillis();
		start = t1;
		repository = SyntheticRepository.getInstance( new ClassPath(this.repository.getClasspath().toString()) );
		cls =  repository.loadClass(aClassname);
		Repository.clearCache();
		t2 = System.currentTimeMillis();
		logger.info("BCEL Syntactic Analysis of Classfile took: " + (t2 - t1) + " ms");
		
		t1 = System.currentTimeMillis();
		jjbc = JJavaBcelClassifier.produce(cls, this.repository);
		t2 = System.currentTimeMillis();
		logger.info("Semantic Analysis of Classfile took: " + (t2 - t1) + " ms");
		
		memoryUsageAtEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		logger.info("Overall analysis took: " + (t2 - start) + " ms");
		
		return jjbc;
	}
	
	void outputToLispFile(File aFile, JJavaBcelClassifier aClassifier) throws Exception
	{	
		FileOutputStream fos = new FileOutputStream(aFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos, 1024 * 1024);
		PrintWriter pw = new PrintWriter(bos);
		String expression;
		long t1,t2,acc;
		
		acc = 0L;
		
		try
		{
			pw.println(";;; -*- Mode: LISP; PACKAGE: JANA -*- ");
			pw.print("(");
			t1 = System.currentTimeMillis();
			expression = this.repository.toSExpression();
			t2 = System.currentTimeMillis();
			acc += t2 - t1;
			pw.print(expression);
			pw.print("\n ");
			pw.print("(");
			t1 = System.currentTimeMillis();
			expression = aClassifier.toSExpression();
			t2 = System.currentTimeMillis();
			acc += t2 - t1;
			pw.println(expression);
			pw.print(")");
			pw.println(")");
			pw.close();
			bos.close();
			fos.close();
		}
		catch(Exception e)
		{
			pw.close();
			bos.close();
			fos.close();
			throw new Exception(e);
		}
		
		logger.info("Conversion to SExpression took: " + acc + " ms");
	}
	
	private void loadClassnameMap() throws IOException
	{
		FileInputStream fis;
		File mapFile;
		
		mapFile = new File(repositoryDirectory.getAbsolutePath() + File.separator + classnameMapFile);
		
		if(!mapFile.canRead())
			throw new IOException("Can't read classname map file " + mapFile.getAbsolutePath() );
		
		fis = new FileInputStream(mapFile);
		
		try
		{
			this.classnameMap = AssociationPairList.read( fis );
		}
		finally
		{
			fis.close();
		}
	}
	
	private void saveClassnameMap() throws Exception
	{
		File mapFile = new File(repositoryDirectory.getAbsolutePath() + File.separator + classnameMapFile);
		
		if(!mapFile.canWrite())
			if(!mapFile.createNewFile())
				throw new IOException("Can't write classname map file " + mapFile.getAbsolutePath() );
		
		this.classnameMap.write( new FileOutputStream(mapFile) );
	}
	
	private Options createOptions()
	{
		Option help = new Option("help", "print this message");
		Option debug = new Option("debug", "print debugging information.");
		
		OptionBuilder.withArgName( "filename" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "use given file to output analysis results." );
		Option ouputFile = OptionBuilder.create( "file" );
		
		OptionBuilder.withArgName( "directoryname" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "the location of the repository where the analyzed files should be placed." );
		Option repositoryDirectory = OptionBuilder.create( "repository" );
		
		OptionBuilder.withArgName( "benchmark-number" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription( "benchmark Fee (0/1)" );
		Option benchmark = OptionBuilder.create( "benchmark" );
		
		OptionBuilder.withArgName( "classpath-entries" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "classpath entries that are added to the default classpath." );
		Option classpath = OptionBuilder.create( "classpath" );
		
		Options options = new Options();
		
		options.addOption(help);
		options.addOption(debug);
		options.addOption(classpath);
		options.addOption(ouputFile);
		options.addOption(repositoryDirectory);
		options.addOption(benchmark);
		
		return options;
	}
	
	public void processCommandLine(String[] args) throws Exception
	{
		HelpFormatter formatter;
		Options options;
		String classpathString;
		String[] remainingArgs;
		JJavaProject dummyProject;
		
		options = createOptions();
		
		formatter = new HelpFormatter();
		
		try
		{
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			
			if( cmd.hasOption("help") )
			{
				String helpString;
				
				helpString = "All identifiers are exported as full strings in the output format." + "\n"
							 + "Jana [options] [classname]";
				formatter.printHelp( helpString, options );
				System.exit(0);
			}
			
			logger.setLevel(JLogLevel.INFO);
			
			if( cmd.hasOption("debug") )
				logger.setLevel(JLogLevel.DEBUG);
			
			if( cmd.hasOption("repository") )
			{
				this.setRepositoryDirectory( cmd.getOptionValue("repository") );
			}
			
			classpathString = "";
			
			if( cmd.hasOption("classpath"))
			{
				classpathString = cmd.getOptionValue("classpath");
				this.repository.addClasspathElements(classpathString);
			}
			else
			{
				JJavaDefaultClasspath defaultClasspath;
				defaultClasspath = new JJavaDefaultClasspath();
				classpathString = defaultClasspath.toString();
			}
			
			/******* IMPORTANT KLUDGE TO MAKE THINGS WORK WITHOUT REPOSITORY *******/
			// create a DUMMY PROJECT, as we DON'T WRITE THE DICTIONARY TO DISK!
			dummyProject = new JJavaImaginaryProject(classpathString); 
			/**********************************************************************/
			
			this.repository = new JJavaBcelRepository(dummyProject);
			
			if( cmd.hasOption("file") )
				this.outputFile = cmd.getOptionValue("file");
			
			if(cmd.hasOption("benchmark"))
			{
				if(cmd.getOptionValue("benchmark").equals("0"))
					benchmark(StarDustCLI.BIG_BENCHMARK_CLASSNAME);
				if(cmd.getOptionValue("benchmark").equals("1"))
					benchmark(StarDustCLI.SMALL_BENCHMARK_CLASSNAME);
				
				System.exit(0);
			}
			
			remainingArgs = cmd.getArgs();
			
			if(remainingArgs.length == 1)
			{	
				JJavaSignature sig = JJavaSignature.signatureFor(remainingArgs[0]);
				
				analyze(sig);
			}
			else
				formatter.printHelp( "Jana [options] [classname]", options );
		}
		catch(ParseException pe)
		{
			formatter.printHelp( "Jana [options] [classname]", options );
		}
	}
	
	public void benchmark(String aClassName) throws Exception
	{
		long start,end,avg = 0;
		JJavaSignature sig;
		
		logger.setLevel(JLogLevel.ERROR);
		Server.setLogLevel(JLogLevel.ERROR);
		
		System.out.println("Analyzing " + aClassName);
		
		System.out.print(".");
		start = System.currentTimeMillis();
		sig = JJavaSignature.signatureFor(aClassName);
		this.analyze(sig);
		end = System.currentTimeMillis();
		System.out.println("\nFirst analysis took: " + (end - start) + "ms");
		
		System.out.println("Analyzing " + aClassName);
		for(int i = 0; i < StarDustCLI.BENCHMARK_SIZE; i++)
		{
			System.out.print(".");
			start = System.currentTimeMillis();
			sig = JJavaSignature.signatureFor(aClassName);
			this.analyze(sig);
			end = System.currentTimeMillis();
			avg += (end - start);					
		}
		
		System.out.println("\nAverage analysis time: " + (avg/StarDustCLI.BENCHMARK_SIZE) + " ms");
	}
	
	public static void main(String[] args)
	{	
		Jana jana;
		
		try
		{
			BasicConfigurator.configure();
			logger.setLevel(JLogLevel.INFO);
			
			jana = new Jana();
			jana.processCommandLine(args);
			logger.info("Jana used " + ((float) (jana.memoryUsageAtEnd - jana.memoryUsageAtStart)  / (1024 * 1024)) + " MBytes of Memory");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}
}
