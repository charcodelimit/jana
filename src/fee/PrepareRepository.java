package fee;

import java.io.IOException;

import jana.java.JJavaSourceJARProject;
import jana.java.JJavaSourceProject;

import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;
import jana.util.logging.JSimpleLineLayout;

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

/**
 * Prepares the Repository Directory
 * 
 * puts all .class files from a source directory into a .jar file in
 * the repository, and creates the project information.
 *   
 * @author chr
 *
 */
public class PrepareRepository 
{
	private static final String prepareRepositoryCLIName = "PrepareRepository";

	private static final JLogger logger = JLogger.getLogger(Fee.DEFAULT_FEE_LOGGER + ".repository.prepare");
	
	private JJavaSourceProject project;
			
	public PrepareRepository()
	{	
	}
	
	private Options createOptions()
	{
		Option help = new Option("h", "help", false, "print this message");
		help.setRequired(false);
		
		Option verbose = new Option("v", "verbose", false, "increase verbosity of output.");
		verbose.setRequired(false);
		
		Option debug = new Option("D", "debug", false, "print debugging information.");
		debug.setRequired(false);
		
		Option analyze = new Option("a" ,"analyze", false, "analyze the project's class-files to find declarations of aspects.");
		
		OptionBuilder.withArgName( "classpath-entries" );
    	OptionBuilder.hasArg();
    	OptionBuilder.isRequired(false);
    	OptionBuilder.withDescription(  "the classpath entries required to run the application." );
    	OptionBuilder.withLongOpt( "classpath" );
    	Option classpath = OptionBuilder.create( "cp" );
		
    	OptionBuilder.withArgName( "directory-name" );
    	OptionBuilder.hasArg();
    	OptionBuilder.isRequired();
    	OptionBuilder.withDescription(  "the location of the repository directory." );
    	OptionBuilder.withLongOpt( "repository" );
		Option repositoryDirectory = OptionBuilder.create( "r" ); 
		
		OptionBuilder.withArgName( "fully-qualified classname-list" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
		OptionBuilder.withDescription(  "An (optional) colon separated list of fully-qualified classnames of classes that contain aspect declarations." );
		OptionBuilder.withLongOpt("aspects");
		Option aspects = OptionBuilder.create();
		
		OptionBuilder.withArgName( "project-name" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
		OptionBuilder.withLongOpt( "project" );
    	OptionBuilder.withDescription(  "the (optional) name of the project." );
		Option projectName = OptionBuilder.create( "p" );
		
		OptionBuilder.isRequired(false);
		OptionBuilder.withDescription( "Make a project from the .class files stored in a .jar-file instead of a directory.");
		OptionBuilder.withLongOpt("jar");
		Option jar = OptionBuilder.create(); 
		
		Options options = new Options();
		
		options.addOption(help);
		options.addOption(debug);
		options.addOption(analyze);
		options.addOption(classpath);
		options.addOption(repositoryDirectory);
		options.addOption(aspects);
		options.addOption(projectName);
		options.addOption(jar);
		
		return options;
	}
	
	public void processCommandLine(String[] args) throws Exception
	{
		HelpFormatter formatter;
		Options options;
		String[] remainingArgs;
		String helpstring;
		String classpath, repositoryDirectoryName, aspects, projectName;
		
		helpstring = prepareRepositoryCLIName + " -classpath -repository [options] " + " source-directory ";
		
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
			
			logger.setLevel(JLogLevel.INFO); 
			Server.setLogLevel(JLogLevel.INFO);
			
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
			
			classpath = cmd.getOptionValue("classpath");
			repositoryDirectoryName = cmd.getOptionValue("repository");
			 
			aspects = "";
			if( cmd.hasOption("aspects") )
				aspects = cmd.getOptionValue("aspects");
			
			projectName = "1";
			if( cmd.hasOption("project") )
				projectName = cmd.getOptionValue("project");
			
			remainingArgs = cmd.getArgs();
			
			if(remainingArgs.length == 1)
			{	
				if(cmd.hasOption("jar"))
				{
					this.project = new JJavaSourceJARProject(projectName, repositoryDirectoryName, remainingArgs[0], classpath);
				}
				else
				{
					this.project = new JJavaSourceProject(projectName, repositoryDirectoryName, remainingArgs[0], classpath);
				}
				
				if(cmd.hasOption("analyze"))
				{
					try
					{
						this.project.prepareRepository(true);
					}
					catch(IOException ioe)
					{
						logger.error(ioe.toString());
						System.exit(1);
					}
				}
				else
				{
					if(aspects.length() == 0)
						logger.warn("No aspects were defined for the project!");
					
					try
					{
						this.project.prepareRepository(aspects);	
					}
					catch(IOException ioe)
					{
						logger.error(ioe.toString());
						System.exit(1);
					}
				}
				
				this.project.saveProjectInfo();
				
				System.out.println("\n----");
				System.out.println("Created Project '" + this.project.getProjectName() + "' in Repository '" + this.project.getRepositoryDirectory() + "'.");
			}
			else
				formatter.printHelp( helpstring, options );
		}
		catch(ParseException pe)
		{	
			formatter.printHelp( helpstring, options );
		}	
	}
	
	public static void main(String[] args)
	{	
		PrepareRepository prepareRepositoryCLI = null;
		
		BasicConfigurator.configure();
		
		try
		{
			prepareRepositoryCLI = new PrepareRepository();
			prepareRepositoryCLI.processCommandLine(args);
			System.out.println("Done.");
		}
		catch(Exception e)
		{
			if(prepareRepositoryCLI.project != null)
				logger.error("Failed to Create Project " + prepareRepositoryCLI.project.getProjectName() + " in Repository " + prepareRepositoryCLI.project.getRepositoryDirectory());
			
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}	
}
