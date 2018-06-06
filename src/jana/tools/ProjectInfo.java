package jana.tools;

import jana.java.JJavaExistingProject;
import jana.java.JJavaProject;
import jana.lang.java.JJavaSignature;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

import fee.FeeServerStatusFrame;
import fee.server.Server;

public class ProjectInfo
{
	public static final String APPLICATION_NAME = "ProjectInfo";
	public static final String DEFAULT_LOGGER = "fee";
	private final static JLogger logger = JLogger.getLogger(DEFAULT_LOGGER); 
	
	private long memoryUsageAtBegin;
	private long memoryUsageAtEnd;
	
	JJavaProject existingProject;
	
	private ProjectInfo()
	{
		super();
		
		this.memoryUsageAtBegin = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	private Options createOptions()
	{
		Option help = new Option("h", "help", false, "print this message");
		help.setRequired(false);
		
		Option debug = new Option("v", "verbose", false, "print debugging information.");
		debug.setRequired(false);
		
		Option verbose = new Option("D", "debug", false, "print debugging information.");
		debug.setRequired(false);
			
		OptionBuilder.withArgName( "directory-name" );
    	OptionBuilder.hasArg();
    	OptionBuilder.isRequired();
    	OptionBuilder.withDescription(  "the location of the repository directory." );
    	OptionBuilder.withLongOpt( "repository" );
		Option repositoryDirectory = OptionBuilder.create( "r" ); 
		
		OptionBuilder.withArgName( "filename" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
    	OptionBuilder.withDescription(  "the pathname relative to the repository directory of a project file that should be analyzed." );
		OptionBuilder.withLongOpt( "projectfile" );
    	Option projectFile = OptionBuilder.create();
		
		OptionBuilder.withArgName( "project-name" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
		OptionBuilder.withLongOpt( "project" );
    	OptionBuilder.withDescription(  "the name of the project." );
		Option projectName = OptionBuilder.create( "p" );
		
		Options options = new Options();
		
		options.addOption(help);
		options.addOption(verbose);
		options.addOption(debug);
		options.addOption(repositoryDirectory);
		options.addOption(projectFile);
		options.addOption(projectName);
		
		return options;
	}
	
	public void processCommandLine(String[] args) throws Exception
	{
		HelpFormatter formatter;
		Options options;
		String helpstring;
		String projectName;
		String projectFilename;
		String repositoryDirectoryName;
		
		helpstring = APPLICATION_NAME + " [options] [classname]*";
		
		options = createOptions();
		
		formatter = new HelpFormatter();
		
		projectName = null;
		projectFilename = null;
		repositoryDirectoryName = null;
		
		try
		{
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
				
			if( cmd.hasOption("help") )
			{
				formatter.printHelp( helpstring, options );
				System.exit(0);
			}
			
			logger.setLevel(Level.WARN); 
			Server.setLogLevel(Level.WARN);
			
			if( cmd.hasOption("verbose") )
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
					logger.setLevel(JLogLevel.VERBOSE);
					Server.setLogLevel(JLogLevel.VERBOSE);
				}
				else
				{
					logger.setLevel(Level.INFO);
					Server.setLogLevel(Level.INFO);
				}
			}
			
			if( cmd.hasOption("debug") )
			{
				logger.setLevel(Level.DEBUG);
				Server.setLogLevel(Level.DEBUG);
			}
			
			if( cmd.hasOption("repository"))
			{
				repositoryDirectoryName = cmd.getOptionValue("repository");
			}
			
			if(repositoryDirectoryName == null)
			{
				System.err.println("Please provide a repository directory name!");
				formatter.printHelp( helpstring, options );
				System.exit(0);
			}
			
			if( cmd.hasOption("project"))
			{
				projectName = cmd.getOptionValue("project");	
			}
			
			if( cmd.hasOption("projectfile"))
			{
				projectFilename = cmd.getOptionValue("projectfile");
			}
			
			if(projectFilename == null && projectName == null)
			{
				System.err.println("Please provide a project name or the name of a project-file!");
				formatter.printHelp( helpstring, options );
				System.exit(0);
			}
			
			if(projectName != null)
				this.existingProject = new JJavaExistingProject(projectName, repositoryDirectoryName);
			
			if(projectFilename != null)
				this.existingProject = new JJavaExistingProject(new File(projectFilename), new File(repositoryDirectoryName));
			
			this.existingProject.loadClassnameMap();
			
			System.out.println("========== Project " + this.existingProject.getProjectName() + " =============");
			System.out.println(this.existingProject.toString());
			System.out.println("========== Aspects ==========");
			for( JJavaSignature sig : this.existingProject.getProjectAspects() )
			{
				System.out.println(sig.qualifiedName());
			}
			System.out.println("========== Classes ==========");
			for( JJavaSignature sig : this.existingProject.getProjectClasses() )
			{
				System.out.println(sig.qualifiedName());
			}
			System.out.println("========== Classname Map ==========");
			for( String entry : this.existingProject.getClassnameEntries() )
			{
				System.out.println(entry);
			}
			System.out.println("========== Classpath ==========");
			System.out.println(this.existingProject.getProjectClasspath().toString());
		}
		catch(ParseException pe)
		{	
			formatter.printHelp( helpstring, options );
			System.exit(0);
		}
		catch(FileNotFoundException fnfe)
		{
			System.err.println(fnfe.toString());
			System.exit(1);
		}
		
		this.memoryUsageAtEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		FeeServerStatusFrame.disposeFrame();
		
		System.gc(); // this should actually cause this.feeServer.finalize() to be called!
	}
	
	public static void main(String[] args)
	{	
		BasicConfigurator.configure();
		
		ProjectInfo info = new ProjectInfo();
		
		try
		{
			logger.setLevel(Level.INFO);
			info.processCommandLine(args);
			logger.info("Jana used " + ((float) (info.memoryUsageAtEnd - info.memoryUsageAtBegin)  / (1024 * 1024)) + " MBytes of Memory");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
			System.exit(1);
		}
	}
}
