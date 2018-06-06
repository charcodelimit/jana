package fee;

import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;
import jana.util.logging.JSimpleLineLayout;

import fee.Fee;
import fee.server.Server;
import fee.stardust.StarDust;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

/**
 * The command line interface to SynTacticAnalysisRequiresDistributionwhenitUsesSlowfrontTends
 * 
 * @author chr
 *
 */
public class StarDustCLI
{	
	private static final JLogger logger = JLogger.getLogger(Fee.DEFAULT_FEE_LOGGER + ".stardust");
	
	private static final String starDustCLIName = "StarDustCLI";

	public static final int BENCHMARK_SIZE = 64;
	public static final String BIG_BENCHMARK_CLASSNAME = "java.lang.Class"; // class used to benchmark Fee
	public static final String SMALL_BENCHMARK_CLASSNAME = "java.lang.Cloneable"; // class used to benchmark Fee
	
	private String outputFilename;
	
	private long memoryUsageAtBegin;
	private long memoryUsageAtEnd;
	
	private StarDust starDust;
	
	public StarDustCLI()
	{	
		this.outputFilename = null;
		this.memoryUsageAtBegin = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	private Options createOptions()
	{
		Option help = new Option("help", "print this message");
		Option debug = new Option("debug", "print debugging information.");
		Option analyze = new Option("analyze", "don't start the server, and analyze the file given as argument instead.");
				
		OptionBuilder.withArgName( "directory-name" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "the location of the repository where the analyzed files should be placed." );
		Option repositoryDirectory = OptionBuilder.create( "repository" );
		
		OptionBuilder.withArgName( "host-name" );
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(  "The host ethernet address of the server." );
		Option host = OptionBuilder.create( "host" );
		
		OptionBuilder.withArgName( "port-number" );
		OptionBuilder.hasArg();
		OptionBuilder.withDescription(  "The number of the port used for Java's RMI server." );
		Option port = OptionBuilder.create( "port" );
		
		OptionBuilder.withArgName( "file" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription( "use given file to output analysis results." );
		Option outputFile = OptionBuilder.create( "filename" );
	
		OptionBuilder.withArgName( "classpath-entries" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "classpath entries that are added to the default classpath." );
		Option classpath = OptionBuilder.create( "classpath" );
		
		OptionBuilder.withArgName( "benchmark-number" );
    	OptionBuilder.hasArg();
		OptionBuilder.withDescription( "benchmark Fee (0/1)" );
		Option benchmark = OptionBuilder.create( "benchmark" );
		
		
		Options options = new Options();
		
		options.addOption(help);
		options.addOption(debug);
		options.addOption(benchmark);
		options.addOption(host);
		options.addOption(port);
		options.addOption(repositoryDirectory);
		options.addOption(classpath);
		options.addOption(analyze);
		options.addOption(outputFile);
		
		return options;
	}
	
	public void processCommandLine(String[] args) throws Exception
	{
		HelpFormatter formatter;
		Options options;
		String[] remainingArgs;
		String helpstring;
		
		helpstring = starDustCLIName + " [options]";
		
		options = createOptions();
		
		formatter = new HelpFormatter();
		
		try
		{
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			
			if( cmd.hasOption("help") )
			{
				formatter.printHelp( helpstring, options );
				System.exit(0);
			}
						
			if( cmd.hasOption("debug") )
			{
				BasicConfigurator.configure();
				
				logger.setLevel(JLogLevel.DEBUG);
				Server.setLogLevel(JLogLevel.DEBUG);
			}
			else if( cmd.hasOption("verbose") )
			{
				int count = 0;
				for(String argument : args)
				{
					if(argument.equals("-verbose") || argument.equals("-v") ||
					   argument.equals("--verbose") || argument.equals("--v"))
						count++;
				}
				
				if(count > 1)
				{
					BasicConfigurator.configure(new ConsoleAppender(new PatternLayout()));
					
					logger.setLevel(JLogLevel.VERBOSE);
					Server.setLogLevel(JLogLevel.VERBOSE);
				}
				else
				{
					BasicConfigurator.configure(new ConsoleAppender(new JSimpleLineLayout()));
					
					logger.setLevel(JLogLevel.INFO);
					Server.setLogLevel(JLogLevel.INFO);
				}
			}
			else
			{
				BasicConfigurator.configure(new ConsoleAppender(new JSimpleLineLayout(),"System.err"));	
				
				logger.setLevel(JLogLevel.WARN); 
				Server.setLogLevel(JLogLevel.WARN);
			}
			
			this.starDust = new StarDust();
						
			if( cmd.hasOption("repository") )
			{
				this.starDust.setRepositoryDirectory( cmd.getOptionValue("repository") );
			}

			if( cmd.hasOption("classpath") )
			{
				this.starDust.addClasspath( cmd.getOptionValue("classpath") );
			}
			
			if( cmd.hasOption("port") )
			{
				this.starDust.setPortNumber(cmd.getOptionValue("port"));
			}
			
			if( cmd.hasOption("host") )
			{
				this.starDust.setHostName(cmd.getOptionValue("host"));
			}
			
			if( cmd.hasOption("file") )
				this.outputFilename = cmd.getOptionValue("file");

			remainingArgs = cmd.getArgs();
			
			if(cmd.hasOption("benchmark"))
			{
				if(cmd.getOptionValue("benchmark").equals("0"))
					benchmark(BIG_BENCHMARK_CLASSNAME);
				if(cmd.getOptionValue("benchmark").equals("1"))
					benchmark(SMALL_BENCHMARK_CLASSNAME);
				
				System.exit(0);
			}
			
			if(cmd.hasOption("analyze"))
			{	
				if(remainingArgs.length > 0)
				{
					if(outputFilename != null)
						this.starDust.analyze(remainingArgs[0]); //, this.outputFilename);
					else
						this.starDust.analyze(remainingArgs[0]);
				}
				else
					formatter.printHelp( helpstring, options );
			}
			else
			{
				if(remainingArgs.length == 0)
					initStarDustRemoteServer();
				else
					formatter.printHelp( helpstring, options );
			}
			
			this.memoryUsageAtEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		}
		catch(ParseException pe)
		{	
			formatter.printHelp( helpstring, options );
		}	
	}
	
	public void benchmark(String aClassName) throws Exception
	{
		long start,end,avg = 0;
		
		logger.setLevel(JLogLevel.ERROR);
		Server.setLogLevel(JLogLevel.ERROR);
		
		System.out.println("Analyzing " + aClassName);
		
		System.out.print(".");
		start = System.currentTimeMillis();
		this.starDust.analyze(aClassName);
		end = System.currentTimeMillis();
		System.out.println("\nFirst analysis took: " + (end - start) + "ms");
		
		System.out.println("Analyzing " + aClassName);
		for(int i = 0; i < BENCHMARK_SIZE; i++)
		{
			System.out.print(".");
			start = System.currentTimeMillis();
			this.starDust.analyze(aClassName);
			end = System.currentTimeMillis();
			avg += (end - start);					
		}
		
		System.out.println("\nAverage analysis time: " + (avg/BENCHMARK_SIZE) + " ms");
	}
	
	private void initStarDustRemoteServer()
	{
		/*
		if (System.getSecurityManager() == null) 
		{
			System.setSecurityManager(new SecurityManager());
	    }*/
	   		
		this.starDust.connectToFeeStarDustServer();
	}

	public static void main(String[] args)
	{	
		StarDustCLI starDustCLI = null;
		
		BasicConfigurator.configure();
		
		try
		{
			starDustCLI = new StarDustCLI();
			starDustCLI.processCommandLine(args);
			logger.info("Jana used " + ((float) (starDustCLI.memoryUsageAtEnd - starDustCLI.memoryUsageAtBegin)  / (1 << 20)) + " MBytes of Memory");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}
}
