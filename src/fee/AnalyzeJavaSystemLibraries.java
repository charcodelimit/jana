package fee;

import jana.java.JJavaSystemLibrariesProject;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;
import jana.util.logging.JSimpleLineLayout;

import java.io.FileNotFoundException;
import java.io.IOException;

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

import fee.server.Server;
import fee.util.ActiveServerException;

public class AnalyzeJavaSystemLibraries
{
	public static final String APPLICATION_NAME = AnalyzeJavaSystemLibraries.class.getName();
	public static final String SERVER_NAME = "StarDust";
	public static final String DEFAULT_FEE_LOGGER = "fee";
	
	private static final String hostOptionName = "address";
	private static final String serverOptionName = "foilserver";
	private static final String clJanaName = "cl-jana";
	
	private final static JLogger logger = JLogger.getLogger(DEFAULT_FEE_LOGGER); 
	
	private FeeServer feeServer;
	
	private long memoryUsageAtBegin;
	private long memoryUsageAtEnd;
	
	private AnalyzeJavaSystemLibraries()
	{
		super();
		
		this.memoryUsageAtBegin = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	private Options createOptions()
	{
		Option help = new Option("h", "help", false, "print this message");
		help.setRequired(false);
		
		Option verbose = new Option("v", "verbose", false, "increase verbosity of output.");
		verbose.setRequired(false);
		
		Option debug = new Option("D", "debug", false, "print debugging information.");
		debug.setRequired(false);
		
		Option full = new Option("f" ,"full", false, "don't compress the analysis output."
				+ "\nAll identifiers are exported as strings.");
						
		OptionBuilder.withArgName( "portnumber" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
    	OptionBuilder.withDescription(  "start as a server that provides a foreign object interface for " + clJanaName + ".");
		OptionBuilder.withLongOpt( "server" );
    	Option server = OptionBuilder.create( "s" );
		
		OptionBuilder.withArgName( "directory-name" );
    	OptionBuilder.hasArg();
    	OptionBuilder.isRequired(false);
    	OptionBuilder.withDescription(  "the location of the repository directory." );
    	OptionBuilder.withLongOpt( "repository" );
		Option repositoryDirectory = OptionBuilder.create( "r" ); 	
    	
    	Option compress;
		OptionBuilder.withArgName( "compression-level" );
		OptionBuilder.withDescription("use GZip to compress the analysis output.");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
		OptionBuilder.withLongOpt( "compress" );
		compress = OptionBuilder.create();
		
		OptionBuilder.withArgName( "hostname" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
    	OptionBuilder.withDescription(  "starts the server on the interface given by hostname.");
    	OptionBuilder.withLongOpt( hostOptionName );
		Option serverAddress = OptionBuilder.create();
		
		OptionBuilder.withArgName( "minimum-compute-servers" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
    	OptionBuilder.withDescription(  "don't start the analysis before <min-num-servers> " + SERVER_NAME + " compute-servers are available." );
		OptionBuilder.withLongOpt( "computeservers" );
    	Option minNumServers = OptionBuilder.create( "cs" );
    	
		Option cliMode = new Option(null, "cli", false, "use only the command line interface. " +
		"No GUI is started.");
		cliMode.setRequired(false);
		
		Options options = new Options();
		
		// modes
		options.addOption(help);	
		options.addOption(server);
		// repository options
		options.addOption(repositoryDirectory);
		// options
		options.addOption(debug);
		options.addOption(verbose);
		options.addOption(full);
		options.addOption(compress);
		options.addOption(minNumServers);
		options.addOption(serverAddress);
		options.addOption(cliMode);
		
		return options;
	}
	
	public void processCommandLine(String[] args) throws Exception
	{
		HelpFormatter formatter;
		Options options;
		String[] remainingArgs;
		String helpstring;
		String classpathString;
		String projectName;
		String minServersString;
		String repositoryDirectoryName;
		
		helpstring = APPLICATION_NAME + " [options] repository-name";
		
		options = createOptions();
		
		try
		{
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			
			if( cmd.hasOption("full") && cmd.hasOption("compress") )
			{
				formatter = new HelpFormatter();
				formatter.printHelp( helpstring, options );
				System.exit(0);
			}
			
			if( cmd.hasOption("help") )
			{
				formatter = new HelpFormatter();
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
			
			repositoryDirectoryName = "";
			
			if( cmd.hasOption("repository"))
			{
				repositoryDirectoryName = cmd.getOptionValue("repository");				
			}
			else
			{
				remainingArgs = cmd.getArgs();
				
				if(remainingArgs != null && remainingArgs.length == 1)
				{
					repositoryDirectoryName = remainingArgs[0];
				}
				else
				{
					formatter = new HelpFormatter();
					formatter.printHelp( helpstring, options );
					System.exit(0);
				}
			}

			// Prepare repository
			
			projectName = "";
			classpathString = "";
			
			try
			{
				JJavaSystemLibrariesProject jbtlp;
				
				jbtlp = new JJavaSystemLibrariesProject(repositoryDirectoryName);
				jbtlp.prepareRepository(true);
				jbtlp.saveProjectInfo();
				
				projectName = jbtlp.getProjectName();
				classpathString = jbtlp.getProjectClasspath().toString();
			}
			catch(IOException ioe)
			{
				System.out.println("\nPlease check the repository directory option: '-repository " + repositoryDirectoryName + "'");
				ioe.printStackTrace();
				System.exit(1);
			}
			
			// Analysis
			
			// create server
			
			if( !cmd.hasOption("cli") )
				this.feeServer = new FeeServer(false);
			else
				this.feeServer = new FeeServer(true);	
			
			// start server
			
			try
			{	
				if( cmd.hasOption(serverOptionName) )
				{
					if( !cmd.hasOption("cli") )
					{
						logger.debug("Starting GUI");
						this.feeServer.openUI();
					}
					
					if( cmd.hasOption(hostOptionName) )
						this.feeServer.startFoilServer( cmd.getOptionValue(serverOptionName), cmd.getOptionValue(hostOptionName) );
					else	
						this.feeServer.startFoilServer( cmd.getOptionValue(serverOptionName), null );
				}
			}
			catch(ActiveServerException ase)
			{
				logger.warn("No Server Started! \n" + ase.toString());
			}
			
			this.feeServer.startStarDustServer(null, null);			
			
			// set options
			
			try
			{
				this.feeServer.setRepositoryDirectory(repositoryDirectoryName);
			}
			catch(IOException ioe)
			{
				System.out.println("\nPlease check the repository directory option: '-repository " + repositoryDirectoryName + "'");
				ioe.printStackTrace();
				System.exit(1);
			}					
						
			if( cmd.hasOption("full") )
				this.feeServer.setCompressionLevel(0);
			
			if( cmd.hasOption("compress") )
			{	
				try
				{
					this.feeServer.setCompressionLevel( Integer.parseInt(cmd.getOptionValue("compress")) );
				}
				catch(NumberFormatException nfe)
				{
					System.err.println(cmd.getOptionValue("compress") + " is no valid compression level! Please use a number between 0-9.");
					this.feeServer.setCompressionLevel(1);
				}
			}
			
			if( ! cmd.hasOption("full") && ! cmd.hasOption("compress") )
			{
				this.feeServer.setCompressionLevel(1);
			}
			
			// one pass
			this.feeServer.setAnalyzeReferencedClasses(false);
			this.feeServer.setAnalyzeSupertypeClasses(false);
			
			this.feeServer.addClasspath(classpathString);
			
			if( cmd.hasOption("computeservers"))
			{
				minServersString = cmd.getOptionValue("computeservers");
				this.feeServer.setMinStarDustComputeServers(Integer.parseInt(minServersString) - 1);
			}
			else
			{
				this.feeServer.setMinStarDustComputeServers(0);
			}
			
			if( !cmd.hasOption("cli"))
			{
				logger.debug("Starting GUI");
				this.feeServer.openUI();
			}
			
			if(cmd.hasOption(serverOptionName))
			{
				this.feeServer.enterServerMode();
			}
			else
			{
				this.feeServer.analyzeProject(projectName);
			}
				
		}
		catch(ParseException pe)
		{	
			System.err.println("Wrong options!");
			formatter = new HelpFormatter();
			formatter.printHelp( helpstring, options );
			System.exit(0);
		}
		catch(FileNotFoundException fnfe)
		{
			System.err.println(fnfe.toString());
			System.exit(1);
		}
		
		this.memoryUsageAtEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		this.feeServer = null;
		
		FeeServerStatusFrame.disposeFrame();
		
		System.gc(); // this should actually cause this.feeServer.finalize() to be called!
	}
	
	public static void main(String[] args)
	{	
		long startTime, endTime;
		
		startTime = System.currentTimeMillis();
		
		AnalyzeJavaSystemLibraries fee = new AnalyzeJavaSystemLibraries();
		
		try
		{
			fee.processCommandLine(args);
			endTime = System.currentTimeMillis();
			logger.info(APPLICATION_NAME + " used " + ((float) (fee.memoryUsageAtEnd - fee.memoryUsageAtBegin)  / (1024 * 1024)) + " MBytes of Memory");
			System.err.println( ((int) ((endTime - startTime) / 1000.0)) + "sec");			
		}
		catch(Exception e)
		{
			endTime = System.currentTimeMillis();
			
			System.err.println( ((int) ((endTime - startTime) / 1000.0)) + "sec");
			
			e.printStackTrace();
			System.err.println(e.toString());
			System.exit(1);
		}
	}
}
